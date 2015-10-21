package com.sprint1.geobus_map;

import java.util.ArrayList;

/**
 * Created by Jose on 10/14/2015.
 * This is the BusStop class, I hope to add to this further on to the project
 * For now this class is used to store values from the json file witht he bus stops and then later
 * can be called from to create markers.
 */
public class BusStop {

    //stores the name of the bus stop
    private String title;

    private double latitude;
    private double longitude;

    // used to store busses that stop at the bus stop location
    private ArrayList<String> busses;


    // constructor
    public BusStop(String title, double latitude, double longitude, ArrayList<String> busses) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.busses =  new ArrayList<String>(busses);
    }

    // getters, returns bus stop name
    public String getTitle() {
        return title;
    }

    // returns the latitude of the bus stop
    public double getLatitude() {
        return latitude;
    }

    // returns  the longitude of the bus stop
    public double getLongitude() {
        return longitude;
    }

    public ArrayList<String> getBusses(){
        return busses;
    }

    // sets the name of the bus stop
    public void setTitle(String title) {
        this.title = title;
    }

    // sets the latitude of the bus stop location
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // sets the longitude of the bus stop
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
