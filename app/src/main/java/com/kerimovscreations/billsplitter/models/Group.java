package com.kerimovscreations.billsplitter.models;

import java.util.ArrayList;

public class Group {

    private String title;
    private ArrayList<Person> members;

    public Group(String title, ArrayList<Person> members) {
        this.title = title;
        this.members = members;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Person> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Person> members) {
        this.members = members;
    }
}
