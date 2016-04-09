package com.bussquad.geobus.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.geobus.BikeRack;
import com.bussquad.geobus.Dining;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 3/29/2016.
 */
public class JsonBikeParkingFileParser {

    ArrayList<BikeRack> bikeRack = new ArrayList<>();

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readBikeJson(reader);
        }

    }

    // Returns A list of Bike Racks
    private List readBikeJson(JsonReader reader) throws IOException {
        Log.i("JsonBikeParking","readBikeJson():  reading bike.json");

        this.bikeRack = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips (bike racks) from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            bikeRack.add(ReadBikeParkingInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return bikeRack;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private BikeRack ReadBikeParkingInfo(JsonReader reader) throws IOException {
        Log.i("JsonBikeParking","readBikeParkingInfo(): parsing the reader file to return a Bike rack " +
                " object");

        String name = "";
        double latitude = 0;
        double longitude = 0;
        int racks = 0;
        ArrayList<String> rackTypes = new ArrayList<>();
        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it
        // stores it to create a Bike Rack Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("name")) {
                name = reader.nextString();
            } else if (token.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(token.equals("longitude")){
                longitude = reader.nextDouble();
            } else if (token.equals("racks")){
               racks = reader.nextInt();
            }else if (token.equals("type"))
            {
                readRackTypes(rackTypes, reader);
            }else{
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new BikeRack(name,latitude,longitude,racks,rackTypes);
    }



    // reads the rack types from the array of rack types in the json file
    public void readRackTypes(List<String> rackTypes, JsonReader reader) throws IOException {

        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {
            rackTypes.add(reader.nextString());
        }

        // reads in a closing brace ]
        reader.endArray();

    }




    // returns a list of bike racks on campus
    public ArrayList<BikeRack> getBikeParking(){
        return bikeRack;
    }

}
