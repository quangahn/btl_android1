package com.example.btl.entites;

public class User {
    String name;
    String userID;
    String email;
    String password;
    String avatar;
    String role;
    Boolean status;

    public User() {
    }

    public User(String userID, String email, String password, String avatar, String role, Boolean status, String name) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.status = true;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
