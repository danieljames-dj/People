package com.danieljames.people;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.danieljames.people.edit.EditContact;
import com.danieljames.people.filter.FilterContact;
import com.danieljames.people.model.ContactList;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;

    private boolean hasPermissions() {
        for (String permission: permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void getPermissions() {
        if (!hasPermissions()) {
            for (String permission: permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            permissions,
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        } else {
            ContactList.contactList.gotPermission();
            ContactList.contactList.refresh(this);
        }
    }

    private void editContacts(String[] contactIds) {
        for (int i = 0; i < contactIds.length; i++) {
            Log.d("contactIds", contactIds[i]);
        }
        Intent intent = new Intent(this, EditContact.class);
        intent.putExtra("contactIds", contactIds);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContactList.contactList = new ContactList();
        final ListView listView = (ListView) findViewById(R.id.contacts_list);
        final ContactsListAdapter adapter = new ContactsListAdapter(this);
        ContactList.contactList.adapter = adapter;
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle(String.valueOf(listView.getCheckedItemCount()) + " Selected");
                adapter.items = listView.getCheckedItemPositions();
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.menu_contextual_actionbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        ArrayList<String> contactIds = new ArrayList<String>();
                        for (int i = 0; i < adapter.items.size(); i++) {
                            if (adapter.items.get(adapter.items.keyAt(i)) == true) {
                                contactIds.add(ContactList.contactList.getElement(adapter.items.keyAt(i)).id);
                            }
                        }
                        mode.finish();
                        editContacts(contactIds.toArray(new String[contactIds.size()]));
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), EditContact.class);
                intent.putExtra("contactIds", new String[]{ContactList.contactList.keys[position]});
                startActivity(intent);
            }
        });
        getPermissions();
    }

    private void openFilters() {
        Intent intent = new Intent(this, FilterContact.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_contacts:
                openFilters();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (hasPermissions()) {
            ContactList.contactList.gotPermission();
            ContactList.contactList.refresh(this);
        }
    }
}
