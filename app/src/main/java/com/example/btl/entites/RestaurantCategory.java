package com.example.btl.entites;

public class RestaurantCategory {
    String id;
    String restaurantId;
    String categoryId;

    public RestaurantCategory(String id, String restaurantId, String categoryId) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
    }

    public RestaurantCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
