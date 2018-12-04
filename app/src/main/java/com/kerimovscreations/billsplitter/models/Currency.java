package com.kerimovscreations.billsplitter.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.kerimovscreations.billsplitter.utils.CommonMethods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Currency {
    private String name, code;

    public static List<Currency> loadArrayFromAsset(Context context, String fileName) {
        ArrayList<Currency> list = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String jsonFile = new String(buffer, "UTF-8");

            list.addAll(CommonMethods.getInstance().getGson().fromJson(jsonFile, new TypeToken<List<Currency>>() {
            }.getType()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return list;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s - %s", code, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
