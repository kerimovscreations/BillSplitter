package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.TransactionsBundle;

public class TransactionsBundleDataWrapper {

    @SerializedName("data")
    @Expose
    private TransactionsBundle bunle;

    public TransactionsBundle getBunle() {
        return bunle;
    }
}
