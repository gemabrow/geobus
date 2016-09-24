package com.bussquad.sluglife.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.MapObject;
import com.bussquad.sluglife.Route;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jose on 8/31/2016.
 */
public class JsonRouteFileReader {


    ArrayList<Route> routes;

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readDiningArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readDiningArray(JsonReader reader) throws IOException {
        this.routes = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips the begining statment from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            routes.add(readRoute(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return routes;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private Route readRoute(JsonReader reader) throws IOException {
        Log.i("JsonRouteFileReader", "readRoute():  reading route_list.json");
        String name = "";
        String routeNumber = "";
        String temp = "";
        int id = -1;

        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a Dining Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("id")) {
                id = reader.nextInt();
            } else if (token.equals("route_name")) {
                name = reader.nextString();
            } else if(token.equals("route_number")){
                routeNumber = reader.nextString();
            } else if(token.equals("stops")){
                temp = reader.nextString();

            }else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        return new Route(id,name,routeNumber,temp);
    }



    public ArrayList<Route> getRoutes(){
        return this.routes;
    }
}
