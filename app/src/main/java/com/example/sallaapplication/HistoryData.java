package com.example.sallaapplication;
public class HistoryData {
    String dataTitle;
    String dataDesc;
    String dataLang;
    String dataImage;
    String key;

    public String getKey() {
        return key;
    }

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

    public HistoryData() {
    }
}
