package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;

public class LocalGroupMember extends RealmObject {

    private Person person;
    private int groupId;

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
