package com.kerimovscreations.billsplitter.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.InviteMemberBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupFormActivity extends BaseActivity {

    public static final String INTENT_ITEM = "ITEM";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rvSharedPeople)
    RecyclerView mRVSharedPeople;
    @BindView(R.id.title)
    EditText mTitle;
    @BindView(R.id.delete_ic)
    ImageView mDeleteIc;

    Group mGroup;

    SharedPeopleListRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_group_form);
    }

    @Override
    public void initVars() {
        super.initVars();

        mGroup = (Group) getIntent().getSerializableExtra(INTENT_ITEM);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

        setupData();
    }

    void setupData() {
        if (mGroup == null) {
            mDeleteIc.setVisibility(View.GONE);
            mGroup = new Group("", new ArrayList<>());
        } else {
            mDeleteIc.setVisibility(View.VISIBLE);
        }

        // Shared people list

        // fake last item
        mGroup.getMembers().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mGroup.getMembers());
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                // TODO: open bottom sheet
                InviteMemberBottomSheetDialogFragment fragment = InviteMemberBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new InviteMemberBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSend(String email) {
                        // TODO: Send invitation
                    }
                });

                fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
            }

            @Override
            public void onSelect(int position) {
                // TODO: open bottom sheet
            }
        });

        mRVSharedPeople.setAdapter(mAdapter);
        mRVSharedPeople.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack(View view) {
        finish();
    }

    @OnClick(R.id.delete_ic)
    void onDelete(View view) {
        promptDeleteDialog();
    }

    /**
     * UI
     */

    void promptDeleteDialog() {
        // TODO: Complete method
    }
}