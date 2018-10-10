package com.example.myfirstapp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

/**
 * Wrapper activity demonstrating the use of the new
 * {@link SensorEvent#values rotation vector sensor}
 * ({@link Sensor#TYPE_ROTATION_VECTOR TYPE_ROTATION_VECTOR}).
 *
 * @see Sensor
 * @see SensorEvent
 * @see SensorManager
 *
 */
public class OpenGLES20Activity extends Activity {
    private GLSurfaceView mGLSurfaceView;
    private SensorManager mSensorManager;
    private MyRenderer mRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        // Create our Preview view and set it as the content of our
        // Activity

        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mRenderer = new MyRenderer();
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setRenderer(mRenderer);
        setContentView(mGLSurfaceView);
    }
    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mRenderer.start();
        mGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mRenderer.stop();
        mGLSurfaceView.onPause();
    }
    class MyRenderer implements GLSurfaceView.Renderer, SensorEventListener {
        private pTriangle mTriangle;
        private Sensor mRotationVectorSensor;
        private final float[] mRotationMatrix = new float[16];
        public MyRenderer() {
            // find the rotation-vector sensor
            mRotationVectorSensor = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ROTATION_VECTOR);
            mTriangle = new pTriangle();
            // initialize the rotation matrix to identity
            //mRotationMatrix[ 0] = 1;
            //mRotationMatrix[ 4] = 1;
            //mRotationMatrix[ 8] = 1;
            //mRotationMatrix[12] = 1;

            mRotationMatrix[0] = 0.1f;
            mRotationMatrix[1] = 0.3f;
            mRotationMatrix[2] = -1f;
            mRotationMatrix[4] = -1f;
            mRotationMatrix[6] = -0.1f;
            mRotationMatrix[9] = 1f;
            mRotationMatrix[10] = 0.3f;
        }
        public void start() {
            // enable our sensor when the activity is resumed, ask for
            // 0.1 ms updates.
            mSensorManager.registerListener(this, mRotationVectorSensor, 100);
        }
        public void stop() {
            // make sure to turn our sensor off when the activity is paused
            mSensorManager.unregisterListener(this);
        }
        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // convert the rotation-vector to a 4x4 matrix. the matrix
                // is interpreted by Open GL as the inverse of the
                // rotation-vector, which is what we want.
                float[] rotationVector = {0,0,0};
                rotationVector[0] = event.values[0];
                rotationVector[1] = event.values[1];
                rotationVector[2] = event.values[2];
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix , rotationVector);

                float[] positionVector = {0,0,0};
                float distance = 5;
                positionVector[0] = distance*(float)Math.sin(rotationVector[0]);
                positionVector[1] = distance*(float)Math.sin(rotationVector[1]);
                positionVector[2] = distance*(float)Math.sin(rotationVector[2]);
                mTriangle.setPos(positionVector[0],positionVector[1],positionVector[2]);
            }
        }
        public void onDrawFrame(GL10 gl) {
            // clear screen
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl.glClearColor(0f, 0f, 0f, 1f);
            // set-up modelview matrix
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -4.0f);
            gl.glMultMatrixf(mRotationMatrix, 0);
            // draw our object
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            mTriangle.draw(gl);
        }
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // set view-port
            gl.glViewport(0, 0, width, height);
            // set projection matrix
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // dither is enabled by default, we don't need it
            gl.glDisable(GL10.GL_DITHER);
            // clear screen in white
            gl.glClearColor(1, 1, 1, 1);
        }

        class pTriangle {

            public float translate[] = {0,0,0};

            // initialize our triangle
            private FloatBuffer mVertexBuffer;
            private FloatBuffer mColorBuffer;
            private ByteBuffer  mIndexBuffer;
            public pTriangle() {
                /*
                final float vertices[] = {
                        -1, -1, -1,		 1, -1, -1,
                        1,  1, -1,	    -1,  1, -1,
                        -1, -1,  1,      1, -1,  1,
                        1,  1,  1,     -1,  1,  1,
                };
                final float colors[] = {
                        1,  1,  1,  1,  0,  0,  0,  0,
                        1,  1,  1,  1,  0,  0,  0,  0,
                        1,  1,  1,  1,  0,  0,  0,  0,
                        1,  1,  1,  1,  0,  0,  0,  0,
                };
                final byte indices[] = {
                        0, 4, 5,    0, 5, 1,
                        1, 5, 6,    1, 6, 2,
                        2, 6, 7,    2, 7, 3,
                        3, 7, 4,    3, 4, 0,
                        4, 7, 6,    4, 6, 5,
                        3, 0, 1,    3, 1, 2
                }; */

                final float vertices[] = {
                        0,0,0.577f,
                        0,-0.5f,-0.288f,
                        0,0.5f,-0.288f,

                };

                /*final float vertices[] = {
                    0,-1,0,
                    0,1,0,
                    0,0,1,
                };*/

                final float colors[] = {
                        0,1,1,1,
                        0,1,1,1,
                        0,1,1,1,

                };


                ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
                vbb.order(ByteOrder.nativeOrder());
                mVertexBuffer = vbb.asFloatBuffer();
                mVertexBuffer.put(vertices);
                mVertexBuffer.position(0);
                ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
                cbb.order(ByteOrder.nativeOrder());
                mColorBuffer = cbb.asFloatBuffer();
                mColorBuffer.put(colors);
                mColorBuffer.position(0);
                this.setPos(0f, 0f, 0f);
                //mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
                //mIndexBuffer.put(indices);
                //mIndexBuffer.position(0);
            }

            public void setPos(float x, float y, float z) {
                translate[0] = x;
                translate[1] = y;
                translate[2] = z;
            }

            public void draw(GL10 gl) {
                //gl.glEnable(GL10.GL_CULL_FACE);
                gl.glFrontFace(GL10.GL_CW);
                gl.glShadeModel(GL10.GL_SMOOTH);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
                gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
                //gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
                gl.glTranslatef(translate[0]+3,translate[1]+3,translate[2]);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}