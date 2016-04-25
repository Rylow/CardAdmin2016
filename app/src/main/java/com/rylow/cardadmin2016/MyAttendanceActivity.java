package com.rylow.cardadmin2016;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.rylow.cardadmin2016.service.Staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by bakht on 25.04.2016.
 */
public class MyAttendanceActivity  extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    ImageView imgPersonAttendancePhoto;
    TextView txtPersonAttendanceName, txtPersonAttendanceRole, txtPersonAttendanceExternal, txtPersonAttendanceReportFor;
    Button btnPersonAttendance;
    ListView listPersonAttendance;
    int staffid, returnTo;


    private static final int RETURN_TO_MAIN_SCREEN = 1;
    private static final int RETURN_TO_ALLSTAFF = 2;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_attendance);

        Intent intent = getIntent();

        staffid = intent.getIntExtra("staffid", 0);
        returnTo = intent.getIntExtra("returnto", RETURN_TO_MAIN_SCREEN);

        imgPersonAttendancePhoto = (ImageView) findViewById(R.id.imgPersonAttendancePhoto);
        txtPersonAttendanceName = (TextView) findViewById(R.id.txtPersonAttendanceName);
        txtPersonAttendanceRole = (TextView) findViewById(R.id.txtPersonAttendanceRole);
        txtPersonAttendanceExternal = (TextView) findViewById(R.id.txtPersonAttendanceExternal);
        txtPersonAttendanceReportFor = (TextView) findViewById(R.id.txtPersonAttendanceReportFor);
        btnPersonAttendance = (Button) findViewById(R.id.btnPersonAttendance);
        listPersonAttendance = (ListView) findViewById(R.id.listPersonAttendance);

        txtPersonAttendanceReportFor.setText("Report for " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR));

        btnPersonAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(MyAttendanceActivity.this)
                        .setFirstDayOfWeek(Calendar.MONDAY)
                        .setPreselectedDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        .setDoneText("Select")
                        .setCancelText("Cancel")
                        .setThemeDark(true);
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });

        Calendar cal = Calendar.getInstance();

        getPersonalAttendance(staffid, true, cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));

    }

    private void getPersonalAttendance(final int staffid, final Boolean fullprofile, final int day, final int month, final int year) {


        AsyncTask query = new AsyncTask<Integer, Void, JSONObject>(){

            @Override
            protected JSONObject doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return requestAttendance(connect, staffid, fullprofile, day, month, year);

                    }
                    else {

                        showErrorMessage();

                        return new JSONObject();


                    }
                }
                else{

                    return requestAttendance(connect, staffid, fullprofile, day, month, year);

                }
            }
        }.execute();


        try {
            JSONObject recievedJSON = (JSONObject) query.get();

            if (fullprofile) {

                JSONObject json = recievedJSON.getJSONObject("person");

                Staff staff = new Staff(json.getString("name"), json.getString("email"), json.getString("role"), json.getString("photo"), json.getString("photofilename"), json.getBoolean("external"),
                        json.getBoolean("active"), json.getInt("terminal"), json.getInt("id"), json.getInt("securitygroup"), false, 0);

                imgPersonAttendancePhoto.setImageBitmap(staff.getPicture());
                txtPersonAttendanceName.setText(staff.getName());
                txtPersonAttendanceRole.setText(staff.getRole());

                if (staff.getExternal())
                    txtPersonAttendanceExternal.setVisibility(View.VISIBLE);
                else
                    txtPersonAttendanceExternal.setVisibility(View.INVISIBLE);

            }

            JSONArray array = recievedJSON.getJSONArray("array");

            ArrayList<String> listAttendance = new ArrayList<>();

            for(int i = 0; i < array.length(); i++){
                JSONObject jsonAttendance = array.getJSONObject(i);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date curDate = new Date(jsonAttendance.getLong("time"));

                listAttendance.add(sdf.format(curDate) + " " + jsonAttendance.getString("terminal"));
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.item_attendance_report,listAttendance);

            listPersonAttendance.setAdapter(adapter);

            adapter.notifyDataSetChanged();




        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject requestAttendance(Connect connect, int staffid, Boolean fullprofile, int day, int month, int year) {

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.REQUEST_PERSONAL_ATTENDANCE);
            json.put("staffid", staffid);
            json.put("day", day);
            json.put("month", month);
            json.put("year", year);
            json.put("fullprofile", fullprofile);

            outToServer.write(json.toString());
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

            if (recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_PERSONAL_ATTENDANCE){

                return recievedJSON;

            }




        } catch (IOException e) {
            showErrorMessage();
            try {
                connect.getClientSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    @Override
    public void onBackPressed() {

        switch (returnTo){

            case RETURN_TO_ALLSTAFF :
                Intent intent = new Intent(MyAttendanceActivity.this, AllStaffAttendanceActivity.class);
                startActivity(intent);
                finish();
                break;
            case RETURN_TO_MAIN_SCREEN :
                Intent intent2 = new Intent(MyAttendanceActivity.this, MainWindowActivity.class);
                startActivity(intent2);
                finish();
                break;


        }



    }

    private void showErrorMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(MyAttendanceActivity.this).create();
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

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {

        txtPersonAttendanceReportFor.setText("Report for " + dayOfMonth + "." + (monthOfYear+1) + "." + year);
        getPersonalAttendance(staffid, false, dayOfMonth, monthOfYear, year);

    }
}
