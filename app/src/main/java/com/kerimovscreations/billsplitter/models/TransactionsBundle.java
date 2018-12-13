package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionsBundle implements Serializable {

    @SerializedName("IOwe")
    @Expose
    ArrayList<Transaction> iOwe;

    @SerializedName("TheyOwe")
    @Expose
    ArrayList<Transaction> theyOwe;

    void processData() {
        for(Transaction transaction: iOwe) {
            transaction.processIOwe();
        }

        for(Transaction transaction: theyOwe) {
            transaction.processTheyOwe();
        }
    }

    public ArrayList<Transaction> getiOwe() {
        return iOwe;
    }

    public void setiOwe(ArrayList<Transaction> iOwe) {
        this.iOwe = iOwe;
    }

    public ArrayList<Transaction> getTheyOwe() {
        return theyOwe;
    }

    public void setTheyOwe(ArrayList<Transaction> theyOwe) {
        this.theyOwe = theyOwe;
    }
}
