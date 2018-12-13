package com.kerimovscreations.billsplitter.activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.recyclerView.TransactionListRVAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.BalanceBottomSheedDialogFragment;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.Transaction;
import com.kerimovscreations.billsplitter.utils.BaseActivity;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;

public class TransactionListActivity extends BaseActivity {

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.rvTransactions)
    RecyclerView mRVTransactions;

    ArrayList<Transaction> mList = new ArrayList<>();

    private TransactionListRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_transaction_list);
    }

    @Override
    public void initVars() {
        super.initVars();

        mAdapter = new TransactionListRVAdapter(getContext(), mList);

        Person person = new Person(Objects.requireNonNull(getRealm().where(LocalProfile.class).findFirst()));
        RealmResults<Currency> currencies = getRealm().where(Currency.class).findAll();

        mAdapter.setOnItemClickListener(position -> {
            BalanceBottomSheedDialogFragment fragment = BalanceBottomSheedDialogFragment.getInstance(mList.get(position));
            fragment.setClickListener(amount -> {
                // TODO: api integration
                Log.e("TRANSACTION", String.valueOf(amount));
            });

            fragment.show(getSupportFragmentManager(), "PRICE_TAG");
        });

        mRVTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        mRVTransactions.setAdapter(mAdapter);
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.back_ic)
    void onBack(View view) {
        finish();
    }
}
