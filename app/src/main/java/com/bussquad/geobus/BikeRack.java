package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jose on 3/22/2016.
 */
public class BikeRack extends MapObject{


    String rackName = "No Facility Name Available";
    private int resourceId = R.drawable.ic_directions_bike_black_24dp;
    double latitude;
    double longitude;
    LatLng location;
    int racks;

    ArrayList<String> rackTypes;


    public enum RackType {
        URACK,
        CORARACK,
        HITCHINGPOSTRACK,
        BIKESHED,
        LOCKER,
        LIGHTINGBOTRACK;
        public boolean exists = false;
    }


    public BikeRack(String rackName, double latitude, double longitude, int racks, ArrayList<String> rackTypes){

        this.rackName = rackName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude,longitude);
        this.racks = racks;

        this.rackTypes = new ArrayList<>(rackTypes);
    }




    // returns the building/ facility the the bike rack is located at
    @Override
    public String getName() {
        return rackName;
    }



    //  sets the name of the buildling/facility that the bike rack is close to
    @Override
    public void setName(String rackName) {
        this.rackName = rackName;
    }




    // gets the latitude of were the bike rack is located
    @Override
    public double getLatitude() {
        return latitude;
    }




    // sets the latitude of the bike rack
    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }




    // gets the longitude of the bike racks
    @Override
    public double getLongitude() {
        return longitude;
    }




    // sets the longitude of the bike racks
    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }




    // gets the geographic location of the bike racks
    @Override
    public LatLng getLocation() {
        return location;
    }





    // sets the location of the bike rack
    @Override
    public void setLocation(LatLng location) {
        this.location = location;
    }




    // returns the number of available racks
    public int getRacks() {
        return racks;
    }




    // sets the number of racks available
    public void setRacks(int racks) {
        this.racks = racks;
    }




    // sets the type of bike racks avaiable at the specific bike rack
    public ArrayList<String> getRackTypes() {
        return rackTypes;
    }




    // returns the type
    public void setRackTypes(ArrayList<String> rackTypes) {
        this.rackTypes = rackTypes;
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
        String accum = "";
        for (int count = 0; count < rackTypes.size();count++ ){

            accum += rackTypes.get(count);

        }

        return accum;
    }




    @Override
    public String getMainInfo() {
        return ("Spaces: " + racks) ;
    }
}
