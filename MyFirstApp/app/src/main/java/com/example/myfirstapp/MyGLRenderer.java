package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
//import android.opengl.EGLConfig;
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.SENSOR_SERVICE;

public class MyGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {

    public SensorManager manager;
    public Sensor rotationVectorSensor;
    private Triangle mTriangle;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] lookAtVector = new float[3];
    private final float HORIZONTAL_SCALE_FACTOR = 1.9f;
    private final float VERTICAL_SCALE_FACTOR = 3.75f;
    Context mContext;

    public MyGLRenderer(Context context) {
        mContext = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // pass context to this class to register sensor listener
        manager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, rotationVectorSensor, 1);
        // Set the background frame colour
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // initialise a triangle
        mTriangle = new Triangle();
        lookAtVector[0] = 0f;
        lookAtVector[1] = 0f;
        lookAtVector[2] = 0f;
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, lookAtVector[0], lookAtVector[1], lookAtVector[2], 0f, 1.0f, 0f);
    }

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        //Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        // redraw background colour
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        //Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mTriangle.draw(mMVPMatrix);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        float [] rotationMatrix = new float[16];
        float [] remappedRot = new float[16];
        float [] orientationVector = new float[3];
        float pitch, yaw, roll;
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            float distance = 5f;
            https://stackoverflow.com/questions/20564735/remapping-coordinate-system-in-android-app
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRot);
            // orientationVector[0] = YAW north = 0, south = pi, east = pi/2, west = -pi/2
            // orientationVector[1] = PITCH sky = -pi/2, earth = pi/2 ish
            // orientationVector[2] = roll
            SensorManager.getOrientation(remappedRot, orientationVector);
            pitch = orientationVector[1];
            yaw = orientationVector[0];
            roll = orientationVector[2];
            Log.i("eulerAngleReadings: ", "pitch: " + pitch * 180 / Math.PI + " yaw " + yaw * 180 / Math.PI + " roll " + roll * 180 / Math.PI);
            // https://learnopengl.com/Getting-started/Camera
            lookAtVector[0] = (float) ( Math.cos(pitch) * Math.cos(yaw) ); // lookAtX
            lookAtVector[1] = (float) Math.sin(pitch); // lookAt Y
            lookAtVector[2] = (float) ( Math.cos(pitch) * Math.sin(yaw) ); // lookAtZ
            // set camera position
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, HORIZONTAL_SCALE_FACTOR*distance*lookAtVector[0],
                    -VERTICAL_SCALE_FACTOR*distance*lookAtVector[1], distance*lookAtVector[2], 0, 1.0f, 0f);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        // this code populates a projection matrix to only render objects in the given frustrum
        Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a shader of the given type (fragment or vertex)
        int shader = GLES20.glCreateShader(type);
        // add source code to the shader, compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}