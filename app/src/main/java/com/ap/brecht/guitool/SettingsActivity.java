package com.ap.brecht.guitool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;

/**
 * Created by hannelore on 29/04/2015.
 */
public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        QustomDialogBuilder exitAlert = new QustomDialogBuilder(this, AlertDialog.THEME_HOLO_DARK);
        //pictureAlert.setMessage("Do you want to make a picture?");
        exitAlert.setMessage(Html.fromHtml("<font color='#FFFFFF'>Do you want to exit the app?"));
        exitAlert.setTitle("ClimbUP");
        exitAlert.setTitleColor("#E98237");
        exitAlert.setDividerColor("#E98237");
        exitAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        exitAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseData.userData=null;
                Intent i = new Intent(SettingsActivity.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SettingsActivity.this.startActivity(i);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        exitAlert.setCancelable(true);
        exitAlert.create().show();
    }
}
