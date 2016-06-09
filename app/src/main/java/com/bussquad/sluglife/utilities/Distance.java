package com.bussquad.sluglife.utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * Created by Jose on 5/12/2016.
 */
public class Distance {


    // intially metric units is the default unit type that measurements will be calculated in
    private boolean usc_units = false;
    private boolean metric_units = true;
    private int earthRadius = 6371000; // meters


    // calculates the distance between two Latlng points in meters
    // @param location1   the first point
    // @param location2   the second point
    // @param speed       the speed that point 2 is moving towards point 1 in meter per second
    public double getETA(LatLng location1, LatLng location2, double speedMetric){

        // returns the distance betwen two latlngs in meter
        double distance  = SphericalUtil.computeDistanceBetween(location1, location2);
        double time = distance/speedMetric;

        return time;
    }



    // calculat3 the distance between two points seperated into latitudes and longitudes
    public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2){

        double distance = 0.0;

        return distance;
    }


    public double convertKilMeterToFeet(double kiloMeter ){
        double feet = 0.0;

        return feet;
    }


}
