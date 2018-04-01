package com.bussquad.sluglife.activity;

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
import android.view.MenuItem;

import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.fragments.NotificationFragment;
import com.bussquad.sluglife.fragments.ScheduleFragment;

import java.util.ArrayList;
import java.util.List;


public class BusStopMenuActivity extends AppCompatActivity  {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int busStopId;
    private String busStopName;
    private ArrayList<String> routes;
    // database that is called whenever a new notification is selected
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.fade_in_place,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialzied bus stop information
        Bundle extras= getIntent().getExtras();
        busStopName = extras.getString("bus_stop_name");
        busStopId = extras.getInt("BUSSTOPID");
        System.out.println("In Create: " + busStopId);

        try{
            routes = new ArrayList<>(extras.getStringArrayList("ROUTES"));
        }catch(Exception ex){

        }

        setContentView(R.layout.bus_stop_menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bus Stop Info");
        toolbar.setSubtitle(busStopName);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        bundle.putInt("BUSSTOPID", busStopId);
        System.out.println(busStopId);
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
