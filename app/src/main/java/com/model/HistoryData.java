package com.model;

public class HistoryData {
    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }
    public HistoryData(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }
    public HistoryData(){

    }
}