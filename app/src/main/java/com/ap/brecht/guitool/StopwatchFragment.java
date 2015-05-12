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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Random;

/**
 * Created by hannelore on 22/04/2015.
 */
public class StopwatchFragment extends Fragment implements View.OnClickListener, SensorEventListener {

    //Sensor variables
    private Sensor mSensor;
    private SensorManager mSensorManager;

    private AcceleroFilter xFilter = new AcceleroFilter();
    private AcceleroFilter yFilter = new AcceleroFilter();
    private AcceleroFilter zFilter = new AcceleroFilter();

    private double xFiltered;
    private double yFiltered;
    private double zFiltered;

    private float[] orientationValues = new float[3];
    private float rotation[] = new float[16];

    private double startTime;
    private double elapsedTime;
    private double oldElapsedTime;

    private double velocity = 0;
    private double oldVelocity = 0;
    private double noVelocityCounter = 0;

    private double correctedVelocity;
    private double oldCorrectedVelocity = Double.NaN;
    private double oldOldCorrectedVelocity = Double.NaN;

    private double height;
    private double oldHeight;

    //Chrono variables
    private Chronometer chrono;
    long timeWhenStopped = 0;
    long elapsedMillis;
    int secs, currentmin, lastmin;

    //GUI
    private View view;
    private ImageButton startButton;
    private TextView txtStart;
    private ImageButton pauzeButton;
    private TextView txtPauze;
    private ImageButton stopButton;
    private TextView txtStop;
    private ImageButton resetButton;
    private TextView txtReset;
    private TextView txtHeight;
    private TextView txtSnelheid;

    private String locatie;
    private String descriptie;
    private String Uid;

    JSONObject jsonResponse;

    private TextToSpeech SayTime;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        chrono = (Chronometer) view.findViewById(R.id.timer);
        startButton = (ImageButton) view.findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);
        stopButton = (ImageButton) view.findViewById(R.id.btnStop);
        stopButton.setOnClickListener(this);
        pauzeButton = (ImageButton) view.findViewById(R.id.btnPauze);
        pauzeButton.setOnClickListener(this);
        resetButton = (ImageButton) view.findViewById(R.id.btnReset);
        resetButton.setOnClickListener(this);

        txtStart = (TextView) view.findViewById(R.id.txtStart);
        txtReset = (TextView) view.findViewById(R.id.txtReset);
        txtPauze = (TextView) view.findViewById(R.id.txtPauze);
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

        //Set start text to agreed notation
        chrono.setText("00:00:00");
        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer c) {
                elapsedMillis = SystemClock.elapsedRealtime() - c.getBase();
                if (elapsedMillis > 3600000L) {
                    c.setFormat("0%s");
                } else {
                    c.setFormat("00:%s");
                }

                secs = ((int) (elapsedMillis)) / 1000;
                currentmin = secs / 60;

                //Check if we need to speak the minutes
                //@ the moment it's every minute
                String alertTime = ((SessionActivity) getActivity()).AlertTime;
                if (alertTime.equals("30s")) {
                    if (secs % 30 == 0) {
                        speakText();
                    }
                } else if (alertTime.equals("1m")) {
                    if (lastmin != currentmin || (currentmin==0 && secs==0)) {
                        speakText();
                    }
                    lastmin = currentmin;
                } else if (alertTime.equals("5m")) {
                    if ((lastmin != currentmin && (currentmin % 5) == 0) || (currentmin==0 && secs==0)) {
                        speakText();
                    }
                    lastmin = currentmin;

                } else if (alertTime.equals("10m")) {
                    if ((lastmin != currentmin && (currentmin % 10) == 0) || (currentmin==0 && secs==0)) {
                        speakText();
                    }
                    lastmin = currentmin;
                }
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chrono.start();
                showStopButton();
                break;
            case R.id.btnPauze:
                timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                chrono.stop();
                hideStopButton();
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
                        chrono.stop();
                        if (mSensorManager != null) {
                            mSensorManager.unregisterListener(StopwatchFragment.this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
                            mSensorManager.unregisterListener(StopwatchFragment.this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
                        }
                        savePicture();
                        new MyAsyncTask().execute();
                    }
                });
                stopAlert.create().show();
                break;
            case R.id.btnReset:
                chrono.setBase(SystemClock.elapsedRealtime());
                timeWhenStopped = 0;
                chrono.stop();
                break;
        }
    }

    private void showStopButton() {
        StartSensor();
        startButton.setVisibility(View.GONE);
        txtStart.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        txtReset.setVisibility(View.GONE);
        pauzeButton.setVisibility(View.VISIBLE);
        txtPauze.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        txtStop.setVisibility(View.VISIBLE);
    }

    private void hideStopButton() {
        startButton.setVisibility(View.VISIBLE);
        txtStart.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        txtReset.setVisibility(View.VISIBLE);
        pauzeButton.setVisibility(View.GONE);
        txtPauze.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        txtStop.setVisibility(View.GONE);
    }

    private void StartSensor()
    {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null){
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),  SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),  SensorManager.SENSOR_DELAY_GAME);
        }
        else {
            //DO SHIT
        }

        startTime = System.currentTimeMillis() / 1000;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    public void onSensorChanged(SensorEvent event)
    {
        mSensor = event.sensor;

        if (mSensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            xFiltered = xFilter.Filter(event.values[0]);
            yFiltered = yFilter.Filter(event.values[1]);
            zFiltered = zFilter.Filter(event.values[2]);
        }

        else if(mSensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            SensorManager.getRotationMatrixFromVector(rotation, event.values);
            SensorManager.getOrientation(rotation, orientationValues);
            double azimuth = Math.toDegrees(orientationValues[0]);
            double pitch = Math.toDegrees(orientationValues[1]);
            double roll = Math.toDegrees(orientationValues[2]);

            double ax = xFiltered * Math.cos(Math.toRadians(roll)) + yFiltered * Math.cos(Math.toRadians(90) - Math.toRadians(roll));
            double ay = yFiltered * Math.cos(Math.toRadians(90) + Math.toRadians(pitch)) + xFiltered * Math.cos(Math.toRadians(90) + Math.toRadians(roll)) + zFiltered * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(roll));

            elapsedTime = (System.currentTimeMillis() / 1000.0) - startTime;

            velocity = oldVelocity + (ay * (elapsedTime - oldElapsedTime));

            if(ay < 0.6 && ay > -0.6 && velocity != 0)
                noVelocityCounter++;
            else
                noVelocityCounter = 0;

            if((noVelocityCounter > 2 && oldOldCorrectedVelocity < 0.5 && oldOldCorrectedVelocity > -0.5) ||  Math.abs(oldOldCorrectedVelocity) > 2 || Double.isNaN(oldOldCorrectedVelocity))
                correctedVelocity = 0;
            else
                correctedVelocity = oldCorrectedVelocity + (ay * (elapsedTime - oldElapsedTime)) * 1.2;

            if (correctedVelocity > 2 || correctedVelocity < - 2)
                correctedVelocity = 0;


            height = oldHeight + (correctedVelocity * (elapsedTime - oldElapsedTime));

            oldElapsedTime = elapsedTime;
            oldVelocity = velocity;
            oldOldCorrectedVelocity = oldCorrectedVelocity;
            oldCorrectedVelocity = correctedVelocity;
            oldHeight= height;

            //tvVelocity.setText(String.valueOf(velocity));
            txtHeight.setText(String.format("%.2f", height) + "m");

        }
    }

    private void speakText() {

        String toSpeak = "";

        if (secs == 0 && currentmin == 0) {
            Random r = new Random();
            int fun = r.nextInt(7);//random number from 0-6
            switch (fun) {
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
                    toSpeak = "You will climb and I will follow";
                    break;
                case 5:
                    toSpeak = "Let's go";
                    break;
                case 6:
                    toSpeak = "Go hard, or go home";
                default:
                    break;
            }
        } else if (secs % 30 == 0 && secs % 60 != 0) {
            if (currentmin == 0) {
                toSpeak = "You have been climbing for " + "30" + " seconds";
            } else if (currentmin == 1) {
                toSpeak = "You have been climbing for " + String.valueOf(currentmin) + " minute and " + "30" + " seconds";
            } else
                toSpeak = "You have been climbing for " + String.valueOf(currentmin) + " minutes and " + "30" + " seconds";
        } else if (currentmin == 1) {
            toSpeak = "You have been climbing for " + String.valueOf(currentmin) + " minute";
        } else {
            toSpeak = "You have been climbing for " + String.valueOf(currentmin) + " minutes";
        }

        SayTime.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void savePicture() {

        locatie = DescriptionFragmentSession.getLocation();
        descriptie = DescriptionFragmentSession.getDescription();

        try {
            if (DatabaseData.PhotoString == null)
                return;

            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
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

            String text = locatie;
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);

            String text2 = descriptie;
            Rect textRect2 = new Rect();
            paint.getTextBounds(text2, 0, text2.length(), textRect2);

            String text3 = String.format("%.2f", height) + "m";

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

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream);
            byte[] imageArray = arrayOutputStream.toByteArray();

            DatabaseData.PhotoString = Base64.encodeToString(imageArray, Base64.DEFAULT);

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to edit picture", Toast.LENGTH_SHORT).show();
        }
    }

    //Method used from someone else!
    private int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());

        protected void onPreExecute() {
            progressDialog.setMessage("Adding to database");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });

            //locatie = DescriptionFragmentSession.getLocation();
            //descriptie = DescriptionFragmentSession.getDescription();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Uid = DatabaseData.userData.getString("uid");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (WelcomeActivity.Username == null) {
                try {
                    WelcomeActivity.Username = String.valueOf(DatabaseData.userData.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {//gewoon zo laten :)
            }

            DatabaseComClass.Session(Uid, locatie, descriptie, Math.round(height * 100.0) / 100.0  ,String.valueOf(elapsedMillis), DatabaseData.PhotoString, progressDialog);
            return null;
        }

        protected void onPostExecute(Void v) {
            try {

                //Close the progressDialog!
                this.progressDialog.dismiss();
                if (DatabaseData.userData.optString("success").toString().equals("1")) {
                    super.onPostExecute(v);
                    Toast.makeText(getActivity(), "Saved data to database", Toast.LENGTH_SHORT).show();
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


