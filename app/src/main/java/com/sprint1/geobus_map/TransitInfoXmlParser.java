package com.sprint1.geobus_map;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser to be used to pull information from XML
 * hosted @ http://skynet.cse.ucsc.edu/bts/coord2.xml
 * and send to 'readFeed(foo)' for processing
 * Created by gerald on 10/14/15, heavily derivative of:
 * http://developer.android.com/training/basics/network-ops/xml.html#consume
 */

public class TransitInfoXmlParser {
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed( parser );
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List markers = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "markers");
        while (parser.next() != XmlPullParser.END_TAG) {
            //if the tag doesn't match, skip to the next line until first valid entry
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag, "marker", for each bus
            if (name.equals("marker")) {
                markers.add(readMarker(parser));
            } else {
                skip(parser);
            }
        }
        return markers;


    }

    // custom marker class to replicate datafields in skynet's XML
    // COULD PROVE PROBLEMATIC WITH INVOLVING METRO LATER ON
    public static class Marker {
        public final double lat;
        public final double lng;
        public final int timestamp;
        public final String route;
        public final int bus_id;
        // need to do something for predictions

        private Marker(double latitude, double longitude, int ts, String rt, int bus_id ) {
            this.lat = latitude;
            this.lng = longitude;
            this.timestamp = ts;
            this.route = rt;
            this.bus_id = bus_id;
        }
    }

    // parses individual marker, initializing each (lat, lng, ts, rt, id)
    private Marker readMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "marker");
        double lat = 0;
        double lng = 0;
        int timestamp = 0;
        String route = null;
        int bus_id = 0;
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
            } else {
                skip(parser);
            }
        }
        return new Marker(lat, lng, timestamp, route, bus_id);
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
                case XmlPullParser.START_TAG;
                    depth++;
                    break;
            }
        }
    }
}

