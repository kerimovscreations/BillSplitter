package com.kerimovscreations.billsplitter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.kerimovscreations.billsplitter.application.GlobalApplication;

import butterknife.ButterKnife;
import io.realm.Realm;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private Context mContext;
    private AppCompatActivity mActivity;
    private Realm mRealm;

    public void onCreateSetContentView(int layoutResource) {
        setContentView(layoutResource);

        ButterKnife.bind(this);

        mContext = this;
        mActivity = this;

        initVars();
    }

    public void initVars() {
        mRealm = GlobalApplication.getRealm();
    }

    public Context getContext() {
        return mContext;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public Realm getRealm() {
        return mRealm;
    }
}
