package com.kerimovscreations.billsplitter.wrappers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kerimovscreations.billsplitter.models.Person;

public class UserDataWrapper {

    @SerializedName("data")
    @Expose
    Person person;

    public Person getPerson() {
        return person;
    }
}
