package com.model;
    public class Post {
        private String postKey;
        private String postText;
        private String photo;
        private String profileImage;
        private String userId;
        private String profileName;
        private Object timeStamp;

        public Post(String postText, String photo, String userId, String profileName, String profileImage) {
            this.postText = postText;
            this.profileImage = profileImage;
            this.profileName = profileName;
            this.photo = photo;
            this.userId = userId;
            this.timeStamp = System.currentTimeMillis();
        }

        public Post(String postText, String userId, String profileName, String profileImage) {
            this.postText = postText;
            this.profileName = profileName;
            this.userId = userId;
            this.profileImage = profileImage;
            this.timeStamp = System.currentTimeMillis();
        }

        public Post() {
            // Default constructor required for Firebase
        }

        public String getPostKey() {
            return postKey;
        }

        public void setPostKey(String postKey) {
            this.postKey = postKey;
        }

        public String getPostText() {
            return postText;
        }

        public void setPostText(String postText) {
            this.postText = postText;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getProfileName() {
            return profileName;
        }

        public void setProfileName(String profileName) {
            this.profileName = profileName;
        }

        public Object getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Object timeStamp) {
            this.timeStamp = timeStamp;
        }
    }