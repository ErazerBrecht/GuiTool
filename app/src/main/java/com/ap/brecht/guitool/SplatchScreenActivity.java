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

import java.util.List;


public class SplatchScreenActivity extends Activity {

    private Handler mHandler = new Handler();
    int[] imgIds = {R.drawable.splashpagina_one, R.drawable.splashpagina_two, R.drawable.splashpagina_three};
    int pointer = 1;
    int i= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splatchscreen);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.background);
                layout.setBackground(getResources().getDrawable(imgIds[pointer]));
                pointer = pointer + (1 * i);

                if (pointer > 1)
                    i = -1;
                else if (pointer < 1)
                    i = 1;

                mHandler.postDelayed(this, 750);
            }
        }, 1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = MotionEventCompat.getActionMasked(event);

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                return true;
            case (MotionEvent.ACTION_MOVE) :
                return true;
            case (MotionEvent.ACTION_UP) :
                Intent i = new Intent(SplatchScreenActivity.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SplatchScreenActivity.this.startActivity(i);
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        BackPressed.CloseApp(this);
    }

}
