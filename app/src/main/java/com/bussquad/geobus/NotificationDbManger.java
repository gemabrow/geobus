package com.bussquad.geobus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;

/**
 * Created by Jose on 2/2/2016.
 */


/*  This class well be used for managing a sqlite database stored locally in the phone.
    The sqlite database will be used to store pending notifications. Every time a notification
    is complete its deleted from the database, everytime the user creates a notification,
    a notification is added to the database. While the database is not empty the NotificaitonService
    will be


*/


public class NotificationDbManger  extends SQLiteOpenHelper {


    public static final String NOTIFICATION_TABLE_NAME = "notifications";
    public static final String ROUTE_TABLE_NAME = "routes";
    public static final String BUSINFO_TABLE_NAME = "bus_stop_info";
    public static final String BUSSTOP_COLUMN_ID = "ID_BUSSTOP";


    // used for getting data from notification table
    private static final int BUSSTOPID_COLUMN = 1;
    private static final int STOPS_COLUMN = 2;
    private static final int NEXTSTOPID_COLUMN = 3;
    private static final int VIBRATE_COLUMN = 4;
    private static final int SOUND_COLUMN = 5;
    private static final int  ROUTE_COLUMN = 6;
    private static final int  CURR_STOP_COLUMN = 7;



    // used for getting data from the rbua_atop_info table
    private static final int XCOORD_COLUMN = 2;
    private static final int YCOORD_COLUMN = 3;
    private static final int STOP_NAME_COLUMN = 4;

    // indicates what row its currently pointing to
    private int currentRow = 0;


    private static String TAG = "NotificationDbManger"; // Tag just for the LogCat window


    // Androids default system path of the applicaiton database
    private static String DB_PATH = "";
    private static String DB_NAME ="notificationManager.sqlite";// Database name


    private SQLiteDatabase mDataBase;
    private final Context mContext;



    // need to pass in information to the super class which takes care of most of the backend stuff
    // passes the name of the Database


    public NotificationDbManger(Context context)
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
        System.out.println("On Create for Notification Db Manger");


    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



    // creates a database if a database does not already exist from the assets folder
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
               // deleteDataBase();

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



    @Override
    public synchronized void close() {

        if(mDataBase != null)
            mDataBase.close();

        super.close();

    }




    // checks if the current database that is open has a table called notificaitons
    public boolean checkTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.query("notifications",null,null,null,null,null,null,null);
            c.getCount();
            return true;
        }catch(Exception ex){
            System.out.println("table does not exist");
        }
        return false;
    }



    // creates a table called notifications
    public void createTable(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table notifications" + "(id integer primary key, " +
                        "ID_BUSSTOP text," +
                        "ROUTE text," +
                        "VIBRATE text," +
                        "SOUND text," +
                        "ID_NEXTSTOP text," +
                        "STOPS text" +
                        "ID_CURR_STOP text)"
        );

    }




    // Deletes the notificaiton table
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS notifications");
    }



    // deletes the specified row from the notificaiton database
    public void deleteRow(String rowID){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(NOTIFICATION_TABLE_NAME, BUSSTOP_COLUMN_ID +" = " + rowID,null );

        }catch(Exception ex){
            System.out.println("No such row or there are not rows available ");
        }
    }



    public int getRowIndex(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications",null,null,null,null,null,null,null);
        return 0;
    }






    // Close the database
    public void closeDataBase()
    {
        if(mDataBase  != null)
        {
            mDataBase.close();
        }

    }




    // returns the directory path of the database
    public void getPath(){

    }



    // checks if the bus stop has a notification already set for it, if there is not notificaiton
    // reflecting that bus stop id, this will return false
    public boolean hasNotifiation(String stopID){

        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" +stopID , null);
        c.moveToFirst();

        try{
            c.getString(BUSSTOPID_COLUMN);
            exists = true;
            System.out.println("bus stop exists");
        }catch (Exception ex){

        }
        c.close();

        return exists;

    }




    // returns true if the database contains notifications that is there are
    // more than 0 rows
    public boolean isNotification(){
        return false;
    }




    // returns the number of current pending notifcations if any
    public int numberOfNotifications(){
        SQLiteDatabase db = this.getReadableDatabase();
        int size = 0;
        try {
            Cursor c = db.query("notifications", null, null, null, null, null, null, null);
            size = c.getCount();
        }catch (Exception ex){

        }
        return size;
    }




    // returns the column names of the table called notifications if the table exists
    public ArrayList<String> getColumnNames(){
       ArrayList<String> columnNames = new ArrayList<>();
        String[] temp;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(NOTIFICATION_TABLE_NAME,null,null,null,null,null,null,null);

        temp = c.getColumnNames();
        for (int count = 0 ; count < temp.length; count++){
            columnNames.add(temp[count]);
        }
        return  columnNames;
    }




    // update nextStop and number of stops left for a given notification from the database
    public boolean updateNextStop(String stopID, String nextStop){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_NEXTSTOP", nextStop);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID});
        return true;
    }




    // update nextStop and number of stops left for a given notification from the database
    public boolean updateCurrStop(String stopID, String currStop){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_CURR_STOP", currStop);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID});
        return true;
    }




    // updates the number of stops left for this notification
    public boolean updateStopsLeft(String stopID, int stops){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STOPS", stops);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID});
        return true;
    }




    // updates the number of stops left for this notification
    public boolean updateRoute(String stopID, String newRoute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ROUTE", newRoute);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID});
        return true;
    }



    // updates a notification that exisists in the database currently
    public boolean updateNotificaiton ( String busStopID,String route,
                                       Boolean vibrate,Boolean sound, Integer nextStop, Integer stops  ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STOPS", stops);
        contentValues.put("ID_NEXTSTOP", nextStop);
        contentValues.put("VIBRATE", vibrate);
        contentValues.put("SOUND", sound);
        contentValues.put("ROUTE", route);
        contentValues.put("ID_CURR_STOP", stops);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{busStopID});
        return true;
    }




    // deletes the notification from the database
    public void deleteNotification(String stopID){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(NOTIFICATION_TABLE_NAME, BUSSTOP_COLUMN_ID +" = " + stopID,null );

        }catch(Exception ex){
            System.out.println("No such row or there are not rows available ");
        }
    }




    // adds a notification to the notification database
    public boolean addNotification(String busStopID, String route,
                                   String vibrate,String sound, int stops ){

        String currStopID;
        String nextStopID = busStopID;

        SQLiteDatabase db = this.getWritableDatabase();


        // get next bus stop Id
        System.out.println("integer " + stops);
        System.out.println("integer " + stops);
        currStopID = "" + getNextBusStopId(busStopID,route,stops);
        System.out.println("stop id: " + currStopID);
        System.out.println("current stop set: " +  getStopName(currStopID));

        if (stops - 1 != 0){
            nextStopID = "" + getNextBusStopId(busStopID,route,stops - 1);
            System.out.println("next stop set: " +  getStopName(nextStopID));
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopID);
        contentValues.put("STOPS", stops);
        contentValues.put("ID_NEXTSTOP", nextStopID);
        contentValues.put("VIBRATE", vibrate);
        contentValues.put("SOUND", sound);
        contentValues.put("ROUTE", route);
        contentValues.put("ID_CURR_STOP", currStopID);
        db.insert("notifications", null, contentValues);

        Cursor  c = db.query("notifications", null, null, null, null, null, null, null);

        System.out.println(c.getCount());

        return true;
    }




    // returns a cursor of the row with the given Bus Stop id
    public Cursor getDataByBusStopID(String stopID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery("select * from bus_stop_info where id=" + stopID, null);

        if (c.getCount() < 1){
            System.out.println("error row is empty");
        }
        c.moveToFirst();


        return c;
    }




    // The next functions are used for retrieving specific data from each row in the table

    // returns a curser of the row by index
    public Cursor getDataByIndex(int index){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" + (index - 1), null);
        c.moveToFirst();
        return c;
    }




    // returns a Latlng object for the specifed bus stop
    public LatLng getLocation(String stopID ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from bus_stop_info where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        LatLng coordinates = new LatLng(c.getDouble(XCOORD_COLUMN), c.getDouble(YCOORD_COLUMN));
        c.close();

        return coordinates;
    }




    // gets the next stop
    public String getNextStop(String stopID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        String nextStop = c.getString(NEXTSTOPID_COLUMN);

        return nextStop;
    }



    // gets the current stop for the specified bus stop id
    public String getCurrStop( String stopID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        String currStop = c.getString(CURR_STOP_COLUMN);

        return currStop;
    }


    // returns a String of the route that the notification is looking for
    public String getRoute(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" + id, null);
        c.moveToFirst();
        String route = c.getString(ROUTE_COLUMN);
        c.close();

        return route;
    }




    // returns the next stop id for the given notification
    public String getNextBusStopId(String stopID, String route , int stopsAway){
        SQLiteDatabase db = this.getReadableDatabase();

        String ROUTE = "";
        String nextStopid ="null";
        int count = 0;
        int column = 0;
        if(route.toLowerCase().contains("outer")){
            ROUTE = "OUTER";
        }else if(route.toLowerCase().contains("inner")){
            ROUTE = "INNER";
        }

        System.out.println(route);
        // gets table
        Cursor c = db.rawQuery("select " + ROUTE +  " from routes", null);

        // moves cursor until
        c.moveToFirst();
        column = c.getColumnIndex(ROUTE);
        int size = c.getCount();
        System.out.println("number of rows" + size);
        // moves the cursor to first index in the row of stops in the route
        for (int index  = 0 ; index < c.getCount(); index++){
            if(c.getString(count).equalsIgnoreCase(stopID)){
                while(count < stopsAway){
                    if(c.isFirst()){
                        c.moveToLast();
                        index = c.getCount() - 1;
                    }else{
                        c.moveToPrevious();
                        index -=1;
                    }
                    count++;
                }

                // skips rows that are null
                while(c.getString(c.getColumnIndex(ROUTE)) == null){
                    c.moveToPrevious();
                    index -=1;
                }
                nextStopid = c.getString(c.getColumnIndex(ROUTE));

                System.out.println(nextStopid);
                System.out.println("next stop id: " + nextStopid + " name; " + getStopName(nextStopid));
                break;
            }else if(c.isLast()){
                System.out.println("error did not find  match!!");
                break;
            }
            c.moveToNext();
        }

        c.close();

        return nextStopid;
    }





    // returns the number of stops left for the notification specified
    public int getStopsLeft(String stopID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        int stopsLeft = c.getInt(STOPS_COLUMN);
        c.close();

        return stopsLeft;
    }




    // Returns the name of the bus stop based on the bus Stop Id given
    public String getStopName(String stopID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from bus_stop_info where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        String stopName = c.getString(STOP_NAME_COLUMN);
        c.close();

        return stopName;
    }




    // returns the stop id of the first bus
    public String getFirstRowStopID(){

        // sets the pointer tot he first row of the database
        currentRow = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications", null, null, null, null, null, null);
        c.moveToFirst();

        String stopId = c.getString(BUSSTOPID_COLUMN);
        c.close();


        return stopId;
    }




    // returns the busID from the specifed row
    public String getNextRowStopID(){
        // moves the pointer to the next row
        currentRow += 1;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications", null, null, null,null,null,null);
        c.moveToPosition(currentRow);
        String stopId = "";
        try
        {
            stopId = c.getString(BUSSTOPID_COLUMN);
        }catch(Exception ex){
        }
        c.close();

        return stopId;

    }




    // if virbate is enabled return true that is setting == 1 , other wise return false
    public Boolean isVibrateEnabled(String busId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications", null, null, null,null,null,null);
        c.moveToFirst();
        int setting = 0;
        try
        {
            setting = c.getInt(VIBRATE_COLUMN);
        }catch(Exception ex){
        }
        c.close();


        return setting == 1;

    }




    // if virbate is enabled return true that is setting == 1 , other wise return false
    public Boolean isSoundEnabled(String busId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications", null, null, null,null,null,null);
        c.moveToFirst();
        int setting = 0;
        try
        {
            setting = c.getInt(SOUND_COLUMN);
        }catch(Exception ex){
        }
        c.close();


        return setting == 1;

    }


    // prints out the specified row as long as their exists a row for the specified locaiton
    public void printRow(int index){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query("notifications", null, null, null, null, null, null);
        int count = 0;

        if(c != null){
            c.moveToFirst();
            while (count < index - 1){

               try{
                   c.moveToNext();
               }catch (Exception ex){
                   System.out.println("no such index");
               }
                count++;
            }
            count = 0;
            while (count < c.getColumnCount()){
                System.out.println("row: " + index + "Column: " + c.getColumnName(count) + " : " + c.getString(count));
                count++;
            }

        }

    }


}
