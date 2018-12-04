package com.kerimovscreations.billsplitter.fragments.intro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.utils.BaseFragment;
import com.squareup.picasso.Picasso;

public class IntroSliderFragment extends BaseFragment {

    private static final String IMG_RESOURCE = "IMG_RES";

    private int mImgRes;

    public IntroSliderFragment() {
        // Required empty public constructor
    }

    public static IntroSliderFragment newInstance(int imgRes) {
        IntroSliderFragment fragment = new IntroSliderFragment();
        Bundle args = new Bundle();
        args.putInt(IMG_RESOURCE, imgRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImgRes = getArguments().getInt(IMG_RESOURCE, 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_intro_slider);
    }

    @Override
    public void initVars() {
        super.initVars();

        ImageView imageView = getContentView().findViewById(R.id.intro_slider_image);
        Picasso.get().load(mImgRes).into(imageView);
    }
}
