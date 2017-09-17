package com.bussquad.sluglife.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.OpersFacility;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.AppController;
import com.bussquad.sluglife.parser.JsonOpersFacilityParser;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class OpersFragment extends MapFragment {



    private ArrayList<OpersFacility> opers;
    private JsonOpersFacilityParser facilityReader = new JsonOpersFacilityParser();
    public OpersFragment() {
        // Required empty public constructor
        enableZoom();
        setZoomLevel((float)17.93418);
        setCamerLocation(new LatLng(36.99434448480415,-122.05450728535652));
        isActivityStartable(false);
        enableIconGen();
    }



    public void getOpersFacilities(){

    }



    // TODO: Rename and change types and number of parameters
    public static OpersFragment newInstance(String param1, String param2) {
        OpersFragment fragment = new OpersFragment();
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
        System.out.println("loading Opers");
        String url = "http://ec2-54-186-252-123.us-west-2.compute.amazonaws.com/facility_count.json";
        String tag_string_req = "getOperFacilityCount";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //    Log.d(TAG, response.toString());
                Log.i("OpersFragment","loadData() Reading Opers data from Json file");
                try{
                    InputStream in =  new ByteArrayInputStream(response.toString().getBytes(StandardCharsets.UTF_8));
                    System.out.println("reading input stream");
                    facilityReader.ReadJsonFile(in);
                    opers = new ArrayList<>(facilityReader.getOpersFacilities());
                    if(opers != null){
                        Intent intent = new Intent("data_ready");

                        intent.putExtra("loaded", true);
                        intent.putExtra("tab_position",getTabPosition());
                        setDataLoadedStatus(true);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }

                } catch (Exception e) {
                    System.out.println("error loading opers: " + e.getMessage());
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("OpersFragment", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        // fills the array of bus stop of bus stop locations

    }




    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects;
        if(opers == null){
            mapObjects = new ArrayList<>();

        } else {
            mapObjects = new ArrayList<MapObject>(opers);

        }

        return mapObjects;

    }






}
