package com.my.weather;

import android.database.Cursor;
import android.util.Log;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by hardi on 2/20/2016.
 */
public class MainActivity extends AppCompatActivity {

    Toolbar mToolbar;
    ImageView iv_toolbarmap;
    SearchView searchView;

    static TextView tv_toolbartitle;
    static FrameLayout container;

    CursorAdapter plcursor;

    ConnectionDetector cd;
    CityPreference ctp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cd = new ConnectionDetector(getApplicationContext());
        ctp=new CityPreference(MainActivity.this);
        if(!cd.isConnectingToInternet()) {
            //If internet connection is not available load last displayed data
            if (!ctp.getMyweather().equalsIgnoreCase("")) {
                GlobalData.simpleAlert(MainActivity.this, "Alert", getResources().getString(R.string.offline_message));
            } else {
                GlobalData.closingAlert(MainActivity.this, "Alert", getResources().getString(R.string.internet_message));
            }
        }
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("");
        container=(FrameLayout)findViewById(R.id.container);
        tv_toolbartitle=(TextView)mToolbar.findViewById(R.id.toolbar_title);
        iv_toolbarmap=(ImageView)mToolbar.findViewById(R.id.toolbar_map);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }

        iv_toolbarmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it_map=new Intent(MainActivity.this,MapActivity.class);
                startActivity(it_map);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        // Get the SearchView and set the searchableview configuration
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint((Html.fromHtml("<font color = #ffffff> Search here </font>")));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
        return false;
    }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor searchCursor = plcursor.getCursor();
                    if(searchCursor.moveToPosition(position)) {
                        String selectedItem = searchCursor.getString(1);
                        searchView.setQuery(selectedItem+"", true);
                    }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                //Clear query
                searchView.setQuery("", false);
                //Collapse the action view
                searchItem.collapseActionView();

                try {
                    query = URLEncoder.encode(query, "utf-8");
                }catch (Exception e){
                    Log.i("Exception",e.toString());
                }
                cd=new ConnectionDetector(MainActivity.this);
                if(cd.isConnectingToInternet())
                    new myLatLon().execute(GlobalData.OPEN_WEATHER_MAP_API2,query.trim());
                else
                    GlobalData.simpleAlert(MainActivity.this,"Alert",getResources().getString(R.string.internet_message));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                new getCities().execute(searchView.getQuery().toString());
                return false;
            }

        });

        return true;
    }


    private class getCities extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            PlaceAPI mPlacesapi=new PlaceAPI();
            final Cursor cursor=mPlacesapi.autocomplete(params[0]);
            plcursor=new PlacesCursorAdapter(MainActivity.this,cursor,1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchView.setSuggestionsAdapter(plcursor);
            plcursor.notifyDataSetChanged();
        }
    }

    private class myLatLon extends AsyncTask<String,Void,JSONObject>{
        @Override
        protected JSONObject doInBackground(String... params) {
            String apiurl,q;

            apiurl=params[0];
            q=params[1];

            JSONObject json = RemoteFetch.getJSONFromQ(MainActivity.this, apiurl, q);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            super.onPostExecute(json);
            if(json!=null){
            try {
                JSONObject jsoncoord = json.getJSONObject("coord");
                CityPreference ctp=new CityPreference(MainActivity.this);
                ctp.setCity(jsoncoord.getDouble("lat"),jsoncoord.getDouble("lon"));
                getSupportFragmentManager().beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(R.id.container))
                        .add(R.id.container, new WeatherFragment())
                        .commit();
            }catch (Exception e){
                Log.i("Exception",e.toString());
            }
            }else{
                Toast.makeText(MainActivity.this,"Unknown Place",Toast.LENGTH_LONG).show();
            }
        }
    }

}


