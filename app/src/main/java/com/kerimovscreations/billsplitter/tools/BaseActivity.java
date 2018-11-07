package com.kerimovscreations.billsplitter.tools;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public void onCreateSetContentView(int layoutResource) {
        setContentView(layoutResource);

        ButterKnife.bind(this);

        initVars();
    }

    public void initVars() {

    }
}
