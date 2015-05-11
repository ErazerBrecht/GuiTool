package com.ap.brecht.guitool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Airien on 29/04/2015.
 */
public class DescriptionFragmentData extends Fragment {
    private View view;

    TextView height;
    TextView duration;
    TextView place;
    TextView description;
    TextView date;

    JSONArray a = null;
    JSONObject o = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_data, container, false);

        height = (TextView) view.findViewById(R.id.HoogteData);
        duration = (TextView) view.findViewById(R.id.DurationData);
        place = (TextView) view.findViewById(R.id.PlaceData);
        description = (TextView) view.findViewById(R.id.DescriptionData);
        date = (TextView) view.findViewById(R.id.DateData);

        try {
            a = DatabaseData.userData.getJSONArray("session");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int arrSize = a.length();
        for (int i = 0; i < arrSize; ++i) {
            try {
                o = a.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (o.getString("sid").equals(DatabaseData.Sid)) {
                    height.setText(o.getString("altitude"));

                    int millis = Integer.valueOf(o.getString("duration"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(millis);

                    int sec = calendar.get(Calendar.SECOND);
                    int minutes = calendar.get(Calendar.MINUTE);
                    int hours = calendar.get(Calendar.HOUR);

                    String text = String.format("%02d", hours - 1) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", sec);

                    duration.setText(text);
                    description.setText(o.getString("description"));
                    place.setText(o.getString("place"));
                    date.setText(o.getString("datum"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
