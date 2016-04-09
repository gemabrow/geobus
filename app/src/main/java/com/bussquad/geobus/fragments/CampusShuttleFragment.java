package com.bussquad.geobus.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.SupportMapFragment;
import com.bussquad.geobus.BusStop;
import com.bussquad.geobus.R;
import com.bussquad.geobus.activity.MainActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CampusShuttleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CampusShuttleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CampusShuttleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters


    private SupportMapFragment mapFragment;
    private GoogleMap mMap;


    private ArrayList<BusStop> busStops;

    private OnFragmentInteractionListener mListener;

    public CampusShuttleFragment() {
        // Required empty public constructor
    }







    public static CampusShuttleFragment newInstance(String param1, String param2) {
        CampusShuttleFragment fragment = new CampusShuttleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i("CampusShuttleFragme", "Creating CampusShuttle");
    }




    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.display_object_loc, container, false);


    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        System.out.println("Attached Shuttle Fragment");
    }




    @Override
    public void onDetach() {
        super.onDetach();

        System.out.println("dettached Fragment");
        mListener = null;
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

















}
