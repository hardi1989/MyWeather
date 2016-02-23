package com.my.weather;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hardi on 2/21/2016.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Double lat=0.0,lon=0.0;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maplayout);
        cd = new ConnectionDetector(getApplicationContext());
        if(!cd.isConnectingToInternet()){
            GlobalData.simpleAlert(MapActivity.this,"Alert",getResources().getString(R.string.internet_message));
        }
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        final CityPreference ctp=new CityPreference(this);
         lat=ctp.getCityLat();
         lon=ctp.getCityLon();
         CameraPosition BONDI =
                new CameraPosition.Builder().target(new LatLng(lat, lon))
                        .zoom(10.5f)
                        .bearing(300)
                        .tilt(50)
                        .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(BONDI));

        new mapForecastAsync(map).execute(GlobalData.OPEN_WEATHER_MAP_API_FIND,lat+"",lon+"");

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            LatLng lt=marker.getPosition();
                ctp.setCity(lt.latitude,lt.longitude);
                startActivity(new Intent(MapActivity.this,MainActivity.class));
                finish();
            }
        });
    }


    private class mapForecastAsync extends AsyncTask<String, Void, JSONObject> {

        GoogleMap mMap;
        mapForecastAsync(GoogleMap map){
            mMap=map;
        }
        @Override
        protected JSONObject doInBackground(String... params) {
            String apiurl,lat,lon;

            apiurl=params[0];
            lat=params[1];
            lon=params[2];
            JSONObject json = RemoteFetch.getJSON(MapActivity.this,apiurl ,lat,lon);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            if(json != null){
                try {
                    JSONArray jArray = json.getJSONArray("list");


                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jobj=jArray.getJSONObject(i);
                        String temp=jobj.getJSONObject("main").getString("temp")+"F";
                        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(jobj.getJSONObject("coord").getDouble("lat"), jobj.getJSONObject("coord").getDouble("lon")))
                        .title(jobj.getString("name"))
                        .snippet(temp)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }

                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MapActivity.this,MainActivity.class));
        finish();
    }
}
