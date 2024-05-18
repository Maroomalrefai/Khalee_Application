package com.model;

public class CommunitiesData {
    private String communityName;
    private String communityID;
    private Integer imageUrl;

    public CommunitiesData(String communityName, String communityID, Integer imageUrl) {
        this.communityName = communityName;
        this.communityID = communityID;
        this.imageUrl = imageUrl;
    }
    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }
    public Integer getImageUrl() {
        return imageUrl;
    }
    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

public CommunitiesData(){

}
}
