package com.my.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hardi on 2/21/2016.
 */
public class ListHourlyAdapter extends BaseAdapter {

    private Context context;
    static final String TAG_TIME="time";
    static final String TAG_TEMP="temp";
    static final String TAG_ICON="icon";

    private ArrayList<HashMap<String,String>> listData;

    public ListHourlyAdapter(Context context,ArrayList<HashMap<String,String>> listData) {
        this.context = context;
        this.listData=listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_vertical, null);
            holder.iv_weather = (ImageView) convertView.findViewById(R.id.iv_weather_icon);
            holder.tv_day = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_maxtemperature = (TextView) convertView.findViewById(R.id.tv_temperature);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(listData.get(position)!=null) {
            holder.tv_day.setText(listData.get(position).get(TAG_TIME));
            holder.tv_maxtemperature.setText(listData.get(position).get(TAG_TEMP));

           Glide.with(context).load("http://openweathermap.org/img/w/" + listData.get(position).get(TAG_ICON) + ".png").into(holder.iv_weather);
        }

        return convertView;
    }

    class ViewHolder{
        TextView tv_day;
        ImageView iv_weather;
        TextView tv_maxtemperature;
    }
}