package com.model;

import java.util.ArrayList;

public class Users {


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Users() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInfo.class)
    }
    public Users(String username, String email, String dateOfBirth, String imageUrl) {
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.imageUrl = imageUrl;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
    private String username;
    private String email;

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    private String dateOfBirth;


}