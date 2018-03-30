package com.bussquad.sluglife.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.bussquad.sluglife.Bus;
import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.MapMenuItem;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.adapters.MapInfoWindowAdapter;
import com.bussquad.sluglife.adapters.OptionListAdapter;
import com.bussquad.sluglife.fragments.BusMapFragment;
import com.bussquad.sluglife.fragments.DiningFragment;
import com.bussquad.sluglife.fragments.EventFragment;
import com.bussquad.sluglife.fragments.LibraryFragment;
import com.bussquad.sluglife.fragments.MarkerFilterDialog;
import com.bussquad.sluglife.fragments.OpersFragment;
import com.bussquad.sluglife.fragments.PermissionDialog;
import com.bussquad.sluglife.utilities.NetworkService;
import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.fragments.MapFragment;
import com.bussquad.sluglife.fragments.NavListFragment;
import com.bussquad.sluglife.adapters.NavItem;
import com.bussquad.sluglife.adapters.DrawerListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
//import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.crash.FirebaseCrash;
import com.google.maps.android.ui.IconGenerator;

import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bussquad.sluglife.fragments.MapFragment.*;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        TabLayout.OnTabSelectedListener,
        GoogleMap.OnInfoWindowClickListener,
        ImageButton.OnClickListener,
        GoogleMap.OnMarkerClickListener,
        AdapterView.OnItemClickListener,
        PermissionDialog.PermissionDialogListner,
        OnFragmentInteractionListener {


    final String TAG1 = "MainActivity";
    public static MainActivity activity;
    public final static String EXTRA_INFO = "com.bussquad.geobus.INFO";

    // Static variables
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private final static int MARKER_UPDATE_INTERVAL = 2000; // in milliseconds
    private final static float ICON_DEGREES_OFFSET = 90;
    private Interpolator interpolator = new DecelerateInterpolator();


    // Android Layouts
    private TabLayout tabLayout;
    private MapViewPager viewPager;
    private ViewPagerAdapter vAdapter;
    private GoogleApiClient client;
    private Location mLastLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Button btnMenuFilter;

    // Camera Position Update
    private String REQUEST_CAMERA_UPDATE_KEY = "CAMERALOC";
    private String CAMERA_POSITION_KEY = "POSITIONKEY";
    private double initalCameraZoom = 14.333;
    private double currentZoom = 14.333;                                // not sure if that should be the value
    private LatLng cameraPosition = new LatLng(36.991406, -122.060731);
    private CameraPosition lstCameraPosition;

    // list of objects retrieved from json files
    private ArrayList<MapObject> activeMapObjects;
    private ArrayAdapter<String> mAdapter;
    private ProgressDialog pDialog;
    private ListPopupWindow listPopupWindow;

    // Parser


    // hash maps
    private Map<Marker, MapObject> objectMarkers = new HashMap<>();
    private Map<String, Marker> busMarkers = new HashMap<String, Marker>();
    private List<Map<String, MapMenuItem>> data = new ArrayList<>();

    NavListFragment eventList;
    private ArrayList<Integer> cardTypes;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    // services
    private NetworkService networkService;
    private Boolean netBound = false;
    private int lastTimeStamp = -1;


    private boolean inCardMode = false;

    // user for managing navigation drawer
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    // busses
    private Map<String, ArrayList<Bus>> activeBusses = new HashMap<>();
    public int currentTab = 0;
    private int previousTab = 0;
    // notification database
    private NotificationDbManger notifDb;

    // used to esablish a connection with the network service if network service is already running
    // it also makes it possible to detach from the service when the MapActivity isStopedlo
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
    Context context;

    String regid;
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";
    private int dbversion;


    private ImageButton btnFilter;
    private ImageButton btnSwitchView;
    private ImageButton btnShowLocation;
    private Menu menu;
    ListPopupWindow popupWindow;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        System.out.println("On Create");

        activeMapObjects = new ArrayList<>();
        activity = this;

        setContentView(R.layout.activity_main);


        notifDb = new NotificationDbManger(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);


        pDialog = new ProgressDialog(this);


        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.menu_open, R.string.menu_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        populateDrawerItems();
        DrawerListAdapter mAdapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(mAdapter);
        //addDrawerItems(); // adds elements to drawer


        LayoutInflater inflater = getLayoutInflater(); // used to display a header at the top of the drawer
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.nav_header_main, mDrawerList, false);


        viewPager = (MapViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.addOnTabSelectedListener(this);


        btnSwitchView = (ImageButton) findViewById(R.id.btnSwitchView);
        btnFilter = (ImageButton) findViewById(R.id.btnFilter);
        btnShowLocation = (ImageButton)findViewById(R.id.btnShowMyLocation);
        btnMenuFilter = (Button) findViewById(R.id.btnMenuFilter);
        btnMenuFilter.setText("");

//      Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);


        notifDb.createDataBase();

        loadJsonFromAsset();



        // set button listners
        btnSwitchView.setOnClickListener(this);
        btnMenuFilter.setOnClickListener(this);
        btnShowLocation.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        mDrawerList.setOnItemClickListener(this);

        // initialize active busses;
        activeBusses.put("INNER", new ArrayList<Bus>());
        activeBusses.put("OUTER", new ArrayList<Bus>());
//        activeBusses.put("UPPER",new ArrayList<Bus>());
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.

    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBackgroundData();

    }


    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        if (netBound) {
            networkService.mapActivityStopped();
            unbindService(serviceConnection);
            netBound = false;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }


    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("Resuming");

    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("data_ready"));
        System.out.println("Starting");
        System.out.println("current tab: " + currentTab);
//




        startBackgroundData();


    }




    // when specific tab is selected  it will check if the user is in map mode or card mode
    //  if the user is in drawmode  it will draw the corresponding markers, clear previous markers
    // if any if in card view it will update the card view to reflect the data currently being viewed
    // by the user
    @Override
    public void onTabSelected(TabLayout.Tab tab) {


        viewPager.setCurrentItem(tab.getPosition(), true);

        if (currentTab != tab.getPosition()) {
            previousTab = currentTab;
            currentTab = tab.getPosition();

            // resets the zoom from opers to span entire campus
            System.out.println("currentZoom level " + currentZoom +  " initialZoom " + initalCameraZoom);
            if (previousTab != currentTab && initalCameraZoom < currentZoom ) {
                zoomToLocation(cameraPosition, initalCameraZoom);
            }
            MapFragment mapFragment = vAdapter.getItem(currentTab);

            if (mapFragment.isDataLoaded()) {
                if (mapFragment.isZoomEnabled()) {
                    zoomToLocation(mapFragment.getCameraLocation(), mapFragment.getZoomLevel());
                }
                drawMarkers(mapFragment.getMapObjects(), mapFragment.isMarkerVisble(), mapFragment.isIconGenEnabled());
                if(mapFragment.isMenuItemsEnabled()){
                    btnMenuFilter.setText(mapFragment.getMapMenuItems().get(mapFragment.getMenuSelectedPosition()).getTitle());
                } else {
                    btnMenuFilter.setText("");
                }
                updateCardView();

            } else {

                // wait until the data loads, display a load screen
            }
        }


    }


    // when tab is unselected
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }


    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void setupViewPager(MapViewPager viewPager) {
        vAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vAdapter.addFrag(new BusMapFragment(), "Campus Shuttles");
        vAdapter.addFrag(new DiningFragment(), "Dining");
//        adapter.addFrag(new MapFragment(), "Bike Parking");
       vAdapter.addFrag(new LibraryFragment(), "Library");
        vAdapter.addFrag(new EventFragment(), "Campus Events");
        vAdapter.addFrag(new OpersFragment(), "OPERS");
        viewPager.setAdapter(vAdapter);
    }


    public void onTabSelectedListener() {

    }


    // sets up a default Tab Icons
    //// TODO: 8/27/2016  make this more modular! 
    private void setupTabIcons() {

        // custom_tab is used therfore if you want to make changes to the text color make the
        // changes in the custom_tab layout.
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Campus Shuttles");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bus_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Dining");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_restaurant_menu_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Libraries");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_local_library_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setText("Campus Events");
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_event_white_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFive);

        TextView tabSix = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSix.setText("OPERS");
        tabSix.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fitness_center_white_24dp, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabSix);


    }


    // adds  a new Tab to tablayout
    private void addTabLayout() {

    }


    // removes a the specified tab from the tablayout
    private void removeTabLayout() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSwitchView:
                switchToListView();
                break;
            case R.id.btnFilter:
                if(vAdapter.getItem(currentTab).isFilterMenuEnabled())
                {
                    showFilterMenu(vAdapter.getItem(currentTab).getFilterMenuResourceID());
                }
                break;
            case R.id.btnShowMyLocation:
                if(checkPermissionStatus(android.Manifest.permission.ACCESS_COARSE_LOCATION) && checkPermissionStatus(android.Manifest.permission.ACCESS_FINE_LOCATION)){

                if(mMap != null){


                    Location myLocation = mMap.getMyLocation();
                    if(myLocation != null){
                        LatLng myLatLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());

                        zoomToLocation(myLatLng,initalCameraZoom);
                    } else {

                    }

                } else {
                    System.out.println("requesting location permission");
                    requestLocationPermission( Manifest.permission.ACCESS_FINE_LOCATION,R.string.location,R.string.location_description);
                    requestLocationPermission( Manifest.permission.ACCESS_COARSE_LOCATION,R.string.location,R.string.location_description);

                }
                }

                break;
            case R.id.btnMenuFilter:
                if(vAdapter.getItem(currentTab).getMapMenuItems().size() == 0){
                    break;
                }
                final ListAdapter adapter = new OptionListAdapter(this,R.layout.list_menu, vAdapter.getItem(currentTab).getMapMenuItems());
//                final ListAdapter adapter =  new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"0", "1", "2"});

                final AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        MapMenuItem menuItem = vAdapter.getItem(currentTab).getMapMenuItems().get(position);

                        listPopupWindow.dismiss();
                        btnMenuFilter.setText(menuItem.getTitle());
                        vAdapter.getItem(currentTab).setMenuSelectedPosition(position);
                        drawMarkers(vAdapter.getItem(currentTab).getMapObjects(),true,false);

                        if(inCardMode){
                            updateCardView();
                        }

                    }
                };
                int selected = vAdapter.getItem(currentTab).getMenuSelectedPosition();
                if(selected != -1){
                    ((OptionListAdapter)adapter).setSelectedItem(selected);
                }
                showPopupList(v, adapter, itemClickListener);
                break;

        }

    }

    private void showFilterMenu(int resourceID){
        MarkerFilterDialog newFragment = new MarkerFilterDialog();
        newFragment.setArrayResourceID(resourceID);
        newFragment.show(getFragmentManager(),"Filter");
    }

    // displays the popup menu list
    private void showPopupList(View anchorView, ListAdapter adapter, AdapterView.OnItemClickListener itemClickListener) {

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 432, r.getDisplayMetrics());
        listPopupWindow = new ListPopupWindow(this,null,0,R.style.ListPopUpWindowStyle);
        listPopupWindow.setBackgroundDrawable(getDrawable(R.color.windowBackground));
        listPopupWindow.setModal(true);
        // listPopupWindow.setListSelector(getResources().getDrawable(R.drawable.btn_borderless));
        listPopupWindow.setAdapter(adapter);
        if(adapter.getCount() >= 9){
            listPopupWindow.setHeight((int)px);
        }
        listPopupWindow.setOnItemClickListener(itemClickListener);
        listPopupWindow.setAnchorView(anchorView);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        listPopupWindow.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast toast;
        int icon = -1;
        if(parent.getAdapter().getClass().equals(DrawerListAdapter.class)){
            icon = mNavItems.get(position).getIcon();
        } else {
        }
        switch (icon) {
            case R.drawable.ic_home_black_24dp:
                toast = Toast.makeText(context, "Home", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_search_black_24dp:
                toast = Toast.makeText(context, "Search", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_directions_bus_black_24dp:
                toast = Toast.makeText(context, "Bus Schedule", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_person_black_24dp:
                toast = Toast.makeText(context, "Account", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_notifications_black_24dp:
                toast = Toast.makeText(context, "Notifications", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_schedule_black_24dp:
                toast = Toast.makeText(context, "Class Schedule", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.drawable.ic_help_black_24dp:
                //  FirebaseCrash.logcat(Log.INFO,TAG,"Crash button clicked");
                //FirebaseCrash.report(ex);

                break;
            case R.drawable.ic_settings_black_24dp:
                break;
            case R.drawable.ic_check_box_black_24dp:
                toast = Toast.makeText(context, "Sign In", Toast.LENGTH_SHORT);
                toast.show();
                break;


        }
    }


    // Called when the dialogfragment agree button is clicked
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }


    // called when the dialog fragment disagress button is clicked
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<MapFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public MapFragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFrag(MapFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }




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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.cameraPosition, (float) this.initalCameraZoom));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // if the version of the phone is 6.0 or greater ask the user for permission to get location
        // information from the user
        if (Build.VERSION.SDK_INT > 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                FirebaseCrash.log(TAG + " Access Coarse Location and Acess " +
                        "Fine Location permissions has not been granted. Requesting permissions");

                requestLocationPermission( android.Manifest.permission.ACCESS_COARSE_LOCATION,R.string.location,R.string.location_description);
                requestLocationPermission( Manifest.permission.ACCESS_FINE_LOCATION,R.string.location,R.string.location_description);

            }
        } else {
            System.out.println("permissions have already been requested");
            mMap.setMyLocationEnabled(true);
        }


        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(getBaseContext()));


        drawMarkers(vAdapter.getItem(0).getMapObjects(),
                vAdapter.getItem(0).isMarkerVisble(),
                vAdapter.getItem(0).isIconGenEnabled());

    }

    public boolean checkPermissionStatus(String permission){
        if (Build.VERSION.SDK_INT > 23) {
            if (ActivityCompat.checkSelfPermission(this,permission
                    ) != PackageManager.PERMISSION_GRANTED ) {

                requestLocationPermission( android.Manifest.permission.ACCESS_COARSE_LOCATION,R.string.location,R.string.location_description);

            }
        }
        return true;
    }

    public void requestLocationPermission(String permission,int title, int permission_msg) {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,permission) ){
            DialogFragment newFragment = PermissionDialog.newInstance(R.string.location_permission_title, R.string.location_permission_msg);
            newFragment.show(getSupportFragmentManager(), "dialog");


        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // enable location dectecter
                    mMap.setMyLocationEnabled(true);
                    System.out.println("permissions requested, location set");

                } else {

                    // permission denied, Disable location detecter .
                    mMap.setMyLocationEnabled(false);

                }
                return;
            }
        }
    }


    public void hideAllMapObjectMarkers(){
       for(Marker marker : mMap.getMarkers()){

       }
    }



    public void drawMarkers(ArrayList<?> mapObjects, boolean visible, boolean enableIconGen) {
        activeMapObjects = (ArrayList<MapObject>)mapObjects;
        mMap.clear();
        IconGenerator iconGen = null;  // custom icon for displaying text
        if (!objectMarkers.isEmpty()) {
            objectMarkers.clear();
        }

        if (vAdapter.getItem(currentTab).isIconGenEnabled()) {
            iconGen = new IconGenerator(this);
            iconGen.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_blank_icon, getTheme()));
            //  iconGen.setColor(R.color.windowBackground);

        }
        for (MapObject temp : activeMapObjects) {
            BitmapDescriptor icon;
            if (iconGen != null) {
                icon = BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(temp.getAdditionalInfo()));
            } else {
                icon = BitmapDescriptorFactory.fromResource(temp.getMapImgResource());
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(temp.getLatitude(), temp.getLongitude()))
                    .title(temp.getName())
                    .snippet(temp.getObjectID())
                    .visible(visible)
                    .icon(icon));


            objectMarkers.put(marker, temp);
        }
    }


    // zoom and move camera to new location
    public void zoomToLocation(LatLng newCameraLoc, double zoom) {

        float cameraZoom = (float) zoom;
        currentZoom = zoom;
        // smooth zoom transition
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCameraLoc, cameraZoom), 2000, null);

    }


    // load all data for each tab that is enabled by the user
    private void loadJsonFromAsset() {


        for (int count = 0; count < tabLayout.getTabCount(); count++) {
            vAdapter.getItem(count).loadData(this);
            vAdapter.getItem(count).setTabPosition(count);
        }


    }


    // populates the navigation drawer menu with options
    private void populateDrawerItems() {
        mNavItems.add(new NavItem("First Lastname", "sammytheslug@gmail.com", R.drawable.ic_account_circle_black_48dp));
        mNavItems.add(new NavItem("Home", false, R.drawable.ic_home_black_24dp));
        mNavItems.add(new NavItem("Search", false, R.drawable.ic_search_black_24dp));
        mNavItems.add(new NavItem("Bus Schedule", false, R.drawable.ic_directions_bus_black_24dp));
//        mNavItems.add(new NavItem(true));               // is a divider
//        mNavItems.add(new NavItem("My Account", true, -1));
//        mNavItems.add(new NavItem("My Profile", false, R.drawable.ic_person_black_24dp));
//        mNavItems.add(new NavItem("Notifications", false, R.drawable.ic_notifications_black_24dp));
//        mNavItems.add(new NavItem("Class Schedule", false, R.drawable.ic_schedule_black_24dp));
        mNavItems.add(new NavItem(true));               // is a divider
        mNavItems.add(new NavItem("Help & Support", true, -1));
        mNavItems.add(new NavItem("Help Center", false, R.drawable.ic_help_black_24dp));
        mNavItems.add(new NavItem("Settings", false, R.drawable.ic_settings_black_24dp));
  //      mNavItems.add(new NavItem("Sign In", false, R.drawable.ic_check_box_black_24dp));
    }


    // closes hamburger menu when users presses the back button on there phone
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 && !mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            getFragmentManager().popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE); // close fragment if ONLY the fragment is open
        } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START) && getFragmentManager().getBackStackEntryCount() == 0) {
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

        lstCameraPosition = mMap.getCameraPosition();
        MapFragment mapFragment = vAdapter.getItem(currentTab);
        if (mapFragment.isActivityStartable()) {
            Intent mIntent = mapFragment.getMapFragmentIntent(this, marker.getTitle(), objectMarkers.get(marker));
            MainActivity.this.startActivity(mIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out_in_place);
        }

    }


    // hides the map in the background and draws list view in front of the map
    public void switchToListView() {
        Log.i("MainActivity", "switching fragments");

        if (inCardMode == false) {
            vAdapter.getItem(currentTab).setCardMode(true);
            inCardMode = true;
            btnSwitchView.setImageResource(R.drawable.ic_map_white_24dp);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("OBJECT_LIST", getData());
            bundle.putIntegerArrayList("TYPE_LIST", cardTypes);

            eventList = new NavListFragment();
            eventList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.map, eventList).commit();
        } else {
            vAdapter.getItem(currentTab).setCardMode(false);
            inCardMode = false;
            btnSwitchView.setImageResource(R.drawable.ic_format_list_bulleted_white_24dp);

            getSupportFragmentManager().beginTransaction().remove(eventList).commit();
        }

    }


    // updates the recycleview based on the tab currently selected
    // precondition eventList must be initialized
    public void updateCardView() {
        if (eventList != null) {
            eventList.updateRecycleViewData(getData(), cardTypes);
        }
    }




    // translate the current markers to recycleview
    public ArrayList<DataObject> getData() {

        ArrayList<DataObject> data = new ArrayList<>();
        cardTypes = new ArrayList<>();

        for (MapObject temp : activeMapObjects) {
            DataObject dataObject = new DataObject(temp.getObjectID(),
                    temp.getImageResource(),
                    temp.getName(),
                    temp.getCardSubText1(),
                    temp.getCardSubText2());

            // if the object has a thumbnail associated with it then return the url for the thumbnail
            // or if the object has a larger view associated with it
            if (temp.getThumbNailUrl() != null) {
                dataObject.setImageUrl(temp.getThumbNailUrl());
            } else if (temp.getFullViewImageUrl() != null) {
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
        try {

            if (buses.get(0).getTimeStamp() == lastTimeStamp) {
                networkService.sameTimeStamp();
            }

        } catch (Exception ex) {
        }


        // iterates through the buses found in the xml file
        for (Bus bus : buses) {

            //        Log.i(TAG, bus.toString());
            LatLng newPos = new LatLng(bus.lat, bus.lng);
            Marker bus_marker = busMarkers.get(Integer.toString(bus.bus_id));

            // allocate to apprioprite active bus stype;
            if (activeBusses.containsKey(bus.direction)) {
                activeBusses.get(bus.direction).add(bus);
            }

            // if marker does not exist, add new marker
            if ((bus_marker == null || !mMap.getDisplayedMarkers().contains(bus_marker)) && currentTab == 0) {
                //ToDo: retrieve stops LatLng from relevant bus stops and store as data in marker for drawing routes
                //PolylineOptions stops = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(newPos)
                        .flat(true)
                        .title(bus.route)
                        .clusterGroup(ClusterGroup.NOT_CLUSTERED)
                        .snippet("Bus ID: " + Integer.toString(bus.bus_id)));
                if (bus.color == Bus.BLUE) {
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                } else if (bus.color == Bus.ORANGE) {
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                } else if (bus.color == Bus.YELLOW) {
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                } else if (bus.color == Bus.GREY) {
                    bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("grey_marker", 64, 111)));
                } else {
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
                        if (bus.color == Bus.BLUE) {
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("blue_marker", 64, 111)));
                        } else if (bus.color == Bus.ORANGE) {
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orange_marker", 64, 111)));
                        } else if (bus.color == Bus.YELLOW) {
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellow_marker", 64, 111)));
                        } else if (bus.color == Bus.GREEN) {
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("green_marker", 64, 111)));
                        } else if (bus.color == Bus.GREY) {
                            bus_marker.setIcon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("grey_marker", 64, 111)));
                        } else {
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

        Intent intent = new Intent(this, NetworkService.class);

        if (!isServiceRunning(NetworkService.class)) {

            intent.putExtra("MAINACTIVITY", true);
            this.startService(intent);
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }


    public void stopBackgroundData() {
        if (!isServiceRunning(NetworkService.class)) {
            this.stopService(new Intent(this, NetworkService.class));
        }
//        locationHandler.removeCallbacks(updateMarkers);
    }




    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }




    @Override
    public boolean onMarkerClick(Marker marker) {
        MapInfoWindowAdapter mapInfoW = new MapInfoWindowAdapter(getBaseContext());
        mapInfoW.setMainInfo("Eta: stuff");
        mMap.setInfoWindowAdapter(mapInfoW);

        return false;
    }




    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Boolean isReady = intent.getBooleanExtra("loaded", true);
            int tabPosition = intent.getIntExtra("tab_position", -1);
            if (isReady && currentTab == tabPosition) {
                MapFragment mapFragment = vAdapter.getItem(currentTab);
                drawMarkers(mapFragment.getMapObjects(), mapFragment.isMarkerVisble(), mapFragment.isIconGenEnabled());
                updateCardView();
            }
        }
    };






}
