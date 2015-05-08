package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by Airien on 22/04/2015.
 */

public class Login extends ActionBarActivity {

        Button btnLogin;
        Button btnRegister;
        EditText Name;
        EditText Password;

        JSONObject jsonResponse;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.login);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setIcon(R.drawable.icon);

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
                DatabaseComClass.Login(String.valueOf(Name.getText()), String.valueOf(Password.getText()), progressDialog);
                return null;
            }

            protected void onPostExecute(Void v) {
                try {
                    //Close the progressDialog!
                    this.progressDialog.dismiss();
                    if (DatabaseData.userData.optString("success").toString().equals("1")) {
                        super.onPostExecute(v);
                        Intent intent = new Intent(Login.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Login.this.startActivity(intent);
                    }
                    else if(DatabaseData.userData.optString("error").toString().equals("1")){
                        Toast.makeText(Login.this, jsonResponse.optString("error_msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void onCancelled() {
                Toast.makeText(Login.this, "Can't login", Toast.LENGTH_SHORT).show();
            }
        }

    }