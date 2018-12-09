package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("hasNextPage")
    @Expose
    private boolean hasNextPage;
    @SerializedName("hasPrevPage")
    @Expose
    private boolean hasPrevPage;
    @SerializedName("nextPageNumber")
    @Expose
    private int nextPageNumber;
    @SerializedName("prevPageNumber")
    @Expose
    private int prevPageNumber;

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public int getNextPageNumber() {
        return nextPageNumber;
    }

    public int getPrevPageNumber() {
        return prevPageNumber;
    }
}
