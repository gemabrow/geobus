package com.bussquad.geobus;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Parser to be used to pull information from XML
 * hosted @ http://skynet.cse.ucsc.edu/bts/coord2.xml
 * and send to 'readFeed(foo)' for processing
 * Created by gerald on 10/14/15, heavily derivative of:
 * http://developer.android.com/training/basics/network-ops/xml.html#consume
 */

class TransitInfoXmlParser {
    private static final String ns = null;
    private static final String TAG = "Parser";
    private ArrayList<Bus> buses;

    public ArrayList<Bus> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.next();
            readFeed(parser);
        } finally {
            in.close();
        }
        return buses;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        buses = new ArrayList<Bus>();
        Queue<String> tags = new LinkedList<String>();
        Stack<String> checkedTags = new Stack<String>();

        tags.add("coord2");
        tags.add("markers");
        tags.add("marker");
        xmlTagChecker(parser, tags, checkedTags);
    }

    private void xmlTagChecker(XmlPullParser parser, Queue<String> tagsToCheck, Stack<String> checkedTags) throws XmlPullParserException, IOException {
        String currentTag;
        checkedTags.add(tagsToCheck.poll());
        parser.require(XmlPullParser.START_TAG, ns, checkedTags.peek());

        while(parser.next() != XmlPullParser.END_TAG) {
            //if the tag doesn't match, skip to the next line until first valid entry
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            currentTag = tagsToCheck.peek();
            if(name.equals(currentTag) && tagsToCheck.size() > 1) {
                xmlTagChecker(parser, tagsToCheck, checkedTags);
            }
            else if(name.equals(currentTag) && tagsToCheck.size() == 1) {
                buses.add(readBus(parser));
                Log.i(TAG, "Bus added, total buses: " + Integer.toString(buses.size()));
            }
            else{
                skip(parser);
            }
        }
    }

    // parses individual marker, initializing each (lat, lng, ts, rt, id)
    private Bus readBus(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "marker");
        double lat = 0.0;
        double lng = 0.0;
        int bus_id = 0;
        int timestamp = 0;
        String route = null;
        String direction = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "lat":
                    lat = readLat(parser);
                    break;
                case "lng":
                    lng = readLng(parser);
                    break;
                case "timestamp":
                    timestamp = readTs(parser);
                    break;
                case "route":
                    route = readRoute(parser);
                    break;
                case "id":
                    bus_id = readId(parser);
                    break;
                case "direction":
                    direction = readDirection(parser);
                    break;
                default:
                    skip(parser);
            }
        }
        return new Bus(lat, lng, timestamp, route, direction, bus_id);
    }

    // processes latitudinal coordinates
    private double readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        double lat = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lat;
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

    // processes name of bus route
    private String readDirection(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "direction");
        String direction = readString(parser);
        parser.require(XmlPullParser.END_TAG, ns, "direction");
        return direction;
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
            result = result.replaceAll(",+",",");
            parser.nextTag();
        }
        return result;
    }

    // Allows for the parser to skip irrelevant tags
    private void skip(XmlPullParser parser) throws IOException, XmlPullParserException {
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
}

