package com.bussquad.sluglife.parser;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.bussquad.sluglife.Event;
import com.bussquad.sluglife.databases.EventDbManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 4/10/2016.
 */
public class JsonEventJsonParser {

    ArrayList<Event> events = new ArrayList<>();
    Context context;
    EventDbManager eventDB;
    public void ReadJsonFile(InputStream in,Context context) throws IOException {

        this.context = context;
        eventDB =  new EventDbManager(context);
        eventDB.createDataBase();
        try (JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"))) {
            readEventArray(reader);
        }

    }

    // Returns A list of BusStops
    private List readEventArray(JsonReader reader) throws IOException {
        this.events = new ArrayList<>();

        // reads in a bracket{
        reader.beginObject();
        // skips the events tag
        reader.skipValue();
        // reads in a brace [
        reader.beginArray();



        while (reader.hasNext()) {

            events.add(readEventInfo(reader));
        }
        // reads in a a brace ]
        reader.endArray();
        reader.endObject();
        return events;
    }






    // reads in one bus stop from the json file at a time and returns a new BusStop Object
    private Event readEventInfo(JsonReader reader) throws IOException {
        Log.i("JsonLibraryFileParser", "readLibraryInfo():  reading library.json");

        String name = "";
        String cardText = "";
        String fullViewTxt = "";
        String location = "";
        String thumnail = "";
        String link = "";
        String date = "";
        String tmp = "";
        double latitude = 0;
        double longitude = 0;
        double cost;
        String tmpText;
        String tmparray[];
        String id  = "";

        // reads in a bracket {
        reader.beginObject();
        // reads in the "event" tag
        reader.skipValue();
        // reads in a bracket {
        reader.beginObject();

        // the String name  stores the next token in the json file and if it equals any of the specified
        // key strings such as name of bus stop, latitude, longitude or busses it stores it to create a Dining Object
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("title")) {
                name = reader.nextString();
            } else if(token.equals("teaser")){
                cardText = reader.nextString();
            } else if(token.equals("location")){
                location = reader.nextString();
            } else if(token.equals("link")){
                tmpText = reader.nextString();
                id = tmpText.substring(tmpText.lastIndexOf('/') + 1);
                link = tmpText;
            } else if(token.equals("event_date")){
                date = reader.nextString();
            } else if(token.equals("thumbnail")){
                thumnail = getImageThumbnailUrl(reader);
            }else if(token.equals("description")){
                tmpText = reader.nextString();
                if(tmpText.contains("\r")){
                    tmparray = tmpText.split("\r");
                    for(String dep : tmparray){
                        fullViewTxt += dep + "\n";
                    }
                } else {
                    fullViewTxt = tmpText;
                }


            } else if (token.equals("latitude")) {
                    tmp = reader.nextString();
                if(!tmp.equals("")) {
                    latitude = Double.parseDouble(tmp);
                }
            } else if(token.equals("longitude")){
                tmp = reader.nextString();
                if(!tmp.equals("")) {
                    longitude = Double.parseDouble(tmp);
                }
            } else {
                reader.skipValue();
            }
        }
        //reads in a closing bracket }
        reader.endObject();
        //reads in a closing bracket }
        reader.endObject();
        Event eventObj = new Event(name,thumnail,date,latitude,longitude);
        eventObj.setObjectID(id);
        eventObj.setCardSubText1(cardText);
        eventObj.setEventWebUrl(link);
        eventObj.setFullViewText1(fullViewTxt);
        eventObj.setLocationName(location);
        eventDB.addEventToDb(eventObj);
        return eventObj;
    }



    // gets the url link to the image thumbnail
    private String getImageThumbnailUrl(JsonReader reader )throws IOException{
        String tmp = "no url";
        reader.beginObject();
        while(reader.hasNext()) {

            String token = reader.nextName();
            if (token.equals("src")) {
                tmp = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();

        return tmp;
    }


    public ArrayList<Event> getEvents(){
        return this.events;
    }



}
