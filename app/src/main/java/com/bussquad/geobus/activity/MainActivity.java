package com.bussquad.geobus.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.androidmapsextensions.AnimationSettings;
import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.DefaultClusterOptionsProvider;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;

import com.bussquad.geobus.BikeRack;
import com.bussquad.geobus.Bus;
import com.bussquad.geobus.BusStop;
import com.bussquad.geobus.DataObject;
import com.bussquad.geobus.Dining;
import com.bussquad.geobus.JsonFileReader;
import com.bussquad.geobus.Library;
import com.bussquad.geobus.MapObject;
import com.bussquad.geobus.utilities.NetworkService;
import com.bussquad.geobus.NotificationDbManger;
import com.bussquad.geobus.NotificationService;
import com.bussquad.geobus.R;
import com.bussquad.geobus.fragments.CampusShuttleFragment;
import com.bussquad.geobus.fragments.DiningFragment;
import com.bussquad.geobus.fragments.MapFragment;
import com.bussquad.geobus.fragments.NavListFragment;
import com.bussquad.geobus.parser.JsonBikeParkingFileParser;
import com.bussquad.geobus.parser.JsonDiningFileParser;
import com.bussquad.geobus.parser.JsonLibraryFileParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        TabLayout.OnTabSelectedListener,
        GoogleMap.OnInfoWindowClickListener,
        ImageButton.OnClickListener{
    public static MainActivity activity;
    public final static String EXTRA_INFO = "com.bussquad.geobus.INFO";
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private final static float ICON_DEGREES_OFFSET = 90;
    private Interpolator interpolator = new DecelerateInterpolator();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton btnSwitchView;

    private GoogleApiClient client;
    private Location mLastLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    // Camera Position Update
    private String REQUEST_CAMERA_UPDATE_KEY = "CAMERALOC";
    private String CAMERA_POSITION_KEY = "POSITIONKEY";
    private CameraPosition lstCameraPosition;
    private CameraPosition currCameraPosition;


    // list of objects retrieved from json files
    private ArrayList<BusStop> busStops;
    private ArrayList<Library> libraries;
    private ArrayList<BikeRack> bikeRacks;
    private ArrayList<Dining> dining;
    private ArrayList<MapObject> displayedMarkers;
    private ArrayAdapter<String> mAdapter;

    // Parser
    private final JsonFileReader busStopReader = new JsonFileReader();
    private final JsonBikeParkingFileParser bikeReader = new JsonBikeParkingFileParser();
    private final JsonDiningFileParser dinReader = new JsonDiningFileParser();
    private final JsonLibraryFileParser libReader = new JsonLibraryFileParser();


    // hash maps
    private Map<String, Marker> visibleMarkers = new HashMap<String, Marker>();
    private Map<Marker,MapObject>objectMarkers = new HashMap<>();
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
    private Map<String,ArrayList<MapObject>> mapObjects = new HashMap<>();

    private Boolean cardMode = false;
    NavListFragment eventList;



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




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);

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

//      Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        // load assets from json files
        loadJsonFromAsset();

        startBackgroundData();
        // set button listners
        btnSwitchView.setOnClickListener(this);
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

            if (tab.getPosition() == 0) {
                drawMarkers(busStops);
//                startBackgroundData();
            } else if (tab.getPosition() == 1) {
                drawMarkers(dining);
            } else if (tab.getPosition() == 2) {
                drawMarkers(bikeRacks);
            } else if (tab.getPosition() == 3) {
                drawMarkers(libraries);
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
        adapter.addFrag(new CampusShuttleFragment(), "Campus Shuttles");
        adapter.addFrag(new DiningFragment(), "Dining");
        adapter.addFrag(new MapFragment(), "Bike Parking");
        adapter.addFrag(new MapFragment(), "Library");
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

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Bike Parking");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bike_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Libraries");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_local_library_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);


    }




    // adds  a new Tab to tablayout
    private void addTabLayout(){

    }




    // removes a the specified tab from the tablayout
    private  void removeTabLayout(){

    }




    @Override
    public void onClick(View v) {

        System.out.println(v.getId());
        switch (v.getId()){
            case R.id.btnSwitchView:
                switchToListView();
                break;

        }
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

        LatLng cameraPosition;
        float cameraZoom = (float)14.333;

        if(currCameraPosition != null){
            cameraPosition = currCameraPosition.target;
            cameraZoom = currCameraPosition.zoom;
        }
        else{
            cameraPosition  = new LatLng(36.991406, -122.060731);
        }

        mMap = googleMap;
       // mMap.setOnMarkerClickListener(this);
        mMap.setClustering(new ClusteringSettings()
                .clusterSize(30)
                .clusterOptionsProvider(
                        new DefaultClusterOptionsProvider(getResources())));

        // set up coordinates for the center of UCSC and move the camera to there with a zoom level of 15 on startup

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
        drawMarkers(busStops);

    }




    public void drawMarkers(ArrayList<?> mapObjects){
        displayedMarkers = (ArrayList<MapObject>)mapObjects;
        for(MapObject temp : displayedMarkers){
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                    .title(temp.getName())
                    .visible(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
            objectMarkers.put(marker,temp);
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
            Log.i("MainActivity", "loadJsonFromAssets() number of bus stops size:" + busStops.size());


            Log.i("MainActivity","loadJsonFromAssets() Reading Bike Rack from Json files");
            // fills the array list with bike parking locations
            in = getAssets().open("bikeRacks.json");
            bikeReader.ReadJsonFile(in);
            bikeRacks = new ArrayList<>(bikeReader.getBikeParking());
            Log.i("MainActivity", "loadJsonFromAssets() number of bike racks:" + bikeRacks.size());


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
        if(tab == 0){
            myIntent = new Intent(MainActivity.this, BusStopMenuActivity.class);
            myIntent.putExtra("bus_stop_name", marker.getTitle()); //Optional parameters
            System.out.println("bus stop id" + objectMarkers.get(marker).getObjectID());
            myIntent.putExtra("ITEMID", objectMarkers.get(marker).getObjectID());
            myIntent.putExtra("COORDINATES", objectMarkers.get(marker).getLatLng());
            MainActivity.this.startActivity(myIntent);
        }else if (tab == 1){
            myIntent = new Intent (MainActivity.this,DiningActivity.class);
            myIntent.putExtra("DINING_NAME",marker.getTitle());
            MainActivity.this.startActivity(myIntent);
        }else if (tab == 3){
            myIntent = new Intent (MainActivity.this,LibraryActivity.class);
            myIntent.putExtra("LIBRARY_NAME",marker.getTitle());
            MainActivity.this.startActivity(myIntent);
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
            eventList = new NavListFragment();
            eventList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.map,eventList).commit();
        }else{
            cardMode = false;
            btnSwitchView.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
            getSupportFragmentManager().beginTransaction().remove(eventList).commit();
        }

    }




    // updates the recycleview based on the tab currently selected
    // precondition eventList must be initialized
    public void  updateCardView(){
        if(eventList != null){
            eventList.updateRecycleViewData(getData());
        }
    }




    // translate the current markers to recycleview
    public ArrayList<DataObject> getData(){
        ArrayList<DataObject> data = new ArrayList<>();
        for(MapObject temp: displayedMarkers){
            DataObject dataObject = new DataObject(temp.getImageResource(),
                    temp.getName(),
                    temp.getMainInfo(),
                    temp.getAdditionalInfo());
            data.add(dataObject);
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
        System.out.println("set markers main activity");


        Location dummyLocation = new Location("Dummy");
        Map<String, Marker> updatedBusMarkers = new HashMap<String, Marker>();
        AnimationSettings settings = new AnimationSettings()
                .duration(MARKER_UPDATE_INTERVAL).interpolator(interpolator);


        // checks if the timestamp has been updated if it has not then it will notifiy the network
        // service that xml file has not been updated
        try{

            if(buses.get(0).getTimeStamp() == lastTimeStamp ){
                networkService.sameTimeStamp();
            }

        }catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }


        // iterates through the buses found in the xml file
        System.out.println("number of buses: " + buses.size());
        for (Bus bus : buses) {

            //        Log.i(TAG, bus.toString());
            LatLng newPos = new LatLng(bus.lat, bus.lng);
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));
            // if marker does not exist, add new marker
            if ((bus_marker == null || !mMap.getDisplayedMarkers().contains(bus_marker)) && currentTab == 0 ) {
                System.out.println("bus marker is null");
                //ToDo: retrieve stops LatLng from relevant bus stops and store as data in marker for drawing routes
                //PolylineOptions stops = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(newPos)
                        .flat(true)
                        .title(bus.route)
                        .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id)));
                if (bus.color == Bus.HUE_AZURE){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                }
                else if (bus.color == Bus.HUE_ORANGE){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                }
                else if (bus.color == Bus.HUE_YELLOW){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                }
                else if (bus.color == Bus.HUE_MAGENTA){
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("magenta_marker", 64, 111)));
                }
                else{
                    System.out.println("no color found");
                    bus_marker.setIcon(BitmapDescriptorFactory.defaultMarker(bus.color));
                }
            } else {
                // if marker exists, check for changes in position
                // and for change in bus route from last update

                int typeChange = bus_marker.getPosition().equals(newPos) ? 0 : 2;
                typeChange -= bus_marker.getTitle().equals(bus.route) ? 0 : 1;
                System.out.println("type change: " + typeChange);
                switch (typeChange) {
                    case (0):
                        break;
                    case (1):
                        bus_marker.setTitle(bus.route);
                        bus_marker.setClusterGroup(bus.clusterGroup);
                        if (bus.color == Bus.HUE_AZURE){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.HUE_ORANGE){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.HUE_YELLOW){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                        }
                        else if (bus.color == Bus.HUE_MAGENTA){
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("magenta_marker", 64, 111)));
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
