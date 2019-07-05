package com.johadahl.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String LOG_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(this);
        RadioGroup person = (RadioGroup)findViewById(R.id.radioUser);
        person.setOnCheckedChangeListener(this);
        // TODO: Toggle of Microphone permission should actually change permission
        showUserSettings();
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "clickedBack");
        updateSettings();
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
    }

    private void showUserSettings(){
        SharedPreferences settings = getSharedPreferences("userSettings", 0);
        boolean isTeacher = settings.getBoolean("isTeacher", true);

        if (isTeacher){
            RadioButton teach = (RadioButton) findViewById(R.id.teacher);
            teach.toggle();
        } else {
            RadioButton student = (RadioButton) findViewById(R.id.student);
            student.toggle();
        }
    }

    private void updateSettings(){
        SharedPreferences settings = getSharedPreferences("userSettings", 0);
        SharedPreferences.Editor editor = settings.edit();
        RadioButton teacher = (RadioButton) findViewById(R.id.teacher);
        editor.putBoolean("isTeacher", teacher.isChecked());
        editor.apply(); // Or should this be commit? Is there a significant difference?
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        updateSettings();
    }
}


