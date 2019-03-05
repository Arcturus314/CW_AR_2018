package com.example.orientationtest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public SensorManager manager;
    public Sensor rotationVectorSensor;
    public Sensor magnetometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        magnetometerSensor   = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        manager.registerListener(this, rotationVectorSensor, 1);
        manager.registerListener(this,  magnetometerSensor, 1);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            Log.i("orientation", Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]));
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.i("magnetometer", Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
