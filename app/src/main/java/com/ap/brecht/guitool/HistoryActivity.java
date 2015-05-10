package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
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
    JSONArray a = null;
    JSONObject o = null;
    Object test = null;

    static ArrayList<Session> sessions;
    SessionAdapter adapter;
    String date;
    String place;
    String day;

    public static final String TAG = HistoryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        list = (ListView) findViewById(R.id.list);

        sessions=new ArrayList<Session>();
        
        actionbar = getSupportActionBar();
        actionbar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
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
            Logout.LogOut(this);
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

    @Override
    protected void onStart(){
        super.onStart();
        /*// Add item to adapter
        sessions.add("Nathan", "San Diego");
        adapter = new SessionAdapter(this, sessions);
        list.setAdapter(adapter);*/


        try {
            a = DatabaseData.userData.getJSONArray("session");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int arrSize = a.length();
        for (int i = 0; i < arrSize; ++i) {

            try {
                o = a.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                date =o.getString("datum").toString();
                String split[]= date.split(" ");
                day = split[0];

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                place=o.getString("place").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            sessions.add(new Session(date,place,day));
        }

        adapter = new SessionAdapter(this, sessions);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HistoryActivity.this, HistoryDataActivity.class);
                try {
                    a = DatabaseData.userData.getJSONArray("session");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    for (int j = 0; j < arrSize; ++j) {
                        if (sessions.get(position).Date.equals(a.getJSONObject(j).getString("datum"))) {
                            DatabaseData.Sid = a.getJSONObject(j).getString("sid").toString();
                        }
                    }
                    i.putExtra("sid", DatabaseData.Sid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HistoryActivity.this.startActivity(i);
            }
        });

    }

}
