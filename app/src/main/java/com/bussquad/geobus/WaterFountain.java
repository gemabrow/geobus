package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 3/22/2016.
 */
public class WaterFountain {

    private String facilityName;
    private Boolean bottleRefill = false;
    private double latitude;
    private double longitude;
    private LatLng location;





    WaterFountain (String facilityName, double latitude, double longitude){

        this.facilityName = facilityName;
        this.latitude = latitude;
        this.longitude = longitude;
        location = new LatLng(latitude,longitude);
    }




    // get the name of the facility or building that the bike rack is located at or closest to
    public String getFacilityName() {
        return facilityName;
    }




    // set the facility or building that the bike rack is closees to
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }




    // checks whether the water fountain has a bottle refil station, returns true if it does
    // otherwise its false
    public Boolean getBottleRefill() {
        return bottleRefill;
    }




    // set whether the water fountain has a bottle refill station, set true if it does otherwise
    // by default its false
    public void setBottleRefill(Boolean bottleRefill) {
        this.bottleRefill = bottleRefill;
    }




    // gets the latitude of the bike rack
    public double getLatitude() {
        return latitude;
    }




    // sets the latitude of the bike rack
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    // gets the longitude of the bike rack
    public double getLongitude() {
        return longitude;
    }




    // sets the longitude of the bike rack
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    // gets the location of the bike rack
    public LatLng getLocation() {
        return location;
    }




    // sets the location of the bike rack
    public void setLocation(LatLng location) {
        this.location = location;
    }








}
