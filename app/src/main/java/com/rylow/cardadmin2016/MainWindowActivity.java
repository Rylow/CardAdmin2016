package com.rylow.cardadmin2016;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

/**
 * Created by bakht on 24.04.2016.
 */
public class MainWindowActivity extends AppCompatActivity {

    private class UserRights{

        private Boolean inSchoolReport;
        private Boolean opendoors;
        private Boolean myattendamce;
        private Boolean securitycameras;
        private Boolean schoolattendance;
        private Boolean serversettings;

        public UserRights() {

            this.inSchoolReport = false;
            this.opendoors = false;
            this.myattendamce = false;
            this.securitycameras = false;
            this.schoolattendance = false;
            this.serversettings = false;

        }

        public Boolean getInSchoolReport() {
            return inSchoolReport;
        }

        public void setInSchoolReport(Boolean inSchoolReport) {
            this.inSchoolReport = inSchoolReport;
        }

        public Boolean getOpendoors() {
            return opendoors;
        }

        public void setOpendoors(Boolean opendoors) {
            this.opendoors = opendoors;
        }

        public Boolean getMyattendamce() {
            return myattendamce;
        }

        public void setMyattendamce(Boolean myattendamce) {
            this.myattendamce = myattendamce;
        }

        public Boolean getSecuritycameras() {
            return securitycameras;
        }

        public void setSecuritycameras(Boolean securitycameras) {
            this.securitycameras = securitycameras;
        }

        public Boolean getSchoolattendance() {
            return schoolattendance;
        }

        public void setSchoolattendance(Boolean schoolattendance) {
            this.schoolattendance = schoolattendance;
        }

        public Boolean getServersettings() {
            return serversettings;
        }

        public void setServersettings(Boolean serversettings) {
            this.serversettings = serversettings;
        }
    }

    ImageView imgMainCurInSchool, imgMainOpenDoors, imgMainMyAttendance, imgMainSecurityCameras, imgMainSchoolAttendance, imgMainServerSettings;
    TextView txtMainCurrentlyInSchool, txtMainOpenDoors, txtMainMyAttendance, txtMainSecurityCameras, txtMainSchoolAttendance, txtMainServerSettings;
    UserRights userRights;
    String cardnumber;
    int staffid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgMainCurInSchool = (ImageView) findViewById(R.id.imgMainCurInSchool);
        imgMainOpenDoors = (ImageView) findViewById(R.id.imgMainOpenDoors);
        imgMainMyAttendance = (ImageView) findViewById(R.id.imgMainMyAttendance);
        imgMainSecurityCameras = (ImageView) findViewById(R.id.imgMainSecurityCameras);
        imgMainSchoolAttendance = (ImageView) findViewById(R.id.imgMainSchoolAttendance);
        imgMainServerSettings = (ImageView) findViewById(R.id.imgMainServerSettings);

        txtMainCurrentlyInSchool = (TextView) findViewById(R.id.txtMainCurrentlyInSchool);
        txtMainOpenDoors = (TextView) findViewById(R.id.txtMainOpenDoors);
        txtMainMyAttendance = (TextView) findViewById(R.id.txtMainMyAttendance);
        txtMainSecurityCameras = (TextView) findViewById(R.id.txtMainSecurityCameras);
        txtMainSchoolAttendance = (TextView) findViewById(R.id.txtMainSchoolAttendance);
        txtMainServerSettings = (TextView) findViewById(R.id.txtMainServerSettings);


        loadUserRights();





    }

    private void loadUserRights() {

        AsyncTask query = new AsyncTask<Integer, Void, UserRights>(){

            @Override
            protected UserRights doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return getUserRights(connect);

                    }
                    else {

                        showErrorMessage();

                        return new UserRights();


                    }
                }
                else{

                    return getUserRights(connect);

                }
            }
        }.execute();

        try {
            userRights = (UserRights) query.get();

            if (!userRights.getInSchoolReport()) {
                imgMainCurInSchool.setVisibility(View.GONE);
                txtMainCurrentlyInSchool.setVisibility(View.GONE);
            }else{
                imgMainCurInSchool.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {

                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                Intent intent = new Intent(MainWindowActivity.this, CurrentlyInSchoolActivity.class);

                                startActivity(intent);
                                finish();

                                break;
                            }
                        }

                        return true;

                    }
                });

            }
            if(!userRights.getOpendoors()) {
                imgMainOpenDoors.setVisibility(View.GONE);
                txtMainOpenDoors.setVisibility(View.GONE);
            }
            else{

                imgMainOpenDoors.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {

                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                Intent intent = new Intent(MainWindowActivity.this, OpenDoorsActivity.class);

                                intent.putExtra("cardnumber", cardnumber);

                                startActivity(intent);
                                finish();

                                break;
                            }
                        }

                        return true;

                    }
                });



            }
            if (!userRights.getMyattendamce()) {
                imgMainMyAttendance.setVisibility(View.GONE);
                txtMainMyAttendance.setVisibility(View.GONE);
            }
            else{

                imgMainMyAttendance.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {

                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                Intent intent = new Intent(MainWindowActivity.this, MyAttendanceActivity.class);

                                intent.putExtra("staffid", staffid);
                                intent.putExtra("returnto", 1);

                                startActivity(intent);
                                finish();

                                break;
                            }
                        }

                        return true;

                    }
                });

            }
            if (!userRights.getSecuritycameras()) {
                imgMainSecurityCameras.setVisibility(View.GONE);
                txtMainSecurityCameras.setVisibility(View.GONE);
            }
            if (!userRights.getSchoolattendance()) {
                imgMainSchoolAttendance.setVisibility(View.GONE);
                txtMainSchoolAttendance.setVisibility(View.GONE);
            }
            else {
                imgMainSchoolAttendance.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {

                        switch (arg1.getAction()) {
                            case MotionEvent.ACTION_DOWN: {

                                Intent intent = new Intent(MainWindowActivity.this, AllStaffAttendanceActivity.class);

                                startActivity(intent);
                                finish();

                                break;
                            }
                        }

                        return true;

                    }
                });


            }
            if (!userRights.getServersettings()) {
                imgMainServerSettings.setVisibility(View.GONE);
                txtMainServerSettings.setVisibility(View.GONE);
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }

    private UserRights getUserRights(Connect connect) {

        UserRights rightsToReturn = new UserRights();

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.REQUEST_USER_RIGHTS);

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

            if (recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_USER_RIGHTS){

                rightsToReturn.setInSchoolReport(recievedJSON.getBoolean("inschoolreport"));
                rightsToReturn.setOpendoors(recievedJSON.getBoolean("opendoors"));
                rightsToReturn.setMyattendamce(recievedJSON.getBoolean("myattendance"));
                rightsToReturn.setSecuritycameras(recievedJSON.getBoolean("securitycameras"));
                rightsToReturn.setSchoolattendance(recievedJSON.getBoolean("schoolattendance"));
                rightsToReturn.setServersettings(recievedJSON.getBoolean("serversettings"));
                cardnumber = recievedJSON.getString("cardnumber");
                staffid = recievedJSON.getInt("id");
            }

            return rightsToReturn;


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

        return rightsToReturn;

    }

    private void showErrorMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainWindowActivity.this).create();
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
    public void onBackPressed() {

        Intent intent = new Intent(MainWindowActivity.this, LoginActivity.class);

        try {
            Connect.getInstance().getClientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(intent);
        finish();

    }

}
