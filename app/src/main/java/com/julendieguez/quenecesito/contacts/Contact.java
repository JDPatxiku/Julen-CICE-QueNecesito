package com.julendieguez.quenecesito.contacts;


public class Contact {
    private String name;
    private String number;
    private String uID;
    public Contact(){

    }

    public void setuID(String uID){this.uID = uID;}
    public void setNumber(String number){
        this.number = number;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getNumber() {
        return number;
    }
    public String getuID(){return uID;}
    public String getName() {
        return name;
    }
}
