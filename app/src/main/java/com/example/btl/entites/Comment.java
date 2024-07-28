package com.example.btl.entites;

import java.util.Date;

public class Comment {
    String id;
    String postID;
    String userID;
    String comment;
    Date date;

    public Comment() {
    }

    public Comment(String id, String postID, String userID, String comment, Date date) {
        this.id = id;
        this.postID = postID;
        this.userID = userID;
        this.comment = comment;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
