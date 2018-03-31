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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//  The ScheduleFragment creates a card with information about the bus schedule
public class ScheduleFragment extends Fragment
{


    private RecyclerView recyleview;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> listObject =  new ArrayList<>();
    private ArrayList<Integer>  viewType = new ArrayList<>();
    private ArrayList<String> schedule = new ArrayList<>();
    private String stopID;
    private String route;
    private NotificationDbManger notifDb;
    private boolean dataSet = false;
    private String eta;
    private TimeHelper timeHelper = new TimeHelper();
    private String date;

    private String tag = "ScheduleFragment.java";


    public ScheduleFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifDb =  new NotificationDbManger(getContext());

        // create database if it does not already exist
       // notifDb.createDataBase();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_schedule, container, false);

        // get bus stop id, this will be used to obtain the bus schedule from either the sqlite
        // database or the mysql database
        stopID = getArguments().getString("BUSSTOPID");


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
//        if(!dataSet) {
        System.out.println("ScheduleFragment loadingbusschedule().java loading bus schedule");
            try {


                // check if the bus schedule does not exist in the sqlite database
                if(!notifDb.hasSchedule(this.stopID,this.route)){
                    notifDb.hasLatestScheduleVersion(this.stopID,this.route);
                    DownloadBusSchedule2 busSchedule2 = new DownloadBusSchedule2(this.stopID,this.route);
                    busSchedule2.execute();
                } else {
                    // if the bus schedule has already been retrieved previously it will be cached in
                    // the sqlite database. This next step will load the schedule from the sqlite db
                    schedule = new ArrayList<>(notifDb.getStopScheduleForRoute(this.stopID, this.route));
                    setBusSchedule(schedule);
                }
                dataSet = true;
            } catch (Exception ex) {
                Log.i(tag,ex.toString());
                FirebaseCrash.log("ScheduleFragment.java There was an error retriveing schedule information for " + route);
            }
//        }


    }

    public void setBusSchedule(ArrayList<String> schedules){

        listObject.clear();
        viewType.clear();
        schedule = new ArrayList<>(schedules);
        System.out.println("retrieving bus schedule");
        try {

            // there was an error retrieving the bus departures
            if(schedules == null){
                Log.i("ScheduleFragment.java","error retriving schedule");
                FirebaseCrash.log("Bus schedule missing for stop " + route);
                DataObject dataObject = new DataObject();
                dataObject.setMainText("No Bus Schedule available for this Route");
                listObject.add(dataObject);
                viewType.add(4);
                // there are no more bus departures at the stop for the given day
            } else if(schedules.size() == 0){
                DataObject dataObject = new DataObject();
                dataObject.setMainText("There are no more depart times for this route at this time");
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

    private class DownloadBusSchedule2 extends AsyncTask<String,String,ArrayList<String>>    {
        /* milliseconds */

        private String stopid;
        private String route;


        DownloadBusSchedule2(String stopid, String route){
            this.stopid = stopid;
            this.route = route;
        }
        @Override
        protected void onPostExecute(ArrayList<String> schedules) {
            super.onPostExecute(schedules);

            setBusSchedule(schedules);


        }





        // retrieve bus schedule from the Santa Cruz Metro WebSite by parsing the website.
        // it uses an asynchornous method of retriving the bus  schedule from the Santa cruz metro site
        @Override
        protected ArrayList<String> doInBackground(String... params) {

            ArrayList<String> schedule = new ArrayList<>();
            try {

                URL url = new URL("http://scmtd.com/en/routes/schedule-by-stop/"+stopid+"/" + date + "#tripDiv");
                Document doc = Jsoup.parse(url,3000);
                Element table = doc.select("table").get(0); //select the first table.
                Elements rows = table.select("tr");



                for (int i = 2; i < rows.size(); i++) { //first row is the col names so skip it.

                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    Elements colh = row.select("th");

                    // gets the column with the depart time
                    if(colh.select("a").text().equalsIgnoreCase(route)){
                        String tmpTime;
                        if(cols.get(1).text().contains(" ")){
                            tmpTime  = cols.get(1).text().split(" ")[1];
                        } else {
                            tmpTime  = cols.get(1).text();
                        }
                        String finalTime = "";

                        // clean up the text of the depart time
                        if(!tmpTime.isEmpty()){
                            if(tmpTime.contains("pm")){

                                finalTime = tmpTime.replace("pm", " PM");
                            } else {
                                finalTime = tmpTime.replace("am", " AM");
                            }
                        }
                        // add the depart time to the schedule list
                        System.out.println("schedule value: " + finalTime);
                        schedule.add(finalTime.trim());
                    }

                }

                return schedule;
            }catch(Exception e){
                System.out.println("ScheduleFragment.java unable to load bus schedule");
                Log.e("ERROR_debug",e.getMessage());
                return schedule;
            } finally {
            }

        }

    }



    // set the eta for the next bus  depart time
    private void setEta(String eta){

        this.eta = eta;
    }



    // get the ETA of the next bus stop
    public String  getETAofNextBus(){

        if(schedule.size() == 0 ){
            return "";
        }
        long closestTime;
        long rt;
        closestTime = timeHelper.getClosestTimeInMillaseconds(schedule);
        rt = timeHelper.getReminingTime(closestTime);
        this.eta = timeHelper.getTimeRemainingAsString(rt);
        return this.eta;

    }



    // this function is called from the parent activity, this sets or updates the date so that the
    // bus schedule can reflect the schedule of the date picked by the user
    public void setDate( int year,int month, int day){

        String date = "";
        date += year+"-";
        if(month < 10){
            date += "0"+month+"-";
        } else {
            date +=month+"-";
        }
        if(day < 10){
            date +="0"+day;
        } else {
            date +=day;
        }
        this.date = date;

    }


    // returns in the current date in the follign format "year-month-day"
    public String getDate(){
        return this.date;
    }

}
