package com.kerimovscreations.billsplitter.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public boolean isValidPasswordLength(String password) {
        return password.length() > 5;
    }

    public boolean isMatchingPasswords(String password1, String password2) {
        return password1.equals(password2);
    }

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }
}
