package com.danieljames.people.filter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.danieljames.people.R;
import com.danieljames.people.model.ContactList;
import com.danieljames.people.model.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FilterContact extends AppCompatActivity {

    FilterContactAdapter adapter;
    private Filter[] filters;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_contact);
        setTitle(R.string.sort_filter_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new FilterContactAdapter(this);
        this.filters = ContactList.contactInterface.getFilters();

        ListView listView = findViewById(R.id.filter_list);
        listView.setAdapter(adapter);
        listView.addFooterView(new View(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setFilter(position);
            }
        });

        Button cancelButton = findViewById(R.id.cancel_filter);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button applyButton = findViewById(R.id.apply_filter);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter();
            }
        });

        Button resetButton = findViewById(R.id.reset_filter);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilter();
            }
        });
    }

    private void setFilter(int position) {
        final Filter filter = this.filters[position];
        final FilterContactAdapter adapter = this.adapter;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(filter.title);
        String[] options = filter.options.toArray(new String[filter.options.size()]);
        boolean[] selected = new boolean[filter.selected.size()];
        for (int i = 0; i < filter.selected.size(); i++) {
            selected[i] = filter.selected.get(i);
        }
        builder.setMultiChoiceItems(options, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                filter.checked(which, isChecked);
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filter.recalculateCount();
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        for (Filter filter: this.filters) {
            filter.resetTempSelected();
        }
        super.onBackPressed();
    }

    void applyFilter() {
        for (Filter filter: this.filters) {
            filter.applyFilter();
        }
        finish();
    }

    void resetFilter() {
        ContactList.contactInterface.resetFilter();
        finish();
    }

    @Override
    public void finish() {
        ContactList.contactList.refreshFilter();
        super.finish();
    }
}
