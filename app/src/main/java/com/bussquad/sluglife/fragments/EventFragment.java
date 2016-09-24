package com.bussquad.sluglife.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bussquad.sluglife.Event;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.AppController;
import com.bussquad.sluglife.activity.EventActivity;
import com.bussquad.sluglife.parser.JsonEventJsonParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends MapFragment {
    private ArrayList<Event> events;
    private JsonEventJsonParser eventReader = new JsonEventJsonParser();

    public EventFragment() {
        // Required empty public constructor
        isActivityStartable(true);
        enableFilterMenuOptions();
        setFilterMenuResourceId(R.array.event_filter_options);
    }




    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.display_object_loc, container, false);
    }




    @Override
    public void loadData(final Context context) {
        System.out.println("loading event");
        String url = "https://events.ucsc.edu/feed/featured.json";
        String  tag_string_req = "string_req";
        System.out.println("loading event data");
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //    Log.d(TAG, response.toString());
                Log.i("EventFragment","loadData() Reading Event data from web based Json file");
                try{
                    System.out.println("trying to load event data");
                    InputStream in =  new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
                    eventReader.ReadJsonFile(in,context);
                    events = new ArrayList<>(eventReader.getEvents());
                    if(events != null){
                        System.out.println("Event data has loaded");
                        Intent intent = new Intent("data_ready");

                        intent.putExtra("loaded", true);

                        intent.putExtra("tab_position",getTabPosition());
                        setDataLoadedStatus(true);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        System.out.println("sent broadcast, there are " + events.size() + "events ");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error loading data+ " + e.getMessage());
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("EventFragment", "Error: " + error.getMessage());
                System.out.println("error loading event data + " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        // fills the array of bus stop of bus stop locations

    }




    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects;
        if(events == null){
            mapObjects = new ArrayList<>();

        } else {
            mapObjects = new ArrayList<MapObject>(events);

        }

        return mapObjects;

    }




    @Override
    public Intent getMapFragmentIntent(Context context,String title,MapObject mapObject) {

        Intent mIntent = new Intent(context, EventActivity.class);
        mIntent.putExtra("EVENTID",mapObject.getObjectID()); //Optional parameters
        return mIntent;
    }









}
