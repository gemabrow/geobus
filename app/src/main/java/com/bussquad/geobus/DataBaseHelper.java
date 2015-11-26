package com.bussquad.geobus;


/*
    This class provides the necssary tools to access the sqlite database and return a list of
    bus schedules for a specific bus stop that exists in the database
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    // first database

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window

    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String DB_NAME ="bus_Schedule_UCSC.sqlite";// Database name
    private SQLiteDatabase mDataBase;
    private final Context mContext;




    // need to pass in information to the super class which takes care of most of the backend stuff
    // passes the name of the Database
    public DataBaseHelper(Context context)
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
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Check that the database exists here: /data/data/your package/databases/DB Name
    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }




    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int length;
        while ((length = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, length);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
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




    // Close the database
    public void closeDataBase()
    {
        if(mDataBase  != null)
        {
            mDataBase.close();
        }

    }



    @Override
    public synchronized void close()
    {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }


    // this is where you create the table or load db
    @Override
    public void onCreate(SQLiteDatabase db) {

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void getAllBusStopSchedule(){
        openDataBase();
        ArrayList<BusStopSchedule> busSchedule = new ArrayList<>();

        System.out.println(mDataBase.getPath());

        Cursor c = mDataBase.query("Bus_Stop_Schedule",null,null,null,null,null,null);
        int count = 0;
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do
                {
                    BusStopSchedule busStop = new BusStopSchedule();
                    busStop.setName(c.getColumnName(count));
                    count = 1;
                    busStop.setId(count);
                    count ++;

                    while(count < c.getColumnCount()) {
                        System.out.println("string " +  c.getString(count));
                        if (c.getString(count) != null){


                            count++;
                        }

                    }

                        busSchedule.add(busStop);
                }
                while (c.moveToNext());
                }
        }

        c.close();
        mDataBase.close();
       // return busSchedule;
    }



    public ArrayList<BusStopSchedule> getBusStopSchedule(int busStopID){

        openDataBase();
        ArrayList<BusStopSchedule> busSchedule = new ArrayList<>();


        Cursor c = mDataBase.query("Bus_Stop_Schedule",null,null,null,null,null,null);
        int count = 1;
        if(c!=null)
        {
            if(c.moveToPosition(busStopID))
            {

                while(count < c.getColumnCount()-1) {


                    if (c.getString(count) !=null && !c.getString(count).isEmpty()){
                        BusStopSchedule busStop = new BusStopSchedule();
                        busStop.setName(c.getColumnName(count));
                        busStop.setBusDepartSchedule(parseBusDepartTimes(c.getString(count), busStop.getName()));
                        busSchedule.add(busStop);
                    }

                    count++;
                }
            }
        }

        c.close();
        mDataBase.close();
        System.out.println("bus schedule size in db helper: " +  busSchedule.size());

        return busSchedule;
    }


    private ArrayList<String> parseBusDepartTimes(String busDepartTimes,String busName ){
        ArrayList<String> departTimes = new ArrayList<>();
        for (String temp:  busDepartTimes.split(","))
        {
                departTimes.add(temp);
        }

        return departTimes;
    }


    // add bus stop to database
//    public void add_busStopSchedule( )
//    {
//        openDataBase();
//        ContentValues iniValues = new ContentValues();
//        iniValues.put("10", busStopSchedule.getInfo());
//        mDataBase.insert("DATABASE_NAME", null, iniValues);
//        closeDataBase();
//    }

//    public void databaseString(){
//        for (BusStopSchedule temp: busSchedule){
//            System.out.println("Bus Stop ID: " + temp.getId() + " Bus stop Name: " + temp.getName());
//        }
//    }


}
