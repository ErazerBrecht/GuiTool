package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Field;

/**
 * Created by Airien on 29/04/2015.
 */
public class HistoryActivity extends ActionBarActivity {
    ActionBar actionbar;
    ListView list;

    public static final String TAG =HistoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        list= (ListView) findViewById(R.id.historyData);

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HistoryActivity.this, SessionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HistoryActivity.this.startActivity(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actionbar = getSupportActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setIcon(R.drawable.icon);


        makeActionOverflowMenuShown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(HistoryActivity.this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HistoryActivity.this.startActivity(i);
        }
        if (id == R.id.action_logout) {
            DatabaseData.userData=null;
            Intent i = new Intent(HistoryActivity.this, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HistoryActivity.this.startActivity(i);
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


    @Override
    public void onBackPressed() {
        BackPressed.CloseApp(this);
    }

}
