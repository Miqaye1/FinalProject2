package com.example.finalproject;

public class ProjectModel {
    private String description;
    private String productImage;
    public ProjectModel(){

    }

    public ProjectModel(String description, String productImage) {
        this.description = description;
        this.productImage = productImage;
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
}
