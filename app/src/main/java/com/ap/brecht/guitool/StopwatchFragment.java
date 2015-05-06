package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hannelore on 22/04/2015.
 */
public class StopwatchFragment extends Fragment implements View.OnClickListener {

    private View view;

    private Button startButton;
    private Button stopButton;
    private Button resetButton;

    JSONObject jsonResponse;

    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours,minutes,seconds,currentmin,lastmin="00";
    private long secs,mins,hrs;
    private boolean stopped = false, start, stop, reset;
    private TextToSpeech SayTime;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        startButton = (Button) view.findViewById(R.id.btnStart);
        startButton.setOnClickListener(this);
        stopButton = (Button) view.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);
        resetButton = (Button) view.findViewById(R.id.btnReset);
        resetButton.setOnClickListener(this);

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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnStart:
                startClick();
                break;
            case R.id.stopButton:
                AlertDialog.Builder stopAlert=new AlertDialog.Builder(v.getContext());
                stopAlert.setMessage("Do you want to quit your session?");
                stopAlert.setTitle("ClimbUP");
                stopAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                stopAlert.setPositiveButton("YES",new DialogInterface.OnClickListener() {
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

    public void startClick (){
        showStopButton();
        ((TextView) view.findViewById(R.id.Height)).setVisibility(View.VISIBLE);
        start=true;
        stop=false;
        if(stopped){
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        else{
            startTime = System.currentTimeMillis();
        }
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    public void stopClick (){
        hideStopButton();
        start=false;
        stop=true;
        mHandler.removeCallbacks(startTimer);
        stopped = true;
       new MyAsyncTask().execute();
    }

    public void resetClick (){
        stopped = false;
        start=false;
        stop=false;
        ((TextView)getView().findViewById(R.id.timer)).setText("00:00:00");
        ((TextView) view.findViewById(R.id.Height)).setVisibility(View.GONE);
    }

    private void showStopButton(){
        startButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void hideStopButton(){
        startButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);
    }


    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */

        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

    	/* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

		/* Setting the timer text to the elapsed time */
        ((TextView) view.findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);
        ((TextView) view.findViewById(R.id.Height)).setText("Current Height:"+ System.getProperty("line.separator")+"meter");


        //Check if we need to speak the minutes
        //@ the moment it's every minute
        String alertTime=((SessionActivity)getActivity()).AlertTime;
        if(alertTime.equals("30s")) {
            if ((secs == 0 && mins > 0) || secs == 30) {
                speakText();
            }
        }else if(alertTime.equals("1m")) {
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin) {
                speakText();
            }
            lastmin = currentmin;
        }else if(alertTime.equals("5m")) {
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin && (mins%5)==0) {
                speakText();
            }
            lastmin = currentmin;

        }else if(alertTime.equals("10m")){
            currentmin = String.valueOf(mins);
            if (lastmin != currentmin && (mins%10)==0) {
                speakText();
            }
            lastmin = currentmin;
        }

    }


    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    public void speakText(){

             String toSpeak="";

                if (secs==30) {
                    if (mins == 0) {
                        toSpeak = "You have been climbing for "+String.valueOf(secs)+" seconds";
                    } else if(mins==1) {
                        toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute and " + String.valueOf(secs) + " seconds";
                    }else
                        toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute and " + String.valueOf(secs) + " seconds";
                }else if(secs==0)
                {
                    if (mins == 1) {
                        toSpeak = "You have been climbing for " + String.valueOf(mins) + " minute";
                    } else
                        toSpeak = "You have been climbing for " + String.valueOf(mins) + " minutes";
                }else  toSpeak = "You have been climbing for " + String.valueOf(mins) + " minutes and "+String.valueOf(secs)+" seconds";

                Toast.makeText(getActivity().getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                SayTime.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);


    }
    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(getView().getContext());
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Writing");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url_select = "http://php-brechtcarlier.rhcloud.com/";

            try {
                // Set up HTTP post
                List<NameValuePair> jsonArray = new ArrayList<NameValuePair>();
                jsonArray.add(new BasicNameValuePair("tag", "time"));
                jsonArray.add(new BasicNameValuePair("name", Login.Name.toString()));
                jsonArray.add(new BasicNameValuePair("time", String.valueOf(elapsedTime)));

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url_select);
                httpPost.setEntity(new UrlEncodedFormEntity(jsonArray));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();

                // Read content & Log
                inputStream = httpEntity.getContent();
            } catch (Exception e) {
                this.progressDialog.dismiss();
                cancel(true);
            }

            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();
            }

            catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            //parse JSON data

            try {
                jsonResponse = new JSONObject(result);

                //Close the progressDialog!
                this.progressDialog.dismiss();
                if (jsonResponse.optString("success").toString().equals("1")) {
                    Toast.makeText(getView().getContext(), "You have wrote your time to the database", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getView().getContext(),WelcomeActivity.class);
                    i.putExtra("Username", Login.Name.toString());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(i);
                }
                else if(jsonResponse.optString("error").toString().equals("1")){
                    Toast.makeText(getView().getContext(), jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getView().getContext(), "Can't write the data", Toast.LENGTH_SHORT).show();
        }
    }
}
