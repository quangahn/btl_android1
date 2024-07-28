package com.example.btl.entites;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class PostEntity implements Serializable {
    String id;
    String title;
    String content;
    String userId;
    String restaurant_id;
    String image;
    Date created_at;
    Date updated_at;
    String product_id;
    Boolean status;
    List<String> likes;
    private int commentCount;

    public PostEntity(String id, String title, String content, String userId, String restaurant_id, String image, Boolean status, String product_id, List<String> likes) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.restaurant_id = restaurant_id;
        this.image = image;
        this.created_at = new Date();
        this.updated_at = new Date();
        this.status = status;
        this.product_id = product_id;
        this.likes =likes ;
        this.commentCount = 0; // khởi tạo mặc định là 0
    }

    public PostEntity() {
    }
    // getter và setter cho commentCount
    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
