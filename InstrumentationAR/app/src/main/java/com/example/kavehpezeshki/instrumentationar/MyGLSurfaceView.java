package com.example.kavehpezeshki.instrumentationar;

import android.opengl.GLSurfaceView;
import android.content.Context;
import android.util.Log;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;

    //note: lat, long, alt all static. There will only be one instance of MyGLSurfaceView.
    static float latDist  = 0f;
    static float longDist = 0f;
    static float altDist  = 0f;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
    }
    public void start(MyGLRenderer mRenderer) {
        this.mRenderer = mRenderer;
        setRenderer(mRenderer);
    }
    //sets the position of the user in OpenGL space from the origin, in terms of latitude, longitude, and altitude
    public void setRendererPos(float latDist, float longDist, float altDist) {
        this.latDist = latDist;
        this.longDist = longDist;
        this.altDist = altDist;
        Log.i("GPS Setup Status: ", "Setting GPS Position to: " + this.latDist + " " + this.longDist + " " + this.altDist);
        queueEvent(new Runnable() {
            public void run() {
                mRenderer.setPos(MyGLSurfaceView.latDist, MyGLSurfaceView.longDist, MyGLSurfaceView.altDist);
            }
        });
    }

    public void passDistances(float[][] distances) {
        mRenderer.flightDists = distances;
    }

    //equivalent to above implementation, but accesses instance variables directly. I'm not sure if this will work.
    public void setRendererPosSingleThread(float latDist, float longDist, float altDist) {
        this.latDist = latDist;
        this.longDist = longDist;
        this.altDist = altDist;
        Log.i("GPS Setup Status: ", "Setting Single Thread GPS Position to: " + this.latDist + " " + this.longDist + " " + this.altDist);
        mRenderer.setPos(latDist, longDist, altDist);
    }
}
