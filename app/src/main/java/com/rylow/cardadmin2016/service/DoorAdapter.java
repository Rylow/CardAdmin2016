package com.rylow.cardadmin2016.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rylow.cardadmin2016.OpenDoorsActivity;
import com.rylow.cardadmin2016.R;

import java.util.ArrayList;

/**
 * Created by s.bakhti on 25.4.2016.
 */
public class DoorAdapter extends ArrayAdapter<OpenDoorsActivity.Door> {

    public DoorAdapter(Context context, ArrayList<OpenDoorsActivity.Door> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OpenDoorsActivity.Door door = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_door, parent, false);
        }
        // Lookup view for data population

        TextView txtDoorName = (TextView) convertView.findViewById(R.id.txtDoorListName);
        TextView txtDoorListOPened = (TextView) convertView.findViewById(R.id.txtDoorListOPened);

        if (door.getSelected())
            txtDoorListOPened.setVisibility(View.VISIBLE);
        else
            txtDoorListOPened.setVisibility(View.GONE);

        // Populate the data into the template view using the data object

        txtDoorName.setText(door.getName());

        // Return the completed view to render on screen
        return convertView;
    }


}
