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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Airien on 29/04/2015.
 */
public class HistoryActivity extends ActionBarActivity {
    ActionBar actionbar;
    String tussenResultaat;
    ListView list;
    ArrayList<String> list1;
    ArrayAdapter<String> adapter;
    JSONArray a = null;
    JSONObject o=null;

    public static final String TAG =HistoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        list= (ListView) findViewById(R.id.historyData);

        list1=new ArrayList<>();


        try {
            a = DatabaseData.userData.getJSONArray("session");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int arrSize = a.length();
        List<String> datum = new ArrayList<String>(arrSize);
        List<String> plaats = new ArrayList<String>(arrSize);
        for (int i = 0; i < arrSize; ++i) {
            try {
                o=a.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                datum.clear();
                datum.add(o.getString("datum").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                plaats.clear();
                plaats.add(o.getString("place").toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tussenResultaat=String.valueOf(datum).replace('[',' ').replace(']',' ').trim() + "\n" + String.valueOf(plaats).replace('[',' ').replace(']',' ').trim();
            list1.add(tussenResultaat);
        }
        adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item,list1);
        list.setAdapter(adapter);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HistoryActivity.this, HistoryDataActivity.class);
                try {
                    i.putExtra("sid",o.getString("sid").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HistoryActivity.this.startActivity(i);
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
