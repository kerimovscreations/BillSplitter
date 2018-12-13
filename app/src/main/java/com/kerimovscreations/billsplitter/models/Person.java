package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;

public class Person extends RealmObject implements Serializable {

    @SerializedName("Id")
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

    @SerializedName("ApiToken")
    @Expose
    private String apiToken;

    public Person() {
    }

    public Person(GroupMember groupMember) {
        this.id = groupMember.getId();
        this.fullName = groupMember.getFullName();
        this.email = groupMember.getEmail();
        this.picture = groupMember.getPicture();
    }

    public Person(LocalGroupMember groupMember) {
        this.id = groupMember.getMember().getId();
        this.fullName = groupMember.getMember().getFullName();
        this.email = groupMember.getMember().getEmail();
        this.picture = groupMember.getMember().getPicture();
    }

    public Person(LocalProfile localProfile) {
        this.id = localProfile.getId();
        this.fullName = localProfile.getFullName();
        this.email = localProfile.getEmail();
        this.picture = localProfile.getPicture();
    }

    public Person(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public Person(int id, String fullName, String email) {
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

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
