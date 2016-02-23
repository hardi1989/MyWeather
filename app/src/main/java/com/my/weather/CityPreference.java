package com.my.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class CityPreference {

    SharedPreferences prefs;

    public CityPreference(Context activity){
        prefs = activity.getSharedPreferences("CityPref",Activity.MODE_PRIVATE);
    }


    Double getCityLon(){

        return Double.parseDouble(prefs.getString("lon", "0"));
      //  return prefs.getString("lon", "");
    }

    Double getCityLat(){
        Log.i("lat",prefs.getString("lat", "0")+"  "+Double.parseDouble(prefs.getString("lat", "0")));
        return Double.parseDouble(prefs.getString("lat", "0"));
        //return prefs.getString("lat", "");
    }


    void setCity(Double lat,Double lon){
        prefs.edit().putString("lat", lat + "").commit();
        prefs.edit().putString("lon", lon + "").commit();
    }


    void setCityId(String cid){
        prefs.edit().putString("cityid","").commit();
    }

    String getCityId(){
        return prefs.getString("cityid", "");
    }

    void setMyweather(String json){
        prefs.edit().putString("myweather", json).commit();
    }

    String getMyweather(){
        return prefs.getString("myweather", "");
    }
}
