package pl.edu.pwr.artificaleyeapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {

    private static final String SPEAK_REPEATEDLY = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    TextToSpeech textToSpeech;

    public void intentBack() {
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Button back = (Button) findViewById(R.id.backButton);
        final ImageView imageView = (ImageView) findViewById(R.id.takenPhoto);
        final Button say = (Button) findViewById(R.id.buttonSay);
        final TextView text = (TextView) findViewById(R.id.textView);
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("pl","PL");
                    textToSpeech.setLanguage(locale);
                }
            }
        });

        imageView.setImageBitmap((Bitmap) getIntent().getExtras().getParcelable(MainActivity.EXTRA_IMAGE));

        final String toSpeak = getIntent().getExtras().getString(MainActivity.EXTRA_TEXT);
        text.setText(toSpeak);
        Toast.makeText(this, toSpeak, Toast.LENGTH_LONG);
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    Timer timer = new Timer();
    final long DELAY = 500; // milliseconds
    timer.cancel();
    timer = new Timer();
    timer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            },
            DELAY
    );
    say.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    });


    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentBack();
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
                "Camera Page", // TODO: Define a title for the content shown.
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
                "Camera Page", // TODO: Define a title for the content shown.
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
