package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

public class Login extends ActionBarActivity {

        Button btnLogin;
        Button btnRegister;
        static EditText Name;
        static EditText Password;

        JSONObject jsonResponse;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.login);

            ActionBar actionBar = getSupportActionBar();

            Name = (EditText) findViewById(R.id.etName);
            Password = (EditText) findViewById(R.id.etPassword);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnRegister = (Button) findViewById(R.id.btnRegister);

            btnRegister.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    Intent i = new Intent(Login.this, RegisterActivity.class);
                    i.putExtra("name", String.valueOf(Name.getText()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Login.this.startActivity(i);
                }
            });

            btnLogin.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new MyAsyncTask().execute();
                }
            });
        }

        class MyAsyncTask extends AsyncTask<Void, Void, Void> {

            private ProgressDialog progressDialog = new ProgressDialog(Login.this);
            InputStream inputStream = null;
            String result = "";

            protected void onPreExecute() {
                progressDialog.setMessage("Login...");
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

                try {// Set up HTTP post
                    List<NameValuePair> jsonArray = new ArrayList<NameValuePair>();
                    jsonArray.add(new BasicNameValuePair("tag", "login"));
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
                    SaveLoginClass.userData = jsonResponse;
                    /*String tag = jsonResponse.optString("tag").toString();
                    String success = jsonResponse.optString("success").toString();
                    String error = jsonResponse.optString("error").toString();
                    String error_msg = jsonResponse.optString("error_msg").toString();*/

                    //Close the progressDialog!
                    this.progressDialog.dismiss();
                    if (SaveLoginClass.userData.optString("success").toString().equals("1")) {
                        super.onPostExecute(v);
                        Intent intent = new Intent(Login.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Login.this.startActivity(intent);
                    }
                    else if(SaveLoginClass.userData.optString("error").toString().equals("1")){
                        Toast.makeText(Login.this, jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onCancelled() {
                Toast.makeText(Login.this, "Can't login", Toast.LENGTH_SHORT).show();
            }
        }

    }