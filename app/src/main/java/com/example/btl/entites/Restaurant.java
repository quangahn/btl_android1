package com.example.btl.entites;

import java.io.Serializable;

public class Restaurant implements Serializable {
    String id;
    String name;
    String address;
    String image;
    private int commentCount;

    public Restaurant(String id, String name, String address, String image) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.image = image;
        this.commentCount = 0; // khởi tạo mặc định là 0
    }

    public Restaurant() {
    }
    // getter và setter cho commentCount
    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
