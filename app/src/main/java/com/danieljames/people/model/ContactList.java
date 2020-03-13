package com.danieljames.people.model;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.danieljames.people.ContactsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ContactList {

    public static ContactList contactList;
    public ContactsListAdapter adapter;
    Boolean hasPermission = false;

    public HashMap<String, Contact> contacts = new HashMap<String, Contact>();
    public String[] originalKeys = new String[0];
    public String[] keys = new String[0];

    public String[] places = new String[0];
    public int placeFilter = -1;

    public String[] groupsUsed = new String[0];
    public HashMap<String, String> groupNames = new HashMap<String, String>();
    public int groupFilter = -1;

    public ContactList() {
    }

    void updateView(Activity activity) {
        Handler mainHandler = new Handler(activity.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refreshFilter();
                adapter.notifyDataSetChanged();
            }
        };
        mainHandler.post(runnable);
    }

    String getPlace(String country, String region, String city) {
        String place = "";
        if (city != null) {
            place += city;
        }
        if (region != null) {
            if (place.length() != 0) {
                place += ", ";
            }
            place += region;
        }
        if (country != null) {
            if (place.length() != 0) {
                place += ", ";
            }
            place += country;
        }
        return place;
    }

    public void refreshFilter() {
        ArrayList<String> keysList = new ArrayList<String>();
        for (int i = 0; i < originalKeys.length; i++) {
            Contact contact = contactList.contacts.get(originalKeys[i]);
            if (placeFilter != -1) {
                if (!Objects.equals(places[placeFilter], getPlace(contact.homeCountry, contact.homeRegion, contact.homeCity)) &&
                        !Objects.equals(places[placeFilter], getPlace(contact.workCountry, contact.workRegion, contact.workCity))) {
                    continue;
                }
            }
            if (groupFilter != -1) {
                if (!contact.labels.contains(groupsUsed[groupFilter])) {
                    continue;
                }
            }
            keysList.add(contact.id);
        }
        keys = keysList.toArray(new String[keysList.size()]);
        adapter.notifyDataSetChanged();
    }

    void fetchList(final Activity activity) {
        final ContentResolver contentResolver = activity.getContentResolver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int keyIndex = 0;
                contacts.clear();
                Cursor cursor;
                cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
                originalKeys = new String[cursor.getCount()];
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Contact contact = new Contact();
                        contact.id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        originalKeys[keyIndex++] = contact.id;
                        contact.displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        contact.rawContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                        contacts.put(contact.id, contact);
                    } while (cursor.moveToNext());
                }
                cursor = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null, null, null, null);
                ArrayList<String> placeSet = new ArrayList<String>();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID));
                        Contact contact = contacts.get(contactId);
                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)) == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME) {
                            contact.homeCountry = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            contact.homeRegion = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            contact.homeCity = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String place = getPlace(contact.homeCountry, contact.homeRegion, contact.homeCity);
                            if (!placeSet.contains(place)) {
                                placeSet.add(place);
                            }
                        } else if (cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)) == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK) {
                            contact.workCountry = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                            contact.workRegion = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                            contact.workCity = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            String place = getPlace(contact.workCountry, contact.workRegion, contact.workCity);
                            if (!placeSet.contains(place)) {
                                placeSet.add(place);
                            }
                        }
                        contacts.put(contactId, contact);
                    } while (cursor.moveToNext());
                }
                places = placeSet.toArray(new String[placeSet.size()]);
//                cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    do {
//                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID));
//                        Contact contact = contacts.get(contactId);
//                        Log.d("name", contact.displayName);
//                        Log.d("email", cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
//                        contacts.put(contactId, contact);
//                    } while (cursor.moveToNext());
//                }
                cursor = contentResolver.query(ContactsContract.Groups.CONTENT_URI,null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        groupNames.put(cursor.getString(19), cursor.getString(9));
                    } while (cursor.moveToNext());
                }
                cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI,null, ContactsContract.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    HashMap<String, Boolean> groupsUsedBool = new HashMap<String, Boolean>();
                    do {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
                        Contact contact = contacts.get(contactId);
                        String label = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                        if (label != null) {
                            groupsUsedBool.put(label, true);
                            if (contact.labels.indexOf(label) == -1) {
                                contact.labels.add(label);
                            }
                        }
                        contacts.put(contactId, contact);
                    } while (cursor.moveToNext());
                    groupsUsed = groupsUsedBool.keySet().toArray(new String[groupsUsedBool.keySet().size()]);
                }
                updateView(activity);
            }
        }).start();
    }

    public void gotPermission() {
        hasPermission = true;
    }

    public void refresh(final Activity activity) {
        if (hasPermission) {
            fetchList(activity);
        } else {
            // get permission
        }
    }

    public int getCount() {
        return keys.length;
    }

    public Contact getElement(int index) {
        return contacts.get(keys[index]);
    }

    void setOpsArray(Activity activity, Contact originalContact, Contact newContact, ArrayList<ContentProviderOperation> ops) {
        ContentResolver contentResolver = activity.getContentResolver();
        boolean homeChanged = !Objects.equals(originalContact.homeCity, newContact.homeCity) ||
                !Objects.equals(originalContact.homeRegion, newContact.homeRegion) ||
                !Objects.equals(originalContact.homeCountry, newContact.homeCountry);
        boolean workChanged = !Objects.equals(originalContact.workCity, newContact.workCity) ||
                !Objects.equals(originalContact.workRegion, newContact.workRegion) ||
                !Objects.equals(originalContact.workCountry, newContact.workCountry);
        if (homeChanged || workChanged) {
            Cursor cursor;
            String contactId = originalContact.id;
            String selection = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredPostal.TYPE + " = ?";
            String[] selectionArgs = new String[]{contactId, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE, null};
            ArrayList<Integer> placeTypes = new ArrayList<Integer>();
            if (homeChanged) {
                placeTypes.add(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
            }
            if (workChanged) {
                placeTypes.add(ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
            }
            for (int i = 0; i < placeTypes.size(); i++) {
                ArrayList<ContentProviderOperation.Builder> builders = new ArrayList<ContentProviderOperation.Builder>();
                selectionArgs[2] = String.valueOf(placeTypes.get(i));
                cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, selection, selectionArgs, null);
                if (cursor.getCount() >= 1) {
                    builders.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                            .withSelection(selection, selectionArgs));
                } else {
                    String tempSelection = ContactsContract.Data.CONTACT_ID + " = ?";
                    String[] tempSelectionArgs = new String[]{String.valueOf(contactId)};
                    Cursor newCursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI, null, tempSelection, tempSelectionArgs, null);
                    if (newCursor.moveToFirst()) {
                        int rawContactId = newCursor.getInt(newCursor.getColumnIndex(ContactsContract.RawContacts._ID));
                        builders.add(android.content.ContentProviderOperation.newInsert(android.provider.ContactsContract.Data.CONTENT_URI)
                                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                                .withValue(ContactsContract.Data.MIMETYPE, selectionArgs[1])
                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, selectionArgs[2]));
                    }
                    if (newCursor != null) {
                        newCursor.close();
                    }
                }
                switch (placeTypes.get(i)) {
                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME:
                        if (originalContact.homeCity != newContact.homeCity) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, newContact.homeCity);
                            }
                        }
                        if (originalContact.homeRegion != newContact.homeRegion) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, newContact.homeRegion);
                            }
                        }
                        if (originalContact.homeCountry != newContact.homeCountry) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, newContact.homeCountry);
                            }
                        }
                        break;
                    case ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK:
                        if (originalContact.workCity != newContact.workCity) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, newContact.workCity);
                            }
                        }
                        if (originalContact.workRegion != newContact.workRegion) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, newContact.workRegion);
                            }
                        }
                        if (originalContact.workCountry != newContact.workCountry) {
                            for (ContentProviderOperation.Builder builder: builders) {
                                builder.withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, newContact.workCountry);
                            }
                        }
                        break;
                }
                if (cursor != null) {
                    cursor.close();
                }
                for (ContentProviderOperation.Builder builder: builders) {
                    ops.add(builder.build());
                }
            }
        }
    }

    public void saveContact(Activity activity, Contact[] contacts) {
        final ContentResolver contentResolver = activity.getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (int i = 0; i < contacts.length; i++) {
            Contact newContact = contacts[i];
            Contact originalContact = this.contacts.get(contacts[i].id);
            setOpsArray(activity, originalContact, newContact, ops);
            this.contacts.put(contacts[i].id, newContact);
        }
        if (ops.size() == 0) {
            Toast.makeText(activity, "Nothing to do", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            ContactList.contactList.adapter.notifyDataSetChanged();
            Toast.makeText(activity, "Contact is successfully edited", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            Log.d("contact update error", String.valueOf(e));
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
