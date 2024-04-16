package com.example.sallaapplication;

// ImageModel.java

public class ImageModel {
    private String imageUrl;

    public ImageModel() {
        // Required empty public constructor
    }

    public ImageModel(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
