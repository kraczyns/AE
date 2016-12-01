package pl.edu.pwr.artificaleyeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by nieop on 30.10.2016.
 */
public class TestPhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static String filename;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void intentConvertPhoto() throws IOException {

        BitmapHandler bitmapHandler = new BitmapHandler();
        ImageConverter imageConverter = new ImageConverter(bitmapHandler.openFile(this, filename));

        Intent intent = new Intent(this, CameraActivity.class);
        Bundle extra = new Bundle();
        extra.putParcelable(MainActivity.EXTRA_IMAGE, bitmapHandler.scaleDownBitmap(imageConverter.getImage(),100, this));
        extra.putString(MainActivity.EXTRA_TEXT, imageConverter.getRecognizedText());
        intent.putExtras(extra);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_test);

        final ImageButton piastowButton = (ImageButton) findViewById(R.id.piastowButton);
        final ImageButton uwagaButton = (ImageButton) findViewById(R.id.uwagaButton);
        final ImageButton strefaRuchuButton = (ImageButton) findViewById(R.id.strefaruchuButton);

        piastowButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                filename = "golina";
                try {
                    intentConvertPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
            }

            );


        strefaRuchuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filename = "strefaruchu";
                try {
                    intentConvertPhoto();
                } catch (IOException e) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TestPhoto Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pl.edu.pwr.artificaleyeapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TestPhoto Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pl.edu.pwr.artificaleyeapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
