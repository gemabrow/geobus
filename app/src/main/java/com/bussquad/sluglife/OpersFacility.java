package com.bussquad.sluglife;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jose on 5/8/2016.
 */
public class OpersFacility extends MapObject {


    private String pep_count = "0";
    private String lastUpdated;
    private String description;



    public OpersFacility(String id, String name, String description, String pep_count, String lastUpdated, String imgUrl, double latitude, double longitude){
        this.setObjectID(id);
        this.setMapImgResource(R.drawable.ic_blank_icon);
        this.description = description;
        this.pep_count = pep_count;
        this.lastUpdated = lastUpdated;
        this.setLatitude(latitude);
        this.setLongitude(longitude);
        this.setLocation(new LatLng(latitude,longitude));
        this.setFullViewImageUrl(imgUrl);
        this.setName(name);
        this.setCardViewType(2);
    }


    @Override
    public void setCardSubText1(String sampleText) {

    }




    // returns the number of people in the Opers Facility
    public String getPep_count() {
        return pep_count;
    }




    // sets the number of people in the Opers Facility
    public void setPep_count(String pep_count) {
        this.pep_count = pep_count;
    }




    // returns the last time the count was updated for the opers  facility
    public String getLastUpdated() {
        return lastUpdated;
    }




    // sets the time the opers facility was last updated
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }




    // returns the description of the facility
    public String getDescription() {
        return description;
    }




    // sets a description of the facility
    public void setDescription(String description) {
        this.description = description;
    }




    @Override
    public String getCardSubText1() {
        if(pep_count.matches("-?\\d+(\\.\\d+)?")){
            return ("Total people count: " + pep_count);
        } else if (pep_count.equalsIgnoreCase("Event")){
            return "Event is currenrly Taking place";
        }

        return pep_count;
    }


    @Override
    public String getCardSubText2() {
        return "Last updated " + this.lastUpdated;
    }


    @Override
    public String getAdditionalInfo() {

        if(pep_count.matches("-?\\d+(\\.\\d+)?")){
            if(pep_count.length() < 2){
                return "0"+pep_count;
            } else {
                return pep_count;
            }
        } else if (pep_count.equalsIgnoreCase("Event")){
            return "Event is currenrly Taking place";
        }

        return pep_count;
    }


}
