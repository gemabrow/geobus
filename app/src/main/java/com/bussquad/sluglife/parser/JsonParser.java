package com.bussquad.sluglife.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.Library;
import com.bussquad.sluglife.MapObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 11/15/2017.
 */
public class JsonParser {


    ArrayList<MapObject> mapObject = new ArrayList<>();

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readMapObjectArray(reader);
        }

    }



    // Returns A list of BusStops
    private List readMapObjectArray(JsonReader reader) throws IOException {
        this.mapObject = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips (allstops) from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            mapObject.add(readMapObjectInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return mapObject;
    }






    // reads in one mpobject from the json file at a time and returns a new MapObject Object
    private MapObject readMapObjectInfo(JsonReader reader) throws IOException {
        Log.i("JsonMapObjectFileReader", "readMapObejctInfo():  reading mapobject.json");
        int id = -1;
        double latitude = 0;
        double longitude = 0;
        String phoneNumber = "";
        String description= "";
        String name = "";
        String website = "";

        MapObject mMapObject = new MapObject();


        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a Dining Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("name")) {
                name = reader.nextString();
                mMapObject.setName(name);
            } else if (token.equals("latitude")) {
                latitude = reader.nextDouble();
                mMapObject.setLatitude(latitude);
            } else if (token.equals("longitude")) {
                longitude = reader.nextDouble();
                mMapObject.setLatitude(longitude);
            } else if(token.equals("id")){
                id = reader.nextInt();
                mMapObject.setId(id);
            } else if(token.equals("description")){
                description = reader.nextString();
                mMapObject.setDescription(description);
            } else if(token.equals("phone")){
                phoneNumber = reader.nextInt() +"";
                mMapObject.setPhoneNumber(phoneNumber);
            } else if(token.equals("website")){
                website = reader.nextString();
                mMapObject.setWebsite(website);
            } else {
                reader.skipValue();
            }
        }

        //reads in a closing bracket }
        reader.endObject();


        return mMapObject;

    }





    // returns an array list of Library locations
    public ArrayList<MapObject> getLibraries(){
        if (mapObject.size() == 0){
            return null;
        }
        return mapObject;
    }

}
