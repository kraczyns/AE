package pl.edu.pwr.artificaleyeapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_IMAGE = "BitmapImage";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static boolean defaultMode = true;
    static public int h, w, lT, hT;

    public void intentCameraPhoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void intentTestPhoto()
    {
        Intent testPhoto = new Intent(this, TestPhotoActivity.class);
        startActivity(testPhoto);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntent();
        final Button start = (Button) findViewById(R.id.startButton);
        final Button test = (Button) findViewById(R.id.testButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentCameraPhoto();
            }
        });

        test.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                intentTestPhoto();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Binarize binarize = new Binarize();
Bitmap bitmap = null;
            try {
                binarize.setImage(imageBitmap);
                bitmap = binarize.toBitmap(this, "xxx");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Intent camIntent = new Intent(this, CameraActivity.class);
            camIntent.putExtra(EXTRA_IMAGE, bitmap);
            startActivity(camIntent);
        }
   }
}

