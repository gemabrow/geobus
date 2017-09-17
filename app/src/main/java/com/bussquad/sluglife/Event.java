package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 4/10/2016.
 */
public class Event extends MapObject {

    String locationDetail = "No location information available";
    private String eventWebUrl;
    String event_date = "Information not available";
    String category;
    double cost = 0.00;


    public Event(String eventName,
                 String imgURL,
                 String date, double latitude, double longitude){

        this.setName(eventName);
        this.event_date = date;
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setImageResource(R.drawable.ic_event_black_24dp);
        this.setCardViewType(1);
        this.setThumbNailUrl(imgURL);
        this.setMapImgResource(R.drawable.ic_event_marker);

    }


    public Event(String eventName, String teaser, double latitude, double longitude,
                  String date, String weblink, String thumbnailLink,String largeImgUrl,
                 String description,double cost,String locationDetail){


        this.setName(eventName);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setImageResource(R.drawable.ic_event_black_24dp);
        this.setThumbNailUrl(thumbnailLink);
        this.setFullViewImageUrl(largeImgUrl);
        this.setCardSubText1(teaser);
        this.setCardViewType(1);
        this.setFullViewText1(description);
        this.eventWebUrl = weblink;
        this.event_date = date;
        this.cost = cost;
        this.locationDetail = locationDetail;
    }



    // sets the facility or other campus locaiton that the event takes place
    public void setLocationName(String locationDetail){
        this.locationDetail = locationDetail;
    }




    public String getLocationDetail(){
        return this.locationDetail;
    }





    // sets the event date;
    public void setEventDate(String event_date){
        this.event_date = event_date;
    }




    public String getEventDate(){
        return this.event_date;
    }

    // sets the card type of the event object to 1, this makes it possible to call a specific
    // card view for events


    // sets the link with more information about the event
    public void setEventWebUrl(String eventWebUrlt){
        this.eventWebUrl = eventWebUrlt;
    }




    // returns a link to the events main page
    public String getEventWebUrl(){
        return this.eventWebUrl;
    }




    // returns the cost of attending the event
    public double getAdmissionCost(){
        return this.cost;
    }




    // sets the cost of admission for the event
    public void setAdmissionCost(double cost){
        this.cost = cost;
    }









}
