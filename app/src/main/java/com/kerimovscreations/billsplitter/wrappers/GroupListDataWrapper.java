package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Pagination;

import java.util.ArrayList;

public class GroupListDataWrapper {

    @SerializedName("data")
    @Expose
    private ArrayList<Group> list;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public ArrayList<Group> getList() {
        return list;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
