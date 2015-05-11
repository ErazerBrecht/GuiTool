package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hannelore on 9/05/2015.
 */
public class ImageFragment extends Fragment {
    private View view;
    private ImageView Picture;
    byte[] decodedString;
    Bitmap decodedByte;

    Button sharePicture;


    JSONArray a = null;
    JSONObject o = null;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);

        Picture = (ImageView) view.findViewById(R.id.Picture);

        sharePicture=(Button) view.findViewById(R.id.btnFacebook);
        sharePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, DatabaseData.Photo);
                sharingIntent.setType("image/jpg");
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Send email using"));


            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(DatabaseData.Photo == null)
                new MyAsyncTask().execute();
            else
                Picture.setImageBitmap(DatabaseData.Photo);
            }


    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());
        protected void onPreExecute() {
            progressDialog.setMessage("Getting image");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    MyAsyncTask.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            DatabaseComClass.getImageSid(DatabaseData.Sid, progressDialog);
            return null;
        }

        protected void onPostExecute(Void v) {
            this.progressDialog.dismiss();
            try {

                decodedString = Base64.decode(DatabaseData.image.getString("image"), Base64.DEFAULT);
                  decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                DatabaseData.Photo = decodedByte;
                Picture.setImageBitmap(decodedByte);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
