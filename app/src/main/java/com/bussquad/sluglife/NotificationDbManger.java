package com.bussquad.sluglife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import java.util.Arrays;
import java.util.List;

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



    // used for getting data from the bus_atop_info table
    private static final int XCOORD_COLUMN = 2;
    private static final int YCOORD_COLUMN = 3;
    private static final int STOP_NAME_COLUMN = 4;
    private static final int ROUTE_LIST_COLUMN = 6;

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
        System.out.println("creating database");
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        System.out.println("databases status: " + mDataBaseExist);
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
                throw new Error("Error Copying DataBase");
            }
        }
        else{
            System.out.println("database already exists ");
        }
    }




    // checks if the table exists or not
    boolean isTableExists( String tableName)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
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

        File dbFile = new File(DB_PATH + DB_NAME);

        return dbFile.exists();
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
    public boolean checkTable(String tablename){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.query(tablename,null,null,null,null,null,null,null);
            c.getCount();
            return true;
        }catch(Exception ex){
            System.out.println("table does not exist");
        }
        return false;
    }




    // checks if the current database that is open has a table called notificaitons
    public int checkTableSize(String tablename){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.query(tablename,null,null,null,null,null,null,null);

            return c.getCount();
        }catch(Exception ex){
            System.out.println("table does not exist");
        }
        return 0;
    }



    // creates a table called notifications
    public void createNotificationTable(){

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



    public void createRouteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table route" + "(route_id integer primary key, " +
                "name text," +
                "route_name text" +
                "longitude text" +
                "stops text"
        );
    }




    public void createBusStopInfoTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table bus_stop_info" + "(idbusstop integer primary key, " +
                "id text," +
                "latitude text" +
                "longitude text" +
                "name text" +
                "routes text" +
                "cardinal_point" +
                "additional_info text)"
        );
    }



    // Deletes the specified table if it exists
    public void deleteTable(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + name);
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
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID+""});
        return true;
    }




    // update nextStop and number of stops left for a given notification from the database
    public boolean updateCurrStop(String stopID, String currStopID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_CURR_STOP", currStopID);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID+""});
        return true;
    }




    // updates the number of stops left for this notification
    public boolean updateStopsLeft(String stopID, int stops){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STOPS", stops);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID+""});
        return true;
    }




    // updates the number of stops left for this notification
    public boolean updateRoute(String stopID, String newRoute){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ROUTE", newRoute);
        db.update("notifications", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID+""});
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
        currStopID = getNextBusStopId(busStopID,route,stops);

        if (stops - 1 != 0){
            nextStopID = getNextBusStopId(busStopID,route,stops - 1);
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

//        Cursor  c = db.query("notifications", null, null, null, null, null, null, null);


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
        Cursor c = db.rawQuery("select * from bus_stop_info where id=" + stopID, null);
        c.moveToFirst();
        LatLng coordinates = new LatLng(c.getDouble(2), c.getDouble(3));
        c.close();

        return coordinates;
    }



    // returns a Latlng object for the specifed bus stop
    public double getLatitude(String stopID ){
        double latitude;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from bus_stop_info where id=" + stopID, null);
        c.moveToFirst();
        latitude =  c.getDouble(XCOORD_COLUMN);
        c.close();

        return latitude;
    }



    // returns a Latlng object for the specifed bus stop
    public double getLongitude(String stopID ){
        double latitude;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from bus_stop_info where id=" + stopID, null);
        c.moveToFirst();
        latitude =  c.getDouble(YCOORD_COLUMN);
        c.close();

        return latitude;
    }


    // gets the next stop
    // TODO fixme nextStop does not make sense, next stop can only be determined based on the route 
    // that the bus is going
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
    public String getRoute(String stopid){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from notifications where id=" + stopid, null);
        c.moveToFirst();
        String route = c.getString(ROUTE_COLUMN);
        c.close();

        return route;
    }




    // returns the next stop id for the given notification
    // @param stopId                    the id associated with the bus stop this is used to look
    //                                  up the correct row in the database
    // @param route                     used to select the correct column in the table, this will
    //                                  help select the correct next stop depending on the route
    // @param stopsaway                 This indicates the number of stops away left to reach the
    //                                  the bus stop the user is waiting to be notified from
    public String getNextBusStopId(String stopID, String route , int stopsAway){
        SQLiteDatabase db = this.getReadableDatabase();

        String ROUTE = "";
        String nextStopid = "";
        int count = 0;
        int column = 0;
        if(route.toLowerCase().contains("outer")){
            ROUTE = "OUTER";
        }else if(route.toLowerCase().contains("inner")){
            ROUTE = "INNER";
        }

        // gets table
        Cursor c = db.rawQuery("select " + ROUTE +  " from routes", null);

        // moves cursor until
        c.moveToFirst();
        column = c.getColumnIndex(ROUTE);
        int size = c.getCount();
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
    public int getStopName(String stopID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select * from bus_stop_info where ID_BUSSTOP=" + stopID, null);
        c.moveToFirst();
        int stopName = c.getInt(STOP_NAME_COLUMN);
        c.close();

        return stopName;
    }




    // returns the stop id of the first bus stop given the colom
    // TODO // FIXME: 5/12/2016 
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




    // returns a list of routes that belong to the bus stop  specified
    public List<String> getBusStopRoute(String busStopId){

        List<String> route;
        String routeData;
        // sets the pointer tot he first row of the database
        currentRow = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from bus_stop_info where id=" + busStopId, null);
        c.moveToFirst();

        String temp1 = c.getString(ROUTE_LIST_COLUMN);
        route = Arrays.asList((temp1).split(","));

        c.close();


        return route;
    }


    // returns the busID from the specifed row
    public String getNextRowStopID(){
        // moves the pointer to the next row
        currentRow += 1;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("notifications", null, null, null,null,null,null);
        c.moveToPosition(currentRow);
        String stopId ="";
        try
        {
            stopId = c.getString(BUSSTOPID_COLUMN);
        }catch(Exception ex){
        }
        c.close();

        return stopId;

    }



    public List<String> getStopScheduleForRoute(String busStopId, String routeName){
        List<String> route;
        String routeData;
        // sets the pointer tot he first row of the database
        currentRow = 0;
        System.out.println("NotificationDBManager getting stop schedule for route");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + "\"" + routeName + "\"" + " from bus_stop_schedule where ID_BUSSTOP=" + busStopId, null);
        c.moveToFirst();

        String temp1 = c.getString(0);
        route = Arrays.asList((temp1).split(";"));


        c.close();

        return route;
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



    // adds a column for the bus stop to the bus schedule database
    public void addBusStopToScheduleDB(String busStopId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopId);
        db.insert("bus_stop_schedule",null,contentValues);
        db.close();
    }



    public void addBusScheduleArray(String busStopId, String route, ArrayList<String> schedule){
        if(hasBusStopInDatabase(busStopId) == false){

            // add the bus stop to the db
            addBusStopToScheduleDB(busStopId);
        }

        // concat the array list of bus bus schedules
        String bus_schedule = "";
        int count = 0;
        for (String tmp : schedule){
            bus_schedule += tmp;
            if(count < schedule.size()){
                bus_schedule +=";";
            }
            count++;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopId);
        contentValues.put("\""+route+"\"" , bus_schedule);
        db.update("bus_stop_schedule", contentValues, "ID_BUSSTOP = ? ", new String[]{busStopId+""});
        db.close();
    }


    // adds the schedule to the specified bus stop. Bus first checks if a row exists in the database
    // for the bus stop. If it does not it will call addBusStopToScheduleDB which will add the
    // bus stop to the database. Then it will add the schedule
    public void addBusSchedule(String busStopId, String route, String schedule){

        if(hasBusStopInDatabase(busStopId) == false){

            // add the bus stop to the db
            addBusStopToScheduleDB(busStopId);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopId);
        contentValues.put("\""+route+"\"" , schedule);
        db.update("bus_stop_schedule", contentValues, "ID_BUSSTOP = ? ", new String[]{busStopId+""});
        db.close();
    }


    // update nextStop and number of stops left for a given notification from the database
    public boolean updateBusSchedule(String busStopId, String route, String schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("\""+route+"\"", schedule);
        db.update("bus_stop_schedule", contentValues, "ID_BUSSTOP = ? ", new String[]{busStopId+""});
        return true;
    }



    public boolean hasLatestScheduleVersion(String stopid, String route){

        return true;
    }



    // checks if the bus stop exists in the bus stop schedule databsase returns true if it does
    // otherwise returns false
    public boolean hasBusStopInDatabase(String busStopId){
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("select bus_stop_schedule where ID_BUSSTOP=" + busStopId, null);
            c.moveToFirst();

            // if the string is emtpy that means no bus schedule is set
            if(c.getString(0).length() == 0){
                return false;
            }
            return true;
        } catch (Exception ex){
            System.out.println("does not have bus stop");
        }
        return false;
    }



    // checks if the bus schedule exists
    public boolean hasSchedule(String busStopId, String routeName){

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = db.rawQuery("select " + "\"" + routeName + "\"" + " from bus_stop_schedule where ID_BUSSTOP=" + busStopId, null);
            c.moveToFirst();

            // if the string is emtpy that means no bus schedule is set
            if(c.getString(0).length() == 0){
                return false;
            }
            return true;
        } catch (Exception ex){
            System.out.println(" does not have bus stop schedule");
        }
        return false;
    }



    /*
        the following functions help manage the bus_stop_info table
     */

    public void addBusStopInfo(BusStop stop){

        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", stop.getObjectID());
            contentValues.put("longitude", stop.getLongitude());
            contentValues.put("latitude", stop.getLatitude());
            contentValues.put("name", stop.getName());
            contentValues.put("cardinal_point", stop.getRoutes());
            contentValues.put("routes", stop.getRoutes());
            db.insert("bus_stop_info", null, contentValues);

        } catch (Exception ex){
            System.out.println("error adding bus to sqlite database");
        }

    }




    public void printTable(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + table ,null);
        c.moveToFirst();
        System.out.println("printing the table: " + table);
        for(int count = 0; count < c.getCount(); count++){

            System.out.println("idbusstop: " + c.getString(0) +  "| id: " + c.getString(1) + "| latitude: "
                    + c.getString(2) + "| longitude: " + c.getString(3) + "| name: " + c.getString(4) );
            c.moveToNext();
        }
    }
}
