package pl.edu.pwr.artificaleyeapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class CameraActivity extends AppCompatActivity {

    public void intentBack()
    {
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Button back = (Button)findViewById(R.id.backButton);

        Intent intent = getIntent();
        Bitmap myPhoto = (Bitmap) intent.getParcelableExtra(MainActivity.EXTRA_IMAGE);
        ImageView imageView = (ImageView) findViewById(R.id.takenPhoto);
        imageView.setImageBitmap(myPhoto);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                intentBack();
            }
        });
    }

}
