package com.danieljames.people.model;

import java.util.ArrayList;

public class Contact {

    public String id, rawContactId, displayName, homeCountry, homeRegion, homeCity, workCountry, workRegion, workCity;
    public ArrayList<Integer> placeId = new ArrayList<>(), labelId = new ArrayList<>();

    public Contact() {}

    public Contact(Contact contact) {
        this.id = contact.id;
        this.rawContactId = contact.rawContactId;
        this.displayName = contact.displayName;
        this.homeCountry = contact.homeCountry;
        this.homeRegion = contact.homeRegion;
        this.homeCity = contact.homeCity;
        this.workCountry = contact.workCountry;
        this.workRegion = contact.workRegion;
        this.workCity = contact.workCity;
        for (Integer placeId: contact.placeId) {
            this.placeId.add(placeId);
        }
        for (Integer labelId: contact.labelId) {
            this.labelId.add(labelId);
        }
    }
}
