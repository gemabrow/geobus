package com.bussquad.sluglife;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.maps.android.SphericalUtil;


import java.util.ArrayList;

// sends a notification when a user creates a notification reminder for a bus stop
public class NotificationService extends Service {

    private NotificationDbManger notifDb;
    private ArrayList<Bus> activeBusList =  new ArrayList<>();
    public static NotificationService service;
    private Handler handler = new Handler();
    private long[] vibratePatternSet ={ 0, 200, 500 };
    private boolean repeatVibrate =  false;
    private boolean repeatSound = false;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public NotificationService() {
    }


    public class LocalBinder extends Binder {
        NotificationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NotificationService.this;
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        notifDb =  new NotificationDbManger(getBaseContext());
        service = this;
        Toast.makeText(this, "NotificationService Started", Toast.LENGTH_LONG).show();
        handler.postDelayed(busCheck, 2000);

        return START_STICKY;

    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "NotificationService Destroyed", Toast.LENGTH_LONG).show();
        handler.removeCallbacks(busCheck);
    }




    private  Runnable busCheck = new Runnable() {

        @Override
        public void run() {

            // checks if the number of notifications is greater than 0, if there are no more
            // notifications the service stops
            int busListSize = 0;
            try{
                // if null keep busListSize = 0
                busListSize = activeBusList.size();
            }catch(Exception ex){
                // do nothing
            }

            if(notifDb.numberOfNotifications() > 0){
                int count;

                // Continues if there are any active buses
                if (busListSize != 0) {

                    // check if any of the buses are within range of the bus stops the user
                    // wants to be notified
                    for (Bus bus : activeBusList) {
                        count = 0;

                        if(notifDb.numberOfNotifications() == 0){
                            break;
                        }
                        int stopId = notifDb.getFirstRowStopID();

                        while (count < notifDb.numberOfNotifications()) {

                            // checks if the bus route is the same the route the user wants to be
                            // notified
                            if(compareBusRoute(bus, stopId)){

                                if(busInRange(bus,stopId)){

                                    sendNotification(stopId);
                                    updateNotification(stopId);
                                }
                            }

                            stopId = notifDb.getNextRowStopID();
                            count++;
                        }


                    }
                }else{
//                    Toast.makeText(getBaseContext(), "no active buses stopping notification", Toast.LENGTH_LONG).show();
//                    stopSelf();
                }
            }else{

                // if there are no more notifications left to check the service stops itself
                stopSelf();
            }

            handler.postDelayed(busCheck, 2000);
        }
    };



    // checks if the route is one that the user wants to be notified for, returns true if the
    // user wants to be notified if any route is nearby
    private boolean compareBusRoute( Bus bus,int stopID) {


        if(notifDb.getRoute(stopID).contains("Any")){
            return true;
        }else return bus.getRoute().toUpperCase().contains(notifDb.getRoute(stopID));

    }



    // compares the distance between bus current coordinates and the bus stop coordinate
    // if the bus is less then or equal to 10 meters from the bus stop this function will return
    // true
    private boolean busInRange(Bus bus, int stopID){

        int currStopId = notifDb.getCurrStop(stopID);
        double distance  = SphericalUtil.computeDistanceBetween(bus.getLocaiton(), notifDb.getLocation(currStopId));

        return distance <= 100;
    }




    // sets the list of active buses if any
    public void setBusList(ArrayList<Bus> busList){

       try{
            activeBusList = new ArrayList<>(busList);
        }catch(Exception e){
           // do nothing
       }
    }




    // updates the current notification with a decremented busStop count, updates current bus stop
    // id, updates next busStop id , updates new coordinates
    public void updateNotification(int stopID){


        int stopsLeft = (notifDb.getStopsLeft(stopID) - 1);
        String route = notifDb.getRoute(stopID);

        int nextStopID;
        int currStopID;

        System.out.println("stops left: " +  stopsLeft );
        if (stopsLeft == 0){
            notifDb.deleteNotification(stopID);
            System.out.println("after deleting : " + notifDb.numberOfNotifications());
        }else{
            currStopID = notifDb.getNextStop(stopID);
            nextStopID = notifDb.getNextBusStopId(stopID, route, stopsLeft);

            notifDb.updateCurrStop(stopID, currStopID);
            notifDb.updateNextStop(stopID, nextStopID);
            notifDb.updateStopsLeft(stopID, stopsLeft);


        }


    }




    // removes notification from the database returns an exception if there is no such
    // notification
    public void removeNotificatoin(int id){

        notifDb.deleteNotification(id);

    }




    // notifies user when there is a bus a the specifed location
    public void sendNotification(int stopID){



        String message  = "";
        String route =  notifDb.getRoute(stopID);
        int stops = notifDb.getStopsLeft(stopID);;

        if(route.equalsIgnoreCase("inner")){
            route = "Inner Loop";
        }else if(route.equalsIgnoreCase(("outer"))){
            route = "Outer Loop";
        }else{
            route = "Bus";
        }


        if (stops > 1){

            message = route + " is " +  stops + " stops away from ";
        }else{
           message = route +  " is 1 stop away from ";
        }

        boolean vibrate =  notifDb.isVibrateEnabled(stopID);
        boolean sound = notifDb.isSoundEnabled(stopID);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        System.out.println("notifyme");

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Bus Alert!");
        notification.setSmallIcon(R.drawable.geobus_icon);
        notification.setContentText(message);
        notification.setSubText(notifDb.getStopName(stopID)+"");
        notification.setTicker("New Message Alert");
        if(vibrate){
            notification.setVibrate(vibratePatternSet);
        }

        notificationManager.notify(001, notification.build());
    }
}
