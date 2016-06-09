package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 2/16/2016.
 */
public class MapObject {


    private  double latitude = 0.0;
    private  double longitude = 0.0;
    private  LatLng location;
    private  String name;
    private int objectID = -1;


    private int cardViewType = 0;
    private String mainInfo;
    private String additionalInfo;
    private String cardSubText1;
    private String cardSubText2;
    private String fullViewText1;
    private String thumbNailUrl;
    private String fullViewImageUrl;


    private int imageResource = R.drawable.ic_map_black_24dp;;
    private int mapImgResource = R.drawable.ic_blank_icon;





    MapObject(){

    }



    public MapObject( int id, String name, double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.objectID = id;
    }



    public MapObject(double latitude, double longitude, String name){
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }


    MapObject(double latitude, double longitude,String name,String mainInfo,String additionalInfo){
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
        this.name = name;
        this.mainInfo = mainInfo;
        this.additionalInfo = additionalInfo;

    }



    public int getObjectID() {
        return objectID;
    }




    public void setObjectID(int objectID) {
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




    public void setCardSubText1(String sampleText){
        this.cardSubText1 = sampleText;
    }



    public String getCardSubText1(){
        return this.cardSubText1;
    }



    public void setCardSubText2(String sampleText){
        this.cardSubText2 = sampleText;
    }



    public String getCardSubText2(){
        return this.cardSubText2;
    }




    public void setFullViewText1(String fullViewText1){
        this.fullViewText1 = fullViewText1;
    }



    public String getFullViewText1(){
        return this.fullViewText1;
    }




    public int getCardViewType() {
        return cardViewType;
    }




    public void setCardViewType(int cardViewType) {
        this.cardViewType = cardViewType;
    }



    public String getThumbNailUrl() {
        return thumbNailUrl;
    }




    public void setThumbNailUrl(String thumbNailUrl) {
        this.thumbNailUrl = thumbNailUrl;
    }




    public String getFullViewImageUrl() {
        return fullViewImageUrl;
    }




    public void setFullViewImageUrl(String fullViewImageUrl) {
        this.fullViewImageUrl = fullViewImageUrl;
    }



    // get the marker icon to show on the map
    public int getMapImgResource() {
        return mapImgResource;
    }



    // set the marker icon to show on the map
    public void setMapImgResource(int mapImgResource) {
        this.mapImgResource = mapImgResource;
    }



}
