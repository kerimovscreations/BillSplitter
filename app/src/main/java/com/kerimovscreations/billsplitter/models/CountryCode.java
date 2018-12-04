package com.kerimovscreations.billsplitter.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.kerimovscreations.billsplitter.utils.CommonMethods;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CountryCode {
    private String name;
    private String code;

    @SerializedName("dial_code")
    private String dialCode;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDialCode() {
        return dialCode;
    }

    public static List<CountryCode> loadArrayFromAsset(Context context, String fileName) {
        ArrayList<CountryCode> list = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String jsonFile = new String(buffer, "UTF-8");

            list.addAll(CommonMethods.getInstance().getGson().fromJson(jsonFile, new TypeToken<List<CountryCode>>() {
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
        return String.format("%s (+%s)", name, dialCode);
    }
}
