package com.bussquad.sluglife.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.Library;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.LibraryActivity;
import com.bussquad.sluglife.parser.JsonLibraryFileParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class LibraryFragment extends MapFragment {
    private ArrayList<Library> libraries;
    private final JsonLibraryFileParser libraryReader = new JsonLibraryFileParser();
    public LibraryFragment() {
        // Required empty public constructor
        isActivityStartable(true);
    }




    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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

        InputStream in;
        // fills the array of bus stop of bus stop locations
        try {
            Log.i("LibraryFragment","loadData() Reading Library data from Json file");
            in = context.getAssets().open("libraries.json");
            libraryReader.ReadJsonFile(in);
            libraries = new ArrayList<>(libraryReader.getLibraries());
            setDataLoadedStatus(true);
            context = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public ArrayList<MapObject> getMapObjects() {

        ArrayList<MapObject> mapObjects = new ArrayList<MapObject>(libraries);

        return mapObjects;

    }



    @Override
    public boolean isActivityStartable(){
        return true;
    }




    @Override
    public Intent getMapFragmentIntent(Context context, String title, MapObject mapObject) {

        Intent mIntent = new Intent(context, LibraryActivity.class);
        mIntent.putExtra("LIBRARY_NAME",title); //Optional parameters
        return mIntent;
    }



}
