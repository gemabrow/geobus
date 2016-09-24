package com.bussquad.sluglife.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.Dining;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 3/29/2016.
 */
public class JsonDiningFileParser {


    ArrayList<Dining> dining = new ArrayList<>();

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readDiningArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readDiningArray(JsonReader reader) throws IOException {
        Log.i("JsonDiningFileParser","readDiningArray():  reading dining.json");
        this.dining = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips (allstops) from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            dining.add(readDiningHallInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return dining;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private Dining readDiningHallInfo(JsonReader reader) throws IOException {
        Log.i("JsonBikeParking", "readDiningHallInfo(): parsing the reader file to return a dining " +
                " object");
        String name = "";
        double latitude = 0;
        double longitude = 0;
        String id = "";
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
            } else if(token.equals("id")){
                id = reader.nextString();
            }else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new Dining(id,name,latitude,longitude);
    }




    // returns an array list of Dining locations
    public ArrayList<Dining> getDining(){
        return dining;
    }


}
