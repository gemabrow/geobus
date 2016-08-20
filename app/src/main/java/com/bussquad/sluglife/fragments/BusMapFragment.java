package com.bussquad.sluglife.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bussquad.sluglife.Bus;
import com.bussquad.sluglife.BusStop;
import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.JsonFileReader;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.MainActivity;
import com.bussquad.sluglife.activity.ScheduleAcitivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class BusMapFragment extends MapFragment {

    private ArrayList<BusStop> busStops;
    private ArrayList<DataObject> dataObjects ;
    private ArrayList<Integer> cardTypeData;
    private final JsonFileReader busStopReader = new JsonFileReader();
    public BusMapFragment() {
        // Required empty public constructor
        isActivityStartable(true);
    }




    // TODO: Rename and change types and number of parameters
    public static BusMapFragment newInstance(String param1, String param2) {
        BusMapFragment fragment = new BusMapFragment();
        Bundle args = new Bundle();
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.display_object_loc, container, false);
    }

    @Override
    public void loadData(Context context) {

        InputStream in;
        // fills the array of bus stop of bus stop locations
        try {
            Log.i("BusMapFragment","loadJsonFromAssets() Reading Bus Stop from Json file");
            in = context.getAssets().open("UCSC_Westside_Bus_Stops.json");
            busStopReader.readBusStopJsonStream(in);
            busStops = new ArrayList<>(busStopReader.getBusStops());
            setDataLoadedStatus(true);

            context = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects = new ArrayList<MapObject>(busStops);

        return mapObjects;

    }




    @Override
    public Intent getMapFragmentIntent(Context context,String title,MapObject mapObject) {

        Intent mIntent = new Intent(context, ScheduleAcitivity.class);
        mIntent.putExtra("bus_stop_name",title); //Optional parameters
        mIntent.putExtra("BUSSTOPID", mapObject.getObjectID());
        mIntent.putExtra("COORDINATES", mapObject.getLatLng());
        return mIntent;
    }


    //
//    // returns the closest bus stops to the user based on the specified distance
//    public ArrayList<BusStop> getClosestStops(){
//        LatLng userLoc;
//        double distance;
//        ArrayList<BusStop> closestStops =  new ArrayList<>();
//
//        for(BusStop stop : busStops){
////             userLoc =  new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
//            userLoc =  new LatLng(37.000084,-122.062960); // testing location
//            distance = SphericalUtil.computeDistanceBetween(userLoc,stop.getLocation());
//            if(distance < 200 ){
//                closestStops.add(stop);
//                System.out.println(stop.getName());
//            }
//        }
//
////        if(closestStops)
//        getEta(closestStops.get(0));
//        return closestStops;
//    }
//
//    public void getEta(BusStop stop){
//        System.out.println("calculating eta: ");
//
//    /*    for (String route : stop.getBusses()){
//            if(activeBusses.containsKey(route)){
//                if(activeBusses.get(route).size() >0){
//                    findCLosestBus(activeBusses.get(route), stop.getObjectID());
//                }
//            }
//        }
//        */
//    }
//
//
//
//
//    // finds the closes buses given the route and bus to the bus stop
//    public void findCLosestBus(ArrayList<Bus> busses, int stopID){
//
//        int closestBus = 0;                         // stores the index of the closes bus thus far
//        double tmpD = 0.0;
//        double clstBus = 0.0;
//        int count = 0;
//        for (Bus bus : busses)        {
//            tmpD = calculateTotalDistance(bus.direction.toUpperCase(), bus.getLocaiton(),stopID);
//
//            if(tmpD >= clstBus){
//                clstBus = tmpD;
//                closestBus = count;
//            }
//            count++;
//        }
//
//        System.out.println("from bus stop: " + notifDb.getStopName(stopID));
//        System.out.println("The closest bus is: " + busses.get(closestBus).bus_id + " of route " + busses.get(closestBus).direction);
//        System.out.println("thee distance is: " + clstBus);
//
//    }
//
//
//
//
//    private double calculateTotalDistance(String route, LatLng busLocation,int stopID){
//        boolean reachedBus =  false;
//        double totalDistance = 0.0;


//        int maxCount = 0;
//        while(!reachedBus && maxCount < 32) {
//
//            // if bus is in between the bus stops measure the eta of between the bus stop to the user
//            // otherwise get its estimated eta
//            LatLng curStop = notifDb.getLocation(stopID);            // get LatLng of current stop
//            stopID  = notifDb.getNextBusStopId(stopID,route,1);             // get the next stopID
//            LatLng nextStop = notifDb.getLocation(stopID);           // get LatLng of next stop
//
//            double distbetween = SphericalUtil.computeDistanceBetween(curStop, nextStop);
//            double toBusNextStop = SphericalUtil.computeDistanceBetween(curStop, busLocation);
//            double toBusCurStop = SphericalUtil.computeDistanceBetween(nextStop, busLocation);
//
//
//            // if the bus is in between the two bus stops then get the distance from the current
//            // stop to the bus otherwise get the distance between the bus stops
//            if(distbetween > toBusCurStop && distbetween > toBusNextStop){
//                System.out.println("Reached the end");
//                totalDistance += toBusCurStop;
//                reachedBus = true;                                  // reached the bus
//            } else {
//                System.out.println(" calculating between bus stops ");
//                totalDistance += distbetween;
//            }
//
//            maxCount++;
//        }
//        return  totalDistance;
//    }

}
