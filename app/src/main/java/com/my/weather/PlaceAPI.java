package com.my.weather;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hardi on 2/22/2016.
 */
public class PlaceAPI {

    private static final String TAG = PlaceAPI.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyAZnknauwPF-Jm8MtGT23NRKCOfwI8DKUs";
    MatrixCursor dataCursor=null;

    public MatrixCursor autocompleteSearch (final String input) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataCursor=   autocomplete(input);
            }
        }).start();
        return dataCursor;
    }



    public MatrixCursor autocomplete (String input) {
        ArrayList<String> resultList = null;
        MatrixCursor menuCursor=null;
        String[] menuCols = new String[] { "_id","city" };
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            menuCursor = new MatrixCursor(menuCols);
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Log.i("search"+i,menuCursor.getCount()+" "+predsJsonArray.getJSONObject(i).getString("description"));
                menuCursor.addRow(new Object[]{i, predsJsonArray.getJSONObject(i).getString("description")});
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return menuCursor;
    }
}
