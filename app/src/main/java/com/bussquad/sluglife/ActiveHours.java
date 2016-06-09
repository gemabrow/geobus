package com.bussquad.sluglife;

/**
 * Created by Jose on 3/22/2016.
 */
public class ActiveHours {

    private String weekDay;
    private String openTime;
    private String closeTime;


    ActiveHours (String weekDay, String openTime, String closeTime){

        this.weekDay = weekDay;
        this.openTime = openTime;
        this.closeTime = closeTime;

    }



    public void formatTime(){



    }



    // returns the day of the week
    public String getWeekDay() {
        return weekDay;
    }




    // rsets the week day
    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }




    // gets the start time of the facility/business
    public String getOpenTime() {
        return openTime;
    }





    // sets the start time of the facility
    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }




    // gets the close time of the facility
    public String getCloseTime() {
        return closeTime;
    }





    // sets the close time of the facility
    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}
