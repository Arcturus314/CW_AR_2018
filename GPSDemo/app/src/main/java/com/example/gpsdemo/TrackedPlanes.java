package com.example.gpsdemo;

import java.util.ArrayList;

/*
stores all planes currently in ADS-B range
 */
public class TrackedPlanes {

    public static int distanceToTrack = 20000; //tracks planes up to 20km out
    public static int distanceToVisualize = 10000; //visualizes planes up to 10km out

    //instance variables

    //an arrayList tracks planes
    ArrayList<Plane> trackedPlanes;

    public TrackedPlanes() {
        //initialize instance variables
        trackedPlanes =  new ArrayList<Plane>();
    }

    public void addPlane(Plane plane) {
        //first check that the plane is not already in the arraylist
        int planePos = -1; //will be set to a positive integer if in the arraylist
        for(int i = 0; i < trackedPlanes.size(); i++) {
            if(trackedPlanes.get(i).equals(plane)) {
                planePos = i;
            }
        }
        if (planePos == -1) {
            trackedPlanes.add(plane);
        }
        else {
            trackedPlanes.remove(planePos);
            trackedPlanes.add(plane);
        }
    }

    public void removePlanesOutOfDistance(TrackObject userPos) {
        //iterates through the arraylist and removes all elements that are a given distance away from the user

        ArrayList<Plane> culledTrackedPlanes = new ArrayList<Plane>();

        for(int i = 0; i < trackedPlanes.size(); i++) {
            if(trackedPlanes.get(i).getTotalDistance(userPos) < distanceToTrack) {
                culledTrackedPlanes.add(trackedPlanes.get(i));
            }
        }

        trackedPlanes = culledTrackedPlanes;
    }

    public ArrayList<Plane> getPlanesInDistance(TrackObject userPos) {
        //returns an ArrayList with all planes within distanceToVisualize

        ArrayList<Plane> visualizedTrackedPlanes = new ArrayList<Plane>();

        for(int i = 0; i < trackedPlanes.size(); i++) {
            if(trackedPlanes.get(i).getTotalDistance(userPos) < distanceToVisualize) {
                visualizedTrackedPlanes.add(trackedPlanes.get(i));
            }
        }

        return visualizedTrackedPlanes;
    }

}
