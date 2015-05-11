package com.ap.brecht.guitool;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Airien on 22/04/2015.
 */
public class RegisterActivity extends ActionBarActivity {

    Button btnRegister;


    Context context;
    EditText Name;
    EditText Password;
    JSONObject jsonResponse;
    String username;
    String userpassword;

    public static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register);

        context=this;

        btnRegister = (Button) findViewById(R.id.btnRegister);
        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=String.valueOf(Name.getText());
                userpassword=String.valueOf(Password.getText());
                if (username.equals("") || userpassword.equals("")){
                    QustomDialogBuilder exitAlert = new QustomDialogBuilder(context, AlertDialog.THEME_HOLO_DARK);
                    exitAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(getResources().getColor(R.color.white) & 0x00ffffff) + ">Please enter a valid username and password?"));
                    exitAlert.setTitle("ClimbUP");
                    exitAlert.setTitleColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                    exitAlert.setDividerColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                    exitAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    exitAlert.setCancelable(true);
                    exitAlert.create().show();


                }else
                {new MyAsyncTask().execute();
                }
            }
        });

        Name = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPassword);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }



    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
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
                jsonArray.add(new BasicNameValuePair("name", username));
                jsonArray.add(new BasicNameValuePair("password", userpassword));

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
            } catch (Exception e) {
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
                    Toast.makeText(RegisterActivity.this, "You have created a new account", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, Login.class);
                    Intent intent = new Intent(RegisterActivity.this, Login.class);
                    intent.putExtra("Username", String.valueOf(Name.getText()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    RegisterActivity.this.startActivity(intent);
                    finish();
                } else if (jsonResponse.optString("error").toString().equals("1")) {
                    Toast.makeText(RegisterActivity.this, jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(RegisterActivity.this, "Can't register", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        BackPressed.CloseApp(this);
    }
}
