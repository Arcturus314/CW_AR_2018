package com.example.kavehpezeshki.instrumentationar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.example.kavehpezeshki.instrumentationar.GetPage;

//imports related to GPS
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.location.LocationManager;
import android.location.LocationListener;

import java.util.Arrays;


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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final float TREE_LAT = 34.105605f;
    private static final float TREE_LON = -117.707557f;
    private static final float TREE_ALT = 385f;


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

//        tree = new TrackObject(-117.711164, 34.106412, 16 + 369); //base of tree is 369 meters off the ground
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
                        //Log.i("GPS Setup Status: ", "not yet at required accuracy. Current accuracy: " + location.getAccuracy());
                    }
                }
                //otherwise update user location
                else {
                    //in the case that the position has already been set, we update position of the User object and find new x,y,z position change
                    //userCurr.setPos(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    userCurr.setPos(location.getLongitude(), location.getLatitude(), location.getAltitude());
                    //TODO: this experimental code needs to be returned to normal
                    //Log.i("GPS Setup Status: ", "Setting Single Thread GPS Position (raw) to: " + location.getLongitude() + " " + location.getLatitude() + " " + location.getAltitude());
                    //final float altDist = (float) userCurr.getDistAlt(tree.getAltitude())/10;
                    //final float longDist = (float) userCurr.getDistLon(tree.getLongitude())/10;
                    //final float latDist = (float) userCurr.getDistLat(tree.getLatitude())/10;
                    //Log.i("GPS Setup Status: ", "Calculated GPS values (long, lat, alt): " + longDist + " " + latDist + " " + altDist);
                    // change this to use actual flight data
                    /*
                        Given a String representation of a flights webpage given in standard form, returns a String representation of the webpage formatted as follows:
                        [
                            [Flight, Alt, Speed, Heading, Lat, Long, Sig, Msgs],
                            [Flight, Alt, Speed, Heading, Lat, Long, Sig, Msgs],
                            ...
                        ]
                    */

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String[][] flightData = GetPage.getFlights(GetPage.getWebPage(GetPage.flightDataUrl));
                                //String [][] flightData = {{"tree", "385", "very slow", "idk", "34.105605", "-117.707557", "garbage", "pls work"}};
                                //Log.i("Flight data:", Arrays.deepToString(flightData));
                                float[][] flightDists = new float[flightData.length + 1][3];
                                for (int i = 0; i < flightData.length; i++) {
                                    if (!flightData[i][1].isEmpty() && !flightData[i][4].isEmpty() && !flightData[i][5].isEmpty() && userLocationSet) {
                                        float lat = Float.parseFloat(flightData[i][4]);
                                        float lon = Float.parseFloat(flightData[i][5]);
                                        float alt = Float.parseFloat(flightData[i][1]);
                                        //Log.i("coords: :", " " + lat + " " + lon + " " + alt);
                                        if (!(lat == 0f && lon == 0 && alt == 0f)) {
                                            flightDists[i][0] = (float) userCurr.getDistLat(lat) / 500;
                                            flightDists[i][1] = (float) userCurr.getDistLon(lon) / 500;
                                            flightDists[i][2] = (float) userCurr.getDistAlt(alt) / 500;
                                        }
                                    }
                                }
                                flightDists[flightData.length][0] = (float) userCurr.getDistLat(TREE_LAT) / 10;
                                flightDists[flightData.length][1] = (float) userCurr.getDistLon(TREE_LON) / 10;
                                flightDists[flightData.length][2] = (float) userCurr.getDistAlt(TREE_ALT) / 10;
                                mSurfaceView.passDistances(flightDists);
                                Log.i("Flight dists:", Arrays.deepToString(flightDists));
                            } catch (Exception e) {
                                Log.i("flight exception: ", e.toString());
                            }
                        }
                    });

                    thread.start();
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
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


    }
}
