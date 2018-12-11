package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Product extends RealmObject implements Serializable {

    @PrimaryKey
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

    public Product() {
    }

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
        if (barCode == null)
            return "";
        else
            return barCode;
    }

    public Category getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
