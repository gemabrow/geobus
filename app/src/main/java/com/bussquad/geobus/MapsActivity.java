package com.bussquad.geobus;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.Polyline;
import com.androidmapsextensions.PolylineOptions;
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
    // Map is used to ensure bus markers are not duplicated, as
    // Google Maps V2 for Android has no method to uniquely ID
    // a marker according to input
    public final static String EXTRA_INFO = "com.bussquad.geobus.INFO";
    static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1; // int used to identify specific permissions in permission callback methods
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private final static float ICON_DEGREES_OFFSET = 90;
    private static final String TAG = "MapsActivity";
    private static final float HUE_RED = 0;
    private static final float HUE_BLUE = 240;
    private static final float HUE_YELLOW = 60;
    private static final float HUE_ORANGE = 30;
    private static final float HUE_AZURE = 210;
    public static MapsActivity activity;
    private final Handler locationHandler = new Handler();
    private final JsonFileReader test = new JsonFileReader();
    private Interpolator interpolator = new DecelerateInterpolator();
    private Boolean showStops = false;
    private GoogleMap mMap;
    private ArrayList<BusStop> busStops;
    private Polyline routeLine;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
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
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        LayoutInflater inflater = getLayoutInflater(); // used to display a header at the top of the drawer
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.nav_header_main, mDrawerList, false);
        mDrawerList.addHeaderView(header, null, false);
        addDrawerItems(); // adds elements to drawer
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

    private void addDrawerItems() { // fills the hamburger menu/sidebar with an array of strings!
        String[] osArray = { "Toggle Bus Stops", "Loop and Upper Campus Info", "Night Core Info", "Night Owl Info", "Night Owl Schedule" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(MapsActivity.this, InfoActivity.class);
            Intent schedIntent = new Intent(MapsActivity.this, NightOwlActivity.class);

            @Override // depending on the string's array index, perform an action
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    showStops = !showStops;
                    drawBusStopMarkers(); // Toggle stops
                    mDrawerLayout.closeDrawer(Gravity.LEFT); // and close the drawer
                } else if (position == 2) { // if the user tapped on Loop and Upper Campus Info
                    intent.putExtra(EXTRA_INFO, "1"); // using extras in an intent in order to set an appropriate textview in the next activity
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(intent); // start info activity
                } else if (position == 3) { // if the user tapped on Night Core Info
                    intent.putExtra(EXTRA_INFO, "2");
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(intent);
                } else if (position == 4) { // if the user tapped on Night Owl Info
                    intent.putExtra(EXTRA_INFO, "3");
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(intent);
                } else if (position == 5) { // if the user tapped on Night Owl Schedule
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    startActivity(schedIntent);
                }
            }
        });
    }

    public void releaseTheBurger(View view) { // opens hamburger/sidebar menu
        mDrawerLayout.openDrawer(mDrawerList);
    }

    @Override
    public void onBackPressed() { // if the hamburger menu/sidebar is opened, allow it to be closed with the back button
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
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

    @Override
    // overridden callback for permissions request in order to handle the user denying or accepting location permission
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
            Log.i(TAG, "NOT setting PolylineCoords");
            //setPolylineCoords(marker);
            //Log.i(TAG, "coords set");
            //toggleRoute(marker);
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
        Location dummyLocation = new Location("Dummy");
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
                        .flat(true)
                        .title(bus.route)
                        .clusterGroup(bus.clusterGroup)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id)));
                        if (bus.color == HUE_AZURE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        else if (bus.color == HUE_ORANGE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        else if (bus.color == HUE_YELLOW)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                        else
                            bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
            }
            else {
                // if marker exists, check for changes in position
                // and for change in bus route name from last update
                int typeChange = bus_marker.getPosition().equals(newPos) ? 0 : 2;
                typeChange -= bus_marker.getTitle().equals(bus.route) ? 0 : 1;
                switch (typeChange) {
                    case (0):
                        break;
                    case (1):
                        bus_marker.setTitle(bus.route);
                        bus_marker.setClusterGroup(bus.clusterGroup);
                        if (bus.color == HUE_AZURE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        else if (bus.color == HUE_ORANGE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        else if (bus.color == HUE_YELLOW)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                        else
                            bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
                    case (2):
                        bus_marker.animatePosition(newPos, settings);
                        bus_marker.setRotation(bus.updateBearing(bus_marker.getPosition(), bus_marker.getRotation() - ICON_DEGREES_OFFSET) + ICON_DEGREES_OFFSET);
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

    public Bitmap resizeMapIcons(String iconName,int width, int height){ // used to resize marker icons so they don't explode to crazy sizes
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //TODO: Check if simple polylines are working correctly and follow up with snap-to-road lines
    private void toggleRoute(Marker busMarker) {
        if (routeLine == null) {
            Log.i(TAG, "routeLine null");
            routeLine = mMap.addPolyline(new PolylineOptions()
                    .addAll((ArrayList<LatLng>) busMarker.getData())
                    .color(Bus.busColor(busMarker.getTitle())));
        } else {
            routeLine.setPoints((ArrayList<LatLng>) busMarker.getData());
            routeLine.setColor(Bus.busColor(busMarker.getTitle()));
            routeLine.setVisible(!routeLine.isVisible());
        }

    }

    private void setPolylineCoords(Marker busMarker) {
        try {
            InputStream in = getAssets().open("UCSC_Westside_Busses.json");
            test.readScheduledStops(in, busMarker.getTitle(), busStops);
            busMarker.setData(test.getStopLocations());
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
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

    // creates dialog for the case of the user denying location permission
    public static class LocationDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            String title = getResources().getString(R.string.dialog_loc_title);        // setting strings to separate variables
            String message = getResources().getString(R.string.dialog_loc_permission); // so that we can apply their HTML formatting
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.drawable.ic_room_pink)
                    .setMessage(Html.fromHtml(message)) // bada-bing, HTML.
                    .setTitle(Html.fromHtml(title))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dismiss the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
