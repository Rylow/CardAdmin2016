package com.rylow.cardadmin2016;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rylow.cardadmin2016.service.DoorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by s.bakhti on 25.4.2016.
 */
public class OpenDoorsActivity extends AppCompatActivity {

    public class Door {

        private String name;
        private Boolean isDoor;
        private int id;
        private Boolean selected = false;

        public Door(String name, Boolean isDoor, int id) {
            this.name = name;
            this.isDoor = isDoor;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getDoor() {
            return isDoor;
        }

        public void setDoor(Boolean door) {
            isDoor = door;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        @Override
        public String toString(){
            return name;
        }
    }

    ArrayList<Door> doorList = new ArrayList<>();
    ArrayList<Door> readerList = new ArrayList<>();
    ListView listDoors, listReaders;
    String cardnumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doors);

        Intent intent = getIntent();

        cardnumber = intent.getStringExtra("cardnumber");

        listDoors = (ListView) findViewById(R.id.listDoors);
        listReaders = (ListView) findViewById(R.id.listReaders);

        fillDoorList();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(OpenDoorsActivity.this, MainWindowActivity.class);

        startActivity(intent);
        finish();

    }

    private void fillDoorList() {

        AsyncTask query = new AsyncTask<Integer, Void, ArrayList<Door>>(){

            @Override
            protected ArrayList<Door> doInBackground(Integer... params) {

                Connect connect = Connect.getInstance();


                if (connect.getClientSocket().isClosed()){

                    if (connect.connect()){

                        return getDoorList(connect);

                    }
                    else {

                        showErrorMessage();

                        return new ArrayList<>();


                    }
                }
                else{

                    return getDoorList(connect);

                }
            }
        }.execute();

        try {
            doorList = (ArrayList<Door>) query.get();

            final DoorAdapter adapter = new DoorAdapter(OpenDoorsActivity.this, doorList);
            final DoorAdapter readerAdapter = new DoorAdapter(OpenDoorsActivity.this, readerList);

            listDoors.setAdapter(adapter);
            listReaders.setAdapter(readerAdapter);

            adapter.notifyDataSetChanged();
            readerAdapter.notifyDataSetChanged();

            listDoors.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {

                    final Door value = (Door) parent.getItemAtPosition(position);

                    AsyncTask querySave = new AsyncTask<Integer, Void, Boolean>(){

                        @Override
                        protected Boolean doInBackground(Integer... params) {

                            Connect connect = Connect.getInstance();

                            JSONObject json = new JSONObject();

                            try {
                                json.put("code", TransmissionCodes.REQUEST_DOOR_OPEN);
                                json.put("cardnumber", cardnumber);
                                json.put("actas", value.getId());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (connect.getClientSocket().isClosed()){

                                if (connect.connect()){


                                    try {
                                        BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));

                                        outToServer.write(json.toString());
                                        outToServer.newLine();
                                        outToServer.flush();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }



                                    return true;

                                }

                                return false;
                            }
                            else{

                                try {
                                    BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));

                                    outToServer.write(json.toString());
                                    outToServer.newLine();
                                    outToServer.flush();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }



                                return true;

                            }

                        }
                    }.execute();



                    value.setSelected(true);
                    adapter.notifyDataSetChanged();

                    Timer revert = new Timer();

                    revert.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    value.setSelected(false);
                                    adapter.notifyDataSetChanged();

                                }
                            });

                        }
                    }, 3000);


                    return true;
                }
            });

            listReaders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int position, long id) {

                    final Door value = (Door) parent.getItemAtPosition(position);

                    AsyncTask querySave = new AsyncTask<Integer, Void, Boolean>(){

                        @Override
                        protected Boolean doInBackground(Integer... params) {

                            Connect connect = Connect.getInstance();

                            JSONObject json = new JSONObject();

                            try {
                                json.put("code", TransmissionCodes.REQUEST_DOOR_OPEN);
                                json.put("cardnumber", cardnumber);
                                json.put("actas", value.getId());


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (connect.getClientSocket().isClosed()){

                                if (connect.connect()){


                                    try {
                                        BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));

                                        outToServer.write(json.toString());
                                        outToServer.newLine();
                                        outToServer.flush();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }



                                    return true;

                                }

                                return false;
                            }
                            else{

                                try {
                                    BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));

                                    outToServer.write(json.toString());
                                    outToServer.newLine();
                                    outToServer.flush();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }



                                return true;

                            }

                        }
                    }.execute();



                    value.setSelected(true);
                    readerAdapter.notifyDataSetChanged();

                    Timer revert = new Timer();

                    revert.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    value.setSelected(false);
                                    readerAdapter.notifyDataSetChanged();

                                }
                            });

                        }
                    }, 3000);


                    return true;
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



    }


    private ArrayList<Door> getDoorList(Connect connect) {

        ArrayList<Door> listToReturn = new ArrayList<>();

        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(connect.getClientSocket().getOutputStream()));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connect.getClientSocket().getInputStream()));

            JSONObject json = new JSONObject();

            json.put("code", TransmissionCodes.REQUEST_DOOR_LIST);

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

            readerList = new ArrayList<>();

            JSONObject recievedJSON = new JSONObject(incString);

            if (recievedJSON.getInt("code") == TransmissionCodes.RESPONSE_DOOR_LIST){

                JSONArray array = recievedJSON.getJSONArray("array");

                for (int i = 0; i < array.length(); i++){

                    JSONObject door = array.getJSONObject(i);

                    if (door.getBoolean("isdoor"))
                        listToReturn.add(new Door(door.getString("name"), door.getBoolean("isdoor"), door.getInt("id")));
                    else
                        readerList.add(new Door(door.getString("name"), door.getBoolean("isdoor"), door.getInt("id")));


                }


            }

            return listToReturn;


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

        return listToReturn;


    }

    private void showErrorMessage(){

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(OpenDoorsActivity.this).create();
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
