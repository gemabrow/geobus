package com.bussquad.sluglife;

import android.os.Debug;

import java.util.ArrayList;

/**
 * Created by Jose on 11/14/2015.

  This class consists of 3 private variables and an array list of String. The Tag String is used for debugging. The Id variable
  is used for identifying the bus stop the that schedule applies to. The busName is the bus number that corresponds to that the 
  schedule reflects. The Arraylist is a list of depart times.
 */

public class BusStopSchedule {

    private static String TAG = "Schedule"; // Tag just for the LogCat window
    private int id;
    private String busName;
    private ArrayList<String> departTime =  new ArrayList<String>();


    // Constructor
    public BusStopSchedule(){

    }




    // sets the name and id for schedule
    public BusStopSchedule(int id, String name) {
        this.id = id;
        this.busName = name;
    }




    public void setId(int id) {
        this.id = id;
    }




    // sets the name of the bus that stops at the specified bus stop
    public void setName(String name) {
        this.busName = name;
    }




    // adds one depart time at a time
    public void addBusDepartTime(String addDepartTime)
    {
        this.departTime.add(addDepartTime);
    }




    //  sets an array of bus depart times
    public void setBusDepartSchedule(ArrayList<String> busDepartSchedule){


        this.departTime = new ArrayList<String>(busDepartSchedule);

    }




    // returns a set of depart times
    public ArrayList<String> getBusDepartTime(){
        return this.departTime;
    }




    // returns the id for the bus schedule
    public int getId() {
        return id;
    }




    // returns the name of the bus for this schedule
    public String getName() {
        return busName;
    }


}
