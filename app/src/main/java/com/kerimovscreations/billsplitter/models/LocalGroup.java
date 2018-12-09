package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocalGroup extends RealmObject {

    @PrimaryKey
    private int id;

    private String title;
    private Currency currency;

    public LocalGroup() {
    }

    public LocalGroup(Group group) {
        this.id = group.getId();
        this.title = group.getTitle();
        this.currency = group.getCurrency();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Currency getCurrency() {
        return currency;
    }
}
