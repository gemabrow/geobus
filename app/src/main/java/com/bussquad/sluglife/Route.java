package com.bussquad.sluglife;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jose on 8/31/2016.
 */
public class Route {

    private String name;                                // name of the route
    private String routeNumber;                         // the number of the route
    private int id;                                  // route id
    private List<String> stopIds;                  // list of stops that the route consists of
    private int totalStops;                             // total number of stops made on this route
    private double length;                              // length of the route in ft
    private double avgTime;                             // avg time it takes to complete one whole
                                                        // pass through the route



    // constructor for Route
    public Route(int id, String name, String routeNumber, String stopIds){
        this.name = name;
        this.routeNumber = routeNumber;
        this.id = id;
        this.stopIds =  Arrays.asList(stopIds.split(","));;
    }




    // the route number is similar to the name however its typically shorter and more condensed
    public void setRouteNumber(String routeNumber){
        this.routeNumber = routeNumber;
    }




    // the name of the route, this does not necessarily have to be the same as the route id
    // @name length must not be zero or null
    public void setName(String name) {
        if(name == null || name.length() == 0){
            throw new RuntimeException("Route name cannot be null");
        }
        this.name = name;
    }




    // the id of the route
    public void setId(int id) {


        this.id = id;
    }



    // sets a list of stopids that the route consists of
    public void setStopIds(List<String> stopIds) {
        this.stopIds = stopIds;
    }




    // returns the name of the route
    public String getName() {
        return name;
    }



    // returns the id of the route
    public int getId() {
        return id;
    }




    public String getRouteNumber(){
        return routeNumber;
    }



    // returns a list of stopids associated with this route
    public List<String> getStopIds() {
        return stopIds;
    }
}
