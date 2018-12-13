package com.kerimovscreations.billsplitter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.application.GlobalApplication;

import java.io.Serializable;
import java.util.Objects;

public class Transaction implements Serializable {

    @SerializedName("UserId")
    @Expose
    private int userId;

    private Person from;
    private Person to;

    @SerializedName("Value")
    @Expose
    private float balance;

    private Currency currency;

    public void processIOwe() {
        this.from = new Person(Objects.requireNonNull(GlobalApplication.getRealm().where(LocalProfile.class).findFirst()));
        this.to = new Person(Objects.requireNonNull(GlobalApplication.getRealm().where(LocalGroupMember.class).equalTo("id", userId).findFirst()));
    }

    public void processTheyOwe() {
        this.to = new Person(Objects.requireNonNull(GlobalApplication.getRealm().where(LocalProfile.class).findFirst()));
        this.from = new Person(Objects.requireNonNull(GlobalApplication.getRealm().where(LocalGroupMember.class).equalTo("id", userId).findFirst()));
    }

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    public Person getTo() {
        return to;
    }

    public void setTo(Person to) {
        this.to = to;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
