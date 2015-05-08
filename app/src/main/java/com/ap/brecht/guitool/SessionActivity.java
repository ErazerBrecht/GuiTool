package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hannelore on 22/04/2015.
 */

public class SessionActivity extends ActionBarActivity implements ActionBar.TabListener{
    ActionBar actionbar;
    ViewPager viewPager;
    SwipePageAdapterSession swipe;
    ActionBar.Tab StopwatchTab;
    ActionBar.Tab GraphTab;
    ActionBar.Tab DescriptionTab;

    String AlertTime;

    public static final String TAG =SessionActivity.class.getSimpleName();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath2;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        viewPager = (ViewPager) findViewById(R.id.pager);
        swipe = new SwipePageAdapterSession(getSupportFragmentManager());

        DatabaseData.PhotoString=null;


        if(DatabaseData.PhotoString==null) {
            QustomDialogBuilder pictureAlert = new QustomDialogBuilder(this, AlertDialog.THEME_HOLO_DARK);
            pictureAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(getResources().getColor(R.color.white) & 0x00ffffff) +">Do you want to make a picture?"));
            pictureAlert.setTitle("ClimbUP");
            pictureAlert.setTitleColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
            pictureAlert.setDividerColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
            pictureAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            pictureAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dispatchTakePictureIntent();
                    galleryAddPic();
                }
            });
            pictureAlert.setCancelable(true);
            pictureAlert.create().show();

        }


        actionbar = getSupportActionBar();
        actionbar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));

        viewPager.setAdapter(swipe);

        StopwatchTab = actionbar.newTab();
        StopwatchTab.setIcon(R.drawable.stopwatch);
        StopwatchTab.setTabListener(this);


        GraphTab = actionbar.newTab();
        GraphTab.setIcon(R.drawable.graph);
        GraphTab.setTabListener(this);

        DescriptionTab = actionbar.newTab();
        DescriptionTab.setIcon(R.drawable.description);
        DescriptionTab.setTabListener(this);

        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionbar.addTab(DescriptionTab);
        actionbar.addTab(StopwatchTab);
        actionbar.addTab(GraphTab);


        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionbar.setSelectedNavigationItem(position);
            }
        });

        makeActionOverflowMenuShown();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        AlertTime = sharedPref.getString("AlertTime","30s");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
           DatabaseData.userData=null;
            Intent i = new Intent(SessionActivity.this, Login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SessionActivity.this.startActivity(i);

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
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onBackPressed() {
       BackPressed.CloseApp(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                DatabaseData.PhotoString="iets";
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);


    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory().toString()+"/ClimbUP/");
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath2=image.getAbsolutePath();
        return image;
    }


}
