package pl.edu.pwr.artificaleyeapp;

import android.content.ClipData;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ModesActivity extends AppCompatActivity {
    public static Parameters chosenOne;
    public static int pos;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public final static String EXTRA_IMAGE = "BitmapImage";

    public void intentCameraPhoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        getIntent();
        final ListView base = (ListView) findViewById(R.id.listView);
        final Button delete = (Button) findViewById(R.id.deleteAllButton);
        final Button start = (Button) findViewById(R.id.choiceButton);
        final ParametersDB modes = new ParametersDB(this);
        final ArrayAdapter arrayAdapter = new ArrayAdapter<Parameters>(this, android.R.layout.simple_list_item_1, modes.selectParameters());
        base.setAdapter(arrayAdapter);
        base.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        base.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Set the item as checked to be highlighted
                base.setItemChecked(position, true);
                view.setBackgroundColor(Color.BLUE);
                pos = position;
                arrayAdapter.notifyDataSetChanged();

            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                modes.deleteAllModes();
                arrayAdapter.notifyDataSetChanged();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                chosenOne = modes.selectParameters().get(pos);
                intentCameraPhoto();
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
                imgConv.setNewSize(chosenOne.getHeight(), chosenOne.getWidth());
                imgConv.setThreshold(chosenOne.getLowThreshold(), chosenOne.getHighThreshold());
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
