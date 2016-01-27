package com.bussquad.geobus;
/*
 This class builds an info window that is built using a fragment. It dynamically creates the fragment on top of the
 fragment_bus_stop_schedule.xml found in the resource folder. This class has several public functinons which work together to build the
 dynamic layout that changes depending on what bus stop is selected in the main activity.
 
*/


import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


public class BusScheduleFragment extends Fragment {

    private TextView busStopInfo;
    private TextView fragClose;
    private ScrollView busScheduleView;
    private View view;

    private String busName = "empty";
    private int busStopId = -1;
    private int size;
    private int layoutID = 0;

    private ArrayList<BusStopSchedule> schedules = new ArrayList<>();




    // Constructor
    public BusScheduleFragment(){

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                              Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bus_stop_schedule, container,false);
        busStopInfo = (TextView)view.findViewById(R.id.busInfo);
        fragClose = (TextView)view.findViewById(R.id.fragClose);
        fragClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeBusSchedule();

            }
        });
        busStopInfo.setText(this.busName);
        busScheduleView = (ScrollView)view.findViewById(R.id.BusScheduleScrollView);
        createLayout();
        return view;
    }



    // sets the name of the bus stop that will then be displayed in a textview when onCreateView is called
    public void setBusStopName(String busStopName){
        this.busName = busStopName;
    }




    // sets the Bus stop Id
    public void setBusStopId(int setBusStopId){
        this.busStopId = setBusStopId;
    }



    // sets an array list of schedules that will be used to populate the horzonatal Scroll view
    public void setBusSchedule(ArrayList<BusStopSchedule> busStopScheduleInfo) {
        this.schedules = new ArrayList<>(busStopScheduleInfo);
    }




    // returns the Id of the bus Stop
    public int getBusStopId(){
        return busStopId;
    }



    // returns the number of schedules in stored in the current fragment
    public int getScheduleSize(){
        return this.schedules.size();
    }




    // creates a layout and adds to the busSchduleView
    public void createLayout(){
        if (!schedules.isEmpty()){
            busScheduleView.addView(createVerLinearLayout());
        }
        else {
            busScheduleView.addView(createNoDepartTime());
        }

    }




    // creates a Linear Layout which is then returned. The LinearLayout contains several LinearLayout
    // Child. This depends on the number of buses that stop in the specified busStop. Each
    // LinearLayout Child contains a horizontal Scroll view which consists of textViews for each
    // Depart time the pertains to that bus
    public LinearLayout createVerLinearLayout (){

        int color = 0;
        LinearLayout verLayout = new LinearLayout(getActivity());
        verLayout.setOrientation(LinearLayout.VERTICAL);
        verLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        for (BusStopSchedule temp : this.schedules){
            verLayout.addView(createHorLinearLayout(temp,color));


            if (color == 0){
                color = 1;
            }
            else{
                color = 0;
            }
        }

        return verLayout;
    }




    // Returns a Horzontal linear Layout which populated with TextView which contains the name of the Bus
    // That stops the specified bus stop and also consists of a horizontal Scroll view which contains
    // several Textview which is determined by how manydepart times the specfied bus has at the
    // bus stop
    public LinearLayout createHorLinearLayout (BusStopSchedule busInfo,int color){
        LinearLayout horLayout = new LinearLayout(getActivity());
        horLayout.setSaveEnabled(true);

        if(color == 0){
            horLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorLightBabyBLueMetro));
        }
        else{
            horLayout.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorDarkBlueMetro));
        }
            horLayout.setOrientation(LinearLayout.HORIZONTAL);
            horLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            horLayout.addView(createTextViewBusName(busInfo.getName()));
            horLayout.addView(createHorScrollView(busInfo));

        return horLayout;
    }




    // returns a horizontalScrollView consisting of Textviews which is determined by how manydepart
    // times the specfied bus has at the bus stop
    public HorizontalScrollView createHorScrollView(BusStopSchedule scheduleInfo){

        HorizontalScrollView horScrollView = new HorizontalScrollView(getActivity());
        LinearLayout horLayout = new LinearLayout(getActivity());
        horLayout.setSaveEnabled(true);
        horScrollView.setSaveEnabled(true);
        horScrollView.setPadding(0, 2, 0, 0);

        horScrollView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        for (String departTime :  scheduleInfo.getBusDepartTime()){
            horLayout.addView(createTextViewDepartTime(departTime));
        }
        horScrollView.addView(horLayout);

        return  horScrollView;

    }




    // creates a Textview that display the depart times for the specified Bus
    public TextView createTextViewDepartTime(String departTime) {
        TextView txtDepartTime = new TextView(getActivity());
        txtDepartTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGoldYellowMetro));
        txtDepartTime.setText(departTime);
        txtDepartTime.setPadding(20, 0, 20, 0);
        txtDepartTime.setTextSize(30);
        txtDepartTime.setSaveEnabled(true);

        return txtDepartTime;
    }

    // creates a Textview if no depart times are available for the specified BusStop
    public TextView createNoDepartTime() {
        TextView txtDepartTime = new TextView(getActivity());
        txtDepartTime.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGoldYellowMetro));
        txtDepartTime.setText("No Schedule Available");
        txtDepartTime.setPadding(20, 0, 20, 0);
        txtDepartTime.setTextSize(50);
        txtDepartTime.setSaveEnabled(true);

        return txtDepartTime;
    }

    // creates a Textview that display the name for the specified Bus
    public TextView createTextViewBusName(String busName) {
        TextView txtBusName = new TextView(getActivity());
        txtBusName.setText(busName);
        txtBusName.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGoldYellowMetro));
        txtBusName.setPadding(10, 0, 10, 0);
        txtBusName.setTextSize(30);
        txtBusName.setGravity(Gravity.CENTER);
        txtBusName.setId(layoutID);
        txtBusName.setSaveEnabled(true);
        layoutID++;
//        txtBusName.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,30));

        txtBusName.setWidth(250);
        return txtBusName;

    }



    // Calls the closeBusStopScheduleFragment() from the MapsActivity and then closes the Fragment
    public void closeBusSchedule(){
        //((MapsActivity)getActivity()).closeBusStopScheduleFragment();
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
