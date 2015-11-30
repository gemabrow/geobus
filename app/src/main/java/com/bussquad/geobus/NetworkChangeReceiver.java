package com.bussquad.geobus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * NetworkChangeReceiver, on network changes leading to momentary disconnects
 * stops all background data
 * Created by gerald on 11/8/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Context mContext = context;
        int status = NetworkUtil.getConnectivityStatus(context);

        Log.e("Receiver ", "NetworkStatus = " + Integer.toString(status));

        if (status == 0) {
            Log.e("Receiver ", "not connected");
            MapsActivity.activity.stopBackgroundData();
        } else {
            Log.e("Receiver ", "connected to internet");
            MapsActivity.activity.startBackgroundData();
        }

    }
}