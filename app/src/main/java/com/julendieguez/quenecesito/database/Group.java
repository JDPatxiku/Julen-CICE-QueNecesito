package com.julendieguez.quenecesito.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julen on 02/05/2017.
 */

public class Group {
    private String name;
    private String description;
    private Map<String,String> users;
    private String id;
    private Map<String,String> items;

    public Group(String _name, String _description){
        name = _name;
        description = _description;
        users = new HashMap<String, String>();
        items = new HashMap<String, String>();
    }

    public void setUsers(String username){
        if(!users.containsValue(username))
            users.put(String.valueOf(users.size()),username);
    }
    public void setItems(String item) {items.put(String.valueOf(items.size()), item); }
    public void setName(String _name){
        name = _name;
    }
    public void setId(String _id){
        id = _id;
    }
    public void setDescription(String _description){ description = _description;}
    public Map<String, String> getUsers(){ return this.users; }
    public Map<String, String> getItems(){ return this.items; }
    public String getName(){ return this.name; }
    public String getDescription(){ return this.description;}
    public String obtainId(){
        return id;
    }
    public void removeUser(String uID){
        String entryToRemove = "";
        for(Map.Entry<String,String> entry: users.entrySet()){
            if(entry.getValue().equals(uID)) {
                entryToRemove = entry.getKey();
                break;
            }
        }
        users.remove(entryToRemove);
    }

    public void removeItem(String iID){
        String entryToRemove = "";
        for(Map.Entry<String, String> entry: items.entrySet()){
            if(entry.getValue().equals(iID)){
                entryToRemove = entry.getKey();
                break;
            }
        }
        items.remove(entryToRemove);
    }

    public void reasignItems(List<String> items){
        this.items = new HashMap<String, String>();
        for (String i: items){
            this.items.put(String.valueOf(this.items.size()),i);
        }
    }

    public boolean anyUserIn(){
        return users.size() >= 1;
    }
    public void prepareToDelete(){
        users = null;
        name = null;
        description = null;
    }

}
