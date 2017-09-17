package com.bussquad.sluglife.activity;
/*
    This Activity is responsible for creating multple fragments that will display the schedule for
    each route that stops at the bus stop
    To determine the bus stop when the activity is created it uses an intent to get the id of the
    bus stop the Activity will be targeting.
    Accesses Mysql server to get the bus schedule for each bus stop
 */




import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.fragments.ScheduleFragment;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.crash.FirebaseCrash;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleAcitivity extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        OnMapReadyCallback,
        CalendarDatePickerDialogFragment.OnDateSetListener,
        Button.OnClickListener {

    // layout related variables
    private static final String TAG = "ScheduleActivity";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String busStopName;
    private TextView txtEta;
    private int day;
    private int year;
    private int month;

    private int tab = 0;

    // Google map
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private double initalCameraZoom = 20;

    // bus stop related varibles
    List<String> routes = new ArrayList<>();
    String busStopId;
    private NotificationDbManger notifDb;
    ViewPagerAdapter adapter;


    // calander related  values
    private Button btnCalendarPicker;
    private String[] monthOfTheYear = {"January","Feburary","March","April","May",
            "June","July","August","September","October","November","December"};
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        txtEta = (TextView)findViewById(R.id.txt_eta);

        // initialize the database
        notifDb =  new NotificationDbManger(getApplicationContext());
        Bundle extras= getIntent().getExtras();
        busStopName = extras.getString("bus_stop_name");
        busStopId = extras.getString("BUSSTOPID");

        // check for routes that stop at the bus stop
        try{
            routes = notifDb.getBusStopRoute(busStopId);
        }catch(Exception ex){
            System.out.println("no routes found");
        }

        // setup the map if map is not already created
        setupMapIfNeeded();

        // setup toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(busStopName);
        setSupportActionBar(toolbar);


        // make it so the user can return back to the view they were in previously
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        float myTabLayoutSize = 360;
        // if the number of tabs is less than 5 set tab mode as fixed other wise make the tabs scrollable
        if (routes.size() < 5 ){
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        }
        tabLayout.addOnTabSelectedListener(this);



        View calendarLayout = findViewById(R.id.calendar_date_picker); // root View id from that link
        btnCalendarPicker = (Button) calendarLayout.findViewById(R.id.btnCalendarPicker);
        setDate();
        btnCalendarPicker.setOnClickListener(this);


    }




    private void setupMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            // Try to obtain the map from the SupportMapFragment.
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_bus_schedule));
          try{
              mapFragment.getMapAsync(this);

          } catch (Exception ex){
          }
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                System.out.println("map is already set");
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCalendarPicker:
                displayCalendar();
                break;
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }




    @Override
    protected void onStop() {
        super.onStop();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    private MapFragment getMapFragment() {
        android.app.FragmentManager fm = null;

        Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "release: " + Build.VERSION.RELEASE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            fm = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
            fm = getFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.fade_in_place,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    // Creates the tabs for each fragment
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<ScheduleFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public ScheduleFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(ScheduleFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }



    // Sets up two fragments, one that will display bus stop schedule information and another
    // that will display the current notificaiton settings
    private void setupViewPager(ViewPager viewPager) {

        // Create bundle which will hold data for the Notification fragment
        // and the Schedule Fragment
        Bundle bundle = new Bundle();
        bundle.putString("BUSSTOPID", busStopId);
        bundle.putStringArray("ROUTE",routes.toArray(new String[routes.size()]));

        // Create a Tab section for each route that stops  at the bus stop
         adapter = new ViewPagerAdapter(getSupportFragmentManager());


        // create a fragment for each route that stops at this bus stop
        for (String route: routes){

            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setArguments(bundle);
            scheduleFragment.setRoute(route);
            scheduleFragment.setDate(Calendar.getInstance().get(Calendar.YEAR)
                    ,Calendar.getInstance().get(Calendar.MONTH) + 1
                    ,Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            adapter.addFragment(scheduleFragment, route);

        }

        viewPager.setAdapter(adapter);
    }




    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        this.tab =  tab.getPosition();
        String eta = adapter.getItem(tab.getPosition()).getETAofNextBus();
        txtEta.setText(eta);

    }




    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }




    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }



    // initialize the map. Zoom in to map to incompase the marker that represents the marker
    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println("onMapReady");
        if(googleMap == null){
            System.out.println("unable to create map");
        } else {
            mMap = googleMap;
            System.out.println("stop id being read " + busStopId);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(notifDb.getLocation(busStopId),
                    (float)initalCameraZoom));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }


        createBusStopMarker();
    }




    // create the marker that will represent the bus stop, This will throw an error if the bus stop
    // does not have an id
    private void createBusStopMarker(){
        if (busStopId == null ){
            FirebaseCrash.log("Bus stop Id is null");
        } else {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_busstop_marker);
            LatLng location = notifDb.getLocation(busStopId);
            Marker busStopMarker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(icon)
                    .visible(true));
        }

    }




    // handle date picker stuff
    // sets the date for the calander picker that shows up above the list of schedules
    private void setDate(){
        this.month = Calendar.getInstance().get(Calendar.MONTH); // prints 10 (October)
        this.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        this.year = Calendar.getInstance().get(Calendar.YEAR);
        btnCalendarPicker.setText(monthOfTheYear[month] + " " + day + ", " + year);
    }




    // displays the calender in a dialogue when the user clicks on the date buttton
    public void displayCalendar() {
        // MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(now.getYear(), now.getMonthOfYear() - 2, now.getDayOfMonth());
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(ScheduleAcitivity.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(year,month,day)
                .setThemeLight();
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }




    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int month, int dayOfMonth) {

        // update the global date
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;

        // set the text of the button to reflect the day that the user clicked on the calendar
        btnCalendarPicker.setText(monthOfTheYear[month] + " " + dayOfMonth + ", " + year);

        // update the date for each schedule fragment
        for(int count = 0 ; count < tabLayout.getTabCount(); count++){
            adapter.getItem(count).setDate(year,month+1,dayOfMonth);
        }
        // manually update currently selected route
        adapter.getItem(tab).loadBusSchedule();

    }


    private final static String BUNDLE_KEY_MAP_STATE = "mapData";


}
