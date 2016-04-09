package com.bussquad.geobus.activity;

/**
 * Created by Jose on 1/29/2016.
 */


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.bussquad.geobus.NotificationDbManger;
import com.bussquad.geobus.R;
import com.bussquad.geobus.fragments.NotificationFragment;
import com.bussquad.geobus.fragments.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;


public class BusStopMenuActivity extends AppCompatActivity  {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String busStopId;
    private String busStopName;
    private ArrayList<String> routes;
    // database that is called whenever a new notificaiton is selected
    private NotificationDbManger notifDb;


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialzied bus stop information
        Bundle extras= getIntent().getExtras();
        busStopName = extras.getString("bus_stop_name");
        busStopId = extras.getString("ITEMID");

        try{
            routes = new ArrayList<>(extras.getStringArrayList("ROUTES"));
        }catch(Exception ex){

        }

        System.out.println("on create Bus Stop Menu");
        setContentView(R.layout.bus_stop_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bus Stop Info");
        toolbar.setSubtitle(busStopName);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Sets up two fragments, one that will display bus stop schedule information and another
    // that will display the current notificaiton settings
    private void setupViewPager(ViewPager viewPager) {

        // Create bundle which will hold data for the Notification fragment
        // and the Schedule Fragment
        Bundle bundle = new Bundle();
        bundle.putString("ITEMID", busStopId);
        bundle.putStringArrayList("ROUTES",routes);


        // creates the Notification Fragment
        NotificationFragment notifFragment = new NotificationFragment();
        notifFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScheduleFragment(), "Schedule");
        adapter.addFragment(notifFragment, "Notification");
        viewPager.setAdapter(adapter);
    }




    // checks if the current bus stop has notifications already set up
    private void checkForNotifications(){

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }
}
