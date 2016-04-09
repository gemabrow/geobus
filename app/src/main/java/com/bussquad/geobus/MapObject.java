package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 2/16/2016.
 */
public class MapObject {


    private  double latitude;
    private  double longitude;
    private  LatLng location;
    private  String name;
    private String objectID = "-1";
    private String mainInfo;
    private String additionalInfo;
    private int imageResource;

    MapObject(){

    }




    public MapObject(double latitude, double longitude, String name){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.mainInfo = "No information provided";
        this.additionalInfo = " No additional info provided";
    }


    MapObject(double latitude, double longitude,String name,String mainInfo,String additionalInfo){
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
        this.name = name;
        this.mainInfo = mainInfo;
        this.additionalInfo = additionalInfo;

    }



    public String getObjectID() {
        return objectID;
    }




    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }




    public double getLatitude() {
        return latitude;
    }




    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    public double getLongitude() {
        return longitude;
    }




    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    public LatLng getLocation() {
        return location;
    }




    public void setLocation(LatLng location) {
        this.location = location;
    }




    public String getName() {
        return name;
    }




    public void setName(String name) {
        this.name = name;
    }




    public String getMainInfo() {
        return mainInfo;
    }




    public void setMainInfo(String mainInfo) {
        this.mainInfo = mainInfo;
    }




    public String getAdditionalInfo() {
        return additionalInfo;
    }




    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }




    public int getImageResource() {
        return imageResource;
    }




    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }




    public LatLng getLatLng(){
        return location;
    }



    public void setLatlng(LatLng setLocation){
        this.location = setLocation;
        this.latitude = setLocation.latitude;
        this.longitude = setLocation.longitude;

    }
}
