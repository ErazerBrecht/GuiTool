package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hannelore on 22/04/2015.
 */
public class DescriptionFragmentSession extends Fragment implements View.OnClickListener {

    private View view;
    private Button SavePicButton;
    private EditText locatie;
    private EditText descriptie;

    static int TAKE_PICTURE = 1;

    static String loc;
    static String des;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_session, container, false);

        SavePicButton = (Button) view.findViewById(R.id.savePicture);
        SavePicButton.setOnClickListener(this);
        locatie = (EditText) view.findViewById(R.id.location);
        descriptie = (EditText) view.findViewById(R.id.description);

        locatie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                loc = String.valueOf(locatie.getText());

            }
        });
        descriptie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                des = String.valueOf(descriptie.getText());
            }
        });

        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (DatabaseData.PhotoString == null) {
                QustomDialogBuilder pictureAlert = new QustomDialogBuilder(getActivity(), AlertDialog.THEME_HOLO_DARK);
                pictureAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(getResources().getColor(R.color.white) & 0x00ffffff) + ">Do you want to make a picture?"));
                pictureAlert.setTitle("ClimbUP");
                pictureAlert.setTitleColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                pictureAlert.setDividerColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
                pictureAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                pictureAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TakePicture();
                    }
                });

                pictureAlert.setCancelable(true);
                pictureAlert.create().show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savePicture:
                save();
                break;
        }
    }

    private void save() {
        try {
            File Drawn = new File(Environment.getExternalStorageDirectory().toString() + "/ClimbUP/");
            Drawn.mkdirs();
            File Drawing = File.createTempFile(
                    "tryout",  /* prefix */
                    ".jpg",         /* suffix */
                    Drawn      /* directory */
            );
            FileOutputStream out = new FileOutputStream(Drawing);

            Bitmap bitmap = BitmapFactory.decodeFile(DatabaseData.PhotoString).copy(Bitmap.Config.RGB_565, true);
            Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
            Canvas canvas = new Canvas(bitmap);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.RED); // Text Color
            paint.setTypeface(tf);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(convertToPixels(getActivity().getApplicationContext(), 84));

            String text = "Brecht het is laat...";
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);

            canvas.drawText(text, 700, 150, paint);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    private void TakePicture() {
        if (hasCamera()) {
            // create intent with ACTION_IMAGE_CAPTURE action
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, SavePic());

            // start camera activity
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    private boolean hasCamera(){
        // method to check if you have a Camera
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Uri SavePic() {
        File f = createImageFile();
        Uri mCurrentPhoto = Uri.fromFile(f);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mCurrentPhoto);
        getActivity().sendBroadcast(mediaScanIntent);
        DatabaseData.PhotoString = mCurrentPhoto.getPath();

        return mCurrentPhoto;
    }

    private File createImageFile(){
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp;
            File storageDir = new File(Environment.getExternalStorageDirectory().toString() + "/ClimbUP/");
            storageDir.mkdirs();
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            return image;
        }

        catch (IOException i)
        {

        }

        catch (Exception e)
        {

        }

        return null;

    }
}


