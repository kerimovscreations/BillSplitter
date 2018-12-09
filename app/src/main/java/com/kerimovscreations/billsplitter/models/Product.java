package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Product extends RealmObject {

    @SerializedName("Id")
    @Expose
    private int id;

    @SerializedName("Name")
    @Expose
    private String name;

    @SerializedName("GroupId")
    @Expose
    private int groupId;

    @SerializedName("BarCode")
    @Expose
    private String barCode;

    @SerializedName("Category")
    @Expose
    private Category category;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getBarCode() {
        return barCode;
    }
}
