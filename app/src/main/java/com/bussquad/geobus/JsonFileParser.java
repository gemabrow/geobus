package com.bussquad.geobus;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 3/23/2016.
 */
public class JsonFileParser {

    ArrayList<Library> libraries;
    ArrayList<Dining> diningHalls;
    ArrayList<BikeRack> bikeRacks;
    ArrayList<WaterFountain>waterFountains;
    ArrayList<BusStop>busStops;
    String type;

    public void ReadJsonFile(InputStream in, String classType) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readBusArray(reader);
        }

    }









//
//    // Returns A list of BusStops
//    private List readDiningArray(JsonReader reader) throws IOException {
//        this.busStops = new ArrayList<>();
//
//        // reads in a bracket{
//        reader.beginObject();
//        // skips (allstops) from json file
//        reader.skipValue();
//        // reads in a brace [
//        reader.beginArray();
//        while (reader.hasNext()) {
//
//            busStops.add(readDiningArray(reader));
//        }
//        // reads in a a brace ]
//        reader.endArray();
//        return busStops;
//    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
  /*  private BusStop readBusStop(JsonReader reader) throws IOException {
        String stopName = "";
        double latitude = 0;
        double longitude = 0;
        int id = -1;
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
            } else if (name.equals("id")){
                id = reader.nextInt();

            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new BusStop(stopName,latitude,longitude,busses, id);
    }

*/


    // reads in the a list of busses that stop at the specified bus stop
    private void readBusArray( JsonReader reader) throws IOException {

        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {
       //     busses.add(reader.nextString());
        }

        // reads in a closing brace ]
        reader.endArray();

    }




    // reads in one Dining object from the json file at a time and returns a new Dining Object
    private Dining readDining(JsonReader reader) throws IOException{

        String name = "";
        double latitude = 0;
        double longitude = 0;

        while (reader.hasNext()) {
            if (name.equals("name")) {
                name = reader.nextString();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(name.equals("longitude")){
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }


        return new Dining(name,latitude,longitude);
    }




    // reads in one Library object from the json file at a time and returns a new Library Object
    private Library readLibraryInfo(JsonReader reader) throws IOException {
        String name = "";
        double latitude = 0;
        double longitude = 0;

        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of
        while (reader.hasNext()) {
            if (name.equals("name")) {
                name = reader.nextString();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(name.equals("longitude")){
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new Library(name,latitude,longitude);
    }




    // reads in one WaterFountain object from the json file at a time and returns a
    // new WaterFountain Object
    private WaterFountain readWaterFountain(JsonReader reader)throws IOException{
        String name = "";
        double latitude = 0;
        double longitude = 0;

        while (reader.hasNext()) {
            if (name.equals("name")) {
                name = reader.nextString();
            } else if (name.equals("latitude")) {
                latitude = reader.nextDouble();
            } else if(name.equals("longitude")){
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }


        return new WaterFountain(name,latitude,longitude);
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
                latitude = reader.nextDouble();
            } else if(name.equals("lat")){
                longitude = reader.nextDouble();
            } else if (name.equals("type") && reader.peek() != JsonToken.NULL) {
                type = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new Bus(id ,latitude,longitude,type);
    }





//    public ArrayList<Bus> getActiveBuses(){
//        return this.buses;
//    }

}
