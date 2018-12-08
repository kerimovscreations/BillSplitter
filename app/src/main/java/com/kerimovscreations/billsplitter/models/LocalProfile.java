package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;

public class LocalProfile extends RealmObject {

    private int id;
    private String fullName;
    private String email;
    private String picture;

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
}