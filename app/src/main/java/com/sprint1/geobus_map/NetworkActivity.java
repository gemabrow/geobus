package com.sprint1.geobus_map;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

/**According to user's preferences, performs background async activity
 * of downloading and parsing xml file for updates to main map activity
 * Created by gerald on 10/13/15.
 */
public class NetworkActivity extends Activity{
    private static final String URL = "http://skynet.cse.ucsc.edu/bts/coord2.xml";;
    private static final String TAG = "NetworkActivity";

    public void load(){
        new DownloadXmlTask().execute(URL);
    }

    // Implementation of AsyncTask used to download XML feed
    private class DownloadXmlTask extends AsyncTask<String, Void, List<TransitInfoXmlParser.Marker>> {
        @Override
        protected List doInBackground(String... urls) {
            Log.i(TAG, String.valueOf(urls));
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
        protected void onPostExecute(List result) {
            MapsActivity.activity.setMarkers(result);
        }
    }

    private List<TransitInfoXmlParser.Marker> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        TransitInfoXmlParser TransitInfoParser = new TransitInfoXmlParser();
        // List of buses
        List<TransitInfoXmlParser.Marker> markers = null;

        try {
            stream = downloadUrl(urlString);
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
                Log.i(TAG, line);
            }
            Log.i(TAG, total.toString());
            markers = TransitInfoParser.parse(stream);
            if(markers == null)
                Log.e(TAG, "MAJOR MALFUNCTION");
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return markers;
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

}
