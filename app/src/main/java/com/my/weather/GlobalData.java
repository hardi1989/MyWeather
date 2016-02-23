package com.my.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by hardi on 2/21/2016.
 */
public class GlobalData {

    static final Double default_lat=40.7127;
    static final Double default_lon=-74.0059;
    static final String OPEN_WEATHER_MAP_API_FIND =
            "http://api.openweathermap.org/data/2.5/find?lat=%s&lon=%s&cnt=30&units=imperial";
    static final String OPEN_WEATHER_MAP_API2 =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial&APPID=2ff507a28637578f916bfbfb296ecec4";

    static String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=imperial&APPID=2ff507a28637578f916bfbfb296ecec4";

    static String OPEN_WEATHER_FORECAST_API =
            "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=imperial&APPID=2ff507a28637578f916bfbfb296ecec4";

    static String OPEN_WEATHER_HOURLY_API =
            "http://api.openweathermap.org/data/2.5/forecast/weather?lat=%s&lon=%s&units=imperial&APPID=2ff507a28637578f916bfbfb296ecec4";

    public static String weathercondition(String code){
        String imageBack="background";
        if(code.startsWith("2")){
            //Thunderstorm
            imageBack="storm";
        }
        else if(code.startsWith("3")){
            //Drizzle
            imageBack="rainy";
        }
        else if(code.startsWith("5")){
            //Rain
            imageBack="rainy";
        }
        else if(code.startsWith("6")){
            //Snow
            imageBack="snow";
        }
        else if(code.startsWith("7")){
            //Atmosphere
            imageBack="foggy";
        }
        else if(code.equalsIgnoreCase("800")){
            //Clear
            imageBack="clearnight";
        }
        else if(code.startsWith("80")){
            //Clouds
            imageBack="cloudy";
        }
        else if(code.startsWith("90")){
            //Extreme
            imageBack="background";
        }
        else if(code.startsWith("9")){
            //Additional
            imageBack="background";
        }
        return  imageBack;
    }

    public static void closingAlert(final Context context, String title,
                                     String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        // Setting OK Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone",
                        "com.android.phone.NetworkSetting");
                intent.setComponent(cName);
                Activity act=(Activity)context;
                act.finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void simpleAlert(final Context context, String title,
                                    String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        // Setting OK Button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone",
                        "com.android.phone.NetworkSetting");
                intent.setComponent(cName);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
