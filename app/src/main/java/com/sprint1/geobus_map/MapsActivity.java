package com.sprint1.geobus_map;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<BusStop> busStops;
    private JsonFileReader test =  new JsonFileReader();

    private List<TransitInfoXmlParser.Marker> bus_markers = new ArrayList<TransitInfoXmlParser.Marker>();

    public void setList(List<TransitInfoXmlParser.Marker> bus_markers){
        this.bus_markers = bus_markers;
    }

    private void startTransitInfoTask() {
        new NetworkActivity(this).loadPage();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gathers transit info from servers and stores in list bus_markers
        startTransitInfoTask();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // loads UCSC_WestSide_Bus_Stops.json file to an array of BusStop Objects which are used
        // to create the busstop markers
        loadJsonFromAsset();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
        String jsonText = null;

        try{

            InputStream in = getAssets().open("UCSC_Westside_Bus_Stops.json");
            test.readBusStopJsonStream(in);
            busStops = new ArrayList<>(test.getBusStops());
            System.out.println("Read File" + busStops.size());


        }
        catch(IOException ex) {
            System.out.println("Error reading file");
        }

    }
}
