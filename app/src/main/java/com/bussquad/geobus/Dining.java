package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Jose on 3/22/2016.
 */
public class Dining extends MapObject{

    private String name;
    private int resourceId = R.drawable.ic_local_dining_black_24dp;
    private double latitude;
    private double longitude;
    private String openTime = "0:00 AM";
    private String closingTime = "0:00 PM";
    private LatLng location;



    public Dining(String name, double latitude, double longitude){

        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
    }



    // returns the name of the restaurant
    @Override
    public String getName() {
        return name;
    }




    // sets the name of the cafe/dining hall
    @Override
    public void setName(String name) {
        this.name = name;
    }




    // returns the latitude of the cafe
    @Override
    public double getLatitude() {
        return latitude;
    }




    // sets the latitude of the cafe/dining hall
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    // returns the longitude of the dining locaiton
    @Override
    public double getLongitude() {
        return longitude;
    }




    // sets the longitude of the dining location
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    // returns the location of the dining hall
    @Override
    public LatLng getLocation() {
        return location;
    }




    @Override
    public void setLocation(LatLng location) {
        this.location = location;
    }



    @Override
    public int getImageResource() {
        return resourceId;
    }




    @Override
    public void setImageResource(int imageResource) {
        this.resourceId =  imageResource;
    }




    @Override
    public String getAdditionalInfo() {
        return super.getAdditionalInfo();
    }




    @Override
    public String getMainInfo() {
        return (this.openTime + " - " + this.closingTime) ;
    }
}
