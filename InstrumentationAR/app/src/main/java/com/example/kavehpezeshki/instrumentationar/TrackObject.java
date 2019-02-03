package com.example.kavehpezeshki.instrumentationar;

import java.lang.Math;

/*
This class represents an abstract object (eg runway, plane, tower) that will be drawn in the AR headset
 */
public class TrackObject {

    //Setting up static variables

    //radius of the Earth in m. We assume the Earth is a sphere
    private static double earthRadius = 6371000.0;

    //Setting up instance variables

    //Represents the longitude of the object, given in degrees. We define the prime meridian as 0, with East the positive direction
    private double longitude;

    //Represents the latitude of the object, given in degrees. We define the equator as 0, with North the positive direction
    private double latitude;

    //Represents the height of the object from the surface of the Earth, given in meters
    private double altitude;


    /*
    Constructor for the TrackObject class

    @param lon: longitude of the object in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat:  latitude of the object in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the object in m from the Earth's surface
     */
    public TrackObject(double lon, double lat, double alt) {
        longitude = lon;
        latitude  = lat;
        altitude  = alt;
    }

    /*
    Sets the longitude, latitude, and altitude. Pass -1 to leave values constant

    @param lon: longitude of the object in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat: latitude of the object in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the object in m from the Earth's surface

    @return: none
     */

    public void setPos(double lon, double lat, double alt) {
        if (lon != -1) { longitude = lon; };
        if (lat != -1) { latitude  = lat; };
        if (alt != -1) { altitude  = alt; };
    }

    /*
    @return longitude
    */
    public double getLongitude() {
        return longitude;
    }

    /*
    @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /*
    @return altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /*
    @param lon: longitude of the point in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat: latitude of the point in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the point in m from the Earth's surface

    @return distance in meters between this and the specified point along the vertical surface
     */
    public double getDistLat(double lat) {
        return earthRadius*(lat-latitude)*(3.1416/180.0);
    }

    /*
    @param lon: longitude of the point in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat: latitude of the point in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the point in m from the Earth's surface

    @return distance in meters between this and the specified point along the vertical surface
    */
    public double getDistLon(double lon) {
        return -1*earthRadius*(lon-longitude)*(3.1416/180.0);
    }

    /*
    @param lon: longitude of the point in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat: latitude of the point in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the point in m from the Earth's surface

    @return distance in meters between this and the specified point along the r^ direction
    */
    public double getDistAlt(double alt) {
        return alt-altitude;
    }

    /*
    @param lon: longitude of the point in degrees, with the prime meridian as 0 deg, and East the positive direction
    @param lat: latitude of the point in degrees, with the equator as 0 deg, and North the positive direction
    @param alt: altitude of the point in m from the Earth's surface

    @return distance in meters between this and the specified point
    */
    public double getTotalDistance(float lon, float lat, float alt) {
        double distLon = getDistLon(lon);
        double distLat = getDistLat(lat);
        double distAlt = getDistAlt(alt);

        return Math.sqrt(distLon*distLon + distLat*distLat + distAlt*distAlt);
    }

    public double getTotalDistance(TrackObject reference) {
        double distLon = getDistLon(reference.getLongitude());
        double distLat = getDistLat(reference.getLatitude());
        double distAlt = getDistAlt(reference.getAltitude());

        return Math.sqrt(distLon*distLon + distLat*distLat + distAlt*distAlt);
    }

}
