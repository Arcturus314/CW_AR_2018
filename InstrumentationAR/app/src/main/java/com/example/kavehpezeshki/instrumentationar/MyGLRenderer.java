package com.example.kavehpezeshki.instrumentationar;

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
import java.lang.Math;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.SENSOR_SERVICE;

public class MyGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {

    public SensorManager manager;
    public Sensor rotationVectorSensor;
    private Triangle mTriangle;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    // arrays that store graphics transformation matrices
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] modelViewMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    // triangle translation matrix
    // translate triangle by gpsCoords - triangle.gpsCoords
    // we do this to keep the camera at the centre of the scene
    private float[] mTranslateM = new float[16];
    float[] mRotationMatrix = new float[16];
    // vector to store x, y, z, coordinates of where the camera should look
    // determined by sensors
    private float[] lookAtVector = new float[3];

    // Context object so that we can access sensor data in the renderer class
    Context mContext;

    //last set of n coordinates to average (FIR filter)
    private int numVectorsForFIR = 10;
    private float[] rollVectors  = new float[numVectorsForFIR];
    private float[] pitchVectors = new float[numVectorsForFIR];
    private float[] yawVectors   = new float[numVectorsForFIR];

    //GPS distances
    private float longDist = 0f; //north-south  currently north-south, correct
    private float latDist = 0f; //east-west     currently up-down, fixed and now correct
    private float altDist = 0f; //up-down      currently east-west, fixed and now correct

    public float[][] flightDists;

    private Triangle[] flightDrawings;

    public MyGLRenderer(Context context) {
        mContext = context;
    }


    public void setPos(float latDist, float longDist, float altDist) {
        Log.i("GPS Setup Status: ", "Starting setPos");
        if(latDist != -1f && longDist != -1f && altDist != -1f) {
            this.latDist = latDist;
            this.longDist = longDist;
            this.altDist = altDist;
            Log.i("GPS Setup Status: ", "setPos: Updating values " + this.latDist + " " + this.longDist + " " + this.altDist);
        }
        Log.i("GPS Setup Status: ", "Ending setPos");
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // pass context to this class to register sensor listener
        manager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, rotationVectorSensor, 1);
        // Set the background frame colour
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // initialise a triangle
        lookAtVector[0] = 0f;
        lookAtVector[1] = 0f;
        lookAtVector[2] = 0f;
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, lookAtVector[0], lookAtVector[1], lookAtVector[2], 0f, 1.0f, 0f);
    }

    public void onDrawFrame(GL10 unused) {
        // Create a rotation transformation for the triangle
        //Log.i("GPS Setup Status: ", "setPos: Rendering");
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        float[] scratch = new float[16];
        float[] transformationMatrix = new float[16];
        // redraw background colour
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // calculate the projection, view, and model transformation
        Matrix.setIdentityM(mTranslateM, 0);

        // create triangles for every flight
        if (flightDists != null) {
            for (int i = 0; i < flightDists.length; i++) {
                Triangle flight;
                flight = new Triangle();
                float lat = flightDists[i][0];
                float lon = flightDists[i][1];
                float alt = flightDists[i][2];

                Matrix.translateM(mTranslateM, 0, lat, alt, -lon);
                //Matrix.multiplyMM(transformationMatrix, 0, mTranslateM, 0, mRotationMatrix, 0);

                Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
                //Matrix.multiplyMM(modelViewMatrix, 0, mMVPMatrix, 0, mTranslateM, 0);

                Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mTranslateM, 0);
                flight.draw(scratch);
            }
        }

        /**
         * The coordinate system below works for the phone
         * positive x is towards north, negative x is towards south (update- pos x towards East, neg x towards West)
         * positive y is towards sky, negative y is towards ground
         * positive z is towards east, negative z is towards west (update- pos z towards South, neg z towards North) update 2: ?????? but updated comment
         */

        /**
         * This coordinate system works for the glasses
         * positive x is towards north, negative x is towards south
         * positive y is towards sky, negative y is towards ground
         * positive z is towards east, negative z is towards west
         */

        //Matrix.translateM(mTranslateM, 0, latDist, altDist, -longDist);

        //Log.i("Drawing: ", "scratch: " + print16ArrByElement(scratch));

    }

    public String print16ArrByElement(float[] input) {
        String output = "";
        for(int i = 0; i < 16; i++) {
            output += input[i] + " ";
        }
        return output;
    }

    //calculates the new output of a FIR given an input vector
    public float calcNextFIR(float newVal, float[] valArr) {
        //adding new value to FIR arr
        float[] newArr = new float[numVectorsForFIR];
        newArr[0] = newVal;
        for(int i = 0; i < numVectorsForFIR-1; i++) {
            newArr[i+1] = valArr[i];
        }
        for(int i = 0; i < numVectorsForFIR; i++) {
            valArr[i] = newArr[i];
        }
        //calculating value to return
        //using decaying powers of 2 right now
        float valSum = 0;
        float total  = 0;
        for(int i = 0; i < numVectorsForFIR; i++) {
            float multFactor = (float) Math.pow(2, -1*i);
            valSum += newArr[i]*multFactor;
            total  += multFactor;
        }
        return valSum/total;

    }

    //logging variables
    float maxPitch = 0;
    float minPitch = 400;
    float maxYaw   = 0;
    float minYaw   = 400;
    float maxRoll  = 0;
    float minRoll  = 400;

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
            //float distance = (float) Math.sqrt(latDist*latDist+ longDist*longDist + altDist*altDist);
            float distance = 20;
            // https://stackoverflow.com/questions/20564735/remapping-coordinate-system-in-android-app
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRot);
            // orientationVector[0] = YAW north = 0, south = pi, east = pi/2, west = -pi/2
            // orientationVector[1] = PITCH sky = -pi/2, earth = pi/2 ish
            // orientationVector[2] = roll
            SensorManager.getOrientation(remappedRot, orientationVector);
            pitch = orientationVector[1];
            yaw = orientationVector[0];
            roll = orientationVector[2];
            //Log.i("Sensor OV Data ", Arrays.toString(orientationVector));
            //Log.i("eulerAngleReadings: ", "pitch: " + pitch * 180 / Math.PI + " yaw " + yaw * 180 / Math.PI + " roll " + roll * 180 / Math.PI);
            //Log.i("distance from tree", "latDist: " + latDist + " longDist " + longDist + " altDist " + altDist);
            // https://learnopengl.com/Getting-started/Camera
            lookAtVector[0] = (float) ( Math.cos(pitch) * Math.cos(yaw) ); // lookAtX
            lookAtVector[1] = (float) Math.sin(pitch); // lookAt Y
            lookAtVector[2] = (float) ( Math.cos(pitch) * Math.sin(yaw) ); // lookAtZ

            // Log.i("Sensor TOT Data ", Arrays.toString(lookAtVector) + Arrays.toString(orientationVector));
            //Log.i("distance: ", distance + "");
            // set camera position
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, lookAtVector[0],
                    -lookAtVector[1], lookAtVector[2], 0, 1.0f, 0f);
            Matrix.setRotateM(mRotationMatrix, 0, -roll, 0, 0, 1f);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        // this code populates a projection matrix to only render objects in the given frustrum
        Matrix.perspectiveM(mProjectionMatrix, 0, 23, ratio, 0.1f, 500);
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