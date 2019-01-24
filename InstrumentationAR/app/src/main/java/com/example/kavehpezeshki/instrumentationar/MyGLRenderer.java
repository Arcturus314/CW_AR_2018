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

    // vector to store x, y, z, coordinates of where the camera should look
    // determined by sensors
    private float[] lookAtVector = new float[3];

    // scale factors for translating objects based on sensor readings
    // determined experimentally to create nice FOV
    private final float HORIZONTAL_SCALE_FACTOR = 1.9f;
    private final float VERTICAL_SCALE_FACTOR = 2.5f;

    // Context object so that we can access sensor data in the renderer class
    Context mContext;

    // gps coordinates of user
    // these live in the renderer class for now, but we can elaborate a better heirarchy later
    private float[] gpsCoords = new float[3];


    //last set of n coordinates to average (FIR filter)
    private int numVectorsForFIR = 10;
    private float[] rollVectors  = new float[numVectorsForFIR];
    private float[] pitchVectors = new float[numVectorsForFIR];
    private float[] yawVectors   = new float[numVectorsForFIR];

    //GPS distances
    private float longDist;
    private float latDist;
    private float altDist;

    public MyGLRenderer(Context context) {
        mContext = context;
    }


    public void setPos(float latDist, float longDist, float altDist) {
        if(latDist != -1f && longDist != -1f && altDist != -1f) {
            this.latDist = latDist;
            this.longDist = longDist;
            this.altDist = altDist;
        }
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
        // assign default viewer position
        // will get updated based on GPS locations
        gpsCoords[0] = 0f;
        gpsCoords[1] = 0f;
        gpsCoords[2] = 1f;
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, lookAtVector[0], lookAtVector[1], lookAtVector[2], 0f, 1.0f, 0f);
    }

    public void onDrawFrame(GL10 unused) {
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        float[] mRotationMatrix = new float[16];
        float[] scratch = new float[16];
        //Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        // redraw background colour
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // calculate the projection, view, and model transformation
        Matrix.setIdentityM(mTranslateM, 0);
        //Matrix.translateM(mTranslateM, 0, 2, 2, 10);
        Matrix.translateM(mTranslateM, 0, longDist, latDist, altDist);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        //Matrix.multiplyMM(modelViewMatrix, 0, mMVPMatrix, 0, mTranslateM, 0);

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mTranslateM, 0);
        mTriangle.draw(scratch);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            float distance = 5f;
            float[] eulerAngles = quaternionToEuler(event.values);
            // because of the sensor orientation on the glasses,
            Log.i("eulerAngleReadings: ", "pitch: " + eulerAngles[2] + " yaw " + eulerAngles[1] + " roll " + eulerAngles[0]);
            Log.i("GPS: ", "lat: " + latDist + " long: " + longDist + " alt: " + altDist);
            lookAtVector[0] = calcNextFIR((float) Math.sin((eulerAngles[1])), yawVectors); // yaw projection
            lookAtVector[1] = calcNextFIR((float) ( Math.cos(eulerAngles[2]) * Math.cos(eulerAngles[1]) ), pitchVectors); // pitch projection
            if (lookAtVector[0] > 1.4f) {
                lookAtVector[1] = 1.4f;
            } else if (lookAtVector[0] < -1.4f) {
                lookAtVector[1] = -1.4f;
            }
            lookAtVector[2] = calcNextFIR((float) ( Math.cos(eulerAngles[2]) * Math.sin(eulerAngles[1]) ), rollVectors); // roll
            // set camera position
            // parametres to this function populate the view matrix appropriately
            // make X the up vector because we define pitch to be rotation of glasses around horizontal
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, HORIZONTAL_SCALE_FACTOR*distance*lookAtVector[0],
                    VERTICAL_SCALE_FACTOR*distance*lookAtVector[1], distance*lookAtVector[2], 0, 1.0f, 0f);
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates in the onDrawFrame() method
        // this code populates a projection matrix to only render objects in the given frustrum
        Matrix.perspectiveM(mProjectionMatrix, 0, 23, ratio, 0.1f, 50);
//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a shader of the given type (fragment or vertex)
        int shader = GLES20.glCreateShader(type);
        // add source code to the shader, compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    /*
     * Taken from: https://stackoverflow.com/questions/30279065/how-to-get-the-euler-angles-from-the-rotation-vector-sensor-type-rotation-vecto
     */
    float[] quaternionToEuler(float[] q) {
        float psi = (float) Math.atan2( -2.*(q[2]*q[3] - q[0]*q[1]) , q[0]*q[0] - q[1]*q[1]- q[2]*q[2] + q[3]*q[3]);
        float theta = (float) Math.asin( 2.*(q[1]*q[3] + q[0]*q[2]));
        float phi = (float) Math.atan2( 2.*(-q[1]*q[2] + q[0]*q[3]) , q[0]*q[0] + q[1]*q[1] - q[2]*q[2] - q[3]*q[3]);
        float [] eulerAngles = {psi, theta, phi};
        return eulerAngles;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}