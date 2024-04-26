package com.model;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postKey;

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    private String postText;
    private String photo;
    private String userId;
  //  private String userProfileImage;
    private Object timeStamp;

    public Post(String postText, String photo
            , String userId
//            , String userProfileImage
    ) {
        this.postText = postText;
        this.photo = photo;
        this.userId = userId;
      //  this.userProfileImage = userProfileImage;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    public void setUserProfileImage(String userProfileImage) {
//        this.userProfileImage = userProfileImage;
//    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPostText() {
        return postText;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUserId() {
        return userId;
    }
//
//    public String getUserProfileImage() {
//        return userProfileImage;
//    }

    public Object getTimeStamp() {
        return timeStamp;
    }
}
