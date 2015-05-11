package com.ap.brecht.guitool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hannelore on 9/05/2015.
 */
public class ImageFragment extends Fragment {
    private View view;
    private ImageView Picture;

    JSONArray a = null;
    JSONObject o = null;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);

        Picture = (ImageView) view.findViewById(R.id.Picture);

        try {
            a = DatabaseData.userData.getJSONArray("session");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int arrSize = a.length();
        for (int i = 0; i < arrSize; ++i) {
            try {
                o = a.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if (o.getString("sid").equals(DatabaseData.Sid)) {
                    byte[] decodedString = Base64.decode(o.getString("image"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Picture.setImageBitmap(decodedByte);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }
}
