package com.bussquad.geobus.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.geobus.Library;
import com.bussquad.geobus.MapObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 4/7/2016.
 */
public class JsonMapObjectFileReader {

    ArrayList<MapObject> mapObjects;

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readDiningArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readDiningArray(JsonReader reader) throws IOException {
        this.mapObjects = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips the begining statment from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            mapObjects.add(readLibraryInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return mapObjects;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private MapObject readLibraryInfo(JsonReader reader) throws IOException {
        Log.i("JsonLibraryFileParser", "readLibraryInfo():  reading library.json");
        String name = "";
        double latitude = 0;
        double longitude = 0;

        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a Dining Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("name")) {
                name = reader.nextString();
            } else if (token.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(token.equals("longitude")){
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new MapObject(latitude,longitude,name);
    }


}
