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
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalGroupMember;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.Person;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

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
    LocalProfile mLocalProfile;
    Realm mRealm;

    boolean mLogoutBtnVisible = false;

    private OnClickListener mListener;

    private LocalGroup mSelectedGroup;

    public interface OnClickListener {
        void onGroup(Group group);

        void onCreateGroup();

        void editProfile();

        void onLogout();
    }

    public void setClickListener(OnClickListener listener) {
        mListener = listener;
    }

    public static MenuBottomSheetDialogFragment getInstance(LocalGroup group) {
        MenuBottomSheetDialogFragment fragment = new MenuBottomSheetDialogFragment();
        fragment.mSelectedGroup = group;
        return fragment;
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
        mRealm = GlobalApplication.getRealm();

        mLocalProfile = mRealm.where(LocalProfile.class).findFirst();

        setupProfileData();

        RealmResults<LocalGroup> groups = mRealm.where(LocalGroup.class).findAll();
        RealmResults<LocalGroupMember> members;
        Group tempGroup;

        for (LocalGroup localGroup : groups) {
            tempGroup = new Group(localGroup);

            members = mRealm.where(LocalGroupMember.class).equalTo("groupId", localGroup.getId()).findAll();
            for (LocalGroupMember localGroupMember : members) {
                tempGroup.getGroupUsers().add(localGroupMember.getMember());
            }

            mList.add(tempGroup);
        }

        mAdapter = new GroupListRVAdapter(getContext(), mList);
        mAdapter.setOnItemClickListener(position -> {
            if (mListener != null) {
                mListener.onGroup(mList.get(position));
            }
        });

        mRVList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRVList.setAdapter(mAdapter);

        if(mSelectedGroup != null){
            for (int i = 0; i < mList.size(); i++) {
                if(mList.get(i).getId() == mSelectedGroup.getId()) {
                    mAdapter.setSelectedIndex(i);
                    break;
                }
            }
        }

        updateLogoutBtnVisibility();
    }

    private void setupProfileData() {
        Picasso.get().load(mLocalProfile.getPicture())
                .resize(200, 200)
                .centerCrop()
                .into(mAvatar);

        mUserName.setText(mLocalProfile.getFullName());
        mUserEmail.setText(mLocalProfile.getEmail());
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

    @OnClick(R.id.edit_btn)
    void onEditProfile() {
        if (mListener != null) {
            mListener.editProfile();
            dismiss();
        }
    }

    @OnClick(R.id.create_group_layout)
    void onCreateGroup() {
        if (mListener != null) {
            mListener.onCreateGroup();
            dismiss();
        }
    }

    @OnClick(R.id.privacy_policy_layout)
    void onPrivacyPolicy() {

    }

    @OnClick(R.id.logout_btn)
    void onLogout() {
        if (mListener != null) {
            mListener.onLogout();
            dismiss();
        }
    }
}
