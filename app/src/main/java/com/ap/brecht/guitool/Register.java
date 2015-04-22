package com.ap.brecht.guitool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by Airien on 22/04/2015.
 */
public class Register extends Activity implements View.OnClickListener {

    Button btnRegister;

    EditText Name;
    EditText Password;
    JSONObject jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);
    }

    @Override
    public void onClick(View v) {
        new MyAsyncTask().execute();
    }
    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setMessage("Registering");
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
                jsonArray.add(new BasicNameValuePair("tag", "register"));
                jsonArray.add(new BasicNameValuePair("name", String.valueOf(Name.getText())));
                jsonArray.add(new BasicNameValuePair("password", String.valueOf(Password.getText())));

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
                    Toast.makeText(getApplicationContext(), "You have created a new account", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(Register.this,Login.class);
                }
                else if(jsonResponse.optString("error").toString().equals("1")){
                    Toast.makeText(getApplicationContext(), jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Can't register", Toast.LENGTH_SHORT).show();
        }
    }
}