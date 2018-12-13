package com.kerimovscreations.billsplitter.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocalGroupMember extends RealmObject {

    @PrimaryKey
    private String id;

    private GroupMember groupMember;
    private int groupId;

    public LocalGroupMember() {
    }

    public LocalGroupMember(GroupMember member, int groupId) {
        this.groupMember = member;
        this.groupId = groupId;
        this.id = groupId + "_" + member.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GroupMember getMember() {
        return groupMember;
    }

    public void setMember(GroupMember member) {
        this.groupMember = member;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
