//package com.bussquad.geobus.utilities;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Jose on 4/8/2016.
// */
//public class AsyncTaskParseJson extends AsyncTask<String,String,String> {
//
//
//    String url = "http://bts.ucsc.edu:8081/location/get";
//    JSONArray dataJsonArr = null;
//
//    @Override
//    protected void onPreExecute(){}
//
//    @Override
//    protected String doInBackground(String... params) {
//        return null;
//    }
//
//    try {
//
//        // instantiate our json parser
//        JsonParser jParser = new JsonParser();
//
//        // get json string from url
//        JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);
//
//        // get the array of users
//        dataJsonArr = json.getJSONArray("Users");
//
//        // loop through all users
//        for (int i = 0; i < dataJsonArr.length(); i++) {
//
//            JSONObject c = dataJsonArr.getJSONObject(i);
//
//            // Storing each json item in variable
//            String firstname = c.getString("firstname");
//            String lastname = c.getString("lastname");
//            String username = c.getString("username");
//
//            // show the values in our logcat
//            Log.e(TAG, "firstname: " + firstname
//                    + ", lastname: " + lastname
//                    + ", username: " + username);
//
//        }
//
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
//    return null;
//
//}
