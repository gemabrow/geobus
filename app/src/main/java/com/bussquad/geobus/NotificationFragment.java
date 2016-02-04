package com.bussquad.geobus;


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

import java.sql.SQLOutput;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements AdapterView.OnItemSelectedListener,View.OnClickListener {


    //this counts how many items's are on the UI for the spinner dialogue
    private int count = 0;


    ImageButton btnNotification;
    Button btnSetNotification;
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        btnNotification = (ImageButton)view.findViewById(R.id.btnNotification);
        btnSetNotification = (Button)view.findViewById(R.id.btnSetNotification);
        txtNotification = (TextView)view.findViewById(R.id.txtNotification);
        txtSubNotifcation = (TextView)view.findViewById(R.id.txtSubNotifcation);
        txtNotiOption = (TextView)view.findViewById(R.id.txtNotiOption);
        txtBusRoute =  (TextView)view.findViewById(R.id.txtBusRoute);
        txtNotificationType = (TextView)view.findViewById(R.id.txtNotificationType);
        // Spinner adapters
        busStopAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.stops_array, R.layout.spinner_item);
        busRouteAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.route_array, R.layout.spinner_item);


        btnSetNotification.setEnabled(false);

        busRouteAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        loopBusSpinner = (Spinner)view.findViewById(R.id.loopBusSpinner);
        loopBusSpinner.setEnabled(false);
        loopBusSpinner.setAdapter(busRouteAdapter);
        loopBusSpinner.setOnItemSelectedListener(this);

        busStopAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        busStopSpinner = (Spinner)view.findViewById(R.id.busStopSpinner);
        busStopSpinner.setEnabled(false);
        busStopSpinner.setAdapter(busStopAdapter);
        busStopSpinner.setOnItemSelectedListener(this);

        btnNotification.setOnClickListener(this);
        btnSetNotification.setOnClickListener(this);
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
    public void onEnableNotification(View view){

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
        System.out.println("button clicked!!!");

        System.out.println("Current option selected: "+ busStopSpinner.getSelectedItem().toString());
        System.out.println("Current option selected: "+ loopBusSpinner.getSelectedItem().toString());



    }



    // used to filter onClick events for different buttons in this fragment class
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNotification:
                onEnableNotification(v);
                break;
            case R.id.btnSetNotification:
                startNotification(v);
                break;
        }
    }


    @Override
    public void onPause() {
        super.onPause();


    }
}
