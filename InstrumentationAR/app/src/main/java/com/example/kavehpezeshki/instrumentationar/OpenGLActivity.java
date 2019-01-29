package com.example.kavehpezeshki.instrumentationar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

//imports related to GPS
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.LocationListener;

public class OpenGLActivity extends Activity{
    private MyGLSurfaceView mSurfaceView;
    private MyGLRenderer  mRenderer;

    public void onCreate(Bundle savedInstanceState) {
        //restoring previous instance
        super.onCreate(savedInstanceState);

        //creating the renderer and surfaceview
        mRenderer    = new MyGLRenderer(this);
        mSurfaceView = new MyGLSurfaceView(this);
        //setting the mSurfaceView renderer to the instantiated mRenderer object
        mSurfaceView.start(mRenderer);
        setContentView(mSurfaceView);
    }

}
