package com.kerimovscreations.billsplitter.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class CommonMethods {

    private static CommonMethods instance = null;

    private Gson mGson;
    private JsonParser mJsonParser;


    private CommonMethods() {
        mGson = new Gson();
        mJsonParser = new JsonParser();
    }

    // static method to create instance of Singleton class
    public static CommonMethods getInstance() {
        if (instance == null)
            instance = new CommonMethods();

        return instance;
    }

    public Gson getGson() {
        return mGson;
    }

    public JsonParser getJsonParser() {
        return mJsonParser;
    }

    public static boolean isValidPasswordLength(String password) {
        return password.length() > 5;
    }

    public static boolean isMatchingPasswords(String password1, String password2) {
        return password1.equals(password2);
    }
}
