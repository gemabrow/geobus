package com.sprint1.geobus_map;

import android.os.AsyncTask;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.net.URL;

/**According to user's preferences, performs background async activity
 * of downloading and parsing xml file for updates to main map activity
 * Created by gerald on 10/13/15.
 */
public class NetworkActivity extends android.app.Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL = "http://skynet.cse.ucsc.edu/bts/coord2.xml";

    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    public static String sPref = null;

    // Uses AsyncTask to download the XML feed from skynet.cse.ucsc.edu.
    public void loadPage() {

        if((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) {
            new DownloadXmlTask().execute(URL);
        }
        else if ((sPref.equals(WIFI)) && (wifiConnected)) {
            new DownloadXmlTask().execute(URL);
        } else {
            // show error
        }
    }

    // Implementation of AsyncTask to download
    // XML feed from http://skynet.cse.ucsc.edu/bts/coord2.xml
    private class DownloadXmlTask extends AsyncTask<String, Void, List<TransitInfoXmlParser.Marker> > {
        List<TransitInfoXmlParser.Marker> markers;
        @Override
        protected List doInBackground(String... urls) {
            try {
                return markers = loadXmlFromNetwork(urls[0]);
            } catch ( IOException e ){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT);
            } catch ( XmlPullParserException e ) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.xml_error), Toast.LENGTH_SHORT);
            }
            return markers;
        }

        @Override
        //****need to draw to Main Google Maps activity on UI thread here*******
        protected void onPostExecute(List<TransitInfoXmlParser.Marker> results) {
        }
    }

    // Downloads XML from skynet, parses it, and returns it as a List
    //********************************** NEED TO GUT THIS ******************************************
    private List loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        TransitInfoXmlParser TransitInfoParser = new TransitInfoXmlParser();
        // List of buses
        List<TransitInfoXmlParser.Marker> markers = null;

        /*
        String title = null;
        String url = null;
        String summary = null;
        Calendar rightNow = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM dd h:mmaa");


        // Checks whether the user set the preference to include summary text
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>" + getResources().getString(R.string.page_title) + "</h3>");
        htmlString.append("<em>" + getResources().getString(R.string.updated) + " " +
                formatter.format(rightNow.getTime()) + "</em>");
        */

        try {
            stream = downloadUrl(urlString);
            markers = TransitInfoParser.parse(stream);
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
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

}
