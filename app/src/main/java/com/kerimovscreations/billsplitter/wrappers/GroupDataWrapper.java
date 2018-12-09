package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Group;

public class GroupDataWrapper {

    @SerializedName("data")
    @Expose
    private Group group;

    public Group getGroup() {
        return group;
    }
}
