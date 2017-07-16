package com.julendieguez.quenecesito.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Julen on 27/04/2017.
 */

public class User {
    private String name;
    private String email;
    private String telephone;
    private Map<String,String> groups;
    public User(String _name, String _email, String _telephone){
        name = _name;
        email = _email;
        telephone = _telephone;
        groups = new HashMap<String,String>();
    }
    public String getName(){return this.name;}
    public String getEmail(){return this.email;}
    public String getTelephone(){return this.telephone;}
    public Map<String,String> getGroups(){
        return this.groups;
    }
    public void addGroup(String g){
        if(!groups.containsValue(g))
            groups.put(String.valueOf(groups.size()),g);
    }
    public void setName(String _name){
        name = _name;
    }
    public void removeGroup(String gID){
        String entryToRemove = "";
        for(Map.Entry<String,String> entry: groups.entrySet()){
            if(entry.getValue().equals(gID)) {
                entryToRemove = entry.getKey();
                break;
            }
        }
        groups.remove(entryToRemove);
    }
}
