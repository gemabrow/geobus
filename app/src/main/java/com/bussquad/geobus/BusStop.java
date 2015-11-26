package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jose on 10/14/2015.
 * This is the BusStop class, I hope to add to this further on to the project
 * For now this class is used to store values from the json file with the bus stops and then later
 * can be called from to create markers.
 */
class BusStop {

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

    // sets the name of the bus stop
    public void setTitle(String title) {
        this.title = title;
    }

    // returns the latitude of the bus stop
    public double getLatitude() {
        return latitude;
    }

    // sets the latitude of the bus stop location
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // returns  the longitude of the bus stop
    public double getLongitude() {
        return longitude;
    }

    // sets the longitude of the bus stop
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean hasBus(String bus) {
        return this.busses.contains(bus);
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public ArrayList<String> getBusses() {
        return busses;
    }

}
