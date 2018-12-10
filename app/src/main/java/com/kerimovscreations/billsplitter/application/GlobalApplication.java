package com.kerimovscreations.billsplitter.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.kerimovscreations.billsplitter.R;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;
    private static Gson mGson;
    private static Retrofit retrofit;
    private static SharedPreferences mPrefs;
    private static Realm mRealm;

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static GlobalApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        mGson = new Gson();
        mPrefs = getContext().getSharedPreferences(getContext().getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);

        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

        Fabric.with(this, new Crashlytics());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        super.onCreate();
    }


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
//        CommonMethods.forceLocale(this, new Locale(LocaleHelper.getLanguage(base)));
        MultiDex.install(this);
    }

    public static Gson getGson() {
        return mGson;
    }

    public static SharedPreferences getmPrefs(Context context) {
        if (mPrefs == null)
            if (context != null)
                mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.local_preference), Context.MODE_PRIVATE);
            else
                mPrefs = getContext().getSharedPreferences(Resources.getSystem().getString( R.string.local_preference), Context.MODE_PRIVATE);

        return mPrefs;
    }

    public static Realm getRealm() {
        return mRealm;
    }
}