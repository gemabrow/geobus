package com.bussquad.sluglife.parser;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.OpersFacility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 5/8/2016.
 */
public class JsonOpersFacilityParser {

    ArrayList<OpersFacility> opersFacilities = new ArrayList<>();
    Context context;

    public void ReadJsonFile(InputStream in) throws IOException {


        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readEventArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readEventArray(JsonReader reader) throws IOException {
        this.opersFacilities = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips the events tag
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();



        while (reader.hasNext()) {

            opersFacilities.add(readEventInfo(reader));
        }
        // reads in a a brace ]
        System.out.println("reading in brace");
        reader.endArray();
        // skips last updated

        System.out.println("skipping value");
//        reader.skipValue();
        // skips closing brace
        System.out.println("ending object");

        return opersFacilities;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private OpersFacility readEventInfo(JsonReader reader) throws IOException {
        Log.i("JsonLibraryFileParser", "readLibraryInfo():  reading library.json");

        String name = "";
        String pep_count = "0";
        String description = "no description available";
        String location = "";
        double latitude = 0.0;
        double longitude = 0.0;
        String imgurl = null;
        String lastupdate = "";
        String id  = "";

        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a Dining Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("id")) {
                id = reader.nextString();
            } else if(token.equals("count")){
                pep_count = reader.nextString();
            } else if(token.equals("facility_name")){
                name = reader.nextString();
            } else if(token.equals("last_updated")){
                lastupdate  = reader.nextString();
            } else if(token.equals("imgurl")){
                imgurl  = reader.nextString();
            } else if(token.equals("latitude")){
                latitude = reader.nextDouble();
            } else if(token.equals("longitude")){
                longitude = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();

        OpersFacility opersFacility = new OpersFacility(id,name,description,pep_count,lastupdate,imgurl,latitude,longitude);
        return opersFacility;
    }



    public ArrayList<OpersFacility> getOpersFacilities(){
        return this.opersFacilities;
    }


}
