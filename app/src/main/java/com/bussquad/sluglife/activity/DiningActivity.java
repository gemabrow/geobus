package com.bussquad.sluglife.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bussquad.sluglife.R;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.datepicker.DatePicker;


//import org.joda.time.DateTime;

import java.util.Calendar;

public class DiningActivity extends AppCompatActivity implements
        CalendarDatePickerDialogFragment.OnDateSetListener,
        Button.OnClickListener {
    DatePicker towDaysAgo;
    private String dinName;
    private String[] monthOfTheYear = {"January","Feburary","March","April","May",
            "June","July","August","September","October","November","December"};
    private Button btnCalendarPicker;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialzied bus stop information
        Bundle extras = getIntent().getExtras();
        dinName = extras.getString("DINING_NAME");

        setContentView(R.layout.dining);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(dinName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView diningDescription = (TextView) findViewById(R.id.description);
        diningDescription.setText("Its a really good place to eat... but sometimes it does suck");

        TextView diningName = (TextView) findViewById(R.id.location_name);
        diningName.setText(dinName);

//        View calendarLayout = findViewById(R.id.calendar_date_picker); // root View id from that link
//        btnCalendarPicker = (Button) calendarLayout.findViewById(R.id.btnCalendarPicker);
//        setDate();
//        btnCalendarPicker.setOnClickListener(this);


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCalendarPicker:
                displayCalendar();
                break;
        }
    }




    public void displayCalendar() {
       // MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(now.getYear(), now.getMonthOfYear() - 2, now.getDayOfMonth());
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(DiningActivity.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
              //  .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo.getDayOfMonth())
         //       .setDateRange(minDate, null)
                .setThemeLight();
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }




    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int month, int dayOfMonth) {


        btnCalendarPicker.setText(monthOfTheYear[month] + " " + dayOfMonth + ", " + year);
    }


    private void setDate(){
        int month = Calendar.getInstance().get(Calendar.MONTH); // prints 10 (October)
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        btnCalendarPicker.setText(monthOfTheYear[month] + " " + dayOfMonth + ", " + year);
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                // when app home button is clicked navigate to parent activity
                onBackPressed();
                overridePendingTransition(R.anim.fade_in_place,R.anim.slide_out_right);
//                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
