package com.kerimovscreations.billsplitter.models;

import java.io.Serializable;

public class Transaction implements Serializable {

    private Person from;
    private Person to;
    private float balance;
    private Currency currency;

    public Transaction() {
    }

    public Transaction(Person from, Person to, float balance, Currency currency) {
        this.from = from;
        this.to = to;
        this.balance = balance;
        this.currency = currency;
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
