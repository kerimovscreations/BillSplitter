package com.kerimovscreations.billsplitter.models;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class LocalProfile extends RealmObject {

    private int id;
    private String fullName;
    private String email;
    private String picture;
    private int lastSelectedGroupId = -1;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setData(Person person) {
        this.id = person.getId();
        this.fullName = person.getFullName();
        this.email = person.getEmail();
        this.picture = person.getPicture();
    }

    public int getLastSelectedGroupId() {
        return lastSelectedGroupId;
    }

    public void setLastSelectedGroupId(int lastSelectedGroupId) {
        this.lastSelectedGroupId = lastSelectedGroupId;
    }
}