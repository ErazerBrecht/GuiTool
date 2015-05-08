package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Arne on 8/05/2015.
 */
public class DatabaseComClass {
    private static String url_select = "http://php-brechtcarlier.rhcloud.com/";

    static InputStream inputStream = null;
    static String result = "";

    private static List<NameValuePair> jsonArray = new ArrayList<NameValuePair>();

    private static void Worker(ProgressDialog p){

        try {// Set up HTTP post
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url_select);
            httpPost.setEntity(new UrlEncodedFormEntity(jsonArray));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // Read content & Log
            inputStream = httpEntity.getContent();
        } catch (Exception e) {
            p.cancel();
        }

        try{
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();

            DatabaseData.userData = new JSONObject(result);
        }
        catch (Exception e){
            Log.e("StringBuilding", "Error converting result " + e.toString());
        }
    }



    public static void Login(String name, String password, ProgressDialog p){
        jsonArray.add(new BasicNameValuePair("tag", "login"));
        jsonArray.add(new BasicNameValuePair("name", name));
        jsonArray.add(new BasicNameValuePair("password", password));

        Worker(p);

    }
    public static void Session(String uid, String place, String description, String altitude, String duration, ProgressDialog p)
    {
        jsonArray.clear();
        jsonArray.add(new BasicNameValuePair("tag", "addSession"));
        jsonArray.add(new BasicNameValuePair("uid", uid));
        jsonArray.add(new BasicNameValuePair("place", place));
        jsonArray.add(new BasicNameValuePair("description", description));
        jsonArray.add(new BasicNameValuePair("altitude", altitude));
        jsonArray.add(new BasicNameValuePair("duration", duration));

        Worker(p);
    }
}
