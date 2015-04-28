package com.ap.brecht.guitool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Airien on 22/04/2015.
 */
public class WelcomeActivity extends ActionBarActivity  {

    Button btnNewSession;
    Button btnHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        ActionBar actionBar = getSupportActionBar();

        btnNewSession = (Button) findViewById(R.id.NewSession);
        btnHistory = (Button) findViewById(R.id.History);

        btnNewSession.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(WelcomeActivity.this, SessionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WelcomeActivity.this.startActivity(i);
            }
        });

    }
}
