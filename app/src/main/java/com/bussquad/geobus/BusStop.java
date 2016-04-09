package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jose on 10/14/2015.
 * This is the BusStop class, I hope to add to this further on to the project
 * For now this class is used to store values from the json file with the bus stops and then later
 * can be called from to create markers.
 */
public class BusStop extends MapObject{

    public final static int BUSSTOP_CLUSTERGROUP = 10;
    //stores the name of the bus stop
    private int resourceId = R.drawable.ic_directions_bus_black_24dp;
    private String name;
    private String busStopID = "-1";
    private double latitude;
    private double longitude;
    private LatLng location;


    // used to store busses that stop at the bus stop location
    private ArrayList<String> busses;
    private ArrayList<BusStopSchedule> schedules;




    // constructor
    public BusStop(String title, double latitude, double longitude, ArrayList<String> busses,String id) {
        this.name = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
        this.busses =  new ArrayList<>(busses);
        this.busStopID = id;
    }




    // getters, returns bus stop name
    @Override
    public String getName() {
        return name;
    }




    // sets the name of the bus stop
    @Override
    public void setName(String title) {
        this.name = title;
    }




    @Override
    public String getObjectID() {
        return busStopID;
    }




    @Override
    public void setObjectID(String objectID) {
        this.busStopID = objectID;
    }




    // set Bus Stop schedules for this specific bus stop location
    public void setBusStopSchedule(ArrayList<BusStopSchedule> setSchedules) {
        this.schedules = new ArrayList<>(setSchedules);
    }




    public ArrayList<BusStopSchedule> getBusStopSchedule() {
        return this.schedules;
    }




    // returns the latitude of the bus stop
    @Override
    public double getLatitude() {
        return latitude;
    }




    // sets the latitude of the bus stop location
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    // returns  the longitude of the bus stop
    @Override
    public double getLongitude() {
        return longitude;
    }




    // sets the longitude of the bus stop
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    // returns the location of the bus stop
    @Override
    public LatLng getLocation(){
        return this.location;
    }




    // stores a list of busses that stop at this bus stop
    public ArrayList<String> getBusses() {
        return busses;
    }




    // returns the image resource id value
    @Override
    public int getImageResource() {
        return resourceId;
    }




    // sets the image resource id
    @Override
    public void setImageResource(int imageResource) {
        this.resourceId =  imageResource;
    }



}
