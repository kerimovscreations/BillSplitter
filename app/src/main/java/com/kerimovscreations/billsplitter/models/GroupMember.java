package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class GroupMember extends RealmObject implements Serializable {

    @SerializedName("UserId")
    @Expose
    private int id;

    @SerializedName("FullName")
    @Expose
    private String fullName;

    @SerializedName("Photo")
    @Expose
    private String picture;

    @SerializedName("Email")
    @Expose
    private String email;

    public GroupMember() {
    }

    public GroupMember(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public GroupMember(int id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
