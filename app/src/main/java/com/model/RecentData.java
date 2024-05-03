package com.model;

public class RecentData {
    private String productName;
    private String companyName;
    private Integer imageUrl;
    private String companyUrl;

    public RecentData(String productName, String companyName, Integer imageUrl, String companyUrl) {
        this.productName = productName;
        this.companyName = companyName;
        this.imageUrl = imageUrl;
        this.companyUrl = companyUrl;
    }
    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }
    public Integer getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Integer imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
