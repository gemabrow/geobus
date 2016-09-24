package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 3/22/2016.
 */
public class Dining extends MapObject{

    private String openTime = "0:00 AM";
    private String closingTime = "0:00 PM";




    public Dining(String facilityid, String name, double latitude, double longitude){

        this.setObjectID(facilityid);
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setImageResource(R.drawable.ic_local_dining_black_24dp);
        this.setMapImgResource(R.drawable.ic_dining_marker);
    }


    @Override
    public String getCardSubText1() {
        return (this.openTime + " - " + this.closingTime) ;
    }


}
