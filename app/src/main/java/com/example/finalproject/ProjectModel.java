package com.example.finalproject;

import java.io.Serializable;
import java.util.List;

public class ProjectModel implements Serializable {
    private String description;
    private String productImage;
    private String userId;
    private List<String> tags;

    public ProjectModel() {
        // Default constructor required for Firebase
    }

    public ProjectModel(String description, String productImage, String userId, List<String> tags) {
        this.description = description;
        this.productImage = productImage;
        this.userId = userId;
        this.tags = tags;
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

/*.\    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }*/
}