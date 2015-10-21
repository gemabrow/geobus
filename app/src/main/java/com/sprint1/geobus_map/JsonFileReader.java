package com.sprint1.geobus_map;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 10/16/2015.
 * This JsosnFileReader is specifically ment for reading the UCSC_WestSide_BussTOP.json file
 * Any other file read through this jsonFilereader will crash the program, I might expand on this later
 * when it comes to reading in bus stop schedules from the UCSC_WestSide_Busses.json file
 */


// this class will read a json file whether it is located in the assets folder
public class JsonFileReader {
    private JsonReader reader;
    private ArrayList<BusStop> BusStops;

    // construtor
    public JsonFileReader(){

    }


    // reads in the BusStops from UCSC_WestSide_BusStop.json file only

    public void readBusStopJsonStream(InputStream in) throws IOException {
        reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
             readBusStopArray(reader);
        }
        finally{
                reader.close();
        }

    }


    // Returns A list of BusStops
    private List readBusStopArray(JsonReader reader) throws IOException {
        this.BusStops = new ArrayList();

        // reads in a bracket{
        reader.beginObject();
        // skips (allstops) from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            BusStops.add(readBusStop(reader));
        }
        printBusStopList(BusStops);

        // reads in a a brace ]
        reader.endArray();
        return BusStops;
    }



    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private BusStop readBusStop(JsonReader reader) throws IOException {
        String stopName = "";
        double latitude = 00;
        double longitude = 00;
        ArrayList<String> busses = new ArrayList<>();

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
            } else if (name.equals("busses") && reader.peek() != JsonToken.NULL) {
                readBusArray(busses, reader);
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new BusStop(stopName,latitude,longitude,busses);
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
            System.out.println("Bus Stop name: " + temp.getTitle() + " Bus stop location: " + temp.getLatitude() + ":" + temp.getLongitude());
            System.out.println("Busses that stop at this location: ");
            for (String busTemp: temp.getBusses()){
                System.out.println("Bus: " + busTemp);
            }

        }
    }


}

