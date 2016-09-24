package com.bussquad.sluglife.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.bussquad.sluglife.Event;
import com.bussquad.sluglife.R;
import com.bussquad.sluglife.databases.EventDbManager;

public class EventActivity extends AppCompatActivity implements Button.OnClickListener{

    private AppController dataController;
    private ImageLoader imageLoader;

    private TextView txtEvenTitle;
    private TextView txtEventDescription;
    private TextView eventCost;
    private TextView txtEventDate;
    private TextView hours;
    private TextView locationInfo;
    private Button btnWebPageLink;
    private ImageView btnFavorite;
    private ImageView imgThumbnail;

    View admissinoLayout;
    View eventDatelayout;
    View hoursOfOperation;
    View locationInfoLayout;


    private EventDbManager eventDB;
    private String webUrl;
    private String eventID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        dataController = AppController.getInstance();
        imageLoader = dataController.getImageLoader();
        Bundle extras = getIntent().getExtras();

        eventDB = new EventDbManager(getBaseContext());
        eventID =  extras.getString("EVENTID");
        Event event = eventDB.getEvent(eventID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SlugLife Event");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtEvenTitle = (TextView) findViewById(R.id.txtEvenTitle);
        txtEvenTitle.setText(event.getName());

        txtEventDescription = (TextView) findViewById(R.id.txtEventDescription);
        txtEventDescription.setText(event.getFullViewText1());

        View dateLayout = findViewById(R.id.ViewEventDate);
        TextView txtDate  = (TextView)dateLayout.findViewById(R.id.txtDetail);
        txtDate.setText("Date");
        txtEventDate = (TextView) dateLayout.findViewById(R.id.txtDescription);
        txtEventDate.setText(event.getEventDate());


        locationInfoLayout = findViewById(R.id.location_information);
        locationInfo = (TextView) locationInfoLayout.findViewById(R.id.location_description);
        locationInfo.setText(event.getLocationDetail());

        admissinoLayout = findViewById(R.id.txtAdmission);
        eventCost = (TextView) admissinoLayout.findViewById(R.id.txtDescription) ;
        eventCost.setText(setCost(event.getAdmissionCost()));

        btnWebPageLink = (Button) findViewById(R.id.btnWebPageUrl);
        webUrl = event.getEventWebUrl();
        btnWebPageLink.setOnClickListener(this);
        // set image
        imgThumbnail = (ImageView) findViewById(R.id.imgThumbNail);
        String urlThumbNail = event.getThumbNailUrl();
        if(urlThumbNail != null){
            imageLoader.get(urlThumbNail, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                    if(response.getBitmap() != null){
                        imgThumbnail.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    imgThumbnail.setImageResource(R.drawable.place_holder_thumbnail);
                }
            });
        }else{
            System.out.println("url thumbnail is null");
        }



       //admissinoLayout = findViewById(R.id.txtAdmission); // root View id from that link


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnWebPageUrl:
                openWebPage();
        }
    }


    private void openWebPage(){
        if(webUrl != null){
            Uri uriUrl = Uri.parse(webUrl);
            Intent launchBrowser = new Intent (Intent.ACTION_VIEW,uriUrl);
            startActivity(launchBrowser);

        }
    }




    private String setCost(double cost){
        if(cost == 0){
            return "Free";
        } else {
            return cost + "";
        }

    }



    // if the home  button is clicked it returns back to the parent activity that orginally
    // created this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.fade_in_place,R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
