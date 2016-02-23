package com.my.weather;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kunal on 2/22/2016.
 */
public class PlacesCursorAdapter extends CursorAdapter {

    public PlacesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.autocomplete_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvBody = (TextView) view.findViewById(R.id.autocomplete_text);
        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow("city"));
        Log.i("Body",body);
        tvBody.setText(body);
    }
}
