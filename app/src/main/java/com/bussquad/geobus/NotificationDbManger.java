package com.bussquad.geobus;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    public static final String BUSSTUP_COLUMN_ID = "id";
    public static final String ROUTE_COLUMN = "ROUTE";
    public static final String XCOORD_COLUMN = "XCOORD";
    public static final String YCOORD_COLUMN = "YCOORD";
    public static final String VIBRATE_COLUMN = "VIBRATE";
    public static final String SOUND_COLUMN = "SOUND";
    public static final String NEXTBUSTOP_COLUMN_ID = "ID_NEXTBUSSTOP";
    public static final String STOPS_COLUMN = "STOPS";

    private static String TAG = "NotificationDbManger"; // Tag just for the LogCat window


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




    // returns true if the database contains notifications that is there are
    // more than 0 rows
    public boolean isNotification(){
        return false;
    }




    // returns the number of current pending notifcations if any
    public int numberOfNotifications(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, NOTIFICATION_TABLE_NAME);
        return numRows;
    }




    // update nextStop and number of stops left for a given notification from the database
    public boolean updateNextStopandStops(Integer id, String nextStop, int stops){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STOPS", stops);
        contentValues.put("ID_NEXTBUSSTOP", nextStop);
        return true;
    }




    // updates a notification that exisists in the database currently
    public boolean updateNotificaiton (Integer id, Integer busStopID,String route, Integer XCOORD, Integer YCOORD,
                                       Boolean vibrate,Boolean sound, Integer nextStop, Integer stops, String stopName  ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopID);
        contentValues.put("ROUTE", route);
        contentValues.put("XCOORD", XCOORD);
        contentValues.put("YCOORD", YCOORD);
        contentValues.put("VIBRATE", vibrate);
        contentValues.put("SOUND", sound);
        contentValues.put("ID_NEXTBUSSTOP", nextStop);
        contentValues.put("STOPS", stops);
        contentValues.put("STOPNAME",stopName);
        db.update("notifications", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }




    // deletes the notification from the database
    public Integer deleteNotification(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }




    // adds a notification to the notification database
    public boolean insertNotification(int busStopID, String route, int XCOORD, int YCOORD,
                                      Boolean vibrate,Boolean sound, int nextStop, int stops ,String stopName ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", busStopID);
        contentValues.put("ROUTE", route);
        contentValues.put("XCOORD", XCOORD);
        contentValues.put("YCOORD", YCOORD);
        contentValues.put("VIBRATE", vibrate);
        contentValues.put("SOUND", sound);
        contentValues.put("ID_NEXTBUSSTOP", nextStop);
        contentValues.put("STOPS", stops);
        contentValues.put("STOPNAME",stopName);
        db.insert("notifications", null, contentValues);


//        mDataBase.execSQL("INSERT notifications");

        return true;
    }




    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
