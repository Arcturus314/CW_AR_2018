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

    public LocationManager locationManager;
    public LocationListener locationListener;

    public TrackObject userCurr;    //the current position of the User
    public TrackObject userRef; //the initial position of the User
    public TrackObject tree;

    public boolean userLocationSet = false;

    public final float requiredAccuracy = 20; //estimate must be 20m accurate to begin tracking

    public void onCreate(Bundle savedInstanceState) {
        //restoring previous instance
        super.onCreate(savedInstanceState);

        //creating the renderer and surfaceview
        mRenderer = new MyGLRenderer(this);
        mSurfaceView = new MyGLSurfaceView(this);
        //setting the mSurfaceView renderer to the instantiated mRenderer object
        mSurfaceView.start(mRenderer);
        setContentView(mSurfaceView);
        //mSurfaceView.setRendererPos(0,0,0);


        //----------------------------
        //      LOCATION TASKS
        //----------------------------

        tree = new TrackObject(-117.7076, 34.1056, 16 + 369); //base of tree is 369 meters off the ground
        //and a reference to the tree switch
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                //setting initial user location
                if (userLocationSet == false) {
                    if (location.getAccuracy() < requiredAccuracy) {
                        Log.i("GPS Setup Status: ", "at required accuracy. Current accuracy: " + location.getAccuracy());
                        userRef = new TrackObject(location.getLongitude(), location.getLatitude(), location.getAltitude());
                        userCurr = new TrackObject(location.getLongitude(), location.getLatitude(), location.getAltitude());
                        userLocationSet = true;
                    } else {
                        Log.i("GPS Setup Status: ", "not yet at required accuracy. Current accuracy: " + location.getAccuracy());
                    }
                }
                //otherwise update user location
                else {
                    //in the case that the position has already been set, we update position of the User object and find new x,y,z position change
                    userCurr.setPos(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    //TODO: this experimental code needs to be returned to normal
                    Log.i("GPS Setup Status: ", "Setting Single Thread GPS Position (raw) to: " + location.getLongitude() + " " + location.getLatitude() + " " + location.getAltitude());
                    final float altDist = Math.abs((float) userCurr.getDistAlt(tree.getAltitude())/100);
                    final float longDist = Math.abs((float) userCurr.getDistLon(tree.getLongitude())/100);
                    final float latDist = Math.abs((float) userCurr.getDistLat(tree.getLatitude())/100);
                    Log.i("GPS Setup Status: ", "Calculated GPS values (long, lat, alt): " + longDist + " " + latDist + " " + altDist);
                    //mRenderer.setPos(.004f, 0.67f, 0f);
                    //mSurfaceView.setRendererPos(.004f, 0.67f, 0f);
                    //mSurfaceView.setRendererPosSingleThread(1f, 2.5f, 0.3f);
                    //mSurfaceView.setRendererPosSingleThread(latDist, longDist, altDist);
                    //mSurfaceView.setRendererPos(latDist, longDist, altDist);

                   // mSurfaceView.setRendererPos(1f, 2.5f, 0.3f);
                }
            }

            //required methods for LocationListener interface
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {//should request the permission here
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

}
