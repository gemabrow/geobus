package com.bussquad.geobus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// sends a notification when a user creates a notification reminder for a bus stop
public class NotificationService extends Service {

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    // notifies user when there is a bus a the specifed location
    public void NotifyUser(){

    }
}
