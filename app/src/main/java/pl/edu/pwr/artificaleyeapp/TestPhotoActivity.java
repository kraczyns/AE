package pl.edu.pwr.artificaleyeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by nieop on 30.10.2016.
 */
public class TestPhotoActivity extends AppCompatActivity{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String EXTRA_IMAGE = "BitmapImage";
    public static String filename;

    public void intentConvertPhoto() throws FileNotFoundException, UnsupportedEncodingException {
       Binarize binarize = new Binarize();
        Intent intent = new Intent(this, CameraActivity.class);
        binarize.openFile(this, filename);
        Bitmap imageBitmap = binarize.getImage();
        ImageConverter imgConv = new ImageConverter(this);
        imgConv.setNewSize(imageBitmap.getHeight(), imageBitmap.getWidth());
        imgConv.setImageConverter(imageBitmap);
        imgConv.setSourceImg(imgConv.resize());
        imgConv.process();
        Bitmap edges = imgConv.getEdgesImg();
        intent.putExtra(EXTRA_IMAGE,  edges);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_test);

        final Button piastowButton = (Button) findViewById(R.id.piastowButton);
        final Button uwagaButton = (Button) findViewById(R.id.uwagaButton);
        final Button strefaRuchuButton = (Button) findViewById(R.id.strefaruchuButton);

        piastowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            filename = "piastow";
                try {
                    intentConvertPhoto();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        strefaRuchuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = "strefaruchu";
                try {
                    intentConvertPhoto();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        uwagaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = "uwaga";
                try {
                    intentConvertPhoto();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
