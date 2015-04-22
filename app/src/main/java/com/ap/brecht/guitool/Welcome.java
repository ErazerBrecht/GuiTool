package com.ap.brecht.guitool;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;

/**
 * Created by Airien on 22/04/2015.
 */
public class Welcome extends ActionBarActivity  {

    Button btnNewSession;
    Button btnHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

    }
}
