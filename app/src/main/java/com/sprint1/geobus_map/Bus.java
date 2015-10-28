package com.sprint1.geobus_map;

/**
 * Representation of buses. float values are used for colors as per requirements
 * by Google Map Markers
 * Created by Jose on 10/21/2015.
 */
public class Bus {
    public static final float HUE_RED = 0;
    public static final float HUE_BLUE = 240;
    public static final float HUE_YELLOW = 60;
    public static final float HUE_ORANGE = 30;
    public static final float HUE_AZURE = 210;

    public final String route;
    public final float color;
    public final double lat;
    public final double lng;
    public final int bus_id;
    public final int timestamp;

    Bus(double lat, double lng, int timestamp, String route, int bus_id)
    {
        this.lat = lat;
        this.lng = lng;
        this.timestamp =  timestamp;
        this.route = route;
        this.bus_id = bus_id;
        switch (route.toLowerCase()) {
            case "loop":
                this.color = HUE_AZURE;
                break;
            case "upper campus":
                this.color = HUE_YELLOW;
                break;
            default:
                this.color = HUE_RED;
                break;
        }
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

    public String toString() {
        return("Bus ID: " + this.bus_id + " Current location: Latitude " + this.lat
                + " Longitude: " + this.lng + "Time Stamp: " + this.timestamp
                + " Route: " + this.route);
    }
    public void printBus(){
        System.out.println("Bus ID: " + this.bus_id + " Current location: Latitude " + this.lat + " Longitude: " + this.lng);
        System.out.println("Time Stamp: " + this.timestamp + " Route: " + this.route);
    }


}
