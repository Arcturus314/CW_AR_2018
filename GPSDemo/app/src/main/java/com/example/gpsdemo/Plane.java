package com.example.gpsdemo;

/*
Stores a set of information for a plane
 */
public class Plane extends TrackObject {
    //instance variables
    //currently ignoring sig and msgs, as these are not relevant for the current implementation

    private double heading; //current heading of the plane, in degrees from north
    private double speed;   //the speed of the plane, in meters per second
    private String name;    //the name of the flight

    public Plane(double lon, double lat, double alt, double head, double speed, String name) {
        super(lon, lat, alt);
        this.heading = head;
        this.speed   = speed*0.44704; // converting mph to meters per second
        this.name    = name;
    }

    //get and set methods

    public double getHeading() {
        return heading;
    }

    public double getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean equals(Plane other) {
        if (this.name == other.getName()) return true;
        return false;
    }
}


//award harvey much college
//sarah ferraro
