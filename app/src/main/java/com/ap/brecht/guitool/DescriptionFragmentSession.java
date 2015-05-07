package com.ap.brecht.guitool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by hannelore on 22/04/2015.
 */
public class DescriptionFragmentSession extends Fragment implements View.OnClickListener {

    private View view;
    private Button SavePicButton;

    /*
    static String loc;
    static String des;
    */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_description_session, container, false);

        //loc=String.valueOf(Location.getText());
        //des=String.valueOf(Description.getText());

        SavePicButton = (Button) view.findViewById(R.id.savePicture);
        SavePicButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savePicture:
                Save();
                break;
        }
    }

    public void Save()
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
            Bitmap originalBitmap = BitmapFactory.decodeFile(SessionActivity.mCurrentPhotoPath2);
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




}


