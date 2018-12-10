package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Category;

import java.util.ArrayList;

public class StatisticsDataWrapper {

    @SerializedName("data")
    @Expose
    private ArrayList<Category> list;

    public ArrayList<Category> getList() {
        return list;
    }
}
