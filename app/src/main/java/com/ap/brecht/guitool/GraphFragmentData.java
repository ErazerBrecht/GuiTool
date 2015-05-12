package com.ap.brecht.guitool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Airien on 29/04/2015.
 */
public class GraphFragmentData extends Fragment {

    private View view;

    JSONArray a = null;
    JSONObject o = null;

    GraphView graphView;

    LineGraphSeries<DataPoint> series;

    DataPoint newDataPoint;
    DataPoint oldDataPoint;

    TextView height;
    TextView speed;

    String hoogte;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_data, container, false);
        height = (TextView) view.findViewById(R.id.HoogteData);
        speed = (TextView) view.findViewById(R.id.Speed);

        graphView = (GraphView) view.findViewById(R.id.graph1);
        oldDataPoint = new DataPoint(0, 0);
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                oldDataPoint
        });
        graphView.addSeries(series);

        // set manual X bounds
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(-20);
        graphView.getViewport().setMaxX(20);

        // set manual Y bounds
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(0);
        graphView.getViewport().setMaxY(40);
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
                    hoogte = o.getString("altitude");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        height.setText(hoogte +"m");
        return view;
    }
}

