package com.ap.brecht.guitool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by hannelore on 22/04/2015.
 */
public class DescriptionFragmentSession extends Fragment {

    private View view;
    private EditText locatie;
    private EditText descriptie;
    private ImageView ivPicture;

    static int TAKE_PICTURE = 1337;

    private static String loc;
    private static String des;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_session, container, false);

        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageBox();
            }
        });
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
                MessageBox();
            } else if (!DatabaseData.PhotoString.equals("")) {
                try {
                    //Save photo in ImageView
                    Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse("file://" + DatabaseData.PhotoString));
                    ivPicture.setImageBitmap(photo);
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Unable to access temporally picture", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void MessageBox()
    {
        QustomDialogBuilder pictureAlert = new QustomDialogBuilder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        pictureAlert.setMessage(Html.fromHtml("<font color=#" + Integer.toHexString(getResources().getColor(R.color.white) & 0x00ffffff) + ">Do you want to make a picture?"));
        pictureAlert.setTitle("ClimbUP");
        pictureAlert.setTitleColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
        pictureAlert.setDividerColor("#" + Integer.toHexString(getResources().getColor(R.color.Orange) & 0x00ffffff));
        pictureAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseData.PhotoString = "";
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1337) {
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse("file://" + DatabaseData.PhotoString));
                ExifInterface ei = new ExifInterface(DatabaseData.PhotoString);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        photo = RotateBitmap(photo, 90, 1000);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        photo = RotateBitmap(photo, 180, 1000);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        photo = RotateBitmap(photo, 270, 1000);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        photo = RotateBitmap(photo, 0, 1000);
                        break;
                    // etc.
                }

                //If the bitmap is changed (rotated) we override the bitmap!
                File f = new File(DatabaseData.PhotoString);
                FileOutputStream fOut = new FileOutputStream(f);
                photo.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
                fOut.flush();
                fOut.close();

            } catch (Exception e) {
                //If this was failed (as example when you don't take a picture, this happens when you press on back) clear PhotoString
                DatabaseData.PhotoString = "";
                Toast.makeText(getActivity().getApplicationContext(), "Unable to access temporally picture", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private Bitmap RotateBitmap(Bitmap source, float angle, int maxwidth) {
        //Resize bitmap
        if (source.getWidth() > maxwidth) {
            source = Bitmap.createScaledBitmap(source, maxwidth, source.getHeight() / (source.getWidth() / maxwidth), true);
        }

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

    private boolean hasCamera() {
        // method to check if you have a Camera
        return getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Uri SavePic() {
        File f = createImageFile();
        Uri mCurrentPhoto = Uri.fromFile(f);
        DatabaseData.PhotoString = mCurrentPhoto.getPath();
        return mCurrentPhoto;
    }

    private File createImageFile() {
        try {
            // Create an temporally PhotoBinaryString
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
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to save temporally picture", Toast.LENGTH_SHORT).show();
        }

        return null;

    }

    public static String getDescription() {
        if (des != null) {
            return String.valueOf(des);
        }
        return " ";
    }

    public static void setDescription(String value) {
        des = value;
    }

    public static String getLocation() {
        if (loc != null) {
            return String.valueOf(loc);
        }
        return " ";
    }

    public static void setLocation(String value) {
        loc = value;
    }
}


