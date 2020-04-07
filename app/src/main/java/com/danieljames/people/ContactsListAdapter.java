package com.danieljames.people;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danieljames.people.model.ContactList;

public class ContactsListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Activity activity;
    public SparseBooleanArray items = new SparseBooleanArray();

    public ContactsListAdapter(Activity activity) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return ContactList.contactList.getCount();
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
            view = inflater.inflate(R.layout.contacts_list_row, null);
        }
        if (items.get(position) == true) {
            view.setBackgroundResource(R.color.selected);
        } else {
            TypedArray array = activity.getTheme().obtainStyledAttributes(new int[] {
                    android.R.attr.colorBackground,
                    android.R.attr.textColorPrimary,
            });
            int backgroundColor = array.getColor(0, 0xFF00FF);
            array.recycle();
            view.setBackgroundColor(backgroundColor);
        }
        TextView keyTextView = view.findViewById(R.id.contact_name);
        keyTextView.setText(ContactList.contactList.getElement(position).displayName);
        return view;
    }
}
