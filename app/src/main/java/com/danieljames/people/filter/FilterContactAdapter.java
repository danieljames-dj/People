package com.danieljames.people.filter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danieljames.people.R;
import com.danieljames.people.model.ContactList;
import com.danieljames.people.model.Filter;


public class FilterContactAdapter extends BaseAdapter {

    LayoutInflater inflater;
    private Filter[] filters;

    public FilterContactAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.filters = ContactList.contactInterface.getFilters();
        for (Filter filter: this.filters) {
            filter.resetTempSelected();
        }
    }

    @Override
    public int getCount() {
        return this.filters.length;
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
        TextView filterTitle = view.findViewById(R.id.filter_title), filterCount = view.findViewById(R.id.filter_count);
        filterTitle.setText(this.filters[position].title);
        int count = this.filters[position].tempSelectedCount;
        if (count == 0) {
            filterCount.setText("Not selected");
        } else {
            filterCount.setText(count + " selected");
        }
        return view;
    }
}
