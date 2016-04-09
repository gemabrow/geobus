package com.bussquad.geobus.parser;

import android.util.JsonReader;
import android.util.JsonToken;

import com.bussquad.geobus.Bus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 3/8/2016.
 */


/*
 Reads the json file from http://bts.ucsc.edu:8081/location/get
 Returns an arrayList of buses retrieved from the file

 */



public class JsonBusFileReader {

    ArrayList<Bus> buses;


    public JsonBusFileReader(){

    }


    // reads in the BusStops from UCSC_WestSide_BusStop.json file only
    public void readBusJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readBusArray(reader);
        }

    }


    // Returns A list of BusStops
    private List readBusArray(JsonReader reader) throws IOException {
        this.buses = new ArrayList<>();


        // reads in a brace [
        reader.beginArray();

        while (reader.hasNext()) {

            buses.add(readBus(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return buses;
    }



    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private Bus readBus(JsonReader reader) throws IOException {
        int id = -1;
        String type = "";
        double latitude = 0;
        double longitude = 0;
        ArrayList<String> busses = new ArrayList<>();

        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a BusStop Obect
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("lon")) {
                longitude = reader.nextDouble();
            } else if(name.equals("lat")){
                latitude = reader.nextDouble();
            } else if (name.equals("type") && reader.peek() != JsonToken.NULL) {
                type = reader.nextString();
                System.out.println("type: " + type);
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new Bus(id ,latitude,longitude,type);
    }





    public ArrayList<Bus> getActiveBuses(){
        return this.buses;
    }

}
