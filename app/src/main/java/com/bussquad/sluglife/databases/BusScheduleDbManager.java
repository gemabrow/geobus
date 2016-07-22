package com.bussquad.sluglife.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jose on 7/15/2016.
 */
public class BusScheduleDbManager extends SQLiteOpenHelper {

    // Androids default system path of the applicaiton database
    private static String DB_PATH = "";
    private static String DB_NAME ="busschedule.sqlite";// Database name

    public BusScheduleDbManager(Context context){
        super(context, DB_NAME, null, 1);// 1? Its database Version
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }





    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void addBusStopToScheduleDB(int stopid){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", stopid);
        db.insert("bus_stop_schedule",null,contentValues);
    }



    // adds the schedule to the specified bus stop
    public void addBusSchedule(int stopid, String route, String schedule){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_BUSSTOP", stopid);
        contentValues.put(route, schedule);

        db.insert("bus_stop_schedule",null,contentValues);
    }


    // update nextStop and number of stops left for a given notification from the database
    public boolean updateBusSchedule(int stopID, String route, String schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(route, schedule);
        db.update("bus_stop_schedule", contentValues, "ID_BUSSTOP = ? ", new String[]{stopID+""});
        return true;
    }



    public boolean hasLatestScheduleVersion(int stopid, String route){

        return true;
    }




    // checks if the bus schedule exists
    public boolean hasSchedule(int busStopId, String routeName){

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

        }
        return false;
    }

}
