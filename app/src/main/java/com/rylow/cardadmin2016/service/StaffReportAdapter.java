package com.rylow.cardadmin2016.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rylow.cardadmin2016.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by s.bakhti on 25.4.2016.
 */
public class StaffReportAdapter extends ArrayAdapter<Staff> {
    public StaffReportAdapter(Context context, ArrayList<Staff> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Staff staff = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inschool_report_list_item, parent, false);
        }
        // Lookup view for data population

        ImageView imgAttendancePhoto = (ImageView) convertView.findViewById(R.id.imgAttendancePhoto);
        TextView txtAttendanceItemName = (TextView) convertView.findViewById(R.id.txtAttendanceItemName);
        TextView txtAttendanceItemTemporary = (TextView) convertView.findViewById(R.id.txtAttendanceItemTemporary);
        TextView txtAttendanceItemJobTitle = (TextView) convertView.findViewById(R.id.txtAttendanceItemJobTitle);
        TextView txtAttendanceItemArrivalTime = (TextView) convertView.findViewById(R.id.txtAttendanceItemArrivalTime);

        try {
            File mydir = getContext().getDir("staffphotos", Context.MODE_PRIVATE);
            File f = new File(mydir, staff.getPhotoFileName());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgAttendancePhoto.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            imgAttendancePhoto.setImageResource(R.drawable.no_photo);
            e.printStackTrace();
        }


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(staff.getTimein());

        txtAttendanceItemArrivalTime.setText(sdf.format(curDate));

        txtAttendanceItemJobTitle.setText(staff.getRole());
        txtAttendanceItemName.setText(staff.getName());

        if (staff.getTimein() != 0) { //In School Report

            txtAttendanceItemTemporary.setText("On Temporary Card");
            txtAttendanceItemArrivalTime.setVisibility(View.VISIBLE);

            if (staff.getOntemporarycard())
                txtAttendanceItemTemporary.setVisibility(View.VISIBLE);
            else
                txtAttendanceItemTemporary.setVisibility(View.GONE);
        }
        else{ //ALL STAFF LIST
            txtAttendanceItemTemporary.setText("External Staff");
            txtAttendanceItemArrivalTime.setVisibility(View.INVISIBLE);

            if (staff.getOntemporarycard())
                txtAttendanceItemTemporary.setVisibility(View.VISIBLE);
            else
                txtAttendanceItemTemporary.setVisibility(View.GONE);

        }


        return convertView;
    }
}
