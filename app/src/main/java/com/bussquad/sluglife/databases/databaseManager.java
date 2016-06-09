package com.bussquad.sluglife.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jose on 4/27/2016.
 */
public class databaseManager extends SQLiteOpenHelper {


    private static String DB_PATH = "";
    private static String DB_NAME ="notificationManager.sqlite";// Database name

    private static final int OBJECTID = 1;

    private SQLiteDatabase mDataBase;
    private final Context mContext;


    public databaseManager(Context context)
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




    public boolean hasItem(int itemid){
        boolean exists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from notifications where ID_BUSSTOP=" +itemid , null);
        c.moveToFirst();

        try{
            c.getString(OBJECTID);
            exists = true;
            System.out.println("bus stop exists");
        }catch (Exception ex){

        }
        c.close();

        return exists;
    }


}
