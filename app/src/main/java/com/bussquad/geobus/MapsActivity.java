package com.bussquad.geobus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {
    static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1; // int used to identify specific permissions in permission callback methods
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private static final String TAG = "MapsActivity";
    public static MapsActivity activity;
    private final Handler locationHandler = new Handler();
    private final JsonFileReader test = new JsonFileReader();
    private Interpolator interpolator = new DecelerateInterpolator();
    private Boolean showStops = false;
    private GoogleMap mMap;
    private ArrayList<BusStop> busStops;
    // Map is used to ensure bus markers are not duplicated, as
    // Google Maps V2 for Android has no method to uniquely ID
    // a marker according to input
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
    private final Runnable updateMarkers = new Runnable() {
        @Override
        public void run() {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast;
            //if data connection exists, fetch bus locations
            if (NetworkUtil.isConnected(context)) {
                toast = Toast.makeText(context, "connecting...", duration);
                NetworkActivity networkActivity = new NetworkActivity();
                networkActivity.load();
            } else {
                if (NetworkUtil.isConnecting(context)) {
                    toast = Toast.makeText(context, "waiting for connection", duration);
                } else {
                    duration = Toast.LENGTH_LONG;
                    toast = Toast.makeText(context, "no network connection", duration);
                }
            }
            if (busMarkers.isEmpty())
                toast.show();

            locationHandler.postDelayed(this, MARKER_UPDATE_INTERVAL);
        }
    };
    private Map<String, Marker> visibleMarkers = new HashMap<String, Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        // loads UCSC_WestSide_Bus_Stops.json file to an array of BusStop Objects which are used
        // to create the busstop markers
        loadJsonFromAsset();

        startBackgroundData();
    }

    @Override
    protected void onPause() {
        stopBackgroundData();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startBackgroundData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopBackgroundData();
        super.onDestroy();
    }

    // draws map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // set up coordinates for the center of UCSC and move the camera to there with a zoom level of 15 on startup
        LatLng ucsc = new LatLng(36.991406, -122.060731);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsc, 15));
        drawBusStopMarkers();
        checkVersion(); // permissions handling for newer versions of Android (6+)
    }

    public void checkVersion(){
        if (Build.VERSION.SDK_INT >= 23) {
            // for Android 6.0 and above: checks if fine location permission is granted--if it isn't, prompt the user to grant during runtime.
            // if the user denies the permission, a dialog will appear, letting him/her know how to enable the permission
            // (this dialog will show on every run if the user ticks the "Never ask again" box, too
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            else
                mMap.setMyLocationEnabled(true);
        }    // for all versions of Android below 6:
        else // once the app has been installed, we can assume that location permissions have been granted. Proceed without a care!
            mMap.setMyLocationEnabled(true);
    }

    // creates dialog for the case of the user denying location permission
    public static class LocationDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_loc_permission)
                    .setTitle(R.string.dialog_loc_title)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dismiss the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override // overridden callback for permissions request in order to handle the user denying or accepting location permission
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    mMap.setMyLocationEnabled(true); // Enables the My Location Layer on the map so users get their current position
                } else { // show dialog confirming that the user denied location & brief instructions on how to enable permission
                    FragmentManager fragmentManager = getFragmentManager();
                    DialogFragment deniedLocation = new LocationDialogFragment();
                    deniedLocation.setCancelable(false); // ensure the user can't accidentally dismiss the dialog by tapping outside of the window
                    deniedLocation.show(fragmentManager, "location"); // show the dialog
                }
                return;
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (busMarkers.containsValue(marker)) {
            LatLng position = marker.getPosition();
            Log.i(TAG, position.toString());
        }
        return false;
    }

    public void stopBackgroundData() {
        locationHandler.removeCallbacks(updateMarkers);
    }

    public void startBackgroundData() {
        locationHandler.postDelayed(updateMarkers, MARKER_UPDATE_INTERVAL);
    }

    /**
     * Given a list of xml_markers (defined in TransitInfoXmlParser),
     * update the map display such that new buses are added as type Marker to mMap,
     * and previously displayed bus markers are moved to their new coordinates
     */
    public void setMarkers(List<Bus> buses) {
        Map<String, Marker> updatedBusMarkers = new HashMap<String, Marker>();
        AnimationSettings settings = new AnimationSettings()
                .duration(MARKER_UPDATE_INTERVAL).interpolator(interpolator);
        for(Bus bus: buses){
            Log.i(TAG, bus.toString());
            LatLng newPos = new LatLng(bus.lat, bus.lng);
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));
            // if marker does not exist, add new marker
            if(bus_marker == null){
                //ToDo: retrieve stops LatLng from relevant bus stops and store as data in marker for drawing routes
                //PolylineOptions stops = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(newPos)
                        .title(bus.route)
                        .clusterGroup(bus.clusterGroup)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id))
                        .icon(BitmapDescriptorFactory.defaultMarker(bus.color)));
            }
            else {
                // if marker exists, check for changes in position
                // and for change in bus route from last update
                int typeChange = bus_marker.getPosition().equals(newPos) ? 0 : 2;
                typeChange -= bus_marker.getTitle().equals(bus.route) ? 0 : 1;
                switch (typeChange) {
                    case (0):
                        break;
                    case (1):
                        bus_marker.setTitle(bus.route);
                        bus_marker.setClusterGroup(bus.clusterGroup);
                        bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
                    case (2):
                        bus_marker.animatePosition(newPos, settings);
                        break;
                }
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

    public void toggleStops(View view) {
        showStops = !showStops;
        drawBusStopMarkers();
    }

    // draws the busstop markers on the google map
    private void drawBusStopMarkers() {
        Map<String, Marker> updatedVisibleMarkers = new HashMap<String, Marker>();

        for (BusStop temp : busStops){
            Marker busStop = visibleMarkers.get(Double.toString(temp.getLatitude() + temp.getLongitude()));
            if (busStop == null) {
                busStop = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                            .title(temp.getTitle())
                            .visible(showStops)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
            }
            else {
                busStop.setVisible(showStops);
                visibleMarkers.remove(Double.toString(temp.getLatitude() + temp.getLongitude()));
            }
            updatedVisibleMarkers.put(Double.toString(temp.getLatitude()+ temp.getLongitude()), busStop);
        }

        // remove all values from busMarkers Map (NOT a Google Map)
        for(Marker marker: visibleMarkers.values()){
            marker.remove();
        }
        // and set to the updatedBusMarkers Map (NOT a Google Map)
        visibleMarkers = updatedVisibleMarkers;
    }

    // loads bus stops specified by json file
    private void loadJsonFromAsset() {

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

}
