package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

/**
 * Created by hannelore on 22/04/2015.
 */
public class StopwatchFragment extends Fragment implements View.OnClickListener {

    private View view;

    private ImageButton startButton;
    private TextView txtStart;
    private ImageButton stopButton;
    private TextView txtStop;
    private ImageButton resetButton;
    private TextView txtReset;
    private TextView txtHeight;
    private TextView txtSnelheid;
    private String locatie;
    private String descriptie;

    String Uid;

    JSONObject jsonResponse;

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours, minutes, seconds, currentmin, lastmin = "00";
    private long secs, mins, hrs;
    private boolean stopped = false, start, stop, reset;
    private TextToSpeech SayTime;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        startButton = (ImageButton) view.findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);
        stopButton = (ImageButton) view.findViewById(R.id.btnStop);
        stopButton.setOnClickListener(this);
        resetButton = (ImageButton) view.findViewById(R.id.btnReset);
        resetButton.setOnClickListener(this);

        txtStart = (TextView) view.findViewById(R.id.txtStart);
        txtReset = (TextView) view.findViewById(R.id.txtReset);
        txtStop = (TextView) view.findViewById(R.id.txtStop);
        txtHeight = (TextView) view.findViewById(R.id.txtHeight);
        txtHeight.setText("21m");
        //txtSnelheid= (TextView) view.findViewById(R.id.SpeedStop);

        SayTime = new TextToSpeech(getActivity().getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            SayTime.setLanguage(Locale.US);
                        }
                    }
                });
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                startClick();
                break;
            case R.id.btnStop:
                QustomDialogBuilder stopAlert = new QustomDialogBuilder(v.getContext(), AlertDialog.THEME_HOLO_DARK);
                stopAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(getActivity().getResources().getColor(R.color.white) & 0x00ffffff) + ">Do you want to quit your session?"));
                stopAlert.setTitle("ClimbUP");
                stopAlert.setTitleColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                stopAlert.setDividerColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                stopAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                stopAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopClick();
                    }
                });
                stopAlert.create().show();
                break;
            case R.id.btnReset:
                resetClick();
                break;
        }
    }

    public void startClick() {
        showStopButton();
        start = true;
        stop = false;
        if (stopped) {
            startTime = System.currentTimeMillis() - elapsedTime;
        } else {
            startTime = System.currentTimeMillis();
        }
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    public void stopClick() {
        start = false;
        stop = true;
        mHandler.removeCallbacks(startTimer);
        stopped = true;
        savePicture();
        new MyAsyncTask().execute();
    }

    public void resetClick() {
        stopped = false;
        start = false;
        stop = false;
        ((TextView) getView().findViewById(R.id.timer)).setText("00:00:00");
    }

    private void showStopButton() {
        startButton.setVisibility(View.GONE);
        txtStart.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        txtReset.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
        txtStop.setVisibility(View.VISIBLE);
    }


    private void updateTimer(float time) {
        secs = (long) (time / 1000);
        mins = (long) ((time / 1000) / 60);
        hrs = (long) (((time / 1000) / 60) / 60);

		/* Convert the seconds to String
         * and format to ensure it has
		 * a leading zero when required
		 */

        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }

    	/* Convert the hours to String and format the String */

        hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

		/* Set the timer text to the elapsed time */
        ((TextView) view.findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);


        //Check if we need to speak the minutes
        //@ the moment it's every minute
        String alertTime = ((SessionActivity) getActivity()).AlertTime;
        if (alertTime.equals("30s")) {
            if (secs == 0  || secs == 30) {
                speakText();
            }
        } else if (alertTime.equals("1m")) {
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin) {
                speakText();
            }
            lastmin = currentmin;
        } else if (alertTime.equals("5m")) {
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin && (mins % 5) == 0) {
                speakText();
            }
            lastmin = currentmin;

        } else if (alertTime.equals("10m")) {
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin && (mins % 10) == 0) {
                speakText();
            }
            lastmin = currentmin;
        }
    }


    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    public void speakText() {

        String toSpeak = "";

        if (secs == 30) {
            if (mins == 0) {
                toSpeak = "You have been climbing for " + String.valueOf(secs) + " seconds";
            } else if (mins == 1) {
                toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute and " + String.valueOf(secs) + " seconds";
            } else
                toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute and " + String.valueOf(secs) + " seconds";
        } else if (secs == 0) {
            if (mins == 0) {
                Random r = new Random();
                int fun = r.nextInt(6);//random number from 0-5
                switch (fun){
                    case 0:
                        toSpeak = "Happy Climbing";
                        break;
                    case 1:
                        toSpeak = "Have fun climbing";
                        break;
                    case 2:
                        toSpeak = "Let's climb";
                        break;
                    case 3:
                        toSpeak = "Enjoy your climb";
                        break;
                    case 4:
                        toSpeak = "You Will climb and I Will follow";
                        break;
                    case 5:
                        toSpeak = "Let's go";
                        break;
                    default:
                        break;

                }
            }
            else if (mins == 1) {
                toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute";
            } else
                toSpeak = "You have been climbing for " + String.valueOf(mins) + " minutes";
        } else
            toSpeak = "You have been climbing for " + String.valueOf(mins) + " minutes and " + String.valueOf(secs) + " seconds";

        SayTime.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void savePicture() {
        try {
            if (DatabaseData.PhotoString == null)
                return;

            String username =  DatabaseData.userData.getJSONObject("user").getString("name");
            String session = String.valueOf(Integer.valueOf(DatabaseData.userData.getJSONArray("session").getJSONObject(DatabaseData.userData.getJSONArray("session").length() - 1).getString("sid")) + 1);
            File Drawn = new File(Environment.getExternalStorageDirectory().toString() + "/ClimbUP/" + username);
            Drawn.mkdirs();
            File Drawing = new File(Drawn, session + ".jpg");
            FileOutputStream out = new FileOutputStream(Drawing);

            Bitmap bitmap = BitmapFactory.decodeFile(DatabaseData.PhotoString).copy(Bitmap.Config.RGB_565, true);
            Typeface tf = Typeface.create("sans-serif-condensed", Typeface.BOLD);
            int x = 50;
            int y = 75;
            int size = 32;
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE); // Text Color
            paint.setTypeface(tf);
            paint.setTextSize(convertToPixels(getActivity().getApplicationContext(), size));

            String text = "Testing";
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);

            String text2 = "Testing2";
            Rect textRect2 = new Rect();
            paint.getTextBounds(text2, 0, text2.length(), textRect2);

            String text3 = "Testing3";

            canvas.drawText(text, x, y, paint);
            canvas.drawText(text2, x, y + textRect.height(), paint);
            canvas.drawText(text3, x, y + textRect.height() + textRect2.height(), paint);

            //Add outline to text!
            Paint stkPaint = new Paint();
            stkPaint.setTypeface(tf);
            stkPaint.setStyle(Paint.Style.STROKE);
            stkPaint.setStrokeWidth(size / 10);
            stkPaint.setColor(Color.BLACK);
            stkPaint.setTextSize(convertToPixels(getActivity().getApplicationContext(), size));
            canvas.drawText(text, x, y, stkPaint);
            canvas.drawText(text2, x, y + textRect.height(), stkPaint);
            canvas.drawText(text3, x, y + textRect.height() + textRect2.height(), stkPaint);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            //This part is used to add Generated picture to Album (Gallery)!
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(Drawing));
            getActivity().sendBroadcast(mediaScanIntent);

            DatabaseData.PhotoString = Drawing.getPath();

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to edit picture", Toast.LENGTH_SHORT).show();
        }
    }

    //Method used from someone else!
    private int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());

        protected void onPreExecute() {
            progressDialog.setMessage("Writing");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
            if (String.valueOf(DescriptionFragmentSession.loc) != null) {
                locatie = String.valueOf(DescriptionFragmentSession.loc);
            } else {
                locatie = " ";
            }
            if (String.valueOf(DescriptionFragmentSession.des) != null) {
                descriptie = String.valueOf(DescriptionFragmentSession.des);
            } else {
                descriptie = " ";
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Uid = DatabaseData.userData.getString("uid");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (WelcomeActivity.Username==null)
            {
                try {
                    WelcomeActivity.Username=String.valueOf(DatabaseData.userData.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {//gewoon zo laten :)
            }
            DatabaseComClass.Session(Uid, locatie, descriptie, "0", String.valueOf(elapsedTime), DatabaseData.PhotoString, progressDialog);
            return null;
        }

        protected void onPostExecute(Void v) {
            try {
                //Close the progressDialog!
                this.progressDialog.dismiss();
                if (DatabaseData.userData.optString("success").toString().equals("1")) {
                    super.onPostExecute(v);
                    Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);

                } else if (DatabaseData.userData.optString("error").toString().equals("1")) {
                    Toast.makeText(getActivity(), jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getView().getContext(), "Can't login", Toast.LENGTH_SHORT).show();
        }
    }

}


