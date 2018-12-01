package com.kerimovscreations.billsplitter.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.GroupListRVAdapter;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MenuBottomSheetDialogFragment extends BottomSheetDialogFragment {

    View mView;

    @BindView(R.id.top_pointer_view)
    View mTopPointerView;
    @BindView(R.id.profile_layout)
    View mProfileLayout;
    @BindView(R.id.avatar)
    CircleImageView mAvatar;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.user_toggle_ic)
    ImageView mUserToggleIc;
    @BindView(R.id.user_email)
    TextView mUserEmail;
    @BindView(R.id.logout_layout)
    View mLogoutLayout;
    @BindView(R.id.rvList)
    RecyclerView mRVList;

    GroupListRVAdapter mAdapter;
    ArrayList<Group> mList = new ArrayList<>();

    boolean mLogoutBtnVisible = false;

    private ClickListener mListener;

    public interface ClickListener {
        void onGroup(Group group);
    }

    public void setClickListener(ClickListener listener) {
        mListener = listener;
    }

    public static MenuBottomSheetDialogFragment getInstance() {
        return new MenuBottomSheetDialogFragment();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.bottom_sheet_dialog_menu, container, false);
        ButterKnife.bind(this, mView);

        initVars();

        return mView;
    }

    void initVars() {

        ArrayList<Person> mPeople = new ArrayList<>();

        mPeople.add(new Person(1, "User 1"));
        mPeople.add(new Person(2, "User 2"));
        mPeople.add(new Person(3, "User 3"));
        mPeople.add(new Person(4, "User 4"));
        mList.add(new Group("My List", mPeople));

        ArrayList<Person> mPeople1 = new ArrayList<>();
        mPeople1.add(new Person(5, "User 5"));
        mList.add(new Group("Kocsis dorm", mPeople1));

        ArrayList<Person> mPeople2 = new ArrayList<>();
        mPeople2.add(new Person(6, "User 6"));
        mPeople2.add(new Person(7, "User 7"));
        mPeople2.add(new Person(8, "User 8"));
        mList.add(new Group("Vodafone work", mPeople2));

        mAdapter = new GroupListRVAdapter(getContext(), mList);
        mAdapter.setOnItemClickListener(position -> {
            if (mListener != null) {
                mListener.onGroup(mList.get(position));
            }
        });

        mRVList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRVList.setAdapter(mAdapter);

        updateLogoutBtnVisibility();
    }

    /**
     * UI
     */
    void updateLogoutBtnVisibility() {
        mLogoutLayout.setVisibility(mLogoutBtnVisible ? View.VISIBLE : View.GONE);
        mUserToggleIc.setImageDrawable(ResourcesCompat.getDrawable(getResources(), mLogoutBtnVisible ? R.drawable.ic_drop_up : R.drawable.ic_drop_down, null));
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.profile_layout)
    void onProfile() {
        mLogoutBtnVisible = !mLogoutBtnVisible;
        updateLogoutBtnVisibility();
    }

    @OnClick(R.id.create_group_layout)
    void onCreateGroup() {

    }

    @OnClick(R.id.privacy_policy_layout)
    void onPrivacyPolicy() {

    }

    @OnClick(R.id.logout_btn)
    void onLogout() {

    }
}
