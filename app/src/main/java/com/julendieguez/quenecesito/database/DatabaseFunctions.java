package com.julendieguez.quenecesito.database;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Julen on 29/04/2017.
 */

public class DatabaseFunctions {
    public static String getUserUID(FirebaseAuth auth){
        return auth.getCurrentUser().getUid();
    }

    public static User fillUserWithDataSnapshot(DataSnapshot data){
        String[] userData = new String[3];
        List<String> groups = null;
        for (DataSnapshot child: data.getChildren()){
            switch(child.getKey()){
                case "email":
                    userData[1] = child.getValue().toString();
                    break;
                case "name":
                    userData[0] = child.getValue().toString();
                    break;
                case "telephone":
                    userData[2] = child.getValue().toString();
                    break;
                case "groups":
                    groups = new ArrayList<String>();
                    for(DataSnapshot subChild: child.getChildren()){
                        groups.add(subChild.getValue().toString());
                    }
                    break;
                }
        }
        User u = new User(userData[0],userData[1],userData[2]);
        if(groups != null){
            for(String s: groups){
                u.addGroup(s);
            }
        }
        return u;
    }
    public static Group fillGroupWithDataSnapshot(DataSnapshot data){
        String[] groupData = new String[2];
        List<String> items = null;
        List<String> users = null;
        for(DataSnapshot child: data.getChildren()){
            switch(child.getKey()){
                case "name":
                    groupData[0] = child.getValue().toString();
                    break;
                case "description":
                    groupData[1] = child.getValue().toString();
                    break;
                case "users":
                    users = new ArrayList<String>();
                    for(DataSnapshot subChild: child.getChildren()){
                        users.add(subChild.getValue().toString());
                    }
                    break;
                case "items":
                    items = new ArrayList<String>();
                    for(DataSnapshot subChild: child.getChildren()){
                        items.add(subChild.getValue().toString());
                    }
                    break;
            }
        }
        Group g = new Group(groupData[0],groupData[1]);
        g.setId(data.getKey());
        if(items != null){
            for(String s: items){
                g.setItems(s);
            }
        }
        if(users != null){
            for(String s: users){
                g.setUsers(s);
            }
        }
        return g;
    }

    public static List<Group> getUserGroupLists(DataSnapshot data, Map<String,String> userGroups){
        if(userGroups == null)
            return null;
        List<Group> groups = new ArrayList<Group>();
        for (DataSnapshot child: data.getChildren()){
            if(userGroups.containsValue(child.getKey())){
                Group g = new Group("","");
                for(DataSnapshot subChild: child.getChildren()){
                    switch(subChild.getKey()){
                        case "name":
                            g.setName(subChild.getValue().toString());
                            break;
                        case "users":
                            for(DataSnapshot subSubChild: subChild.getChildren()){
                                g.setUsers(subSubChild.getValue().toString());
                            }
                            break;
                        case "description":
                            g.setDescription(subChild.getValue().toString());
                            break;
                        case "items":
                            for(DataSnapshot subSubChild: subChild.getChildren()){
                                g.setItems(subSubChild.getValue().toString());
                            }
                            break;
                    }
                }
                g.setId(child.getKey());
                groups.add(g);
            }
        }
        return groups;
    }
    public static List<Item> getGroupItemList(DataSnapshot data, Group g){
        if(g == null)
            return null;
        List<Item> items  = new ArrayList<Item>();
        for(DataSnapshot child: data.getChildren()){
            if(g.getItems().containsValue(child.getKey())){
                Item i = new Item("","","",0,0);
                i.setId(child.getKey());
                for(DataSnapshot subChild: child.getChildren()){
                    switch (subChild.getKey()){
                        case "author":
                            i.setAuthor(subChild.getValue().toString());
                            break;
                        case "brand":
                            i.setBrand(subChild.getValue().toString());
                            break;
                        case "name":
                            i.setName(subChild.getValue().toString());
                            break;
                        case "price":
                            i.setPrice(Float.valueOf(subChild.getValue().toString()));
                            break;
                        case "quantity":
                            i.setQuantity(Integer.parseInt(subChild.getValue().toString()));
                    }
                }
                items.add(i);
            }
        }
        return items;
    }

    public static void updateUser(FirebaseAuth auth, FirebaseDatabase db, User user){
        DatabaseReference ref = db.getReference("users");
        Map<String, Object> users = new HashMap<String, Object>();
        users.put(DatabaseFunctions.getUserUID(FirebaseAuth.getInstance()),user);
        ref.updateChildren(users);
    }
    public static void updateOtherUser(final String uid, final FirebaseDatabase db, final String gID){
        DatabaseReference ref = db.getReference("users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> users = new HashMap<String, Object>();
                User u = fillUserWithDataSnapshot(dataSnapshot);
                u.addGroup(gID);
                users.put(uid,u);
                DatabaseReference subref = db.getReference("users");
                subref.updateChildren(users);
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }
    public static void updateGroup(FirebaseAuth auth, FirebaseDatabase db, Group g){
        DatabaseReference ref = db.getReference("groups");
        Map<String, Object> groupMap = new HashMap<String, Object>();
        groupMap.put(g.obtainId(),g);
        ref.updateChildren(groupMap);
    }

    public static void updateItem(FirebaseAuth auth, FirebaseDatabase db, Item i){
        DatabaseReference ref = db.getReference("items");
        Map<String, Object> itemMap = new HashMap<String,Object>();
        itemMap.put(i.obtainId(),i);
        ref.updateChildren(itemMap);
    }

    public static void deleteItem(FirebaseAuth auth, FirebaseDatabase db, String iID){
        DatabaseReference ref = db.getReference("items").child(iID);
        ref.removeValue();
    }
    public static class GetNumberMatch extends AsyncTask<String,String,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL testUrl = new URL(params[0]);
                connection = (HttpURLConnection) testUrl.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line="";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                JSONObject jsonObject = new JSONObject(buffer.toString());
                return jsonObject.getBoolean("match");
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
