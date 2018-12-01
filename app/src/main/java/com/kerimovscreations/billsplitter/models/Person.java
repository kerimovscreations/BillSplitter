package com.kerimovscreations.billsplitter.models;

import java.io.Serializable;

public class Person implements Serializable {

    private int id;
    private String fullName;
    private String picture;
    private String email;

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
}
