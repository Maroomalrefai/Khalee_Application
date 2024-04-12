package com.example.sallaapplication;

public class HistoryData {
    private String imageUrl;
    private String recognizedText;

    public HistoryData(String title, String desc, String lang, String imageURL) {
        // Default constructor required for Firebase
    }

    public HistoryData(String imageUrl, String recognizedText) {
        this.imageUrl = imageUrl;
        this.recognizedText = recognizedText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRecognizedText() {
        return recognizedText;
    }

    public void setRecognizedText(String recognizedText) {
        this.recognizedText = recognizedText;
    }
}
