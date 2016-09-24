package com.bussquad.sluglife;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jose on 10/16/2015.
 * This JsosnFileReader is specifically ment for reading the UCSC_WestSide_BussTOP.json file
 * Any other file read through this jsonFilereader will crash the program, I might expand on this later
 * when it comes to reading in bus stop schedules from the UCSC_WestSide_Busses.json file
 */


// this class will read a json file whether it is located in the assets folder
public class JsonFileReader {
    private static String TAG = "JsonFileReader"; // Tag just for the LogCat window
    private ArrayList<BusStop> BusStops;
    private ArrayList<LatLng> coordinates;

    // construtor
    public JsonFileReader(){

    }




    // reads in the BusStops from UCSC_WestSide_BusStop.json file only
    public void readBusStopJsonStream(InputStream in) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readBusStopArray(reader);
        }

    }




    // Returns A list of BusStops
    private List readBusStopArray(JsonReader reader) throws IOException {
        this.BusStops = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips (allstops) from json file
        String name = reader.nextName();
        System.out.println("reading: " + reader.nextString());
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            BusStops.add(readBusStop(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return BusStops;
    }




    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private BusStop readBusStop(JsonReader reader) throws IOException {
        String stopName = "";
        String routes = "";
        double latitude = 0;
        double longitude = 0;
        String id = "";
        List<String> busses = new ArrayList<>();

        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a BusStop Obect
        while (reader.hasNext()) {

            String name = reader.nextName();
            if (name.equals("name")) {
                stopName = reader.nextString();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(name.equals("longitude")){
                longitude = reader.nextDouble();
            } else if (name.equals("routes") && reader.peek() != JsonToken.NULL) {
                routes = reader.nextString();
                busses = Arrays.asList(routes.split(","));

            } else if (name.equals("id")){
                id = reader.nextString();

            } else {
                reader.skipValue();
            }
        }

        //reads in a closing bracket }
        reader.endObject();
        return new BusStop(stopName,latitude,longitude,busses,routes, id);
    }




    // reads in the a list of busses that stop at the specified bus stop
    private void readBusArray(ArrayList<String> busses, JsonReader reader) throws IOException {

        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {
            busses.add(reader.nextString());
        }

        // reads in a closing brace ]
        reader.endArray();

    }




    // returns a list of Bustops read from the json file
    public ArrayList<BusStop> getBusStops(){
        return this.BusStops;
    }




    // for testing purposes only , prints a list of bus stop information read from the file.
    public void printBusStopList(ArrayList<BusStop> BusStops){
        for (BusStop temp: BusStops){
            System.out.println("Bus Stop name: " + temp.getName() + " Bus stop location: " + temp.getLatitude() + ":" + temp.getLongitude());
            System.out.println("Busses that stop at this location: ");
            for (String busTemp: temp.getBusses()){
                System.out.println("Bus: " + busTemp);
            }

        }
    }
  
  
  
  
    //************************************************************************************************//
    // read in UCSC_Westside_Busses.json file only
    public void readScheduledStops(InputStream in, String route, ArrayList<BusStop> allStops) throws IOException {
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readBuses(reader, route, allStops);
        }
    }




    private void readBuses(JsonReader reader, String route, ArrayList<BusStop> allStops) throws IOException {
        ArrayList<String> scheduledStops = new ArrayList<String>(16);
        coordinates = new ArrayList<LatLng>(16);

        reader.beginObject();
        reader.skipValue();
        reader.beginArray();
        while (reader.hasNext()) {
            readBusArray(reader, route, scheduledStops);
        }
        //reader.endArray();
        //reader.endObject();
        Log.i(TAG, "Finished JSONing");
        for (BusStop stop : allStops) {
            coordinates.add(scheduledStops.indexOf(stop.getName()), stop.getLatLng());
        }
    }




    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private void readBusArray(JsonReader reader, String route, ArrayList<String> scheduledStops) throws IOException {
        Log.d(TAG, "begin reader");
        reader.beginObject();
        while (reader.hasNext()) {
            Log.i(TAG, reader.peek().toString());
            String name = reader.nextName();
            Log.d(TAG, name);
            if (name.equals("name") && reader.peek().equals(route)) {
                //found match in schedule, take in bus stop locations
                reader.skipValue();
                readStringArray(scheduledStops, reader);
                Log.i(TAG, "MATCHED");
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }




    // reads in the a list of busses that stop at the specified bus stop
    private void readStringArray(ArrayList<String> targets, JsonReader reader) throws IOException {

        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {
            targets.add(reader.nextString());
        }

        // reads in a closing brace ]
        reader.endArray();

    }




    public ArrayList<LatLng> getStopLocations() {
        return coordinates;
    }

}

