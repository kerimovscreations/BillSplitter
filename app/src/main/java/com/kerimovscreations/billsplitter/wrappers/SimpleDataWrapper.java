package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Pagination;
import com.kerimovscreations.billsplitter.models.Person;

import java.util.ArrayList;
import java.util.List;

public class SimpleDataWrapper {

    @SerializedName("data")
    @Expose
    private List<Person> data = new ArrayList<>();

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public List<Person> getData() {
        return data;
    }

    public void setData(List<Person> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
