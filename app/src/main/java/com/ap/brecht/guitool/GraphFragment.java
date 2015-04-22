package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * Created by hannelore on 22/04/2015.
 */
public class GraphFragment extends Fragment implements View.OnClickListener{

    private View view;

    Button btnRandom;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    DataPoint newDataPoint;
    DataPoint oldDataPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        btnRandom = (Button) view.findViewById(R.id.btnRandom);
        btnRandom.setOnClickListener(this);
        graphView = (GraphView) view.findViewById(R.id.graph);
        oldDataPoint = new DataPoint(0,0);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnRandom:
                AddRandomNumber();
                break;
            default:
                break;
        }
    }

    private void AddRandomNumber()
    {
        DataPoint newDataPoint = new DataPoint(randInt(-20,20), randInt(0,40));
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


