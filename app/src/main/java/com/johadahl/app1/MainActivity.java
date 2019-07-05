package com.johadahl.app1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "MainActivity";
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private  boolean permissionToRecordAccepted = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button backButton = findViewById(R.id.settings);
        backButton.setOnClickListener(this);

        final Button validate = findViewById(R.id.validate);
        validate.setOnClickListener(this);

        SharedPreferences preferences = getSharedPreferences("userSettings",Context.MODE_PRIVATE);
        boolean st = preferences.getBoolean("isTeacher",true);
        Log.e("Main","Boo : "+st);
        if(!st)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings:
                Log.d(LOG_TAG, "clickedSettings");
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                break;
            case R.id.validate:
                Log.d(LOG_TAG, "clickedValidate");
                SharedPreferences userSettings = getSharedPreferences("userSettings", 0);

                EditText pass = (EditText) findViewById(R.id.stringInput);
                String passphrase = pass.getText().toString();

                if (passphrase.length() == 0){
                    // TODO: Add alert dialog if empty passphrase
                }

                // TODO: Make student predefined setting (currently null)


                if (userSettings.getBoolean("isTeacher", true)) {
                    Intent teachVal = new Intent(this, teacherValidation.class);
                    teachVal.putExtra("passphrase", passphrase);
                    startActivity(teachVal);
                    break;
                } else {
                    Intent studVal = new Intent(this, studentValidation.class);
                    studVal.putExtra("passphrase", passphrase);
                    startActivity(studVal);
                    break;
                }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }
}

