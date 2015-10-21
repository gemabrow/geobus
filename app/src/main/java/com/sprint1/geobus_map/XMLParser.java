package com.sprint1.geobus_map;


import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Jose on 10/21/2015.
 */
public class XMLParser {

    // We don't use namespaces
    private static final String ns = null;
    private ArrayList<Bus> busses;

    XMLParser(){

    }
    public void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
    }


    // process the feed. It looks for the element tagged "markers" as the starting point for recursively
    // processing the feed.  If the tag isn't the markers feed it skips it
    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        busses = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "coord2");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            // if the next tag contains markers then read in the entry
            if(parser.getName().equals("markers")){

                parser.require(XmlPullParser.START_TAG, ns, "markers");
                // Starts by looking for the markers tag, anything else is skipped
                while (parser.next() != XmlPullParser.END_TAG) {

                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();

                    if (name.equals("marker")) {
                        busses.add(readBus(parser));
                    } else {
                        skip(parser);
                    }
                }
            }
            else{
                skip(parser);
            }

        }

        printBusList();
      // return busses;
    }


    // From the xml file, the parser finds the entries with the following names: lat for latitude,
    // lng for longitude, timestamp, route, and bus id. It skips any other entry
    // such as predictions and index
    private Bus readBus(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "marker");
        int bus_id = 0;
        double lat = 00;
        double lng = 00;
        int timestamp = 0;
        String route = null;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("lat")) {
                lat = readLat(parser);
            } else if (name.equals("lng")) {
                lng = readLng(parser);
            } else if (name.equals("timestamp")) {
                timestamp = readTs(parser);
            } else if (name.equals("route")) {
                route = readRoute(parser);
            } else if (name.equals("id")) {
                bus_id = readId(parser);
            }
            else {
                skip(parser);
            }
        }

        // creates a and returns a new Bus Object
        return new Bus(lat,lng,timestamp,route,bus_id);
    }

    // processes longitudinal coordinates
    private double readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        double lng = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lng;
    }

    // processes longitudinal coordinates
    private double readLng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lng");
        double lng = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lng");
        return lng;
    }

    // processes timestamp
    private int readTs(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "timestamp");
        int ts = readInt(parser);
        parser.require(XmlPullParser.END_TAG, ns, "timestamp");
        return ts;
    }

    // processes name of bus route
    private String readRoute(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "route");
        String route = readString(parser);
        parser.require(XmlPullParser.END_TAG, ns, "route");
        return route;
    }

    // processes bus id
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        int bus_id = readInt(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return bus_id;
    }

    // For latitude and longitude, extract their double values
    private double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
        double result = 0;
        if (parser.next() == XmlPullParser.TEXT) {
            result = Double.parseDouble( parser.getText() );
            parser.nextTag();
        }
        return result;
    }

    // For timestamp and bus id, extract their integer values
    private int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
        int result = 0;
        if (parser.next() == XmlPullParser.TEXT) {
            result = Integer.parseInt(parser.getText());
            parser.nextTag();
        }
        return result;
    }

    // For bus route name, extract its String (potential to be used for other values later)
    private String readString(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }



    // Consumes any skipped entries
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    // prints a list of bus info from the xml file
    public void  printBusList(){
        System.out.println(" printing bus list");
            for (Bus temp: busses){
                temp.printBus();


            }
    }
}