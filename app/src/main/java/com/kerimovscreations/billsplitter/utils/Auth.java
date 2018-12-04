package com.kerimovscreations.billsplitter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kerimovscreations.billsplitter.R;

public class Auth {
    private static Auth mInstance = null;

    private Auth() {
    }

    public static Auth getInstance() {
        if(mInstance == null)
            mInstance = new Auth();

        return mInstance;
    }

    public boolean isLogged(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);

        return mPrefs.contains(context.getString(R.string.local_preference_token));
    }

    public String getToken(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);

        return mPrefs.getString(context.getString(R.string.local_preference_token), "");
    }

    public void saveToken(Context context, String token) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(context.getString(R.string.local_preference_token), token);
        prefsEditor.apply();
    }

    public void removeToken(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove(context.getString(R.string.local_preference_token));
        prefsEditor.apply();
    }

    public void logout(Context context) {
        removeToken(context);
    }
}
