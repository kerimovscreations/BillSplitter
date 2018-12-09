package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Category extends RealmObject {

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("Name")
    @Expose
    private String title;

    @SerializedName("Color")
    @Expose
    private String hexColor;

    public Category() {
    }

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
