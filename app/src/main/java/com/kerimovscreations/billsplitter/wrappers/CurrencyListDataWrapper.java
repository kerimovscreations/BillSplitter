package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.Pagination;

import java.util.ArrayList;

public class CurrencyListDataWrapper {

    @SerializedName("data")
    @Expose
    private ArrayList<Currency> list;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public ArrayList<Currency> getList() {
        return list;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
