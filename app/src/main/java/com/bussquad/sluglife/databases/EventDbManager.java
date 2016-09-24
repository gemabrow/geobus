package com.bussquad.sluglife.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bussquad.sluglife.Event;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Jose on 4/26/2016.
 */
public class EventDbManager extends SQLiteOpenHelper {


    private static String TAG ="EventDbManager";
    private static String DB_PATH = "";
    private static String DB_NAME ="notificationManager.sqlite";// Database name


    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private static final int EVENTID = 1;
    private static final int EVENTTITLE = 2;
    private static final int EVENTTEASER = 3;
    private static final int EVENTLOCATION = 4;
    private static final int EVENTIMGURL = 5;
    private static final int EVENTDATE = 6;
    private static final int EVENTWEBLINK = 7;
    private static final int EVENTTHUMNAILLINK = 8;
    private static final int EVENTDESCRIPTION = 9;
    private static final int EVENTXCORD = 10;
    private static final int EVENTYCORD = 11;
    private static final int EVENTADMISSION = 12;


    public EventDbManager(Context context)
    {
        super(context, DB_NAME, null, 1);// 1? Its database Version
        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createDataBase()
    {
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                // TO-DO : update database if it already exists without deleting the current set notifications
//                System.out.println("DataBse already exists delete");
                deleteDataBase();

                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
        else{
            System.out.println("database already exists ");
        }
    }




    private void deleteDataBase(){
        mContext.deleteDatabase(DB_PATH + DB_NAME);
    }




    //Check that the database exists here: /data/data/your package/databases/DB Name
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }



    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }




    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return mDataBase != null;
    }




    // checks if the current database that is open has a table called ucsc_events
    public boolean checkTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.query("ucsc_events",null,null,null,null,null,null,null);
            c.getCount();
            return true;
        }catch(Exception ex){
            System.out.println("table does not exist");
        }
        return false;
    }



    // creates a table called ucsc_events
    public void createTable(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table ucsc_events" + "(id integer primary key, " +
                "eventid text," +
                "title text," +
                "teaser text," +
                "location text," +
                "imgurl text," +
                "date text" +
                "weblink text" +
                "thumbnail text" +
                "description text" +
                "xcord text" +
                "ycord text" +
                "admission)"
        );

    }




    // Deletes the ucsc_event table
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS ucsc_events");
    }



    public boolean hasEventId(int eventID){
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where event_id =" +eventID , null);
        c.moveToFirst();

        try{
            c.getString(EVENTID);
            exists = true;
            System.out.println("Events exists");
        }catch (Exception ex){

        }
        c.close();

        return exists;
    }




    // returns the number of events in the database
    public int numberOfEvents(){
        SQLiteDatabase db = this.getReadableDatabase();
        int size = 0;
        try {
            Cursor c = db.query("ucsc_events", null, null, null, null, null, null, null);
            size = c.getCount();
        }catch (Exception ex){

        }
        return size;
    }







    // adds an event item to the ucsc_events database
    public boolean addEventToDb(Event eventobj){


        SQLiteDatabase db = this.getWritableDatabase();

        // get next bus stop Id


        ContentValues contentValues = new ContentValues();
        contentValues.put("eventid", eventobj.getObjectID());
        contentValues.put("title", eventobj.getName());
        contentValues.put("teaser", eventobj.getCardSubText1());
        contentValues.put("location", eventobj.getLocationDetail());
        contentValues.put("imgurl", eventobj.getFullViewImageUrl());
        contentValues.put("date", eventobj.getEventDate());
        contentValues.put("weblink", eventobj.getEventWebUrl());
        System.out.println("thumbnail url in DB: " + eventobj.getThumbNailUrl());
        contentValues.put("thumbnail", eventobj.getThumbNailUrl());
        contentValues.put("description", eventobj.getFullViewText1());
        contentValues.put("xcord", eventobj.getLatitude());
        contentValues.put("ycord", eventobj.getLongitude());
        contentValues.put("admission", eventobj.getAdmissionCost());
        db.insert("ucsc_events", null, contentValues);

        Cursor  c = db.query("ucsc_events", null, null, null, null, null, null, null);

        System.out.println(c.getCount());

        return true;
    }




    // returns a LatLng object specifing the graphical location of the event of the event
    public LatLng getLocation(String eventID ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        LatLng coordinates = new LatLng(c.getDouble(EVENTXCORD), c.getDouble(EVENTYCORD));
        c.close();

        return coordinates;
    }




    // returns the title of the eventID if the eventID doesnot exist then an error will be thrown
    public String getTitle(int eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String title =  c.getString(EVENTTITLE);
        c.close();

        return title;
    }




    // returns the link with additional information of the eventID if the eventID doesnot exist then an error will be thrown
    public String getEventURLImageLink(String eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String link =  c.getString(EVENTIMGURL);
        c.close();

        return link;
    }





    // returns the date that the event will take place given the eventID if the eventID doesnot exist
    // then an error will be thrown
    // TODO error check if no date is available
    // TODO error check if eventID does not exist
    public String getEventDate(String eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String date =  c.getString(EVENTDATE);
        c.close();

        return date;
    }




    // returns the weblink with more informaiton about the event given the eventID
    // if the eventID does not exist then an error will be thrown
    // TODO error check if no weblink is available
    // TODO error check if eventID does not exist
    public String getEventWeblink(String eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String weblink =  c.getString(EVENTWEBLINK);
        c.close();

        return weblink;
    }




    // returns the thumbnail link for the event if the eventID doesnot exist
    // then an error will be thrown
    // TODO error check if no thumbnail is available
    // TODO error check if eventID does not exist
    public String getEventThumbnail(String eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String thumbnaillink =  c.getString(EVENTTHUMNAILLINK);
        c.close();

        return thumbnaillink;
    }




    // returns description that the event will take place given the eventID if the eventID doesnot exist
    // then an error will be thrown
    // TODO error check if no description is available
    // TODO error check if eventID does not exist
    public String getEventDescription(String eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String description =  c.getString(EVENTDESCRIPTION);
        c.close();

        return description;
    }




    // returns description that the event will take place given the eventID if the eventID doesnot exist
    // then an error will be thrown
    // TODO error check if no description is available
    // TODO error check if eventID does not exist
    public double getEventAdmissionCost(int eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        double cost =  c.getDouble(EVENTADMISSION);
        c.close();

        return cost;
    }




    // returns a smaller description of the event will take place given the eventID if the
    // eventID doesnot exist then an error will be thrown
    // TODO error check if no description is available
    // TODO error check if eventID does not exist
    public String getEventTeaser(int eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();
        String teaser =  c.getString(EVENTTEASER);
        c.close();

        return teaser;
    }




    public Event getEvent(String eventID){
        Event event;
        String title;
        String teaser;
        String date;
        String location;
        String description;
        String largeImageUrl;
        String thumbnailLink;
        String eventWeblink;
        double cost;
        double latitude;
        double longitude;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        c.moveToFirst();

        title =  c.getString(EVENTTITLE);
        teaser = c.getString(EVENTTEASER);
        largeImageUrl = c.getString(EVENTIMGURL);
        thumbnailLink =  c.getString(EVENTTHUMNAILLINK);
        date = c.getString(EVENTDATE);
        description = c.getString(EVENTDESCRIPTION);
        location = c.getString(EVENTLOCATION);
        latitude = c.getDouble(EVENTXCORD);
        longitude = c.getDouble(EVENTYCORD);
        eventWeblink = c.getString(EVENTWEBLINK);
        cost =  c.getDouble(EVENTADMISSION);

        event  = new Event(title,teaser,latitude,longitude,date,eventWeblink,thumbnailLink,largeImageUrl,
                description,cost,location);
        return event;
    }



    // checks if the event is already in the table, if it is it returns true, if its not it returns
    // false
    public boolean checkIfEventExists(int eventID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ucsc_events where eventid=" + eventID, null);
        if(c.moveToFirst()){
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }




}
