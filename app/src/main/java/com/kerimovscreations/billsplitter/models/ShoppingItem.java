package com.kerimovscreations.billsplitter.models;

public class ShoppingItem {

    private String title, date;
    private boolean isDone = false;
    private boolean isHeader = false;

    public ShoppingItem(String title, String date, boolean isDone, boolean isHeader) {
        this.title = title;
        this.date = "13 November 2018";
        this.isDone = isDone;
        this.isHeader = isHeader;
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
}
