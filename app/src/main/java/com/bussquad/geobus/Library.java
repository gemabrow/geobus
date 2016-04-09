package com.bussquad.geobus;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 3/22/2016.
 */

/*

    This classes is used to manage variables related to Library object
 */


public class Library extends MapObject {

    String libName = "No Name Available";
    private int resourceId = R.drawable.ic_local_library_black_24dp;
    private String openTime = "0:00 AM";
    private String closingTime = "0:00 PM";
    double latitude;
    double longitude;
    LatLng location;
    int studyRooms;


    public Library(String libName, double latitude, double longitude){

        this.libName = libName;
        this.latitude = latitude;
        this.longitude = longitude;
        location = new LatLng(latitude,longitude);

    }




    // Returns the name of the Library, if there is no name assigned it returns No Name Available;
    @Override
    public String getName() {
        return libName;
    }



    // sets the name of the library
    @Override
    public void setName(String libName) {
        this.libName = libName;
    }




    // returns the latitude of the Library
    @Override
    public double getLatitude() {
        return latitude;
    }




    // sets the latitude of the Library
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    // returns the longitude of the Library
    public double getLongitude() {
        return longitude;
    }




    // sets the lonitude of the Library
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    // returns the location of the Library
    @Override
    public LatLng getLocation() {
        return location;
    }




    // sets the location of the library
    @Override
    public void setLocation(LatLng location) {
        this.location = location;
    }



    // returns the number of study rooms at the librart
    public int getStudyRooms() {
        return studyRooms;
    }



    // sets the number of study rooms avaialble at the library
    public void setStudyRooms(int studyRooms) {
        this.studyRooms = studyRooms;
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
        return "StudyRooms available: 2";
    }




    @Override
    public String getMainInfo() {
        return (this.openTime + " - " + this.closingTime) ;
    }
}
