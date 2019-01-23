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

    private GLSurfaceView mGLView;
    private MyGLRenderer mRenderer;

    public LocationManager locationManager;
    public LocationListener locationListener;

    public TrackObject user;    //the current position of the User
    public TrackObject userRef; //the initial position of the User
    public TrackObject tree;
    public boolean userLocationSet = false;

    public double latitude;
    public double longitude;
    public double altitude;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create a GLSurfaceView instance and
        // set it as the ContentView for this Activity
        mRenderer = new MyGLRenderer(this);
        mGLView = new MyGLSurfaceView(this);
        mGLView.setRenderer(mRenderer);
        setContentView(mGLView);

        mRenderer.setGPSDist(-1f,-1f,-1f);

        tree = new TrackObject(-117.7076, 34.1056, 16+369); //base of tree is 369 meters off the ground
        //and a reference to the tree switch
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {


            public void onLocationChanged(Location location) {


                String latitudeStr = Double.toString(location.getLatitude());
                String longitudeStr = Double.toString(location.getLongitude());
                String heightStr    = Double.toString(location.getAltitude());

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                altitude = location.getAltitude();

                /*
                //unused
                changeAltText(height);
                changeLongText(longitude);
                changeLatText(latitude);
                */

                //setting position of the user after acquiring initial location
                if (userLocationSet == false) {
                    userRef = new TrackObject(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    user = new TrackObject(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    userLocationSet = true;
                }
                else {
                    //in the case that the position has already been set, we update position of the User object and find new x,y,z position change
                    user.setPos(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    float altDist = (float) user.getDistAlt(tree.getAltitude());
                    float longDist = (float) user.getDistLon(tree.getLongitude());
                    float latDist = (float) user.getDistLat(tree.getLatitude());
                    mRenderer.setGPSDist(latDist, longDist, altDist);
                    }
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}

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