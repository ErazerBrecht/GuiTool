package com.ap.brecht.guitool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Airien on 29/04/2015.
 */
public class GraphFragmentData extends Fragment implements View.OnClickListener {

    private View view;

    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    DataPoint newDataPoint;
    DataPoint oldDataPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph_data, container, false);
        graphView = (GraphView) view.findViewById(R.id.graph);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

}


