package com.bussquad.sluglife.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.RecyclerViewAdapter;

import java.util.ArrayList;


public class NavListFragment extends Fragment {

    private final String KEY_OBJECT_LIST = "OBJECT_LIST";
    FragmentActivity mActivity;
    private OnFragmentInteractionListener mListener;

    private RecyclerView recyleview;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<DataObject> listObject =  new ArrayList<>();
    private ArrayList<Integer> cardTypes = new ArrayList<>();

    public NavListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NavListFragment newInstance(String param1, String param2) {
        NavListFragment fragment = new NavListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = this.getArguments();
        if(bundle != null){
            System.out.println("bundle is not null");
            listObject =  bundle.getParcelableArrayList(KEY_OBJECT_LIST);
            cardTypes = bundle.getIntegerArrayList("TYPE_LIST");
        }
        View layout = inflater.inflate(R.layout.fragment_nav_list, container, false);

        mAdapter = new RecyclerViewAdapter(getActivity().getBaseContext(),listObject);
        mAdapter.setDataSetTypes(cardTypes);
        recyleview = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyleview.setAdapter(mAdapter);
        recyleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {

            }
        });

        // initialize the adapter
        return layout;
    }




    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }




    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




    public void loadDataObjects(){

    }



    // updates the data currently in the recycle view adatper
    public void updateRecycleViewData(ArrayList<DataObject> dataList, ArrayList<Integer> typeList){
        System.out.println("loading data");
        mAdapter.swap(dataList);
        mAdapter.swapTypeList(typeList);
    }





}
