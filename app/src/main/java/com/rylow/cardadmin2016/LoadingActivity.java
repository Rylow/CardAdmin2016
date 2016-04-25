package com.rylow.cardadmin2016;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rylow.cardadmin2016.service.Staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by bakht on 25.04.2016.
 */
public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        new Thread(new Runnable() {
            @Override
            public void run() {

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

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        loadPictures(connect, array);

                    }
                    else {

                        showErrorMessage();

                    }
                }
                else{

                    loadPictures(connect, array);

                }




            }
        }).start();



    }

    private void loadPictures(Connect connect, JSONArray array) {

        JSONObject finalJSON = new JSONObject();
        try {

            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            finalJSON.put("code", TransmissionCodes.REQUEST_PICTURES);
            finalJSON.put("array", array);

            outToServer.write(finalJSON.toString());
            outToServer.newLine();
            outToServer.flush();

            File mydir = getApplicationContext().getDir("staffphotos", Context.MODE_PRIVATE);

            while (true){

                String incString = inFromServer.readLine();

                if (incString != null) {
                    incString = incString.trim();
                }
                else {
                    incString = "";
                    showErrorMessage();
                    connect.getClientSocket().close();
                    break;
                }

                JSONObject recievedJSON = new JSONObject(incString);

                if (recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_PICTURES){

                    JSONObject json = recievedJSON.getJSONObject("person");
                    Staff staff = new Staff(json.getString("name"), json.getString("email"), json.getString("role"), json.getString("photo"), json.getString("photofilename"), json.getBoolean("external"),
                            json.getBoolean("active"), json.getInt("terminal"), json.getInt("id"), json.getInt("securitygroup"), json.getBoolean("ontemporarycard"), json.getLong("timein"));

                    if (staff.getPhoto().length() > 0){ //WE DONT HAVE A PICTURE
                        File staffPicture = new File(mydir, staff.getPhotoFileName()); //Getting a file within the dir.
                        FileOutputStream out = new FileOutputStream(staffPicture);
                        out.write(staff.getPhotoAsByteArray());
                        out.flush();
                        out.close();
                        staff.setPhoto("");
                    }


                }
                else{
                    break;
                }

            }





        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(LoadingActivity.this, MainWindowActivity.class);

        startActivity(intent);
        finish();



    }

    private void showErrorMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(LoadingActivity.this).create();
                alertDialog.setTitle("Failure");
                alertDialog.setMessage("Connection to the server is not available. Probably mobile connection is not available at this moment. Please again later.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);

                startActivity(intent);
                finish();

            }
        });



    }

}
