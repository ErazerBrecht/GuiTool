package com.ap.brecht.guitool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;

/**
 * Created by Brecht on 8/05/2015.
 */
public class BackPressed {

    static void CloseApp(final Activity activity) {

        QustomDialogBuilder exitAlert = new QustomDialogBuilder(activity, AlertDialog.THEME_HOLO_DARK);
        exitAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(activity.getResources().getColor(R.color.white) & 0x00ffffff) + ">Do you want to exit the app?"));
        exitAlert.setTitle("ClimbUP");
        exitAlert.setTitleColor("#" + Integer.toHexString(activity.getResources().getColor(R.color.Orange) & 0x00ffffff));
        exitAlert.setDividerColor("#" + Integer.toHexString(activity.getResources().getColor(R.color.Orange) & 0x00ffffff));
        exitAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        exitAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseData.userData = null;
                DatabaseData.PhotoString = null;
                Intent i = new Intent(activity, SplatchScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        exitAlert.setCancelable(true);
        exitAlert.create().show();
    }
}
