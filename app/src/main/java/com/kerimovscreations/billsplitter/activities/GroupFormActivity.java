package com.kerimovscreations.billsplitter.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.InviteMemberBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.utils.BaseActivity;

import java.util.ArrayList;
import java.util.Objects;

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
    @BindView(R.id.currency)
    TextView mCurrency;
    @BindView(R.id.action_btn)
    ImageView mActionBtn;

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
            mGroup = new Group("", new ArrayList<>());
            mActionBtn.setVisibility(View.VISIBLE);
        } else {
            mActionBtn.setVisibility(View.GONE);
        }

        // Shared people list

        // fake last item
        mGroup.getMembers().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mGroup.getMembers(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                if (mGroup.getMembers().get(position).getId() > 0) {

                } else {
                    InviteMemberBottomSheetDialogFragment fragment = InviteMemberBottomSheetDialogFragment.getInstance();
                    fragment.setClickListener(new InviteMemberBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSend(String email) {
                            // TODO: Send invitation
                            mGroup.getMembers().add(mGroup.getMembers().size() - 1, new Person(1, email, email));
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
                }
            }

            @Override
            public void onDelete(int position) {
                mGroup.getMembers().remove(position);
                mAdapter.notifyDataSetChanged();
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

    @OnClick(R.id.currency_layout)
    void onCurrency(View view) {
        promptCurrencyPickerDialog();
    }

    @OnClick(R.id.action_btn)
    void onAction(View view) {
        if (mGroup == null) {
            // TODO: save content
        } else {
            promptDeleteDialog();
        }
    }

    /**
     * UI
     */

    void promptDeleteDialog() {
        // TODO: Complete method
    }

    void promptCurrencyPickerDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.ic_select_currency);
        builderSingle.setTitle(getString(R.string.select_currency));

        final ArrayAdapter<Currency> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item);
        arrayAdapter.addAll(Objects.requireNonNull(Currency.loadArrayFromAsset(getContext(), "currency.json")));

        builderSingle.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, position) -> {
            Currency code = arrayAdapter.getItem(position);

            if (code != null)
                mCurrency.setText(code.toString());
        });
        builderSingle.show();
    }
}