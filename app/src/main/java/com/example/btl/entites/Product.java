package com.example.btl.entites;

public class Product {
    String id;
    String name;
    float price;
    String categoryID;

    public Product(String id, String name, float price, String categoryID) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryID = categoryID;
    }

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}
