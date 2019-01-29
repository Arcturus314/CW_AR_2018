package com.example.kavehpezeshki.instrumentationar;

import android.opengl.GLSurfaceView;
import android.content.Context;

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
        setRenderer(mRenderer);
    }
    //sets the position of the user in OpenGL space from the origin, in terms of latitude, longitude, and altitude
    public void setRendererPos(float latDist, float longDist, float altDist) {
        this.latDist = latDist;
        this.longDist = longDist;
        this.altDist = altDist;
        queueEvent(new Runnable() {
            public void run() {
                mRenderer.setPos(MyGLSurfaceView.latDist, MyGLSurfaceView.longDist, MyGLSurfaceView.altDist);
            }
        });
    }
}
