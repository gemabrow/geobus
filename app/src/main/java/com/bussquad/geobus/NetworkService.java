package com.bussquad.geobus;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

/**
 * Created by Jose on 2/13/2016.
 */
public class NetworkService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private Handler handler = new Handler();
    private String tString = "";
    private Toast toast;
    private Boolean mapActivityActive = false;


    public class LocalBinder extends Binder {
        NetworkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NetworkService.this;
        }
    }





    public NetworkService (){

    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
//        service = this;
        try{
            mapActivityActive = intent.getBooleanExtra("MAPACTIVITY",false);
            System.out.println(mapActivityActive);
        }catch (Exception ex){
            // empty
        }
        Toast.makeText(this, "Network Service Started", Toast.LENGTH_LONG).show();
        handler.postDelayed(getCoordinates, 5000);

        return START_STICKY;

    }


    private final Runnable getCoordinates = new Runnable() {

        @Override
        public void run() {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            //if data connection exists, fetch bus locations
            if (NetworkUtil.isConnected(context) && !tString.equals("connecting....")) {
                NetworkActivity networkActivity = new NetworkActivity(context);
                networkActivity.load();
            } else if (!tString.equals("no connection.")) {
                tString = "no connection";
                duration = Toast.LENGTH_LONG;
            }
//            //if no bus markers have been retrieved, display toast
//            if (!tString.equals("no connection.")) {
//                if (busMarkers.isEmpty() || tString.equals("no connection")) {
//                    toast = Toast.makeText(context, tString, duration);
//                    toast.show();
//                    tString = tString + ".";
//                }
//                locationHandler.postDelayed(this, MARKER_UPDATE_INTERVAL);
//            }
            if(mapActivityActive || isServiceRunning(NotificationService.class )){
                System.out.println("network service is running");
                handler.postDelayed(getCoordinates, 5000);
            }else{
                // if mapActivity is not running and if there is no notifications pending
                // stop getting map coordinates
                stopSelf();
                System.out.println("stopping network service");
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "NetworkService Destroyed", Toast.LENGTH_LONG).show();
        handler.removeCallbacks(getCoordinates);
    }



    public void mapActivityStopped(){
        mapActivityActive =  false;
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}
