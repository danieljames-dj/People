package com.danieljames.people.edit;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.danieljames.people.R;
import com.danieljames.people.model.Contact;
import com.danieljames.people.model.ContactList;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditContact extends AppCompatActivity {

    String[] contactIds;
    EditText[] places = new EditText[6];//, homeRegion, homeCountry, workCity, workRegion, workCountry;
    int[] placeIds = {R.id.homeCity, R.id.homeRegion, R.id.homeCountry, R.id.workCity, R.id.workRegion, R.id.workCountry};
    int[] placeContainerIds = {R.id.homeCityContainer, R.id.homeRegionContainer, R.id.homeCountryContainer, R.id.workCityContainer, R.id.workRegionContainer, R.id.workCountryContainer};

    void saveContacts() {
        Contact[] contacts = new Contact[contactIds.length];
        for (int i = 0; i < contacts.length; i++) {
            contacts[i] = new Contact(ContactList.contactList.contacts.get(contactIds[i]));
            contacts[i].homeCity = places[0].getText().toString();
            contacts[i].homeRegion = places[1].getText().toString();
            contacts[i].homeCountry = places[2].getText().toString();
            contacts[i].workCity = places[3].getText().toString();
            contacts[i].workRegion = places[4].getText().toString();
            contacts[i].workCountry = places[5].getText().toString();
            if (contacts[i].homeCity.length() == 0) {
                contacts[i].homeCity = null;
            }
            if (contacts[i].homeRegion.length() == 0) {
                contacts[i].homeRegion = null;
            }
            if (contacts[i].homeCountry.length() == 0) {
                contacts[i].homeCountry = null;
            }
            if (contacts[i].workCity.length() == 0) {
                contacts[i].workCity = null;
            }
            if (contacts[i].workRegion.length() == 0) {
                contacts[i].workRegion = null;
            }
            if (contacts[i].workCountry.length() == 0) {
                contacts[i].workCountry = null;
            }
        }
        ContactList.contactList.saveContact(this, contacts);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        contactIds = getIntent().getStringArrayExtra("contactIds");
        if (contactIds.length == 1) {
            TextView textView = (TextView) findViewById(R.id.edit_name);
            textView.setVisibility(View.VISIBLE);
            textView.setText(ContactList.contactList.contacts.get(contactIds[0]).displayName);
        }
        Contact contact = ContactList.contactList.contacts.get(contactIds[0]);
        String[] contactPlaces = {contact.homeCity, contact.homeRegion, contact.homeCountry, contact.workCity, contact.workRegion, contact.workCountry};
        for (int i = 0; i < 6; i++) {
            places[i] = (EditText) findViewById(placeIds[i]);
            findViewById(placeContainerIds[i]).setVisibility(View.VISIBLE);
            if (contactPlaces[i] != null) {
                places[i].setText(contactPlaces[i]);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_save:
                saveContacts();
        }
        return super.onOptionsItemSelected(item);
    }
}
