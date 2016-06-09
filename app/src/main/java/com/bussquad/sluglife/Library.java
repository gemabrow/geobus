package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 3/22/2016.
 */

/*

    This classes is used to manage variables related to Library object
 */


public class Library extends MapObject {

    private String openTime = "0:00 AM";
    private String closingTime = "0:00 PM";
    int studyRooms;


    public Library(String libName, double latitude, double longitude){

        this.setName(libName);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setImageResource(R.drawable.ic_local_library_black_24dp);
        this.setMapImgResource(R.drawable.ic_library_marker);

    }







    // returns the number of study rooms at the librart
    public int getStudyRooms() {
        return studyRooms;
    }



    // sets the number of study rooms avaialble at the library
    public void setStudyRooms(int studyRooms) {
        this.studyRooms = studyRooms;
    }






    @Override
    public String getAdditionalInfo() {
        return "StudyRooms available: 2";
    }




    @Override
    public String getCardSubText1() {
        return (this.openTime + " - " + this.closingTime) ;
    }
}
