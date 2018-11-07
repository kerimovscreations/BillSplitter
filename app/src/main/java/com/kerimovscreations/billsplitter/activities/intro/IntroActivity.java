package com.kerimovscreations.billsplitter.activities.intro;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.activities.auth.LoginActivity;
import com.kerimovscreations.billsplitter.fragments.intro.IntroSliderFragment;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class IntroActivity extends BaseActivity {

    @BindView(R.id.intro_view_pager)
    ViewPager mPager;
    @BindView(R.id.sign_in_btn)
    Button mSignInBtn;

    private PagerAdapter mPagerAdapter;
    private final int[] mSliderImagesResources = {R.drawable.bg_tutorial,
            R.drawable.bg_tutorial,
            R.drawable.bg_tutorial};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_intro);
    }

    @Override
    public void initVars() {
        super.initVars();

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.sign_in_btn)
    void onSignIn(View view) {
        toLogin();
    }

    /**
     * Navigation
     */

    void toLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Slider adapter class
     */

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return IntroSliderFragment.newInstance(mSliderImagesResources[position]);
        }

        @Override
        public int getCount() {
            return mSliderImagesResources.length;
        }
    }
}
