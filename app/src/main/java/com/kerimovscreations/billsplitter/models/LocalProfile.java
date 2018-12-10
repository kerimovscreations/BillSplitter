package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocalProfile extends RealmObject {

    @PrimaryKey
    private int id;

    private String fullName;
    private String email;
    private String picture;
    private boolean isSocialLogin = false;
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

    public boolean isSocialLogin() {
        return isSocialLogin;
    }

    public void setSocialLogin(boolean socialLogin) {
        isSocialLogin = socialLogin;
    }
}