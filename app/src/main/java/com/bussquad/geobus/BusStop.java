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

    public final static int BUSSTOP_CLUSTERGROUP = 10;
    //stores the name of the bus stop
    private String title;
    private String cardinalDirc;
    private int busStopID = -1;

    private double latitude;
    private double longitude;
    private LatLng location;


    // used to store busses that stop at the bus stop location
    private ArrayList<String> busses;
    private ArrayList<BusStopSchedule> schedules;




    // constructor
    public BusStop(String title, double latitude, double longitude, ArrayList<String> busses,int id) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
        this.busses =  new ArrayList<>(busses);
        this.busStopID = id;
    }




    // getters, returns bus stop name
    public String getTitle() {
        return title;
    }




    // sets the name of the bus stop
    public void setTitle(String title) {
        this.title = title;
    }




    public void setBusStopId(int setBusStopID) {
        this.busStopID = setBusStopID;
    }




    // set Bus Stop schedules for this specific bus stop location
    public void setBusStopSchedule(ArrayList<BusStopSchedule> setSchedules) {
        this.schedules = new ArrayList<>(setSchedules);
    }




    public ArrayList<BusStopSchedule> getBusStopSchedules() {
        return this.schedules;
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




    public LatLng getLocation(){
        return this.location;
    }
    // returns the id of the busStop
    public int getBusStopID(){
        System.out.println("busStopId" + this.busStopID);
        return  this.busStopID;
    }




    public boolean hasBus(String bus) {
        return this.busses.contains(bus);
    }



    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }




    // stores a list of busses that stop at this bus stop
    public ArrayList<String> getBusses() {
        return busses;
    }




    // if there has not been a bus stop id assigned to this bus, then it will return false
    // otherwise it will return true
    public boolean hasId(){
        return (this.busStopID >= 0);
    }

}
