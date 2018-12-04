package com.kerimovscreations.billsplitter.utils;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppApiClient {

    private static AppApiClient instance = null;
    private Context context = null;

    private AppApiService appApiService;

    public static AppApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new AppApiClient(context);
        }
        return instance;
    }

    private AppApiClient(Context contextLocal) {
        this.context = contextLocal;

        Gson helperGson = new Gson();

        GsonBuilder gsonBuilder = new GsonBuilder();


        Gson gson = gsonBuilder.setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        appApiService = retrofit.create(AppApiService.class);
    }

    public AppApiService getAppApiService() {
        return this.appApiService;
    }

}

