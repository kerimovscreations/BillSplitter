package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Group implements Serializable {

    @SerializedName("Id")
    @Expose
    private int id = 0;

    @SerializedName("Name")
    @Expose
    private String title;

    @SerializedName("Currency")
    @Expose
    private Currency currency;

    @SerializedName("GroupsUsers")
    @Expose
    private ArrayList<Person> groupUsers = new ArrayList<>();

    public Group() {
    }

    public Group(LocalGroup localGroup) {
        this.id = localGroup.getId();
        this.title = localGroup.getTitle();
        this.currency = localGroup.getCurrency();
        this.groupUsers = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public ArrayList<Person> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(ArrayList<Person> groupUsers) {
        this.groupUsers = groupUsers;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
