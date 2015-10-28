package com.sprint1.geobus_map;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    public static MapsActivity activity;

    private GoogleMap mMap;
    private ArrayList<BusStop> busStops;
    private JsonFileReader test =  new JsonFileReader();

    Handler locationHandler = new Handler();
    final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    // Map is used to ensure bus markers are not duplicated, as
    // Google Maps V2 for Android has no method to uniquely ID
    // a marker according to input
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();

    /* Given a list of xml_markers (defined in TransitInfoXmlParser),
     * update the map display such that new buses are added as type Marker to mMap,
     * and previously displayed bus markers are moved to their new coordinates
     */
    public void setMarkers(List<Bus> buses){
        Map<String, Marker> updatedBusMarkers = new HashMap<String, Marker>();

        for(Bus bus: buses){
            Log.i(TAG, bus.toString());
            // if marker exists, move its location
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));
            // if not, add new marker
            if(bus_marker == null){
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(bus.lat, bus.lng))
                        .title(bus.route)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id))
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(bus.color)));
            }
            else {
                bus_marker.setPosition(new LatLng(bus.lat, bus.lng));
                busMarkers.remove(Integer.toString(bus.bus_id));
            }
            updatedBusMarkers.put(Integer.toString(bus.bus_id), bus_marker);
        }
        // remove all values from busMarkers Map (NOT a Google Map)
        for(Marker marker: busMarkers.values()){
            marker.remove();
        }
        // and set to the updatedBusMarkers Map (NOT a Google Map)
        busMarkers = updatedBusMarkers;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // loads UCSC_WestSide_Bus_Stops.json file to an array of BusStop Objects which are used
        // to create the busstop markers
        loadJsonFromAsset();

       locationHandler.postDelayed(updateMarkers, MARKER_UPDATE_INTERVAL);
    }


   // draws map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true); // Enables the My Location Layer on the map so users get their current position

        // set up coordinates for the center of UCSC and move the camera to there with a zoom level of 15 on startup
        LatLng ucsc = new LatLng(36.991406, -122.060731);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsc, 15));
        drawBusStopMarkers();
    }

    // draws the busstop markers on the google map
    public void drawBusStopMarkers(){

        for (BusStop temp : busStops){
            mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                            .title(temp.getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop))
            );
        }
    }

    // loads bus stops specified by json file
    public void loadJsonFromAsset(){

        try{
            InputStream in = getAssets().open("UCSC_Westside_Bus_Stops.json");
            test.readBusStopJsonStream(in);
            busStops = new ArrayList<>(test.getBusStops());
            System.out.println("Read Files" + busStops.size());
        }
        catch(IOException ex) {
            System.out.println("Error reading file");
        }
    }

    Runnable updateMarkers = new Runnable() {
        @Override
        public void run(){
            //networkInfo = cm.getActiveNetworkInfo();
            //if(networkInfo != null && networkInfo.isConnected()){
            NetworkActivity networkActivity = new NetworkActivity();
            networkActivity.load();

            if(busMarkers.isEmpty()) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "connecting...", duration);
                toast.show();
            }
            locationHandler.postDelayed(this, MARKER_UPDATE_INTERVAL);
        }
    };

    @Override
    protected void onDestroy() {
        locationHandler.removeCallbacks(updateMarkers);
        super.onDestroy();
    }

}
