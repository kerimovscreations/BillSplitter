package com.kerimovscreations.billsplitter.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    private View mContentView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutRes) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContentView = inflater.inflate(layoutRes, container, false);

        ButterKnife.bind(this, mContentView);

        initVars();

        return mContentView;
    }

    public void initVars() {

    }

    public View getContentView() {
        return mContentView;
    }
}
