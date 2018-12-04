package com.kerimovscreations.billsplitter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private Context mContext;
    private AppCompatActivity mActivity;

    public void onCreateSetContentView(int layoutResource) {
        setContentView(layoutResource);

        ButterKnife.bind(this);

        mContext = this;
        mActivity = this;

        initVars();
    }

    public void initVars() {

    }

    public Context getContext() {
        return mContext;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }
}
