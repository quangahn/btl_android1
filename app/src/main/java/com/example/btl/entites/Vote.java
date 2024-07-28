package com.example.btl.entites;

public class Vote {
    String id;
    String userID;
    String restaurantID;
    int vote;

    public Vote(String id, String userID, String restaurantID, int vote) {
        this.id = id;
        this.userID = userID;
        this.restaurantID = restaurantID;
    }

    public Vote() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}