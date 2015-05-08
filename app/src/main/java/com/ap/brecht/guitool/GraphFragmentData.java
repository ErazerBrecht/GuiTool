package com.ap.brecht.guitool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * Created by Airien on 29/04/2015.
 */
public class GraphFragmentData extends Fragment {

    private View view;

    Button btnRandom;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    DataPoint newDataPoint;
    DataPoint oldDataPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_data, container, false);
        btnRandom = (Button) view.findViewById(R.id.btnRandom1);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnRandom1:
                        AddRandomNumber();
                        break;
                    default:
                        break;
            }}
        });
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

        return view;
    }


    private void AddRandomNumber() {
        DataPoint newDataPoint = new DataPoint(randInt(-20, 20), randInt(0, 40));
        series = new LineGraphSeries<DataPoint>(new DataPoint[]
                {
                        oldDataPoint,
                        newDataPoint
                });

        int color = Color.argb(255, randInt(100, 255), randInt(100, 255), randInt(100, 255));
        series.setColor(color);
        graphView.addSeries(series);
        oldDataPoint = newDataPoint;
    }

    private static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}

