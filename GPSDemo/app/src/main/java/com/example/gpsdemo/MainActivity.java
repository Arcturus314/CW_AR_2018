package com.example.gpsdemo;

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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public LocationManager locationManager;
    public LocationListener locationListener;

    public TrackObject user;    //the current position of the User
    public TrackObject userRef; //the initial position of the User
    public TrackObject tree;
    public boolean userLocationSet = false;

    public double latitude;
    public double longitude;
    public double altitude;

    public Switch treeSwitch;


    public void sendMessage(View view) {
        userRef.setPos(longitude, latitude, altitude);
        changeSavedText(Double.toString(userRef.getAltitude()) + "/" + Double.toString(userRef.getLongitude()) + "/" + Double.toString(userRef.getLatitude()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creating the tree object
        tree = new TrackObject(-117.7076, 34.1056, 16);
        //and a reference to the tree switch
        treeSwitch = (Switch) findViewById(R.id.treeSwitchPhysical);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {


            public void onLocationChanged(Location location) {


                String latitudeStr = Double.toString(location.getLatitude());
                String longitudeStr = Double.toString(location.getLongitude());
                String heightStr    = Double.toString(location.getAltitude());
                changeGPSText(heightStr+"/"+longitudeStr+"/"+latitudeStr);

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
                    changeAltText("0");
                    changeLongText("0");
                    changeLatText("0");
                    changeSavedText(Double.toString(userRef.getAltitude()) + "/" + Double.toString(userRef.getLongitude()) + "/" + Double.toString(userRef.getLatitude()));
                    userLocationSet = true;
                }
                else {
                    //in the case that the position has already been set, we update position of the User object and find new x,y,z position change
                    user.setPos(location.getLongitude(), location.getLatitude(), location.getAltitude());

                    if (treeSwitch.isChecked()) {
                        changeAltText(Double.toString(user.getDistAlt(tree.getAltitude())));
                        changeLongText(Double.toString(user.getDistLon(tree.getLongitude())));
                        changeLatText(Double.toString(user.getDistLat(tree.getLatitude())));
                    }
                    else {
                        changeAltText(Double.toString(user.getDistAlt(userRef.getAltitude())));
                        changeLongText(Double.toString(user.getDistLon(userRef.getLongitude())));
                        changeLatText(Double.toString(user.getDistLat(userRef.getLatitude())));
                    }
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

        setContentView(R.layout.activity_main);
    }

    void changeAltText(String newText) {
        TextView posView = (TextView) findViewById(R.id.altPos);
        //posView.setText("Altitude (m): "+newText);
        posView.setText("Altitude Change (m): "+newText);
    }
    void changeLongText(String newText) {
        TextView posView = (TextView) findViewById(R.id.longPos);
        //posView.setText("Longitude: "+newText);
        posView.setText("Longitude Change (m): "+newText);
    }
    void changeLatText(String newText) {
        TextView posView = (TextView) findViewById(R.id.latPos);
        //posView.setText("Latitude: "+newText);
        posView.setText("Latitude Change (m): "+newText);
    }
    void changeGPSText(String newText) {
        TextView posView = (TextView) findViewById(R.id.gpsData);
        posView.setText("GPS: " + newText);
    }
    void changeSavedText(String newText) {
        TextView posView = (TextView) findViewById(R.id.savedPos);
        posView.setText("Saved: " + newText);
    }
}
