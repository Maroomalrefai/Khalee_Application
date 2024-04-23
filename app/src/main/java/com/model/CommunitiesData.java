package com.model;

public class CommunitiesData {
    String communityName;
    Integer imageUrl;
    public CommunitiesData(String communityName, Integer imageUrl) {
        this.communityName = communityName;
        this.imageUrl=imageUrl;
    }
    public String getCommunityName() {
        return communityName;
    }

//    public void setCommunityName(String communityName) {
//        this.communityName = communityName;
//    }


    public Integer getImageUrl() {
        return imageUrl;
    }

//    public void setImageUrl(Integer imageUrl) {
//        this.imageUrl = imageUrl;
//    }
}
