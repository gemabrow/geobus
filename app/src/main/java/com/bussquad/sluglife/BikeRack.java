package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Jose on 3/22/2016.
 */
public class BikeRack extends MapObject{


    String rackName = "No Facility Name Available";
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
        this.setName(rackName);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setImageResource(R.drawable.ic_directions_bike_black_24dp);
        this.racks = racks;
        this.rackTypes = new ArrayList<>(rackTypes);
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
    public String getAdditionalInfo() {
        String accum = "";
        for (int count = 0; count < rackTypes.size();count++ ){

            accum += rackTypes.get(count);

        }

        return accum;
    }


    @Override
    public String getCardSubText1() {
        return ("Spaces: " + racks);
    }




    @Override
    public String getCardSubText2() {
        String accum = "";
        for (int count = 0; count < rackTypes.size();count++ ){

            accum += rackTypes.get(count);

        }

        return accum;
    }




    @Override
    public String getMainInfo() {
        return ("Spaces: " + racks);
    }
}
