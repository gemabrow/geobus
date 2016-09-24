package com.bussquad.sluglife;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jose on 1/22/2016.
 */
public class DataObject implements Parcelable{

    private static final String KEY_ID = "OBJID";
    private static final String KEY_HEADER = "header";
    private static final String KEY_MAIN = "maintxt";
    private static final String KEY_SUBTEXT = "subtxt";
    private static final String KEY_RESOURCEID = "ID";
    private static final String KEY_IMAGEURL ="IMGURL";

    private String objectid = "";
    private int iconId;
    private String imgUrl;
    private String headerText;
    private String mainText;
    private String subText;

    public DataObject(){

    }

    public DataObject(String objectid,int resourceId, String headerText, String mainText, String subText){
        this.objectid = objectid;
        this.iconId = resourceId;
        this.headerText = headerText;
        this.mainText = mainText;
        this.subText = subText;


    }


    public int getIconId() {
        return iconId;
    }




    public void setIconId(int iconId) {
        this.iconId = iconId;
    }




    public String getHeaderText() {
        return headerText;
    }




    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }




    public String getMainText() {
        return mainText;
    }




    public void setMainText(String mainText) {
        this.mainText = mainText;
    }




    public String getSubText() {
        return subText;
    }




    public void setSubText(String subText) {
        this.subText = subText;
    }



    public void setImageUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }



    public String getImageUrl(){
        return this.imgUrl;
    }



    public void setObjectid(String objectid){
        this.objectid = objectid;
    }




    public String getObjectid(){
        return objectid;
    }
    @Override
    public int describeContents() {
        return 0;
    }





    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RESOURCEID,iconId);
        bundle.putString(KEY_HEADER, headerText);
        bundle.putString(KEY_MAIN,mainText);
        bundle.putString(KEY_SUBTEXT,subText);
        bundle.putString(KEY_IMAGEURL,imgUrl);
        bundle.putString(KEY_ID,objectid);

        dest.writeBundle(bundle);

    }



    public static final Parcelable.Creator<DataObject> CREATOR = new Parcelable.Creator<DataObject>() {

        public DataObject createFromParcel(Parcel in) {

            // read the bundle contain key value pairs from the parcel
            Bundle bundle = in.readBundle();


            // instantiate a DataObject using the values from the bundle
            DataObject newObject = new DataObject(bundle.getString(KEY_ID),
                    bundle.getInt(KEY_RESOURCEID),
                    bundle.getString(KEY_HEADER),
                    bundle.getString(KEY_MAIN),
                    bundle.getString(KEY_SUBTEXT)
            );

            if(bundle.getString(KEY_IMAGEURL) != null){
                newObject.setImageUrl(bundle.getString(KEY_IMAGEURL));
            }

            return newObject;
        }

        public DataObject[] newArray(int size) {
            return new DataObject[size];
        }
    };




    public DataObject(Parcel in) {
        objectid = in.readString();
        iconId = in.readInt();
        headerText = in.readString();
        mainText = in.readString();
        subText = in.readString();
        imgUrl = in.readString();
    }


}