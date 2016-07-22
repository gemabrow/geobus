/*
    The Fragment displays the bus schedule in a recyecle view. It first checks if the schedule for the route
    exists in the sqlite database stored locally on the phone. If it doesnt it will check the mysql server
    for the schedule and if it finds one it will download the schedule and store in the sqlite database
    and finally display it in the recycleview with the bus schedule. If there already exists a bus schedule
    it will check for an updated schedule in the mysql server and updates the sqlite database
    stored locally on the phone. If there are no updates to bus schedules the fragment will check if

 */
package com.bussquad.sluglife.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.RecyclerViewAdapter;
import com.bussquad.sluglife.TimeHelper;
import com.bussquad.sluglife.parser.JsonFileReader;
import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.crash.FirebaseCrash;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class ScheduleFragment extends Fragment
{


    private RecyclerView recyleview;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> listObject =  new ArrayList<>();
    private ArrayList<Integer>  viewType = new ArrayList<>();
    private ArrayList<String> schedule = new ArrayList<>();
    private int stopID;
    private String route;
    private NotificationDbManger notifDb;
    private boolean dataSet = false;
    private String eta;
    private TimeHelper timeHelper = new TimeHelper();





    public ScheduleFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifDb =  new NotificationDbManger(getContext());

        // create database if it does not already exist
        notifDb.createDataBase();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_schedule, container, false);

        // get bus stop id, this will be used to obtain the bus schedule from either the sqlite
        // database or the mysql database
        stopID = getArguments().getInt("BUSSTOPID");


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load the bus schedule
        loadBusSchedule();

        recyleview = (RecyclerView) layout.findViewById(R.id.bus_schedule_recyleview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyleview.setLayoutManager(linearLayoutManager);


        mAdapter = new RecyclerViewAdapter(getActivity().getBaseContext(),listObject);
        if(dataSet){
            mAdapter.setDataSetTypes(viewType);
        }

        // set the adapter at this point the Adapter can contain either 0 items in the list or
        // multiple
        recyleview.setAdapter(mAdapter);

        return layout;
    }







    // retrieves the bus schedule from the sqlite database, if there is no schedule it will show nothing
    public void loadBusSchedule(){
        if(!dataSet) {

            try {


                // check if the bus schedule exists in the sqlite database
                if(notifDb.hasSchedule(this.stopID,this.route)){
                    notifDb.hasLatestScheduleVersion(this.stopID,this.route);
                    DownloadBusSchedule busSchedule = new DownloadBusSchedule(this.stopID,this.route);
                    busSchedule.execute();
                } else {
                    schedule = new ArrayList<>(notifDb.getStopScheduleForRoute(this.stopID, this.route));
                    setBusSchedule(schedule);
                }
                dataSet = true;
            } catch (Exception ex) {
                FirebaseCrash.log("ScheduleFragment.java There was an error retriveing schedule information for " + route);
            }
        }


    }

    public void setBusSchedule(ArrayList<String> schedules){

        schedule = new ArrayList<>(schedules);
        try {

            if(schedules == null){
                FirebaseCrash.log("Bus schedule missing for stop " + route);
                DataObject dataObject = new DataObject();
                dataObject.setMainText("No Bus Schedule available for this Route");
                listObject.add(dataObject);
                viewType.add(4);
            } else {

                for (String route : schedules) {
                    DataObject dataObject = new DataObject();
                    dataObject.setMainText(route);
                    listObject.add(dataObject);
                    viewType.add(3);
                }
            }


            mAdapter.setDataSetTypes(viewType);
            mAdapter.notifyDataSetChanged();

        } catch (Exception ex){

            System.out.println("error findinng closest time: " +  ex.getMessage());
            FirebaseCrash.report(ex);
        }
    }




    public void setRoute(String route){
        this.route = route;
    }

    private class DownloadBusSchedule extends AsyncTask<String,String,ArrayList<String>>    {
        /* milliseconds */
        public static final int CONNECTION_TIMEOUT=10000;
        public static final int READ_TIMEOUT=15000;

        private int stopid;
        private String route;

        //ProgressDialog pdLoading = new ProgressDialog(ScheduleFragment.this);

        DownloadBusSchedule(int stopid, String route){
            this.stopid = stopid;
            this.route = route;
        }
        @Override
        protected void onPostExecute(ArrayList<String> schedules) {
            super.onPostExecute(schedules);

            setBusSchedule(schedules);

        }





        // retrieve bus schedule from the mysql database
        @Override
        protected ArrayList<String> doInBackground(String... params) {

            try {
                URL url = new URL("http://ec2-54-186-252-123.us-west-2.compute.amazonaws.com/scripts/bus_schedule_data.php?routename=" + this.route
                        +"&stopid="+this.stopid);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(CONNECTION_TIMEOUT);
                conn.setConnectTimeout(READ_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                JsonFileReader jsonData = new JsonFileReader();
                jsonData.readJsonStream(conn.getInputStream());
                // set the bus schedule to the sqlite database
                notifDb.addBusSchedule(this.stopid,this.route,jsonData.getRawData());
                return jsonData.getData();

            }catch(Exception e){
                System.out.println("error retrieving data");
                return null;
            } finally {
            }

        }

    }






    private void setEta(String eta){


        this.eta = eta;
    }

    public String  getETAofNextBus(){
        long closestTime;
        long rt;
        closestTime = timeHelper.getClosestTimeInMillaseconds(schedule);
        rt = timeHelper.getReminingTime(closestTime);
        this.eta = timeHelper.getTimeRemainingAsString(rt);
        return this.eta;

    }

}
