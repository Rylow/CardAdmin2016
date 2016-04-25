package com.rylow.cardadmin2016;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rylow.cardadmin2016.service.Staff;
import com.rylow.cardadmin2016.service.StaffReportAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bakht on 25.04.2016.
 */
public class AllStaffAttendanceActivity extends AppCompatActivity {

    ListView listAllStaff;
    ArrayList<Staff> listStaff;
    StaffReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_staff_attendance);

        listAllStaff = (ListView) findViewById(R.id.listAllStaff);

        getAllStaff();


    }

    private void getAllStaff() {

        final JSONArray array = new JSONArray();

        File mydir = getApplicationContext().getDir("staffphotos", Context.MODE_PRIVATE); //Creating an internal dir;

        File[] list = mydir.listFiles();

        for (File f : list){

            JSONObject json = new JSONObject();
            try {
                json.put("name", f.getName());
                json.put("size", f.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            array.put(json);

        }

        AsyncTask query = new AsyncTask<Integer, Void, JSONArray>(){

            @Override
            protected JSONArray doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return requestAllStaffList(connect, array);

                    }
                    else {

                        showErrorMessage();

                        return new JSONArray();


                    }
                }
                else{

                    return requestAllStaffList(connect, array);

                }
            }
        }.execute();

        try {
            JSONArray arrayStaff = (JSONArray) query.get();

            listStaff = new ArrayList<>();

            for (int i = 0; i < arrayStaff.length(); i++){

                JSONObject json = arrayStaff.getJSONObject(i);
                Log.v("aaaa", json.toString());


                Staff staff = new Staff(json.getString("name"), json.getString("email"), json.getString("role"), json.getString("photo"), json.getString("photofilename"), json.getBoolean("external"),
                        json.getBoolean("active"), json.getInt("terminal"), json.getInt("id"), json.getInt("securitygroup"), json.getBoolean("ontemporarycard"), json.getLong("timein"));

                if (staff.getPhoto().length() > 1){ //WE DONT HAVE A PICTURE
                    File staffPicture = new File(mydir, staff.getPhotoFileName()); //Getting a file within the dir.
                    FileOutputStream out = new FileOutputStream(staffPicture);
                    out.write(staff.getPhotoAsByteArray());
                    out.flush();
                    out.close();
                    staff.setPhoto("");
                }

                listStaff.add(staff);


            }


            adapter = new StaffReportAdapter(AllStaffAttendanceActivity.this, listStaff);

            listAllStaff.setAdapter(adapter);

            adapter.notifyDataSetChanged();

            listAllStaff.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {

                    final Staff value = (Staff) parent.getItemAtPosition(position);

                    Intent intent = new Intent(AllStaffAttendanceActivity.this, MyAttendanceActivity.class);

                    intent.putExtra("staffid", value.getId());
                    intent.putExtra("returnto", 2);

                    startActivity(intent);
                    finish();
                    return true;
                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JSONArray requestAllStaffList(Connect connect, JSONArray array) {

        JSONObject finalJSON = new JSONObject();
        try {

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            finalJSON.put("code", TransmissionCodes.REQUEST_ALL_STAFF_LIST);
            finalJSON.put("array", array);

            outToServer.write(finalJSON.toString());
            outToServer.newLine();
            outToServer.flush();

            String incString = inFromServer.readLine();

            if (incString != null) {
                incString = incString.trim();
            }
            else {
                incString = "";
                showErrorMessage();
                connect.getClientSocket().close();

            }

            JSONObject recievedJSON = new JSONObject(incString);

            if (recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_ALL_STAFF_LIST){

                return recievedJSON.getJSONArray("array");


            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONArray();


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AllStaffAttendanceActivity.this, MainWindowActivity.class);

        startActivity(intent);
        finish();

    }

    private void showErrorMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(AllStaffAttendanceActivity.this).create();
                alertDialog.setTitle("Failure");
                alertDialog.setMessage("Connection to the server is not available. Probably mobile connection is not available at this moment. Please again later.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });



    }

}
