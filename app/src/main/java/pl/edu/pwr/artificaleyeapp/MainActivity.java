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

    public void intentChangeModes()
    {
        Intent changeMode = new Intent(this, ModesActivity.class);
        startActivity(changeMode);
    }

    public void intentAddMode()
    {
        Intent addMode = new Intent(this, AddModeActivity.class);
        startActivity(addMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntent();
        final Button start = (Button) findViewById(R.id.startButton);
        final Button change = (Button) findViewById(R.id.choiceOfModeButton);
        final Button add = (Button) findViewById(R.id.addModeButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentCameraPhoto();
            }
        });

        change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                intentChangeModes();
            }
        });

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                intentAddMode();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageConverter imgConv = new ImageConverter(this);
            imgConv.setNewSize(imageBitmap.getHeight(), imageBitmap.getWidth());
            imgConv.setImageConverter(imageBitmap);
            imgConv.setSourceImg(imgConv.resize());
            imgConv.process();
            Bitmap edges = imgConv.getEdgesImg();
            Intent camIntent = new Intent(this, CameraActivity.class);
            camIntent.putExtra(EXTRA_IMAGE, edges);
            startActivity(camIntent);
        }
   }
}

