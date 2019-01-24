package com.example.kavehpezeshki.instrumentationar;

import android.opengl.GLSurfaceView;
import android.content.Context;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer mRenderer;
    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
    }
    public void start() {
        mRenderer = new MyGLRenderer(this);
        setRenderer(mRenderer);
    }
    //sets the position of the user in OpenGL space from the origin, in terms of latitude, longitude, and altitude
    //note - the vars here are automatically set to final. Why?
    public void setRendererPos(float latDist, float longDist, float altDist) {
        queueEvent(new Runnable() {
            public void run() {
                mRenderer.setPos(latDist, longDist, altDist);
            }
        });
    }
}
