package com.example.finalproject;

import java.io.Serializable;

public class ProjectModel implements Serializable {
    private String description;
    private String productImage;
    private String userId;

    public ProjectModel() {}

    public ProjectModel(String description, String productImage, String userId) {
        this.description = description;
        this.productImage = productImage;
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
