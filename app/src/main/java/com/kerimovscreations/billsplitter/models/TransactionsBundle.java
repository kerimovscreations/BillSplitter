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

    public TransactionsBundle processData(LocalGroup group) {
        for (int i = 0; i < iOwe.size(); i++) {
            iOwe.get(i).processIOwe(group);
        }

        for (int i = 0; i < theyOwe.size(); i++) {
            theyOwe.get(i).processTheyOwe(group);
        }

        for (int i = 0; i < iOwe.size(); i++) {
            for (int j = 0; j < theyOwe.size(); j++) {
                if (iOwe.get(i).getTo().getId() == theyOwe.get(j).getFrom().getId()) {
                    if (iOwe.get(i).getBalance() > theyOwe.get(j).getBalance()) {
                        iOwe.get(i).setBalance(iOwe.get(i).getBalance() - theyOwe.get(j).getBalance());
                        theyOwe.get(j).setBalance(0);
                    } else {
                        theyOwe.get(i).setBalance(theyOwe.get(i).getBalance() - iOwe.get(j).getBalance());
                        iOwe.get(j).setBalance(0);
                    }
                }
            }
        }

        return this;
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
