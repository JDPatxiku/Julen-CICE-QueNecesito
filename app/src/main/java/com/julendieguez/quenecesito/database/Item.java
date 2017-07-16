package com.julendieguez.quenecesito.database;

/**
 * Created by Julen on 05/05/2017.
 */

public class Item {
    private String id;
    private String name;
    private String author;
    private String group;
    private String brand;
    private float price;
    private int quantity;

    public Item(String author, String name, String brand, float price, int quantity){
        this.author = author;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
    }
    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}
    public String getGroup() {return group;}
    public void setGroup(String group) {this.group = group;}
    public String getBrand() {return brand;}
    public void setBrand(String brand) {this.brand = brand;}
    public float getPrice() {return price;}
    public void setPrice(float price) {this.price = price;}
    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public void setId(String id){this.id = id;}
    public String obtainId(){return id;}
}
