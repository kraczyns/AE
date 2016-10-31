package pl.edu.pwr.artificaleyeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by nieop on 30.10.2016.
 */
public class TestPhotoActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String EXTRA_IMAGE = "BitmapImage";
    public static String filename;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public void intentConvertPhoto() throws FileNotFoundException, UnsupportedEncodingException {
        Binarize binarize = new Binarize();
        Intent intent = new Intent(this, CameraActivity.class);
        Bitmap imageBitmap = binarize.toBitmap(this, filename, null);
        intent.putExtra(EXTRA_IMAGE, binarize.scaleDownBitmap(imageBitmap, 100, this));
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_test);

        final Button piastowButton = (Button) findViewById(R.id.piastowButton);
        final Button uwagaButton = (Button) findViewById(R.id.uwagaButton);
        final Button strefaRuchuButton = (Button) findViewById(R.id.strefaruchuButton);

        piastowButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                filename = "piastow";
                try {
                    intentConvertPhoto();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
