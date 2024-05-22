package com.model;

public class ProductData {
    private String productName;
    private String companyName;
    private String imageUrl;
    private String companyUrl;

    public ProductData(String productName, String companyName, String imageUrl, String companyUrl) {
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
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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
    public ProductData() {
    }
}
