package com.bussquad.sluglife;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.InterruptibleChannel;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

/**
 * Created by Jose on 7/17/2016.
 */
public class TimeHelper {


    private static int HOURTOSECONDS = 3600;
    private static int HOURSTOMINUTES = 60;
    private static int SECONDSTOMILLA = 1000;

    public TimeHelper(){

    }

    public int getClosestTimeArrayIndex(ArrayList<String> schedule){

        Time curTime = getCurrentTime();
        long tempTime = 0;
        int index = 0;
        for(int count = 0 ; count < schedule.size()- 1; count++){
            tempTime = converToMillaSeconds(schedule.get(count));

            // if it finds a time that is greater than the current time the search will
            // stop and will return the index of the closest time.
            if(curTime.compareToInMillaSec(tempTime) < 0 ){
                if(index  == 0){
                    return index;
                } else {
                    return index--;

                }

            }

            index++;
        }

        if(curTime.compareToInMillaSec(tempTime) >= 0 && index == schedule.size() -1){
            // the next bus arrives the next day
            return 0;
        }





        return index ;
    }



    public long getClosestTimeInMillaseconds(ArrayList<String> schedule){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        Time curTime = new Time(hour,minutes,seconds);
        long tempTime = 0;
        int index = 0;
        for(int count = 0 ; count < schedule.size()- 1; count++){
            tempTime = converToMillaSeconds(schedule.get(count));

            // if it finds a time that is greater than the current time the search will
            // stop and will return the index of the closest time.
            if(curTime.compareToInMillaSec(tempTime) < 0 ){
                return tempTime;

            }

            index++;
        }

        if(curTime.compareToInMillaSec(tempTime) >= 0 && index == schedule.size() -1){
            // the next bus arrives the next day
            return converToMillaSeconds(schedule.get(0));
        }





        return converToMillaSeconds(schedule.get(index));
    }


    public long converToMillaSeconds(String time){

        String splitTime[] = time.split(":");
        String time2 = "";
        long timeMilla = 0;

        int hour = Integer.valueOf(splitTime[0].trim());
        if(splitTime[1].contains("AM")){

            time2 = splitTime[1].replace("AM","").trim();
            if(hour != 12){
                timeMilla += hour * HOURTOSECONDS * SECONDSTOMILLA;
            }
        } else if (splitTime[1].contains("PM")) {

            if(hour == 12){
                timeMilla += hour * HOURTOSECONDS * SECONDSTOMILLA;
            } else {
                timeMilla += (12 + hour) * HOURTOSECONDS * SECONDSTOMILLA;
            }
            time2 = splitTime[1].replace("PM","").trim();
        }
        timeMilla += Integer.valueOf(time2) * SECONDSTOMILLA;

        return  timeMilla;
    }




    public String getTimeRemainingAsString(long millaseconds){

        Calendar cd = Calendar.getInstance();
        cd.setTimeInMillis(millaseconds);

        String time = "";

        int hour = cd.get(Calendar.HOUR);
        int minutes = cd.get(Calendar.MINUTE);
        int seconds = cd.get(Calendar.SECOND);

        if(hour != 0){
            if(hour == 1){
                time = " one hour";
                return time;
            }
            time += hour + " hours";
            if(minutes != 0){
                time += " and " + minutes + " minutes";
            }
        }
        else if(minutes != 0){
            time += minutes + " minutes ";

        } else if (seconds != 0){
            time += seconds + " seconds";
        }


        return time;
    }


    public long getReminingTime(long time){
        long timeRemaining;
        timeRemaining =  getCurrentTime().totalTime - time;
        if (timeRemaining < 0){
            return -1;  // this indicatest the time has already passed
        }
        return timeRemaining;
    }

    public Time getCurrentTime(){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        Time curTime = new Time(hour,minutes,seconds);

        return curTime;
    }



    public long getCurrentTimeInMillaseconds(){
        Date time = new Date();


        return time.getTime();
    }


}
