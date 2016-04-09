package com.bussquad.geobus.fragments;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bussquad.geobus.NotificationDbManger;
import com.bussquad.geobus.NotificationService;
import com.bussquad.geobus.R;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLOutput;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {

    private NotificationDbManger notifDb;
    private String busStopId;
    private ArrayList<String> routes;
    private ArrayList<String> newRoutes = new ArrayList<>();
    //this counts how many items's are on the UI for the spinner dialogue
    private int count = 0;
    private String[] routeSelection = {"OUTER","INNER","UPPER","ANY","Custom"};


    ImageButton btnNotification;
    Button btnSetNotification;
    Button btnCancelNotification;
    Button btnstopService;
    Button btnUpdateNotification;
    Spinner busStopSpinner;
    Spinner loopBusSpinner;
    TextView txtNotification;
    TextView txtSubNotifcation;
    TextView txtNotiOption;
    TextView txtBusRoute;
    TextView txtNotificationType;
    ArrayAdapter<CharSequence> busStopAdapter;
    ArrayAdapter<CharSequence> busRouteAdapter;
    boolean notiEnabled = false;




    // used to store notification information for the current bus stop selected

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notifDb =  new NotificationDbManger(getContext());

        // create database if it does not already exist
        notifDb.createDataBase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        btnSetNotification = (Button)view.findViewById(R.id.btnSetNotification);
        btnCancelNotification = (Button)view.findViewById(R.id.btnCancelNotification);
        btnstopService = (Button)view.findViewById(R.id.btnstopService);
        btnUpdateNotification = (Button)view.findViewById(R.id.btnUpdateNotification);
        txtNotification = (TextView)view.findViewById(R.id.txtNotification);

        if(isNotificationServiceRunnig(NotificationService.class)){
            btnstopService.setEnabled(true);
        }
        else{
            btnstopService.setEnabled(false);
        }



        txtNotiOption = (TextView)view.findViewById(R.id.txtNotiOption);
        txtBusRoute =  (TextView)view.findViewById(R.id.txtBusRoute);
        txtNotificationType = (TextView)view.findViewById(R.id.txtNotificationType);
        // Spinner adapters


        busStopAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.stops_array, R.layout.spinner_item);

        busRouteAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.route_array, R.layout.spinner_item);


        // get bundles sent from parent activity
        busStopId = String.valueOf(getArguments().getString("ITEMID"));
        System.out.println(busStopId);
        routes = new ArrayList<>(notifDb.getBusStopRoute(busStopId));

        // removes any route that is not an outer or an inner
        for (String route : routes){

            System.out.println("route: " + route);
            if(route.equalsIgnoreCase("OUTER") || route.equalsIgnoreCase("INNER")){
                System.out.println("found match" + route);
                newRoutes.add(route);
            }
        }

        // if there are more than one selection available add ANY in the end of the list
        if (newRoutes.size() > 1) {
            newRoutes.add("ANY");
        }



        ArrayAdapter<String> adp =  new ArrayAdapter<String>(getContext(),R.layout.spinner_item,newRoutes);

        busRouteAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);



        // set drop down item lists
        loopBusSpinner = (Spinner)view.findViewById(R.id.loopBusSpinner);
        loopBusSpinner.setAdapter(adp);
        loopBusSpinner.setOnItemSelectedListener(this);

        busStopAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        busStopSpinner = (Spinner)view.findViewById(R.id.busStopSpinner);
        busStopSpinner.setAdapter(busStopAdapter);
        busStopSpinner.setOnItemSelectedListener(this);


        // update any previous settings
        if(notifDb.hasNotifiation(busStopId)){
            btnSetNotification.setEnabled(false);
            btnCancelNotification.setEnabled(true);
            btnUpdateNotification.setEnabled(true);

        }
        if(busStopId.equalsIgnoreCase("-1")){
            btnSetNotification.setEnabled(false);
        }



        // set button click listeners
        btnSetNotification.setOnClickListener(this);
        btnCancelNotification.setOnClickListener(this);
        btnstopService.setOnClickListener(this);
        btnUpdateNotification.setOnClickListener(this);
        return view;




    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if (count < 2){
            count++;
        }else{
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }

        // Showing selected spinner item
    }




    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




// when button is clicked it can enable or unenable Notifications Option
    public void onEnableNotification(View view) {

        System.out.println("Clicked!!");
        // if notifications is not enable change notifications text to enable form
        // else notifications are set to disable thus disable notifications.
        if(!notiEnabled){

            btnNotification.setImageResource(R.drawable.ic_check_box_black_24dp);
            txtNotification.setTextColor(Color.parseColor("#d9000000"));
            txtSubNotifcation.setTextColor(Color.parseColor("#8C000000"));
            txtNotiOption.setTextColor(Color.parseColor("#d9000000"));
            txtBusRoute.setTextColor(Color.parseColor("#d9000000"));
            txtNotificationType.setTextColor(Color.parseColor("#d9000000"));
            busStopSpinner.setEnabled(true);
            loopBusSpinner.setEnabled(true);
            btnSetNotification.setEnabled(true);

            notiEnabled = true;
        }else{
            txtNotification.setTextColor(Color.parseColor("#61000000"));
            txtSubNotifcation.setTextColor(Color.parseColor("#61000000"));
            txtNotiOption.setTextColor(Color.parseColor("#61000000"));
            txtBusRoute.setTextColor(Color.parseColor("#61000000"));
            txtNotificationType.setTextColor(Color.parseColor("#61000000"));
            busStopSpinner.setEnabled(false);
            loopBusSpinner.setEnabled(false);
            btnSetNotification.setEnabled(false);
            btnNotification.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
            notiEnabled = false;
        }


    }




    // checks the current options set by the user and the sets the notificaitons
    // adds notification to a database that is being checked by the runnable in the actvity class
    public void startNotification(View view){

        // populate information from the currently selected options

        int stops = busStopSpinner.getSelectedItemPosition() + 1;
        String vibrate = "1";
        String sound = "1";

        // checks if there is already a database
        System.out.println("Bus stop ID: " + busStopId);

        notifDb.addNotification(busStopId,newRoutes.get(loopBusSpinner.getSelectedItemPosition()), vibrate, sound, stops);
        btnCancelNotification.setEnabled(true);

        // checks if there is already a notifiationService running if there is no service it
        // starts the service and service ends once there are no more pending notifications
        if(!isNotificationServiceRunnig(NotificationService.class)){
            Intent notification = new Intent(getActivity(),NotificationService.class);
            getActivity().startService(notification);
            btnstopService.setEnabled(true);
            btnUpdateNotification.setEnabled(true);
            btnSetNotification.setEnabled(false);
        }


    }


    public void cancelNotification(View view){

        System.out.println("deleting row notifications in database " + notifDb.numberOfNotifications());
        notifDb.deleteRow(busStopId);
        System.out.println("size of database " +  notifDb.numberOfNotifications());
        btnCancelNotification.setEnabled(false);
        btnUpdateNotification.setEnabled(false);
        btnSetNotification.setEnabled(true);

    }







    // used to filter onClick events for different buttons in this fragment class
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSetNotification:
                startNotification(v);
                break;
            case R.id.btnCancelNotification:
                cancelNotification(v);
                break;
            case R.id.btnstopService:
                stopServiceRunning(v);
                break;
            case R.id.btnUpdateNotification:
                updateNotificationRunning(v);
                break;

        }
    }



    public void stopServiceRunning(View view){
        getActivity().stopService(new Intent(getActivity(), NotificationService.class));
        btnstopService.setEnabled(false);
    }




    public void updateNotificationRunning(View view){
        int stops = busStopSpinner.getSelectedItemPosition() + 1;
        String vibrate = "1";
        String sound = "1";



        // update the route set
        notifDb.updateStopsLeft(busStopId,stops);
        notifDb.updateRoute(busStopId,newRoutes.get(loopBusSpinner.getSelectedItemPosition()));
    }


    @Override
    public void onPause() {
        super.onPause();


    }


    private boolean isNotificationServiceRunnig(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
