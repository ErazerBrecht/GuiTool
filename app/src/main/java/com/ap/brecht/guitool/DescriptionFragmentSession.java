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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
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
import android.widget.ImageView;
import android.widget.Toast;

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
    private ImageView ivPicture;

    static int TAKE_PICTURE = 1337;

    static String loc;
    static String des;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_session, container, false);

        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
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
            else {
                try {
                    //Save photo in ImageView
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse("file://" + DatabaseData.PhotoString));
                    ivPicture.setImageBitmap(photo);
                }
                catch (Exception e)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Unable to access temporally picture", Toast.LENGTH_SHORT).show();
                }
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

            String text = "Testing";
            Rect textRect = new Rect();
            paint.getTextBounds(text, 0, text.length(), textRect);

            canvas.drawText(text, 700, 150, paint);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            //This part is used to add Generated picture to Album (Gallery)!
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(Drawing));
            getActivity().sendBroadcast(mediaScanIntent);

        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to edit picture", Toast.LENGTH_SHORT).show();
        }
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 1337 ) {
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse("file://" + DatabaseData.PhotoString));
                ExifInterface ei = new ExifInterface(DatabaseData.PhotoString);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        photo = RotateBitmap(photo, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        photo = RotateBitmap(photo, 180);
                        break;
                    // etc.
                }

                //If the bitmap is changed (rotated) we override the bitmap!
                File f =new File(DatabaseData.PhotoString);
                FileOutputStream fOut = new FileOutputStream(f);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
                fOut.flush();
                fOut.close();
            }
            catch (Exception e)
            {
                Toast.makeText(getActivity().getApplicationContext(), "Unable to access temporally picture", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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
        DatabaseData.PhotoString = mCurrentPhoto.getPath();

        return mCurrentPhoto;
    }

    private File createImageFile(){
        try {
            // Create an temporally image
            String imageFileName = "temp";
            File storageDir = new File(Environment.getExternalStorageDirectory().toString() + "/ClimbUP/.temp");
            storageDir.mkdirs();
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            return image;
        }

        catch (Exception e)
        {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to save temporally picture", Toast.LENGTH_SHORT).show();
        }

        return null;

    }
}


