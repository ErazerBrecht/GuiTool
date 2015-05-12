package com.ap.brecht.guitool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hannelore on 9/05/2015.
 */
public class ImageFragment extends Fragment {
    private View view;
    private ImageView Picture;
    File Drawn;
    File Drawing;

    byte[] decodedString;
    Bitmap decodedByte;

    Button savePicture;
    Button sharePicture;

    Uri a = null;
    JSONObject o = null;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);

               Picture = (ImageView) view.findViewById(R.id.Picture);

        savePicture = (Button) view.findViewById(R.id.btnSave);
        savePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SaveImage();
                AddToGallary();
            }
        });

        sharePicture=(Button) view.findViewById(R.id.btnFacebook);
        sharePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImage();
                a= Uri.parse("file://" + Drawing.getAbsolutePath());

                //Facebook Part (Credits to Anja)
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, a);
                sharingIntent.setType("image/jpg");
                view.getContext().startActivity(Intent.createChooser(sharingIntent, "Send email using"));
            }
        });

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                Drawing.delete();
                Drawn.delete();
            }
        }
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

    private void SaveImage()
    {
        try {
            String username = DatabaseData.userData.getJSONObject("user").getString("name");
            String name = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            File Drawn = new File(Environment.getExternalStorageDirectory().toString() + "/ClimbUP/" + username);
            Drawn.mkdirs();
            File Drawing = new File(Drawn, name + ".jpg");
            FileOutputStream out = new FileOutputStream(Drawing);

            Bitmap bitmap = DatabaseData.Photo.copy(Bitmap.Config.RGB_565, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            out.flush();
            out.close();

            Toast.makeText(getActivity().getApplicationContext(), "Saved Image!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Couldn't save image!", Toast.LENGTH_SHORT).show();
        }
    }


    private void AddToGallary()
    {
        //This part is used to add Generated picture to Album (Gallery)!
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(Drawing));
        getActivity().sendBroadcast(mediaScanIntent);
        DatabaseData.PhotoString = Drawing.getPath();
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

                decodedString = Base64.decode(DatabaseData.PhotoBinaryString, Base64.DEFAULT);
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
