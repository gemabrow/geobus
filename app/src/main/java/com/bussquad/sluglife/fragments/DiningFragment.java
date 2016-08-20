package com.bussquad.sluglife.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.Dining;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.DiningActivity;
import com.bussquad.sluglife.parser.JsonDiningFileParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class DiningFragment extends MapFragment {
    private ArrayList<Dining> dining;
    private final JsonDiningFileParser dinReader = new JsonDiningFileParser();



    public DiningFragment() {
        // Required empty public constructor
        isActivityStartable(true);
    }




    // TODO: Rename and change types and number of parameters
    public static DiningFragment newInstance(String param1, String param2) {
        DiningFragment fragment = new DiningFragment();
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
    public void loadData(Context context) {
        System.out.println("loading dining");
        InputStream in;
        // fills the array of bus stop of bus stop locations
        try {
            Log.i("DiningFragment","loadJsonFromAssets() Reading Bus Stop from Json file");
            in = context.getAssets().open("dining.json");
            dinReader.ReadJsonFile(in);
            dining = new ArrayList<>(dinReader.getDining());
            setDataLoadedStatus(true);
            context = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects = new ArrayList<MapObject>(dining);

        return mapObjects;

    }



    @Override
    public Intent getMapFragmentIntent(Context context, String title, MapObject mapObject) {

        Intent mIntent = new Intent(context, DiningActivity.class);
        mIntent.putExtra("DINING_NAME",title); //Optional parameters
        return mIntent;
    }


}
