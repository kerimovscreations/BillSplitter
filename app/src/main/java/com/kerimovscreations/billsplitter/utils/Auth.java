package com.kerimovscreations.billsplitter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.Person;

import io.realm.Realm;

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

        return "Bearer " + mPrefs.getString(context.getString(R.string.local_preference_token), "");
    }

    public void saveProfile(Context context, Person person) {
        GlobalApplication.getRealm().executeTransaction(realm -> {
            LocalProfile profile = new LocalProfile();
            profile.setData(person);
            realm.copyToRealmOrUpdate(profile);
        });
        saveToken(context, person.getApiToken());
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
        GlobalApplication.getRealm().executeTransaction(realm -> realm.deleteAll());
        removeToken(context);
    }

    public void updateProfile(Person person) {
        GlobalApplication.getRealm().executeTransaction(realm -> {
            LocalProfile profile = new LocalProfile();
            profile.setData(person);
            realm.copyToRealmOrUpdate(profile);
        });
    }
}
