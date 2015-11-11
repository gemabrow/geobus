package com.bussquad.geobus;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**According to user's preferences, performs background async activity
 * of downloading and parsing xml file for updates to main map activity
 * Created by gerald on 10/13/15.
 */
class NetworkActivity {
    private static final String URL = "http://skynet.cse.ucsc.edu/bts/coord2.xml";
    private static final String TAG = "NetworkActivity";

    public void load(){
        new DownloadXmlTask().execute(URL);
    }

    private ArrayList<Bus> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        TransitInfoXmlParser TransitInfoParser = new TransitInfoXmlParser();
        // List of buses
        ArrayList<Bus> buses = null;

        try {
            stream = downloadUrl(urlString);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                buses = TransitInfoParser.parse(stream);
                stream.close();
            }
        }

        return buses;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(100000 /* milliseconds */);
        conn.setConnectTimeout(150000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();

    }

    // Implementation of AsyncTask used to download XML feed
    private class DownloadXmlTask extends AsyncTask<String, Void, ArrayList<Bus>> {
        @Override
        protected ArrayList<Bus> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "CONNECTION SNAFU");
            } catch (XmlPullParserException e) {
                Log.e(TAG, "XML SNAFU");
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Bus> result) {
            MapsActivity.activity.setMarkers(result);
        }
    }

}
