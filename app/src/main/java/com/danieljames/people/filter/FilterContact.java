package com.danieljames.people.filter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.danieljames.people.R;
import com.danieljames.people.model.ContactList;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FilterContact extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FilterContactAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_contact);
        Spinner spinner = (Spinner) findViewById(R.id.filter_group);
        spinner.setOnItemSelectedListener(this);
        List<String> filters = new ArrayList<String>();
        filters.add("Places");
        filters.add("Labels");
        adapter = new FilterContactAdapter(this);
        final ListView listView = (ListView) findViewById(R.id.filter_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setFilter(position);
            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void setFilter(int position) {
        switch (adapter.filter) {
            case 0:
                ContactList.contactList.placeFilter = position - 1;
                ContactList.contactList.refreshFilter();
                break;
            case 1:
                ContactList.contactList.groupFilter = position - 1;
                ContactList.contactList.refreshFilter();
                break;
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adapter.filter = position;
        switch (position) {
            case 0:
                adapter.filters = ContactList.contactList.places;
                adapter.notifyDataSetChanged();
                break;
            case 1:
                String[] filters = new String[ContactList.contactList.groupsUsed.length];
                for (int i = 0; i < filters.length; i++) {
                    filters[i] = ContactList.contactList.groupNames.get(ContactList.contactList.groupsUsed[i]);
                }
                adapter.filters = filters;
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //
    }
}
