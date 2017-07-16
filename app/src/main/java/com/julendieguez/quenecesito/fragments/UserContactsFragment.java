package com.julendieguez.quenecesito.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.julendieguez.quenecesito.MainMenuActivity;
import com.julendieguez.quenecesito.R;
import com.julendieguez.quenecesito.contacts.Contact;
import com.julendieguez.quenecesito.recyclerviewcomponents.ContactAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserContactsFragment extends Fragment {
    private MainMenuActivity parentActivity;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private List<Contact> contacts;
    private List<String> currentUsers;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ContactAdapter cAdapter;

    public UserContactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_user_contacts, container, false);
        setValues(inflatedView);
        checkPermissions();
        return inflatedView;
    }

    private void setValues(View v){
        if(parentActivity == null)
            parentActivity = (MainMenuActivity) getActivity();
        contacts = new ArrayList<Contact>();
        currentUsers = new ArrayList<String>();
        getGroupUserNumbers();
        parentActivity.changeMenuValues(false);
        recyclerView = (RecyclerView) v.findViewById(R.id.groupRecycler);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setReferences(){

    }
    private void getGroupUserNumbers(){
        for (Map.Entry<String, String> entry : parentActivity.getGroupData().getUsers().entrySet()){
            currentUsers.add(String.valueOf(entry.getValue()));
        }
    }

    private void getContacts(){
        ContentResolver cr = parentActivity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact c = new Contact();
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                if(name != null)
                    c.setName(name);
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo  = formatPhoneNumber(phoneNo);
                        if(!phoneNo.equals("0"))
                            c.setNumber(phoneNo);
                    }
                    pCur.close();
                }
                try{
                    if(!c.getName().equals(null) && !c.getNumber().equals(null)) {
                        contacts.add(c);
                    }
                }catch (NullPointerException e){
                    //En algunas versiones de android da error sin motivo aparente, así que mejor prevenirlo.
                }
            }
        }
        discardContacts();
    }

    private Boolean numberExistsOnGroup(String number){
        return currentUsers.contains(number);
    }
    private void discardContacts(){
        if(contacts.size() > 0){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("numbers");
            ref.keepSynced(true);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Contact> verifiedContacts = new ArrayList<Contact>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(Contact c : contacts){
                            if(c.getNumber().equals(snapshot.getKey())) {
                                c.setuID(snapshot.getValue().toString());
                                if(!numberExistsOnGroup(snapshot.getValue().toString()))
                                    verifiedContacts.add(c);
                            }
                        }
                    }
                    contacts.clear();
                    for(Contact c : verifiedContacts){
                        contacts.add(c);
                    }
                    verifiedContacts.clear();
                    fillListWithContacts();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void fillListWithContacts(){
        cAdapter = new ContactAdapter(this,contacts);
        recyclerView.setAdapter(cAdapter);
    }

    @SuppressLint("NewApi")
    private String formatPhoneNumber(String phoneNo){
        TelephonyManager tMgr = (TelephonyManager)parentActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String number;
        try {
            if(phoneNo.charAt(0) == '+') {
                number = PhoneNumberUtils.formatNumber(phoneNo, tMgr.getSimCountryIso());
                String toRemove = number.substring(0, number.indexOf(" "));
                number = number.replace(toRemove, "");
                number = number.replace(" ", "");
            }else{
                number = phoneNo.replace(" ", "");
            }
        }catch (Exception e){
            number = "0";
        }
        return number;
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && parentActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }


}
