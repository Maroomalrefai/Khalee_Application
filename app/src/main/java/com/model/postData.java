package com.model;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class postData {
    String dataLike;
    String  dataPostBody;
    String  dataPostImage;
    String dataProfileImage;
    String dataProfileName;

    public String getDataLike() {
        return dataLike;
    }

    public void setDataLike(String dataLike) {
        this.dataLike = dataLike;
    }

    public String getDataPostBody() {
        return dataPostBody;
    }

    public void setDataPostBody(String dataPostBody) {
        this.dataPostBody = dataPostBody;
    }

    public String getDataPostImage() {
        return dataPostImage;
    }

    public void setDataPostImage(String dataPostImage) {
        this.dataPostImage = dataPostImage;
    }

    public String getDataProfileImage() {
        return dataProfileImage;
    }

    public void setDataProfileImage(String dataProfileImage) {
        this.dataProfileImage = dataProfileImage;
    }

    public String getDataProfileName() {
        return dataProfileName;
    }

    public void setDataProfileName(String dataProfileName) {
        this.dataProfileName = dataProfileName;
    }

    public postData(String dataLike, String dataPostBody, String dataPostImage, String dataProfileImage, String dataProfileName) {
        this.dataLike = dataLike;
        this.dataPostBody = dataPostBody;
        this.dataPostImage = dataPostImage;
        this.dataProfileImage = dataProfileImage;
        this.dataProfileName = dataProfileName;
    }

    public postData() {
    }
}
