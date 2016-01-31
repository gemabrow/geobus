package com.bussquad.geobus;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.DefaultClusterOptionsProvider;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.Polyline;
import com.androidmapsextensions.PolylineOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    // Map is used to ensure bus markers are not duplicated, as
    // Google Maps V2 for Android has no method to uniquely ID
    // a marker according to input
    public final static String EXTRA_INFO = "com.bussquad.geobus.INFO";
    static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1; // int used to identify specific permissions in permission callback methods
    static final int ACCESS_COARSE_LOCATION = 2;
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private final static float ICON_DEGREES_OFFSET = 90;
    private static final String TAG = "MapsActivity";
    public static MapsActivity activity;
    private final Handler locationHandler = new Handler();
    private final JsonFileReader test = new JsonFileReader();
    private Toast toast;
    private Interpolator interpolator = new DecelerateInterpolator();
    private Boolean infoWindowActive = false;
    private String tString = "";
    private GoogleMap mMap;
    private ArrayList<BusStop> busStops;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DataBaseHelper database_Helper;
    private Polyline routeLine;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mdAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    protected BottomSheetLayout bottomSheetLayout;

    // google maps
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
    private Map<Marker, BusStop> busStopMarkers = new HashMap<Marker, BusStop>();
    private Map<String, Marker> visibleMarkers = new HashMap<String, Marker>();
    private GoogleApiClient client;
    private Location mLastLocation;
    private  SupportMapFragment mapFragment;


    // layout
    private ImageButton btnExpand;
    private boolean mapExpanded = false;

    private final Runnable updateMarkers = new Runnable() {

        @Override
        public void run() {
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            //if data connection exists, fetch bus locations
            if (NetworkUtil.isConnected(context) && !tString.equals("connecting....")) {
                NetworkActivity networkActivity = new NetworkActivity();
                networkActivity.load();
            } else if (!tString.equals("no connection.")) {
                tString = "no connection";
                duration = Toast.LENGTH_LONG;
            }

            //if no bus markers have been retrieved, display toast
            if (!tString.equals("no connection.")) {
                if (busMarkers.isEmpty() || tString.equals("no connection")) {
                    toast = Toast.makeText(context, tString, duration);
                    toast.show();
                    tString = tString + ".";
                }
                locationHandler.postDelayed(this, MARKER_UPDATE_INTERVAL);
            }


            //otherwise, if no connection, stop background data
            else {
                stopBackgroundData();
            }

            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return  ;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        LayoutInflater inflater = getLayoutInflater(); // used to display a header at the top of the drawer
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.nav_header_main, mDrawerList, false);

        btnExpand = (ImageButton)findViewById(R.id.swToFullScrBtn);

        mDrawerList.addHeaderView(header, null, false);
        addDrawerItems(); // adds elements to drawer

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

//
//        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
//        bottomSheetLayout.setPeekOnDismiss(true);
//        bottomSheetLayout.setShouldDimContentView(false);
//        bottomSheetLayout.setInterceptContentTouch(false);
//

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mdAdapter = new RecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mdAdapter);


        addDrawerItems(); // adds elements to drawer
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        // sets up the FragmentManager and the  BusScheduleFragment which will be populated with
        // information based on the bus schedule of the specified bus stop

        database_Helper = new DataBaseHelper(MapsActivity.this);
        database_Helper.createDataBase();

        // loads UCSC_WestSide_Bus_Stops.json file to an array of BusStop Objects which are used
        // to create the busstop markers
        loadJsonFromAsset();
        startBackgroundData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
    }




    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 20; index++) {
            DataObject obj = new DataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }
        return results;
    }

   


   public void stopBackgroundData() {
        locationHandler.removeCallbacks(updateMarkers);
    }



  
    @Override
    protected void onPause() {
        try { // exception handling specific to Android 6, as the permissions allow/deny dialog would make toast.cancel(); trigger an NPE
            toast.cancel();
        } catch (NullPointerException permissionBlocked) {
            System.out.println("Toast not canceled due to permissions dialog");
        }
        super.onPause();
    }
  
  


    @Override
    protected void onDestroy() {
        toast.cancel();
        stopBackgroundData();
        super.onDestroy();
        System.exit(0);
    }

  
  
  
    // draws map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setClustering(new ClusteringSettings()
                .clusterSize(30)
                .clusterOptionsProvider(
                        new DefaultClusterOptionsProvider(getResources())));

        // set up coordinates for the center of UCSC and move the camera to there with a zoom level of 15 on startup
        LatLng ucsc = new LatLng(36.991406, -122.060731);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsc, 15));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

        createBusStopMarkers();
        drawBusStopMarkers();
        checkVersion(); // permissions handling for newer versions of Android (6+)

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent myIntent = new Intent(MapsActivity.this, BusStopMenuActivity.class);
        myIntent.putExtra("bus_stop_name", marker.getTitle()); //Optional parameters
        MapsActivity.this.startActivity(myIntent);
    }

  
  
    private void addDrawerItems() { // fills the hamburger menu/sidebar with an array of strings!
        String[] osArray = {"Toggle Bus Stops", "Loop and Upper Campus Info", "Night Core Info", "Night Owl Info", "Night Owl Schedule", "Manual Refresh"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(MapsActivity.this, InfoActivity.class);
            Intent schedIntent = new Intent(MapsActivity.this, NightOwlActivity.class);

            @Override // depending on the string's array index, perform an action
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    drawBusStopMarkers(); // Toggle stops
                    mDrawerLayout.closeDrawer(GravityCompat.START); // and close the drawer
                } else if (position == 2) { // if the user tapped on Loop and Upper Campus Info
                    intent.putExtra(EXTRA_INFO, "1"); // using extras in an intent in order to set an appropriate textview in the next activity
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intent); // start info activity
                } else if (position == 3) { // if the user tapped on Night Core Info
                    intent.putExtra(EXTRA_INFO, "2");
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intent);
                } else if (position == 4) { // if the user tapped on Night Owl Info
                    intent.putExtra(EXTRA_INFO, "3");
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intent);
                } else if (position == 5) { // if the user tapped on Night Owl Schedule
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(schedIntent);
                } else if (position == 6) {
                    startBackgroundData();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
    }

  
  

    // Opens up the hamburger menu
    public void releaseTheBurger(View view) { // opens hamburger/sidebar menu
        mDrawerLayout.openDrawer(mDrawerList);
    }




    // ,Moves screen to users position and zooms screen into the users current position
    public void setToUserLocation(View view){

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        mLastLocation =  LocationServices.FusedLocationApi.getLastLocation(client);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            LatLng location = new LatLng(latitude,longitude);


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        }

    }




    // expands google maps to fit the entire screen when the full screen
    public void expandMap(View view){


        if(mRecyclerView.getVisibility() != View.GONE){
            btnExpand.setImageResource(R.drawable.ic_fullscreen_exit_black_24dp);
            mRecyclerView.animate().
                    translationY(mRecyclerView.getHeight())
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                                super.onAnimationEnd(animation);
                                System.out.println("called");
                                mRecyclerView.setVisibility(View.GONE);

                        }
                    });
        }
        else{
            System.out.println("should be visible");
            btnExpand.setImageResource(R.drawable.ic_fullscreen_black_24dp);

            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setAlpha(0.0f);
            mRecyclerView.animate().
                    translationY(0)
                    .alpha(1.0f)
                    .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                        super.onAnimationEnd(animation);


                }
            });
        }




    }




    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START))
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); // close fragment if ONLY the fragment is open
        else if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() == 0)
            mDrawerLayout.closeDrawer(GravityCompat.START); // if both the fragment and the drawer is open, only close the drawer
        else if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() > 0) {
            mDrawerLayout.closeDrawer(GravityCompat.START); // close the drawer if it's open
        } else {
            this.finish();
            System.exit(0);// close the app otherwise
        }
    }


  
  
    public void checkVersion() {
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
            }
            case ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else { // show dialog confirming that the user denied location & brief instructions on how to enable permission
                    FragmentManager fragmentManager = getFragmentManager();
                    DialogFragment deniedLocation = new LocationDialogFragment();
                    deniedLocation.setCancelable(false); // ensure the user can't accidentally dismiss the dialog by tapping outside of the window
                    deniedLocation.show(fragmentManager, "location"); // show the dialog
                }
            }
        }
    }


  

    public void createBusStopMarkers() {

        for (BusStop temp : busStops) {
            Marker busStopMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                    .title(temp.getTitle())
                    .visible(false)
                    .clusterGroup(BusStop.BUSSTOP_CLUSTERGROUP)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));

            if (temp.hasId()) {
                System.out.println("adding bus stop");
                busStopMarkers.put(busStopMarker, temp);
            }
            visibleMarkers.put(Double.toString(temp.getLatitude() + temp.getLongitude()), busStopMarker);
        }
    }


  
  
    public void startBackgroundData() {
        tString = "connecting";
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
        for (Bus bus : buses) {
            Log.i(TAG, bus.toString());
            LatLng newPos = new LatLng(bus.lat, bus.lng);
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));
            // if marker does not exist, add new marker
            if (bus_marker == null) {
                //ToDo: retrieve stops LatLng from relevant bus stops and store as data in marker for drawing routes
                //PolylineOptions stops = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(newPos)
                        .flat(true)
                        .title(bus.route)
                        .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id)));
                if (bus.color == Bus.HUE_AZURE)
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                else if (bus.color == Bus.HUE_ORANGE)
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                else if (bus.color == Bus.HUE_YELLOW)
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                else
                    bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
            } else {
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
                        if (bus.color == Bus.HUE_AZURE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        else if (bus.color == Bus.HUE_ORANGE)
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        else if (bus.color == Bus.HUE_YELLOW)
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
        for (Marker marker : busMarkers.values()) {
            marker.remove();
        }
        // and set to the updatedBusMarkers Map (NOT a Google Map)
        busMarkers = updatedBusMarkers;
    }


  
  
    public Bitmap resizeMapIcons(String iconName, int width, int height) { // used to resize marker icons so they don't explode to crazy sizes
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



  
  
    // draws the busstop markers on the google map
    public void drawBusStopMarkers() {

        for (BusStop temp : busStops) {
            Marker busStop = visibleMarkers.get(Double.toString(temp.getLatitude() + temp.getLongitude()));
            busStop.setVisible(!busStop.isVisible());
        }
    }


  
  
    // loads bus stops specified by json file
    private void loadJsonFromAsset() {

        try {
            InputStream in = getAssets().open("UCSC_Westside_Bus_Stops.json");
            test.readBusStopJsonStream(in);
            busStops = new ArrayList<>(test.getBusStops());
            System.out.println("Read Files" + busStops.size());
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }

  
  
  
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bussquad.geobus/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

  
  
  
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.bussquad.geobus/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        int id;
        String busStopName;


        if (busStopMarkers.containsKey(marker)) {

            id = busStopMarkers.get(marker).getBusStopID();
            busStopName = busStopMarkers.get(marker).getTitle();

            busStopMarkers.get(marker).setBusStopSchedule(database_Helper.getBusStopSchedule(id));
        } else {
            id = -1;
            busStopName = marker.getTitle();
        }



        return false;
    }



    private void showMenuSheet(final MenuSheetView.MenuType menuType, final String busStopName) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MapsActivity.this, menuType, busStopName , new MenuSheetView.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MapsActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.schedule) {
                            showMenuSheet(menuType == MenuSheetView.MenuType.LIST ? MenuSheetView.MenuType.GRID : MenuSheetView.MenuType.LIST, busStopName);
                        }
                        System.out.println("hello clicked on the menu");
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }








}
