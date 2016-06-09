//package com.bussquad.geobus.parser;
//
//import android.app.ProgressDialog;
//import android.app.SearchManager;
//import android.content.DialogInterface;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//
//import org.apache.http.;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.util.Log;
//
///**
// * Created by Jose on 4/8/2016.
// */
//public class JsonBusParser{
//    final String TAG = "JsonParser.java";
//
//    static InputStream is = null;
//    static JSONObject jObj = null;
//    static String json = "";
//
//    public JSONObject getJSONFromUrl(String url) {
//
//        // make HTTP request
//        try {
//
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            HttpPost httpPost = new HttpPost(url);
//
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//            is = httpEntity.getContent();
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            json = sb.toString();
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error converting result " + e.toString());
//        }
//
//        // try parse the string to a JSON object
//        try {
//            jObj = new JSONObject(json);
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing data " + e.toString());
//        }
//
//        // return JSON String
//        return jObj;
//}
