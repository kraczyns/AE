package pl.edu.pwr.artificaleyeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_IMAGE = "BitmapImage";
    public final static String EXTRA_TEXT = "RecognizedString";
public final static String EXTRA_CAM = "CameraData";
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
            Bitmap image = (Bitmap) extras.get(EXTRA_CAM);

            BitmapHandler bitmapHandler = new BitmapHandler();
            ImageConverter imageConverter = new ImageConverter(image);

            Intent intent = new Intent(this, CameraActivity.class);
            Bundle extra = new Bundle();
            extra.putParcelable(MainActivity.EXTRA_IMAGE, imageConverter.getImage());
            extra.putString(MainActivity.EXTRA_TEXT, imageConverter.getRecognizedText());
            intent.putExtras(extra);
            startActivity(intent);
        }
   }
}

