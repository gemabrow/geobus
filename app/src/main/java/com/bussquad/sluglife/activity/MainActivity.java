package com.bussquad.sluglife.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.DefaultClusterOptionsProvider;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;

import com.bussquad.sluglife.BikeRack;
import com.bussquad.sluglife.Bus;
import com.bussquad.sluglife.BusStop;
import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.Dining;
import com.bussquad.sluglife.Event;
import com.bussquad.sluglife.JsonFileReader;
import com.bussquad.sluglife.Library;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.OpersFacility;
import com.bussquad.sluglife.adapters.MapInfoWindowAdapter;
import com.bussquad.sluglife.parser.JsonEventJsonParser;
import com.bussquad.sluglife.parser.JsonOpersFacilityParser;
import com.bussquad.sluglife.utilities.GCMRegistrationIntentService;
import com.bussquad.sluglife.utilities.NetworkService;
import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.NotificationService;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.fragments.MapFragment;
import com.bussquad.sluglife.fragments.NavListFragment;
import com.bussquad.sluglife.parser.JsonBikeParkingFileParser;
import com.bussquad.sluglife.parser.JsonDiningFileParser;
import com.bussquad.sluglife.parser.JsonLibraryFileParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
//import com.google.firebase.crash.FirebaseCrash;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        TabLayout.OnTabSelectedListener,
        GoogleMap.OnInfoWindowClickListener,
        ImageButton.OnClickListener,
        GoogleMap.OnMarkerClickListener
{
    @Override
    public boolean onMarkerClick(Marker marker) {
        MapInfoWindowAdapter mapInfoW = new MapInfoWindowAdapter(getBaseContext());
        mapInfoW.setMainInfo("Eta: stuff");
        mMap.setInfoWindowAdapter(mapInfoW);

        return false;
    }

    final String TAG1 = "MainActivity";
    public static MainActivity activity;
    public final static String EXTRA_INFO = "com.bussquad.geobus.INFO";
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private final static float ICON_DEGREES_OFFSET = 90;
    private final static int INNER = 0;
    private final static int OUTER = 1;
    private final static int UPPER = 2;
//    private final static int UPPEROUTER = 3;
    private Interpolator interpolator = new DecelerateInterpolator();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton btnSwitchView;
    private ImageButton btnFilter;

    private GoogleApiClient client;
    private Location mLastLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    // Camera Position Update
    private String REQUEST_CAMERA_UPDATE_KEY = "CAMERALOC";
    private String CAMERA_POSITION_KEY = "POSITIONKEY";
    private double initalCameraZoom = 14.333;
    private LatLng cameraPosition  = new LatLng(36.991406, -122.060731);
    private CameraPosition lstCameraPosition;

    // list of objects retrieved from json files
    private ArrayList<BusStop> busStops;
    private ArrayList<Library> libraries;
    private ArrayList<BikeRack> bikeRacks;
    private ArrayList<Dining> dining;
    private ArrayList<Event> events;
    private ArrayList<OpersFacility> opers;

    private ArrayList<MapObject> displayedMarkers;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog pDialog;

    // Parser
    private final JsonFileReader busStopReader = new JsonFileReader();
    private final JsonBikeParkingFileParser bikeReader = new JsonBikeParkingFileParser();
    private final JsonDiningFileParser dinReader = new JsonDiningFileParser();
    private final JsonLibraryFileParser libReader = new JsonLibraryFileParser();
    private JsonEventJsonParser eventReader = new JsonEventJsonParser();
    private JsonOpersFacilityParser facilityReader = new JsonOpersFacilityParser();


    // hash maps
    private Map<String, Marker> visibleMarkers = new HashMap<String, Marker>();
    private Map<Event,Marker> eventMarkers = new HashMap<>();
    private Map<Marker,MapObject>objectMarkers = new HashMap<>();
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
    private Map<String,ArrayList<MapObject>> mapObjects = new HashMap<>();

    private Boolean cardMode = false;
    NavListFragment eventList;
    private ArrayList<Integer> cardTypes;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    // services
    private NetworkService networkService;
    private NotificationService notificationService;
    private Boolean netBound = false;
    private Boolean notifBound = false;
    private int lastTimeStamp = -1;



    //drawer
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;


    // busses
    private Map<String,ArrayList<Bus>> activeBusses = new HashMap<>();
    public int currentTab = 0;
    private int previousTab = 0;
    // notification database
    private NotificationDbManger notifDb;

    // used to esablish a connection with the network service if network service is already running
    // it also makes it possible to detach from the service when the MapActivity isStoped
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            NetworkService.LocalBinder binder = (NetworkService.LocalBinder) service;
            networkService = binder.getService();
            netBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            netBound = false;
        }
    };



    String regId;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String PROJECTID = "sluglife-1319";


    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.activity_main);


        notifDb = new NotificationDbManger(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);


        pDialog = new ProgressDialog(this);


        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        addDrawerItems(); // adds elements to drawer


        LayoutInflater inflater = getLayoutInflater(); // used to display a header at the top of the drawer
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.nav_header_main, mDrawerList, false);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setOnTabSelectedListener(this);


        btnSwitchView = (ImageButton) findViewById(R.id.btnSwitchView);
        btnFilter  = (ImageButton) findViewById(R.id.btnFilter);

//      Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        // load assets from json files
        loadJsonFromAsset();

        startBackgroundData();
        // set button listners
        btnSwitchView.setOnClickListener(this);


        // initialize active busses;
        activeBusses.put("INNER",new ArrayList<Bus>());
        activeBusses.put("OUTER",new ArrayList<Bus>());
//        activeBusses.put("UPPER",new ArrayList<Bus>());
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
            System.out.println("regid: " + regId);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        System.out.println("regid: again" + regId);
    }



    /**
     * Registers the application with GCM connection servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm =  GoogleCloudMessaging.getInstance(context);
                    }
                    regid = InstanceID.getInstance(context).getToken(PROJECTID,"GCM");
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


        }.execute(null, null, null);
    }


    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }




    /**
     * Gets the current registration token for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration token, or empty string if there is no existing
     *         registration token.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }



    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }




    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBackgroundData();

    }




    @Override
    public void onStop() {
        super.onStop();
        if (netBound) {
            System.out.println("un binding service");
            networkService.mapActivityStopped();
            unbindService(serviceConnection);
            netBound = false;
        }
    }




    @Override
    public void onStart(){
        super.onStart();
        startBackgroundData();
    }




    // when specific tab is selected draw the corresponding markers, clear previous markers
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        mMap.clear();
        viewPager.setCurrentItem(tab.getPosition());

        if (currentTab != tab.getPosition()) {
            previousTab = currentTab;
            currentTab = tab.getPosition();
            if (previousTab != currentTab  && previousTab == 4 ){
                zoomToLocation(cameraPosition,initalCameraZoom);
            }
            switch (tab.getPosition()) {

                case 0:
                    drawMarkers(busStops,true,false);
                    break;
                case 1:
                    drawMarkers(dining,true,false);
                    break;
//                case 2:
//                    drawMarkers(bikeRacks,true,false);
//                    break;
                case 2:
                    drawMarkers(libraries,true,false);
                    break;
                case 3:
                    // draw events on map
                    System.out.println("4th case");
                    drawEventMarkers();
                    break;
                case 4:
                    System.out.println("5th case");
                    // switch to event view
                    drawOperFacilities();
                    break;

            }
            // update the cards in the list view
            updateCardView();
        }


    }




    // when tab is unselected
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }




    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }




    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MapFragment(), "Campus Shuttles");
        adapter.addFrag(new MapFragment(), "Dining");
//        adapter.addFrag(new MapFragment(), "Bike Parking");
        adapter.addFrag(new MapFragment(), "Library");
        adapter.addFrag(new MapFragment(), "Campus Events");
        adapter.addFrag(new MapFragment(), "OPERS");
        viewPager.setAdapter(adapter);
    }




    public void onTabSelectedListener(){

    }




    // sets up a default Tab Icons
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Campus Shuttles");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bus_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Dining");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_local_dining_black_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
//
//        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
//        tabThree.setText("Bike Parking");
//        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bike_black_24dp, 0, 0);
//        tabLayout.getTabAt(2).setCustomView(tabThree);
//
        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Libraries");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_local_library_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setText("Campus Events");
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFive);

        TextView tabSix = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSix.setText("OPERS");
        tabSix.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fitness_center_black_24dp, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabSix);


    }




    // adds  a new Tab to tablayout
    private void addTabLayout(){

    }




    // removes a the specified tab from the tablayout
    private  void removeTabLayout(){

    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnSwitchView:
                switchToListView();
                break;
            case R.id.btnFilter:
                createCrash();
//                getClosestStops();
                break;

        }
    }

    private void createCrash(){
//        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
//        FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");

        throw new NullPointerException("Fake null pointer exception");
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }




        @Override
        public int getCount() {
            return mFragmentList.size();
        }




        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }






    // Map Functionality

    // draws google map and sets up initial settings.
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
       // mMap.setOnMarkerClickListener(this);
        mMap.setClustering(new ClusteringSettings()
                .clusterSize(30)
                .clusterOptionsProvider(
                        new DefaultClusterOptionsProvider(getResources())));

        // set up coordinates for the center of UCSC and move the camera to there with a zoom level of 15 on startup

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.cameraPosition, (float)this.initalCameraZoom));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(getBaseContext()));
        drawMarkers(busStops,true,false);

    }




    public void drawMarkers(ArrayList<?> mapObjects, boolean visible, boolean enableIconGen){
        IconGenerator iconGen = null;  // custom icon for displaying text
        displayedMarkers = (ArrayList<MapObject>)mapObjects;
        if(!objectMarkers.isEmpty()){
            objectMarkers.clear();
        }

        if(enableIconGen){
            iconGen = new IconGenerator(this);
            iconGen.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_blank_icon,null));

        }
        for(MapObject temp : displayedMarkers){
            BitmapDescriptor icon;
            if (iconGen != null){
                icon = BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(temp.getAdditionalInfo()));
            } else {
                icon = BitmapDescriptorFactory.fromResource(temp.getMapImgResource());
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                    .title(temp.getName())
                    .snippet(temp.getAdditionalInfo())
                    .visible(visible)
                    .icon(icon));


            objectMarkers.put(marker,temp);
        }
    }



    public void drawEventMarkers(){
        // Tag used to cancel the request
        String  tag_string_req = "string_req";
        String url = "https://events.ucsc.edu/feed/featured.json";

//        pDialog.setMessage("Loading...");
//        pDialog.show();


        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
//                pDialog.hide();
                loadJsonEventAsset(response.toString());
                if(events != null){

                    drawMarkers(events,true,false);
                    updateCardView();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
            }
        });

//        requestQueue.add(strReq);

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }




    // zoom and move camera to new location
    public void zoomToLocation(LatLng newCameraLoc, double zoom){

        float cameraZoom = (float)zoom;

        // smooth zoom transition
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCameraLoc, cameraZoom),2000,null);

    }




    public void drawOperFacilities(){
        String url = "http://ec2-54-186-252-123.us-west-2.compute.amazonaws.com/facility_count.json";
        String tag_string_req = "getOperFacilityCount";
        zoomToLocation(new LatLng(36.99434448480415,-122.05450728535652),17.93418);
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
            //    Log.d(TAG, response.toString());
                loadFacilityJsonData(response.toString());
                if(opers != null){


                    drawMarkers(opers,true,true);
                    updateCardView();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }





    private void loadFacilityJsonData(String input){
        try{
            InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            facilityReader.ReadJsonFile(in);
            opers = new ArrayList<>(facilityReader.getOpersFacilities());



        }catch(Exception ex){
            Log.e(TAG,"Error: " + ex.getMessage());
        }
    }




    private void loadJsonEventAsset(String input){
        try{
            InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            eventReader.ReadJsonFile(in,getBaseContext());
            events = new ArrayList<>(eventReader.getEvents());


        }catch(Exception ex){
            Log.e(TAG,"Error: " + ex.getMessage());
        }

    }




    // loads bus stops specified by json file
    private void loadJsonFromAsset() {
        InputStream in;
        try {

            Log.i("MainActivity","loadJsonFromAssets() Reading Bus Stop from Json file");
            // fills the array of bus stop of bus stop locations
            in = getAssets().open("UCSC_Westside_Bus_Stops.json");
            busStopReader.readBusStopJsonStream(in);
            busStops = new ArrayList<>(busStopReader.getBusStops());


            Log.i("MainActivity","loadJsonFromAssets() Reading Bike Rack from Json files");
            // fills the array list with bike parking locations
            in = getAssets().open("bikeRacks.json");
            bikeReader.ReadJsonFile(in);
            bikeRacks = new ArrayList<>(bikeReader.getBikeParking());


            Log.i("MainActivity","loadJsonFromAssets() Reading Libraries from Json files");
            // fills the array list with library locations
            in = getAssets().open("libraries.json");
            libReader.ReadJsonFile(in);
            libraries = new ArrayList<>(libReader.getLibraries());
            Log.i("MainActivity", "loadJsonFromAssets() number of libraries:" + libraries.size());


            Log.i("MainActivity","loadJsonFromAssets() Reading Dining from Json files");
            // fills thee array list with dining locations retrieved from the json file
            in = getAssets().open("dining.json");
            dinReader.ReadJsonFile(in);
            dining = new ArrayList<>(dinReader.getDining());
            Log.i("MainActivity", "loadJsonFromAssets() number of dining locations:" + dining.size());


        } catch (IOException ex) {

            Log.e("MainActivity", "loadJsonFromAssets()"+ ex.getMessage() );


        }
    }




    // Populates the hamburger menu.
    private void addDrawerItems() { // fills the hamburger menu/sidebar with an array of strings!
        String[] osArray = {"Toggle Bus Stops", "Loop and Upper Campus Info", "Night Core Info", "Night Owl Info", "Night Owl Schedule", "Manual Refresh"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            Intent schedIntent = new Intent(MainActivity.this, NightOwlActivity.class);

            @Override // depending on the string's array index, perform an action
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                   // drawMarkers(); // Toggle stops
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
                    //  startBackgroundData();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
    }




    // closes hamburger menu when users presses the back button on there phone
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            getFragmentManager().popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE); // close fragment if ONLY the fragment is open
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() == 0){
            mDrawerLayout.closeDrawer(GravityCompat.START); // if both the fragment and the drawer is open, only close the drawer
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() > 0) {
            mDrawerLayout.closeDrawer(GravityCompat.START); // close the drawer if it's open
        } else {
            super.onBackPressed();
        }
    }




    // Opens up the hamburger menu
    public void releaseTheBurger(View view) { // opens hamburger/sidebar menu
        mDrawerLayout.openDrawer(mDrawerList);
    }




    // filters the marker that was clicked and starts an activity based on the marker selected
    @Override
    public void onInfoWindowClick(Marker marker) {

        int tab = tabLayout.getSelectedTabPosition();
        lstCameraPosition = mMap.getCameraPosition();
        Intent myIntent;
        switch(tab){
            case 0:
                myIntent = new Intent(MainActivity.this, BusStopMenuActivity.class);
                myIntent.putExtra("bus_stop_name", marker.getTitle()); //Optional parameters
                System.out.println("bus stop id " + objectMarkers.get(marker).getObjectID());
                myIntent.putExtra("BUSSTOPID", objectMarkers.get(marker).getObjectID());
                myIntent.putExtra("COORDINATES", objectMarkers.get(marker).getLatLng());
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
                break;
            case 1:
                myIntent = new Intent (MainActivity.this,DiningActivity.class);
                myIntent.putExtra("DINING_NAME",marker.getTitle());
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
                break;
            case 2:
                myIntent = new Intent (MainActivity.this,LibraryActivity.class);
                myIntent.putExtra("LIBRARY_NAME",marker.getTitle());
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
                break;
            case 3:
                myIntent = new Intent (MainActivity.this,EventActivity.class);
                myIntent.putExtra("EVENTID",objectMarkers.get(marker).getObjectID());
                MainActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.fade_out_in_place);
                break;
        }


    }





    // hides the map in the background and draws list view in front of the map
    public void switchToListView (){
        Log.i("MainActivity", "switching fragments");

        if(cardMode == false){
            cardMode = true;
            btnSwitchView.setImageResource(R.drawable.ic_map_black_24dp);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("OBJECT_LIST", getData());
            bundle.putIntegerArrayList("TYPE_LIST",cardTypes);

            if(currentTab == 0){
                getClosestStops();

            }

            eventList = new NavListFragment();
            eventList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.map,eventList).commit();
        }else{
            cardMode = false;
            btnSwitchView.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
            getSupportFragmentManager().beginTransaction().remove(eventList).commit();
        }

    }




    // returns the closest bus stops to the user based on the specified distance
    public ArrayList<BusStop> getClosestStops(){
        LatLng userLoc;
        double distance;
        ArrayList<BusStop> closestStops =  new ArrayList<>();

        for(BusStop stop : busStops){
//             userLoc =  new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
            userLoc =  new LatLng(37.000084,-122.062960); // testing location
            distance = SphericalUtil.computeDistanceBetween(userLoc,stop.getLocation());
            if(distance < 200 ){
                closestStops.add(stop);
                System.out.println(stop.getName());
            }
        }

//        if(closestStops)
        getEta(closestStops.get(0));
        return closestStops;
    }

    public void getEta(BusStop stop){
        System.out.println("calculating eta: ");

        for (String route : stop.getBusses()){
            if(activeBusses.containsKey(route)){
                if(activeBusses.get(route).size() >0){
                    findCLosestBus(activeBusses.get(route), stop.getObjectID());
                }
            }
        }
    }




    // finds the closes buses given the route and bus to the bus stop
    public void findCLosestBus(ArrayList<Bus> busses, int stopID){

        int closestBus = 0;                         // stores the index of the closes bus thus far
        double tmpD = 0.0;
        double clstBus = 0.0;
        int count = 0;
        for (Bus bus : busses)        {
            tmpD = calculateTotalDistance(bus.direction.toUpperCase(), bus.getLocaiton(),stopID);

            if(tmpD >= clstBus){
                clstBus = tmpD;
                closestBus = count;
            }
            count++;
        }

        System.out.println("from bus stop: " + notifDb.getStopName(stopID));
        System.out.println("The closest bus is: " + busses.get(closestBus).bus_id + " of route " + busses.get(closestBus).direction);
        System.out.println("thee distance is: " + clstBus);

    }




    private double calculateTotalDistance(String route, LatLng busLocation,int stopID){
        boolean reachedBus =  false;
        double totalDistance = 0.0;
        int maxCount = 0;
        while(!reachedBus && maxCount < 32) {

            // if bus is in between the bus stops measure the eta of between the bus stop to the user
            // otherwise get its estimated eta
            LatLng curStop = notifDb.getLocation(stopID);            // get LatLng of current stop
            stopID  = notifDb.getNextBusStopId(stopID,route,1);             // get the next stopID
            LatLng nextStop = notifDb.getLocation(stopID);           // get LatLng of next stop

            double distbetween = SphericalUtil.computeDistanceBetween(curStop, nextStop);
            double toBusNextStop = SphericalUtil.computeDistanceBetween(curStop, busLocation);
            double toBusCurStop = SphericalUtil.computeDistanceBetween(nextStop, busLocation);


            // if the bus is in between the two bus stops then get the distance from the current
            // stop to the bus otherwise get the distance between the bus stops
            if(distbetween > toBusCurStop && distbetween > toBusNextStop){
                System.out.println("Reached the end");
                totalDistance += toBusCurStop;
                reachedBus = true;                                  // reached the bus
            } else {
                System.out.println(" calculating between bus stops ");
                totalDistance += distbetween;
            }

            maxCount++;
        }
        return  totalDistance;
    }



    // updates the recycleview based on the tab currently selected
    // precondition eventList must be initialized
    public void  updateCardView(){
        if(eventList != null){
            eventList.updateRecycleViewData(getData(),cardTypes);
        }
    }




    // translate the current markers to recycleview
    public ArrayList<DataObject> getData(){

        ArrayList<DataObject> data = new ArrayList<>();
        cardTypes = new ArrayList<>();

        for(MapObject temp: displayedMarkers){
            DataObject dataObject = new DataObject(temp.getObjectID(),
                    temp.getImageResource(),
                    temp.getName(),
                    temp.getCardSubText1(),
                    temp.getCardSubText2());

            // if the object has a thumbnail associated with it then return the url for the thumbnail
            // or if the object has a larger view associated with it
            if(temp.getThumbNailUrl()!= null){
                dataObject.setImageUrl(temp.getThumbNailUrl());
            } else if(temp.getFullViewImageUrl() != null){
                dataObject.setImageUrl(temp.getFullViewImageUrl());
            }

            data.add(dataObject);
            cardTypes.add(temp.getCardViewType());
        }

        return data;
    }




    /**
     * Given a list of xml_markers (defined in TransitInfoXmlParser),
     * update the map display such that new buses are added as type Marker to mMap,
     * and previously displayed bus markers are moved to their new coordinates
     * This function is called by NetworkActvity
     */
    public void setMarkers(List<Bus> buses) {

        Location dummyLocation = new Location("Dummy");
        Map<String, Marker> updatedBusMarkers = new HashMap<String, Marker>();
        activeBusses.get("INNER").clear();
        activeBusses.get("OUTER").clear();


        AnimationSettings settings = new AnimationSettings()
                .duration(MARKER_UPDATE_INTERVAL).interpolator(interpolator);


        // checks if the timestamp has been updated if it has not then it will notifiy the network
        // service that xml file has not been updated
        try{

            if(buses.get(0).getTimeStamp() == lastTimeStamp ){
                networkService.sameTimeStamp();
            }

        }catch(Exception ex){
        }


        // iterates through the buses found in the xml file
        for (Bus bus : buses) {

            //        Log.i(TAG, bus.toString());
            LatLng newPos = new LatLng(bus.lat, bus.lng);
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));

            // allocate to apprioprite active bus stype;
            if(activeBusses.containsKey(bus.direction)){
                activeBusses.get(bus.direction).add(bus);
            }

            // if marker does not exist, add new marker
            if ((bus_marker == null || !mMap.getDisplayedMarkers().contains(bus_marker)) && currentTab == 0 ) {
                //ToDo: retrieve stops LatLng from relevant bus stops and store as data in marker for drawing routes
                //PolylineOptions stops = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(newPos)
                        .flat(true)
                        .title(bus.route)
                        .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id)));
                if (bus.color == Bus.BLUE){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                }
                else if (bus.color == Bus.ORANGE){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                }
                else if (bus.color == Bus.YELLOW){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                }
                else if (bus.color == Bus.GREY){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("grey_marker", 64, 111)));
                }
                else{
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("green_marker", 64, 111)));
                }
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
                        if (bus.color == Bus.BLUE){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.ORANGE){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.YELLOW){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.GREEN){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("green_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.GREY){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("grey_marker", 64, 111)));
                        }
                        else{
                            bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
                        }
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
        System.out.println(iconName);
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }




    // sets a runnable that is called every 2000 milleseconds, this runnable updates that map
    // every 2000 milleseconds
    public void startBackgroundData() {

        System.out.println("starting service! ");
        Intent intent = new Intent(this,NetworkService.class);

        if(!isServiceRunning(NetworkService.class)){

            intent.putExtra("MAINACTIVITY", true);
            this.startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }



    public void stopBackgroundData() {
        if(!isServiceRunning(NetworkService.class)){
            System.out.println("Stoping service");
            this.stopService(new Intent(this, NetworkService.class));
        }
//        locationHandler.removeCallbacks(updateMarkers);
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
