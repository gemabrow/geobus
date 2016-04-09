package com.bussquad.geobus.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.geobus.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiningFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DiningFragment() {
        // Required empty public constructor
    }





//
//    public void onActivityCreated(Bundle paramBundle)
//    {
//        super.onActivityCreated(paramBundle);
//        this.mAct = getActivity();
//        Helper.isOnline(this.mAct, true, true);
//        paramBundle = getArguments().getString(MainActivity.DATA);
//        if (paramBundle.startsWith("@"))
//        {
//            this.maps = getResourceString(paramBundle.substring(1), this.mAct);
//            this.mode = this.ARRAY;
//        }
//        for (;;)
//        {
//            MapsInitializer.initialize(this.mAct);
//            this.googleMap = this.mMapView.getMap();
//            this.mMapView.getMapAsync(new OnMapReadyCallback()
//            {
//                public void onMapReady(GoogleMap paramAnonymousGoogleMap)
//                {
//                    MapsFragment.access$002(MapsFragment.this, paramAnonymousGoogleMap);
//                    if (MapsFragment.this.mode == MapsFragment.this.ARRAY)
//                    {
//                        MapsFragment.this.lat = Double.valueOf(Double.parseDouble(MapsFragment.this.maps[3]));
//                        MapsFragment.this.lon = Double.valueOf(Double.parseDouble(MapsFragment.this.maps[4]));
//                        LatLng localLatLng = new LatLng(MapsFragment.this.lat.doubleValue(), MapsFragment.this.lon.doubleValue());
//                        paramAnonymousGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localLatLng, Integer.parseInt(MapsFragment.this.maps[5])));
//                        paramAnonymousGoogleMap.addMarker(new MarkerOptions().title(MapsFragment.this.maps[1]).snippet(MapsFragment.this.maps[2]).position(localLatLng)).showInfoWindow();
//                        paramAnonymousGoogleMap = MapsFragment.this.getArguments().getString(MainActivity.DATA);
//                        MapsFragment.this.maps = MapsFragment.getResourceString(paramAnonymousGoogleMap, MapsFragment.this.mAct);
//                        MapsFragment.access$102(MapsFragment.this, (TextView)MapsFragment.this.ll.findViewById(2131689643));
//                        MapsFragment.this.text.setText(Html.fromHtml(MapsFragment.this.maps[0]));
//                        return;
//                    }
//                    MapsFragment.access$102(MapsFragment.this, (TextView)MapsFragment.this.ll.findViewById(2131689643));
//                    MapsFragment.this.text.setVisibility(8);
//                    MapsFragment.this.currentLocation();
//                    paramAnonymousGoogleMap.clear();
//                }
//            });
//            return;
//            this.mode = this.PLACES;
//            this.query = paramBundle;
//        }
//    }





    public static DiningFragment newInstance(String param1, String param2) {
        DiningFragment fragment = new DiningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.display_object_loc, container, false);
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }





    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        System.out.println("view restored dining");
    }





    @Override
    public void onStart() {
        super.onStart();

    }




    @Override
    public void onResume() {
        super.onResume();

    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
