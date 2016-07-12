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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.fragments.NotificationFragment;
import com.bussquad.sluglife.fragments.ScheduleFragment;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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


    // Google map
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private double initalCameraZoom = 20;

    // bus stop related varibles
    List<String> routes = new ArrayList<>();
    Integer busStopId;
    private NotificationDbManger notifDb;



    // calander related  values
    private Button btnCalendarPicker;
    private String[] monthOfTheYear = {"January","Feburary","March","April","May",
            "June","July","August","September","October","November","December"};
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // initialize the database
        notifDb =  new NotificationDbManger(getApplicationContext());
        Bundle extras= getIntent().getExtras();
        busStopName = extras.getString("bus_stop_name");
        busStopId = extras.getInt("BUSSTOPID");

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
        if (routes.size() < 4 ){
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

        public void addFragment(Fragment fragment, String title) {
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
        bundle.putInt("BUSSTOPID", busStopId);
        bundle.putStringArray("ROUTE",routes.toArray(new String[routes.size()]));

        // Create a Tab section for each route that stops  at the bus stop
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        // create a fragment for each route that stops at this bus stop
        for (String route: routes){

            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setArguments(bundle);
            scheduleFragment.setRoute(route);
            adapter.addFragment(scheduleFragment, route);

        }

        viewPager.setAdapter(adapter);
    }




    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        System.out.println(tab.getPosition());



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
            LatLng location = notifDb.getLocation(busStopId);
            Marker busStopMarker = mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .visible(true));
        }

    }




    // handle date picker stuff
    // sets the date for the calander picker that shows up above the list of schedules
    private void setDate(){
        int month = Calendar.getInstance().get(Calendar.MONTH); // prints 10 (October)
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        btnCalendarPicker.setText(monthOfTheYear[month] + " " + dayOfMonth + ", " + year);
    }




    // displays the calender in a dialogue when the user clicks on the date buttton
    public void displayCalendar() {
        // MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(now.getYear(), now.getMonthOfYear() - 2, now.getDayOfMonth());
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(ScheduleAcitivity.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setThemeLight();
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }




    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int month, int dayOfMonth) {
        btnCalendarPicker.setText(monthOfTheYear[month] + " " + dayOfMonth + ", " + year);
    }


}
