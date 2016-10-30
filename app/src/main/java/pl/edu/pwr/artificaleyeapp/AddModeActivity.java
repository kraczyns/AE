package pl.edu.pwr.artificaleyeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.Policy;

public class AddModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mode);
        getIntent();

        final Button save = (Button) findViewById(R.id.saveButton);
        final ParametersDB paramDB = new ParametersDB(this);
        final EditText name = (EditText) findViewById(R.id.nameText);
        final EditText width = (EditText) findViewById(R.id.widthText);
        final EditText height = (EditText) findViewById(R.id.heightText);
        final EditText lowThreshold = (EditText) findViewById(R.id.lowThresholdText);
        final EditText highThreshold = (EditText) findViewById(R.id.highThresholdText);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parameters param = new Parameters(name.getText().toString(), Integer.parseInt(width.getText().toString()),
                                                  Integer.parseInt(height.getText().toString()), Integer.parseInt(lowThreshold.getText().toString()),
                                                    Integer.parseInt(highThreshold.getText().toString()));
                paramDB.addMode(param);
            }
        });
    }
}
