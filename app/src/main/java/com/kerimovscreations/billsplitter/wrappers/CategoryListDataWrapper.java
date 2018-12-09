package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Pagination;

import java.util.ArrayList;

public class CategoryListDataWrapper {

    @SerializedName("data")
    @Expose
    private ArrayList<Category> list;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public ArrayList<Category> getList() {
        return list;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
