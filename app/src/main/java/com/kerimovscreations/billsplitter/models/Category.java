package com.kerimovscreations.billsplitter.models;

public class Category {
    private String title;
    private String hexColor;

    public Category(String title, String hexColor) {
        this.title = title;
        this.hexColor = hexColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }
}
