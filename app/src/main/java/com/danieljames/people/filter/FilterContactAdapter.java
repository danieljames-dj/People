package com.danieljames.people.filter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danieljames.people.R;

public class FilterContactAdapter extends BaseAdapter {

    public String[] filters = new String[0];
    public int filter = 0;
    LayoutInflater inflater;

    public FilterContactAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filters.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.filter_contact_row, null);
        }
        TextView keyTextView = (TextView) view.findViewById(R.id.filter_contact_row);
        if (position == 0) {
            keyTextView.setText("none");
        } else {
            keyTextView.setText(filters[position - 1]);
        }
        return view;
    }
}
