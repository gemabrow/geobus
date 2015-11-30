package com.bussquad.geobus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;



public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent(); // receive intent from MapsActivity
        int infoFlag = Integer.parseInt(intent.getStringExtra(MapsActivity.EXTRA_INFO)); // and convert the info sent to an int
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String info;
        TextView tv = (TextView)findViewById(R.id.tv);
        if (infoFlag == 1) { // if the user tapped on Loop and Upper Campus Info...
            info = getResources().getString(R.string.loop_upper);
            tv.setText(Html.fromHtml(info));
        }
        else if (infoFlag == 2) { // if the user tapped on Night Core Info...
            info = getResources().getString(R.string.night_core);
            tv.setText(Html.fromHtml(info));
        }
        else if (infoFlag == 3) { // if the user tapped on Night Owl Info...
            info = getResources().getString(R.string.night_owl);
            tv.setText(Html.fromHtml(info));
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // makes the actionbar back button behave like the hardware back button
        if (item.getItemId() == android.R.id.home) {      // such that it doesn't cause the maps activity to redraw
            onBackPressed();
            return true;
        }
        return false;
    }
}
