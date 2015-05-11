package com.ap.brecht.guitool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;


public class SplatchScreenActivity extends Activity {

    private Handler mHandler = new Handler();
    private RelativeLayout layout;
    int[] imgIds = {R.drawable.splashpagina0, R.drawable.splashpagina1, R.drawable.splashpagina2, R.drawable.splashpagina3};
    int pointer = 1;
    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splatchscreen);

        layout = (RelativeLayout) findViewById(R.id.background);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.background);
                layout.setBackground(getResources().getDrawable(imgIds[pointer]));
                pointer = pointer + (1 * i);

                if (pointer > 3)
                    pointer = 0;

                mHandler.postDelayed(this, 300);
            }
        }, 300);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_UP):
                Intent i = new Intent(SplatchScreenActivity.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SplatchScreenActivity.this.startActivity(i);
               break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        BackPressed.CloseApp(this);
    }
}
