package com.bussquad.geobus;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

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
    public static final float HUE_MAGENTA = 300;
    public static final int UCSC_CLUSTERGROUP = 1;
    public static final int SCMETRO_CLUSTERGROUP = 0;

    public  String route;
    public  String direction;
    public  float color;
    public  double lat;
    public  double lng;
    public  LatLng location;
    public final int bus_id;
    public  int timestamp;
    public  int clusterGroup;

    public Bus(double lat, double lng, int timestamp, String route, String direction, int bus_id){
        this.lat = lat;
        this.lng = lng;
        this.location = new LatLng(lat,lng);
        this.timestamp =  timestamp;
        this.direction = direction;
        this.bus_id = bus_id;
        switch (route.toLowerCase()) {
            case "loop":
                if (direction.equals("outer"))
                {
                    this.clusterGroup = UCSC_CLUSTERGROUP;
                    route = "Outer Loop";
                    this.color = HUE_AZURE;
                }
                else {
                    this.clusterGroup = UCSC_CLUSTERGROUP + 1;
                    route = "Inner Loop";
                    this.color = HUE_ORANGE;
                }
                break;
            case "upper campus":
                this.clusterGroup = UCSC_CLUSTERGROUP + 2;
                route = "Upper Campus";
                this.color = HUE_YELLOW;
                break;
            default:
                this.clusterGroup = SCMETRO_CLUSTERGROUP;
                this.color = HUE_RED;
                break;
        }
        this.route = route;
    }




    public Bus(int bus_id, double lat, double lng, String route){

        this.bus_id = bus_id;
        this.lat = lat;
        this.lng = lng;
        this.route = route;

        switch (route.toLowerCase()) {
            case "loop":
                this.color = HUE_AZURE;
                this.route = "LOOP";
                break;
            case "upper campus":
                this.color = HUE_YELLOW;
                this.route = "UPPER CAMPUS";
                break;
            default:
                this.color = HUE_RED;
                break;
        }
        this.route = route;
    }



    // constructor
    public Bus(int bus_id, double lat, double lng, int timestamp, String route, String direction) {
        this.lat = lat;
        this.lng = lng;
        this.location = new LatLng(lat,lng);
        this.timestamp =  timestamp;
        this.direction = direction;
        this.bus_id = bus_id;

        switch (route.toLowerCase()) {
            case "loop":
                if (direction.equals("outer"))
                {
                    this.clusterGroup = UCSC_CLUSTERGROUP;
                    route = "Outer Loop";
                    this.color = HUE_AZURE;
                }
                else {
                    this.clusterGroup = UCSC_CLUSTERGROUP + 1;
                    route = "Inner Loop";
                    this.color = HUE_ORANGE;
                }
                break;
            case "upper campus":
                this.clusterGroup = UCSC_CLUSTERGROUP + 2;
                route = "Upper Campus";
                this.color = HUE_YELLOW;
                break;
            default:
                this.clusterGroup = SCMETRO_CLUSTERGROUP;
                this.color = HUE_RED;
                break;
        }
        this.route = route;
    }




    public static int busColor(String route) {
        float cVal;
        switch (route) {
            case ("Outer Loop"):
                cVal = HUE_AZURE;
                break;
            case ("Inner Loop"):
                cVal = HUE_ORANGE;
                break;
            case ("Upper Campus"):
                cVal = HUE_YELLOW;
                break;
            case ("Loop"):
                cVal = HUE_RED;
                break;
            case("OUT OF SERVICE/SORRY"):
                cVal = HUE_MAGENTA;
                break;
            default:
                cVal = HUE_RED;
        }
        return Color.HSVToColor(new float[]{cVal, 1.0f, 1.0f});
    }




    public double getLat() {
        return lat;
    }




    public double getLng() {
        return lng;
    }



    public LatLng getLocaiton(){
        return location;
    }



    public int getTimeStamp() {
        return timestamp;
    }




    public String getRoute() {
        return route;
    }




    public int getBus_id() {
        return bus_id;
    }




    // returns the bearing angle from prevPos to current position of bus
    // type float as required by marker options
    public float updateBearing(LatLng prevPos, float prevBearing) {
        double dLim = 0.0001;
        double latDelta = (this.lat - prevPos.latitude);
        double lngDelta = (this.lng - prevPos.longitude);

        // if if the differences in coordinates are significant, update bearing
        // otherwise, return the previous bearing

        return (latDelta < dLim && lngDelta < dLim) ?
                prevBearing : (float) calcBearing(prevPos);
    }




    // trig magic to calculate bearing based on two sets of coordinates
    // returns as degrees as double
    private double calcBearing(LatLng prevPos) {
        double currLng = this.lng;
        double currLat = Math.toRadians(this.lat);
        double prevLng = prevPos.longitude;
        double prevLat = Math.toRadians(prevPos.latitude);
        double lngDelta = Math.toRadians(currLng - prevLng);
        double y = Math.sin(lngDelta) * Math.cos(currLat);
        double x = Math.cos(prevLat) * Math.sin(currLat) - Math.sin(prevLat) * Math.cos(currLat) * Math.cos(lngDelta);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }




    public String toString() {
        return("Bus ID: " + this.bus_id + " Current location: Latitude " + this.lat
                + " Longitude: " + this.lng + " Time Stamp: " + this.timestamp
                + " Route: " + this.route + " Direction: " + this.direction);
    }




    public void printBus(){
        System.out.println("Bus ID: " + this.bus_id + " Current location: Latitude " + this.lat + " Longitude: " + this.lng);
        System.out.println(" Time Stamp: " + this.timestamp + " Route: " + this.route + " Predictions: " + this.direction);
    }

}
