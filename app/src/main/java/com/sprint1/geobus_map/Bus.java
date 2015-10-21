package com.sprint1.geobus_map;

/**
 * Created by Jose on 10/21/2015.
 */
public class Bus {
    public final double lat;
    public final double lng;
    public final int timestamp;
    public final String route;
    public final int bus_id;

    Bus(double lat, double lng, int timestamp, String route, int bus_id)
    {
        this.lat = lat;
        this.lng = lng;
        this.timestamp =  timestamp;
        this.route = route;
        this.bus_id = bus_id;
    }


    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getRoute() {
        return route;
    }

    public int getBus_id() {
        return bus_id;
    }


    public void printBus(){
        System.out.println("Bus ID: " + this.bus_id + " Current location: Latitude " + this.lat + " Longitude: " + this.lng);
        System.out.println("Time Stamp: " + this.timestamp + " Route: " + this.route);
    }


}
