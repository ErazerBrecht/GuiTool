package com.ap.brecht.guitool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hannelore on 10/05/2015.
 */
public class SessionAdapter extends ArrayAdapter<Session> {
    public  SessionAdapter(Context context, ArrayList<Session> sessions){
        super(context, 0, sessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Session session = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_history_listview_layout, parent, false);
        }
        // Lookup view for data population
        TextView Date = (TextView) convertView.findViewById(R.id.Date);
        TextView Place = (TextView) convertView.findViewById(R.id.Place);
        // Populate the data into the template view using the data object
        Date.setText(session.Day);
        Place.setText(session.Place);
        // Return the completed view to render on screen
        return convertView;
    }

}
