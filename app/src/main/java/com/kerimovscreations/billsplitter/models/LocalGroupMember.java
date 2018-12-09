package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocalGroupMember extends RealmObject {

    @PrimaryKey
    private String id;

    private Person person;
    private int groupId;

    public LocalGroupMember() {
    }

    public LocalGroupMember(Person person, int groupId) {
        this.person = person;
        this.groupId = groupId;
        this.id = groupId + "_" + person.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
