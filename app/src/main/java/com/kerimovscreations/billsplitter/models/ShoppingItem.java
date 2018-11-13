package com.kerimovscreations.billsplitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingItem implements Serializable {

    private String title, date;
    private boolean isDone = false;
    private boolean isHeader = false;
    private List<Person> sharedPeople;

    public ShoppingItem(String title, String date, boolean isDone, List<Person> sharedPeople, boolean isHeader) {
        this.title = title;
        this.date = "13 November 2018";
        this.isDone = isDone;
        this.isHeader = isHeader;
        this.sharedPeople = sharedPeople;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void toggleDone() {
        this.isDone = !this.isDone;
    }

    public List<Person> getSharedPeople() {
        return sharedPeople;
    }

    public void setSharedPeople(List<Person> sharedPeople) {
        this.sharedPeople = sharedPeople;
    }
}
