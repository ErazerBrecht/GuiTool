package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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


    static String loc;
    static String des;

   String mCurrentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_session, container, false);

        SavePicButton = (Button) view.findViewById(R.id.savePicture);
        SavePicButton.setOnClickListener(this);
        locatie=(EditText) view.findViewById(R.id.location);
        descriptie=(EditText) view.findViewById(R.id.description);

        locatie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                loc=String.valueOf(locatie.getText());

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
                des=String.valueOf(descriptie.getText());
            }
        });

        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(DatabaseData.PhotoString==null) {
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
                        dispatchTakePictureIntent();
                        galleryAddPic();
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

    private void save()
    {
        try {
            File Drawn = new File(Environment.getExternalStorageDirectory().toString()+"/ClimbUP/");
            Drawn.mkdirs();
            File Drawing = File.createTempFile(
                    "tryout",  /* prefix */
                    ".jpg",         /* suffix */
                    Drawn      /* directory */
            );
            FileOutputStream out = new FileOutputStream(Drawing);

            // NEWLY ADDED CODE STARTS HERE [
            Bitmap originalBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            Bitmap copyBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(copyBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE); // Text Color
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(16); // Text Size
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
            // some more settings...

            canvas.drawBitmap(copyBitmap, 0, 0, paint);
            canvas.drawText("Testing...", 10, 10, paint);
            // NEWLY ADDED CODE ENDS HERE ]

            copyBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1337);
                DatabaseData.PhotoString="iets";
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory().toString()+"/ClimbUP/");
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}


