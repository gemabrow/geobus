package com.bussquad.sluglife.parser;

import android.util.JsonReader;
import android.util.JsonToken;

import com.bussquad.sluglife.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jose on 7/10/2016.
 */
public class JsonFileReader {

    ArrayList<String>data ;
    String rawData = "no data";
    public JsonFileReader(){

    }


    // reads in the BusStops from UCSC_WestSide_BusStop.json file only
    public void readJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readDataArray(reader);
        }

    }


    // Returns A list of BusStops
    private List readDataArray(JsonReader reader) throws IOException {

        // reads in a bracket {
        reader.beginObject();

        while (reader.hasNext()) {

            this.data = new ArrayList<>(Arrays.asList((readData(reader)).split(";")));
        }
        // reads in a a brace ]
        reader.endObject();
        return data;
    }



    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private String readData(JsonReader reader) throws IOException {
        // reads in a brace [

        reader.skipValue();
        reader.beginArray();
        reader.beginObject();
        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a BusStop Obect
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("schedule")) {
                this.rawData = reader.nextString();
        } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        reader.endArray();
        //reads in a closing bracket }
        return this.rawData;
    }





    public ArrayList<String> getData(){
        return this.data;
    }



    public String getRawData(){
        return this.rawData;
    }

}
