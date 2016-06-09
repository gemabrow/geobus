package com.bussquad.sluglife;

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
    // used to store busses that stop at the bus stop location
    private ArrayList<String> busses;
    private ArrayList<BusStopSchedule> schedules;




    // constructor
    public BusStop(int id, String name,double latitude, double longitude) {
        this.setObjectID(id);
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setImageResource(R.drawable.ic_directions_bus_black_24dp);
        this.setMapImgResource(R.drawable.ic_busstop_marker);
    }



    // constructor
    public BusStop(String name, double latitude, double longitude, ArrayList<String> busses,int id) {
        this.setObjectID(id);
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.busses =  new ArrayList<>(busses);
        this.setImageResource(R.drawable.ic_directions_bus_black_24dp);
        this.setMapImgResource(R.drawable.ic_busstop_marker);
    }




    // set the Metro schedule for the bus
    public void setBusStopSchedule(ArrayList<BusStopSchedule> setSchedules) {
        this.schedules = new ArrayList<>(setSchedules);
    }



    // returns the schedule of the bus
    public ArrayList<BusStopSchedule> getBusStopSchedule() {
        return this.schedules;
    }




    // returns brief information about the bus stop
    @Override
    public String getAdditionalInfo() {
        return getEtaNextBus();
    }




    // set the information about the bus stop
    @Override
    public void setAdditionalInfo(String additionalInfo) {
    }




    // returns the next bus that will be arriving at the bus stop
    @Override
    public String getCardSubText1() {
        return super.getCardSubText1();
    }



    // returns the Eta of the next bus that will be arriving at the bus stop
    @Override
    public String getCardSubText2() {
        return super.getCardSubText2();
    }




    // set the card type to use
    @Override
    public void setCardViewType(int cardViewType) {
        super.setCardViewType(cardViewType);
    }





    public String getEtaNextBus(){
        return "Next Bus: 10";
    }
    // stores a list of busses that stop at this bus stop
    public ArrayList<String> getBusses() {
        return busses;
    }






}
