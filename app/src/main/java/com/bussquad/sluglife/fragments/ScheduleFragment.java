package com.bussquad.sluglife.fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.NotificationDbManger;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.RecyclerViewAdapter;
import com.bussquad.sluglife.parser.JsonFileReader;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



public class ScheduleFragment extends Fragment
{


    private RecyclerView recyleview;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> listObject =  new ArrayList<>();
    private ArrayList<Integer>  viewType = new ArrayList<>();
    private int stopID;
    private String route;
    private NotificationDbManger notifDb;
    private boolean dataSet = false;





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

        // get bus stop id
        stopID = getArguments().getInt("BUSSTOPID");
       // route = getArguments().getString("ROUTE");



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadBusSchedule();

        recyleview = (RecyclerView) layout.findViewById(R.id.bus_schedule_recyleview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyleview.setLayoutManager(linearLayoutManager);


        mAdapter = new RecyclerViewAdapter(getActivity().getBaseContext(),listObject);
        if(dataSet){
            mAdapter.setDataSetTypes(viewType);
        }

        recyleview.setAdapter(mAdapter);

        // initialize the adapter
        return layout;
    }




    // retrieves the bus schedule from the sqlite database, if there is no schedule it will show nothing
    public void loadBusSchedule(){
        if(!dataSet) {

            try {


                if(notifDb.hasSchedule(this.stopID,this.route)){
                    System.out.println("getting bus schedule from mysql ");
                    DownloadBusSchedule busSchedule = new DownloadBusSchedule(this.stopID,this.route);
                    busSchedule.execute();
                } else {
                    setBusSchedule( new ArrayList<>(notifDb.getStopScheduleForRoute(this.stopID, this.route)));
                }
                dataSet = true;
            } catch (Exception ex) {
                FirebaseCrash.log("ScheduleFragment.java There was an error retriveing schedule information for " + route);
            }
        }


    }

    public void setBusSchedule(ArrayList<String> schedules){

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
                return jsonData.getData();

            }catch(Exception e){
                System.out.println("error retrieving data");
                return null;
            } finally {
            }

        }





    }
}
