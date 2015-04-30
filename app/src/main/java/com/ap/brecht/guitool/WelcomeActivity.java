package com.ap.brecht.guitool;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by Airien on 22/04/2015.
 */
public class WelcomeActivity extends ActionBarActivity  {

    Button btnNewSession;
    Button btnHistory;
    public static final String TAG =WelcomeActivity.class.getSimpleName();

    String Username;
    TextView Welkom;
    TextView UsernameTextView;
    JSONObject jsonObject;

    private Handler mHandler = new Handler();
    int i = 1;
    int j = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        btnNewSession = (Button) findViewById(R.id.NewSession);
        btnHistory = (Button) findViewById(R.id.History);

        btnNewSession.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(WelcomeActivity.this, SessionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WelcomeActivity.this.startActivity(i);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(WelcomeActivity.this,HistoryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                WelcomeActivity.this.startActivity(i);
            }
        });
        makeActionOverflowMenuShown();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        try {
            jsonObject = SaveLoginClass.userData;
            Username = jsonObject.getJSONObject("user").getString("name");
        } catch (JSONException e) {
            //some exception handler code.
        }

        Welkom = (TextView) findViewById(R.id.tvWelcome);
        UsernameTextView = (TextView) findViewById(R.id.tvUsername);

        UsernameTextView.setText(String.valueOf(Username));

       mHandler.postDelayed(new Runnable() {
            public void run() {
                i = i + j;
                Welkom.setShadowLayer(i, 0,0, Color.BLACK);

                if(i > 8)
                    j = -1;
                else if (i < 2)
                    j = 1;

                mHandler.postDelayed(this, 200);
            }
        }, 100);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //return true;
            Intent i = new Intent(WelcomeActivity.this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            WelcomeActivity.this.startActivity(i);
        }
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeActionOverflowMenuShown() {
        //Devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        //This code adds the overflow menu manually
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }


}
