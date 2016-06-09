package com.bussquad.sluglife.parser;

import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.Library;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 3/29/2016.
 */
public class JsonLibraryFileParser {

    ArrayList<Library> libraries = new ArrayList<>();

    public void ReadJsonFile(InputStream in) throws IOException {

        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readDiningArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readDiningArray(JsonReader reader) throws IOException {
        this.libraries = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips (allstops) from json file
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();
        while (reader.hasNext()) {

            libraries.add(readLibraryInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        return libraries;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private Library readLibraryInfo(JsonReader reader) throws IOException {
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
        return new Library(name,latitude,longitude);
    }





    // returns an array list of Library locations
    public ArrayList<Library> getLibraries(){
        if (libraries.size() == 0){
            return null;
        }
        return libraries;
    }


}
