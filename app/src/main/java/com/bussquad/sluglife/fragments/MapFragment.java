package com.bussquad.sluglife.fragments;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bussquad.sluglife.DataObject;
import com.bussquad.sluglife.MapMenuItem;
import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.activity.ScheduleAcitivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;


// Adds functionality to the fragment to handle map clicks

public class MapFragment extends Fragment  {


    private OnFragmentInteractionListener mListener;

    private ImageButton filter;
    private ImageButton btnSwitchView;
    private int tabPosition = 0;
    private boolean cardMode = false;
    private boolean dataLoaded = false;
    private boolean markerVisble = true;
    private boolean enableIconGen = false;
    private boolean hasFilterMenuItems = false;
    private boolean hasMenuItems = false;
    private boolean hasZoom = false;
    private boolean startsActivity = false;
    private boolean sortClosestToFurthest = true;

    private float zoomLevel = 14;
    private LatLng cameraLocation;
    private ArrayList<MapMenuItem> menuItems;
    private int menuSelectedPosition = 0; // set to first one by default
    private int totalMenuItems = 0;
    private float layoutHeight;
    private double minDistance = 100;           // default distance is set to 100 meters


    private int menuFilterResourceID = 0;                // resource id for the filter menu items
    // store the date to display in the card views
    private ArrayList<DataObject> listObjects = new ArrayList<>();
    // store the type of views for each data object
    private ArrayList<Integer>viewTypes = new ArrayList<>();
    private ArrayList<MapObject>mapObjects = new ArrayList<>();


    public MapFragment() {
        // Required empty public constructor
        menuItems  = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.display_object_loc, container, false);
        layoutHeight = view.getHeight();
        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        // dereference the hold on the context to avoid memory leaks
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }





    // load date for the map fragment
    public void loadData(Context context){

        context = null;
    }



    // returns a list of data objects
    public ArrayList<DataObject> getListObjects() {
        return listObjects;
    }







    // returns a list of view types
    public ArrayList<Integer> getViewTypes() {
        return viewTypes;
    }





    // object id is used to filter the map objects ac
    public ArrayList<MapObject> getMapObjects(){
        return this.mapObjects;
    }




    // sets a list of view types
    public void setViewTypes(ArrayList<Integer> viewTypes) {
        this.viewTypes = viewTypes;
    }




    // sets a list of objects
    public void setListObjects(ArrayList<DataObject> listObjects) {
        this.listObjects = listObjects;
    }




    public void setMapObjects(ArrayList<MapObject> mapObjects){
        this.mapObjects = mapObjects;
    }





    // returns with the current map fragment is in card mode or not
    public boolean isCardMode() {
        return cardMode;
    }




    // sets the map fragment in card mode
    public void setCardMode(boolean cardMode) {

        this.cardMode = cardMode;
    }


    // returns true if the marke is visible returns false if the marker is set
    // invicble
    public boolean isMarkerVisble() {
        return markerVisble;
    }



    // make the marker in this mapFragment visble or invisble
    public void setMarkerVisble(boolean markerVisble) {
        this.markerVisble = markerVisble;
    }



    // returns true if an Icon has to be generated or modified at runtime
    public boolean isIconGenEnabled() {
        return enableIconGen;
    }




    // enable or un enable icon generation
    public void enableIconGen() {
        this.enableIconGen = true;
    }



    public void disableIconGen(){
        this.enableIconGen = false;
    }




    // set the zoom of how much space intially the map fragment will take up
    public void setZoomLevel(float zoomlvl){
        this.zoomLevel = zoomlvl;
    }




    // get the zoom level set for the map fragment to be used in th emap
    public float getZoomLevel(){
        return this.zoomLevel;
    }



    // if zoom
    public boolean isZoomEnabled(){
        return this.hasZoom;
    }




    // diaable zoom
    public void disableZoom(){
        this.hasZoom = false;
    }




    // enable zoom level
    public void enableZoom(){
        this.hasZoom = true;
    }




    // location in which the screen will move to
    public void setCamerLocation(LatLng location){
        this.cameraLocation = location;
    }



    // location that the camera on map will be moved to
    public LatLng getCameraLocation(){
        return this.cameraLocation;
    }




    // returns true if the data for the mapfragment has been loaded or false if
    // it hasnt loaded
    public boolean isDataLoaded(){
        return this.dataLoaded;
    }



    // update the statu of whether the data has been loaded or not
    public void setDataLoadedStatus(boolean loaded){
        this.dataLoaded = loaded;
    }



    public void isActivityStartable(boolean startsActivity){

        this.startsActivity = startsActivity;
    }



    public boolean isActivityStartable(){
        return this.startsActivity;
    }



    public Intent getMapFragmentIntent(Context context,String title,MapObject mapObject){

        Intent mIntent = new Intent(context, ScheduleAcitivity.class);

        return mIntent;

    }



    public void setTabPosition(int tab){
        this.tabPosition = tab;
    }



    public int getTabPosition(){
        return this.tabPosition;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
            public void switchToListView();
    }



    // sets the menu items for the map fragment and also sets the total number of menu items
    // for the map fragment if any
    public void setMapMenuItems(ArrayList<MapMenuItem> menuItems){

        if (menuItems == null){
            throw new RuntimeException("MapMenuItems cannot be null");
        }


        this.totalMenuItems =  menuItems.size();
        this.menuItems = new ArrayList<>(menuItems);
    }


    public ArrayList<MapMenuItem> getMapMenuItems(){

        return this.menuItems;
    }


    // set the options that will show up in the popup menu which can be used to set filters to which
    // mapobjects will be displayed on the map
    public void setFilterMenuResourceId(int resourceId){
        this.menuFilterResourceID = resourceId;
    }


    // returns a set of menu items that will present possible filtering options to the user
    public int getFilterMenuResourceID(){
        return this.menuFilterResourceID;
    }



    public void enableFilterMenuOptions(){
        this.hasFilterMenuItems = true;
    }



    public void disableFilterMenuOptions(){
        this.hasFilterMenuItems = false;
    }



    public boolean isFilterMenuEnabled(){
        return this.hasFilterMenuItems;
    }

    // enable menu items for mapobject,when enabled this will allow the user to filter mapobjects
    // based on a certain category
    public void enableMenuItems(){
        this.hasMenuItems = true;
    }


    public void disableMenuItems(){
        this.hasMenuItems = false;
    }



    public boolean isMenuItemsEnabled(){
        return this.hasMenuItems;
    }



    public int getTotalMenuItems(){
        return this.totalMenuItems;
    }


    public float getLayoutHeight(){
        return this.layoutHeight;
    }


    public void setMenuSelectedPosition(int position){
        this.menuSelectedPosition = position;
    }




    // returns the currently selected menu item, if there are not menuitems for the selected mapfragment
    // it will return -1 indicating that there is no menu items set
    public int getMenuSelectedPosition(){

        if(totalMenuItems == 0){
            return -1;
        }
        return this.menuSelectedPosition;
    }



    // returns the map objects within range. Range can be set with the function setRange()
    public ArrayList<MapObject> getClosestMarkers(Location userLocation){
        ArrayList<MapObject> closestMapObjects = new ArrayList<>();

        for(MapObject mapObject : getMapObjects()){
            if(userLocation.distanceTo(mapObject.getObjLocation()) <= minDistance){
                closestMapObjects.add(mapObject);
            }
        }
        return closestMapObjects;
    }
}
