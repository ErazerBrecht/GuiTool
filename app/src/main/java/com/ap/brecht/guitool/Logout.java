package com.ap.brecht.guitool;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Brecht on 9/05/2015.
 */
public class Logout {

    public static void LogOut(Activity activity)
    {
        DatabaseData.userData = null;
        DatabaseData.PhotoString = null;
        Intent i = new Intent(activity, Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
    }
}
