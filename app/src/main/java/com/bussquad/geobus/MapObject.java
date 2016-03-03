package com.bussquad.geobus;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 2/16/2016.
 */
public class MapObject {


    public  double lat;
    public  double lng;
    public  LatLng location;

    MapObject(){

    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public LatLng getLocation() {
        return location;
    }
}
