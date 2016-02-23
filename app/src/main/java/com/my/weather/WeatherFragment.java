package com.my.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    TextView detailsField;
    TextView currentTemperatureField;

    ConnectionDetector cd;
    CityPreference ctp;

    ListView lv_forecast;
    HorizontalListView list_dailytemp;

    ImageView loading1,loading2,loading3;
    Double lat,lon;

    Animation rotate;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctp = new CityPreference(getActivity());

        cd = new ConnectionDetector(getActivity());
        if (!cd.isConnectingToInternet()) {
            //If internet connection is not avalable load last displayed data
            if (!ctp.getMyweather().equalsIgnoreCase("")) {
                try {
                    loading1.setVisibility(View.INVISIBLE);
                    loading2.setVisibility(View.INVISIBLE);
                    loading3.setVisibility(View.INVISIBLE);
                    list_dailytemp.setVisibility(View.INVISIBLE);
                    renderWeather(new JSONObject(ctp.getMyweather()));
                } catch (Exception e)
                {}
            }
        } else {
            rotate = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.rotation);
            loading1.startAnimation(rotate);
            loading2.startAnimation(rotate);
            loading3.startAnimation(rotate);

            lat = ctp.getCityLat();
            lon = ctp.getCityLon();
            new myweatherAsync().execute(GlobalData.OPEN_WEATHER_MAP_API, lat+"", lon+"");
            new myForecastAsync().execute(GlobalData.OPEN_WEATHER_FORECAST_API, lat+"", lon+"");
            new myForecastHourlyAsync().execute(GlobalData.OPEN_WEATHER_HOURLY_API, lat+"", lon+"");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    private class myweatherAsync extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String apiurl,lat,lon;
            apiurl=params[0];
            lat=params[1];
            lon=params[2];
            JSONObject json = RemoteFetch.getJSON(getActivity(),apiurl ,lat,lon);
            CityPreference ctp=new CityPreference(getActivity());
            ctp.setMyweather(json.toString());
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            loading1.clearAnimation();
            loading1.setVisibility(View.INVISIBLE);
            if(jsonObject == null){
                Toast.makeText(getActivity(),"Unknown Place 1",
                        Toast.LENGTH_LONG).show();
            }else{
                    renderWeather(jsonObject);
            }
        }
    }


    private class myForecastAsync extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... params) {
            String apiurl,lat,lon;
            ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String, String>>();

            apiurl=params[0];
            lat=params[1];
            lon=params[2];
            JSONObject json = RemoteFetch.getJSON(getActivity(),apiurl ,lat,lon);
            try {
                JSONArray jArray = json.getJSONArray("list");

                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jobj=jArray.getJSONObject(i);
                    JSONObject jmain=jobj.getJSONObject("temp");
                    JSONObject jweather=jobj.getJSONArray("weather").getJSONObject(0);
                    DateFormat df = DateFormat.getDateInstance();
                    String updatedOn = df.format(new Date(jobj.getLong("dt") * 1000));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(ListForecastAdapter.TAG_DAY, updatedOn);
                    map.put(ListForecastAdapter.TAG_MINTEMP,  jmain.getDouble("min") + " F");
                    map.put(ListForecastAdapter.TAG_MAXTEMP,  jmain.getDouble("max") + " F");
                    map.put(ListForecastAdapter.TAG_ICON, jweather.getString("icon"));

                    data.add(map);
                }

            }catch (Exception e){
                Log.i("Exception",e.toString());
                return null;
            }

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
            super.onPostExecute(result);
            if(result == null){
                Toast.makeText(getActivity(),"Unknown Place 2",
                        Toast.LENGTH_LONG).show();
            }else{
                ListForecastAdapter adapter = new ListForecastAdapter(getActivity(),result);
                lv_forecast.setAdapter(adapter);
            }
            loading3.clearAnimation();
            loading3.setVisibility(View.INVISIBLE);
        }
    }



    private class myForecastHourlyAsync extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... params) {
            String apiurl,lat,lon;
            ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String, String>>();

            apiurl=params[0];
            lat=params[1];
            lon=params[2];
            JSONObject json = RemoteFetch.getJSON(getActivity(),apiurl ,lat,lon);

            try {
                JSONArray jArray = json.getJSONArray("list");

                for (int i = 0; i < 8; i++) {
                    JSONObject jobj=jArray.getJSONObject(i);
                    JSONObject jmain=jobj.getJSONObject("main");
                    JSONObject jweather=jobj.getJSONArray("weather").getJSONObject(0);
                    DateFormat df = DateFormat.getTimeInstance();
                    String updatedOn = df.format(new Date(jobj.getLong("dt") * 1000));

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(ListHourlyAdapter.TAG_TIME, updatedOn);
                    map.put(ListHourlyAdapter.TAG_TEMP,  jmain.getDouble("temp") + " F");
                    map.put(ListHourlyAdapter.TAG_ICON, jweather.getString("icon"));

                    data.add(map);
                }

            }catch (Exception e){
                Log.i("Exception", e.toString());
                return null;
            }

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
            super.onPostExecute(result);
            if(result == null){
                Toast.makeText(getActivity(),"Unknown Place 3",
                        Toast.LENGTH_LONG).show();
            }else{
                ListHourlyAdapter adapter = new ListHourlyAdapter(getActivity(),result);
                list_dailytemp.setAdapter(adapter);
            }
            loading2.clearAnimation();
            loading2.setVisibility(View.INVISIBLE);

        }
    }



    private void renderWeather(JSONObject json){
        try {

            MainActivity.tv_toolbartitle.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country") );
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            String icon=details.getString("icon");
            icon= GlobalData.weathercondition(icon);
            int id = getActivity().getResources().getIdentifier(icon, "drawable", getActivity().getPackageName());
            MainActivity.container.setBackgroundResource(id);

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " F");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa"+
                    "\n"   +"Last update: " + updatedOn);


        }catch(Exception e){
            Log.e("SimpleWeather", e.toString());
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        loading1=(ImageView)rootView.findViewById(R.id.current_temperature_field_loading);
        loading2=(ImageView)rootView.findViewById(R.id.list_dailytemp_loading);
        loading3=(ImageView)rootView.findViewById(R.id.listview_forcast_loading);

        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        lv_forecast=(ListView)rootView.findViewById(R.id.listview_forcast);
        list_dailytemp=(HorizontalListView)rootView.findViewById(R.id.list_dailytemp);


        return rootView;
    }

}
