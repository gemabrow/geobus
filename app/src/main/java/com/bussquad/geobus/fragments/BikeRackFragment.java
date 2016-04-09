package com.bussquad.geobus.fragments;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.DefaultClusterOptionsProvider;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.SupportMapFragment;
import com.bussquad.geobus.BikeRack;
import com.bussquad.geobus.Dining;
import com.bussquad.geobus.Library;
import com.bussquad.geobus.R;
import com.bussquad.geobus.parser.JsonBikeParkingFileParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class BikeRackFragment extends Fragment  {

    private CameraPosition lstCameraPosition;
    private CameraPosition currCameraPosition;
    private ArrayList<BikeRack> bikeRacks;



    private final JsonBikeParkingFileParser bikeReader = new JsonBikeParkingFileParser();

    private OnFragmentInteractionListener mListener;


    public BikeRackFragment() {
        // Required empty public constructor
    }


    public static BikeRackFragment newInstance(String param1, String param2) {
        BikeRackFragment fragment = new BikeRackFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.display_object_loc, container, false);
    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    @Override
    public void onStart() {
        super.onStart();

        System.out.println("Starting BikeRack");
    }




    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resuming BikeRack");
    }







    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

        public void OnMarkerSelected(Marker marker);

    }
}
