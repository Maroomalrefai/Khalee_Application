package com.model;

public class Users {
    private String username;
    private String email;
    private String dateOfBirth;
    private String imageUrl;
    private String uid; // Ensure this field is present and populated

    // Default constructor required for calls to DataSnapshot.getValue(Users.class)
    public Users() {}

    public Users(String username, String email, String dateOfBirth, String imageUrl, String uid) {
        this.username = username;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.imageUrl = imageUrl;
        this.uid = uid;
    }

    // Getter and setter methods
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
