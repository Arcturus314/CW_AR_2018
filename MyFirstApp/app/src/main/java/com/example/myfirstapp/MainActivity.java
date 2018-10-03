package com.example.myfirstapp;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        /startOpenGLActivity();
    }

    /* this doesn't work and I don't know why
    public void startOpenGLActivity() {
        Intent intent = new Intent(this, OpenGLES20Activity.class);
        startActivity(intent);
    }
    */
}
