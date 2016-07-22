package com.bussquad.sluglife;

import android.support.v7.widget.MenuItemHoverListener;

/**
 * Created by Jose on 7/18/2016.
 */
public class Time {

    int day;
    int hour;
    int minute;
    int seconds;
    int millaseconds;

    long totalTime = 0;


    private static int DAYSTOHOURS = 24;
    private static int HOURTOSECONDS = 3600;
    private static int HOURSTOMINUTES = 60;
    private static int MINUTESTOSECONDS = 60;
    private static int SECONDSTOMILLA = 1000;


    Time (int hours, int minutes,int seconds ){
        this.day = 0;
        this.hour = hours;
        this.minute = minutes;
        this.seconds = seconds;
        this.millaseconds = 0;
        setTotalTime();

    }


    Time(int hour, int minute, int seconds, int millaseconds){
        this.day = 0;
        this.hour = hour;
        this.minute = minute;
        this.seconds = seconds;
        this.millaseconds = millaseconds;
        setTotalTime();
    }



    Time(int day, int hour, int minute, int seconds, int millaseconds){
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.seconds = seconds;
        this.millaseconds = millaseconds;
        setTotalTime();
    }



    // compare the time
    public int compareTo(Time otherTime){
        long t1 = getTimeInMillaSec(this);
        long t2 = getTimeInMillaSec(this);

        if(t1 == t2){
            return 0;
        } else if (t1 < t2){
            return -1;
        } else {
            return 1;
        }
    }



    // compare the time
    public int compareToInMillaSec(long t2){
        if(totalTime == t2){
            return 0;
        } else if (totalTime < t2){
            return -1;
        } else {
            return 1;
        }
    }


    public void setTotalTime(){

        long totalseconds = 0;

        totalseconds += this.day * DAYSTOHOURS * HOURTOSECONDS * SECONDSTOMILLA;
        totalseconds += this.hour * HOURTOSECONDS *  SECONDSTOMILLA;
        totalseconds += this.minute * MINUTESTOSECONDS * SECONDSTOMILLA;
        totalseconds += this.seconds * SECONDSTOMILLA;
        totalseconds += this.millaseconds;

        this.totalTime = totalseconds;
        System.out.println(totalTime);
    }




    public long getTimeInMillaSec(Time time){
        long millaseconds = 0;

        millaseconds += time.day * DAYSTOHOURS * HOURTOSECONDS * SECONDSTOMILLA;
        millaseconds += time.hour * HOURTOSECONDS *  SECONDSTOMILLA;
        millaseconds += time.minute * MINUTESTOSECONDS * SECONDSTOMILLA;
        millaseconds += time.seconds * SECONDSTOMILLA;
        millaseconds += time.millaseconds;

        return millaseconds;
    }






}
