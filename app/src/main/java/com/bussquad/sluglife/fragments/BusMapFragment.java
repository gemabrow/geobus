package com.bussquad.sluglife.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.BusStop;
import com.bussquad.sluglife.JsonFileReader;
import com.bussquad.sluglife.MapMenuItem;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.Route;
import com.bussquad.sluglife.activity.ScheduleActivity;
import com.bussquad.sluglife.parser.JsonRouteFileReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class BusMapFragment extends MapFragment {

    NotificationDbManger ndbase;
    private ArrayList<BusStop> busStops;
    private ArrayList<MapMenuItem> menuItems =  new ArrayList<>();
    private ArrayList<Route>routes = new ArrayList<>();
    public BusMapFragment() {
        // Required empty public constructor
        isActivityStartable(true);
        enableMenuItems();
        enableFilterMenuOptions();
        setFilterMenuResourceId(R.array.bus_filter_options);

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
        JsonFileReader busStopReader = new JsonFileReader();
        JsonRouteFileReader routeReader = new JsonRouteFileReader();

        ndbase = new NotificationDbManger(context);
        InputStream in;
        // fills the array of bus stop of bus stop locations
        try {
            Log.i("BusMapFragment","loadJsonFromAssets() Reading Bus Stop from Json file");
            in = context.getAssets().open("bus_stop_info.json");
            busStopReader.readBusStopJsonStream(in);
            busStops = new ArrayList<>(busStopReader.getBusStops());
            if(!ndbase.checkTable("bus_stop_info") || ndbase.checkTableSize("bus_stop_info") == 0 ){

                if(ndbase.checkTableSize("bus_stop_info")!= 0){
                  //  ndbase.createBusStopInfoTable();

                }

                // add bus stop to table
                for(BusStop stop: busStops){
                    ndbase.addBusStopInfo(stop);
                }
                ndbase.printTable("bus_stop_info");
            }

            Log.i("BusMapFragment","Loading Route info from Json File");
            in = context.getAssets().open("route_list.json");
            routeReader.ReadJsonFile(in);
            routes = new ArrayList<>(routeReader.getRoutes());
            createMenuItems();
            setDataLoadedStatus(true);

            context = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createMenuItems(){
        if(menuItems.size() != 0){
            Log.i("BusMapFragment", "menuItems have already been initialized");
            return;
        }
        if(routes == null){

        }
        for (int count = 0 ; count < routes.size(); count++){
            if(routes.get(count).getRouteNumber().equalsIgnoreCase("campus")){
                menuItems.add(new MapMenuItem(routes.get(count).getRouteNumber(),count));
            } else {
                menuItems.add(new MapMenuItem("Route " +routes.get(count).getRouteNumber(),count));
            }
        }
        setMapMenuItems(menuItems);
    }

    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects =  new ArrayList<>();
        if(getMenuSelectedPosition() != -1){
            for(BusStop  busStop : busStops){
                if(routes.get(getMenuSelectedPosition()).getStopIds().contains(busStop.getObjectID())){
                    mapObjects.add(busStop);
                }
            }
        } else {
            mapObjects = new ArrayList<MapObject>(busStops);
        }

        return mapObjects;

    }







    @Override
    public Intent getMapFragmentIntent(Context context,String title,MapObject mapObject) {

        Intent mIntent = new Intent(context, ScheduleActivity.class);
        mIntent.putExtra("bus_stop_name",title); //Optional parameters
        mIntent.putExtra("BUSSTOPID", mapObject.getObjectID());
        mIntent.putExtra("COORDINATES", mapObject.getLatLng());
        return mIntent;
    }



}
