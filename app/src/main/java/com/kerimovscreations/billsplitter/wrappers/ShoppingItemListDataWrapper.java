package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Pagination;
import com.kerimovscreations.billsplitter.models.ShoppingItem;

import java.util.ArrayList;

public class ShoppingItemListDataWrapper {

    @SerializedName("data")
    @Expose
    private ArrayList<ShoppingItem> list;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public ArrayList<ShoppingItem> getList() {
        return list;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
