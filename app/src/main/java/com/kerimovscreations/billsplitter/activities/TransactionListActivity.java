package com.kerimovscreations.billsplitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.recyclerView.TransactionListRVAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.BalanceBottomSheedDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.Transaction;
import com.kerimovscreations.billsplitter.models.TransactionsBundle;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.UserDataWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends BaseActivity {

    private final String TAG = "TRANS_LIST";

    public static final String TYPE = "TYPE";
    public static final String DATA = "DATA";
    public static final String GROUP_ID = "GROUP_ID";
    public static final int TYPE_INCOME = 1, TYPE_OUTCOME = 2;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.rvTransactions)
    RecyclerView mRVTransactions;

    ArrayList<Transaction> mList = new ArrayList<>();

    private int mType;
    private TransactionListRVAdapter mAdapter;
    private int mGroupId;
    AppApiService mApiService;

    ArrayList<Call> mCalls = new ArrayList<>();

    TransactionsBundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_transaction_list);
    }

    @Override
    public void onBackPressed() {

        for (Call call : mCalls) {
            if (call != null && !call.isExecuted()) {
                showProgress(false);
                call.cancel();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (Call call : mCalls) {
            if (call != null && !call.isExecuted())
                call.cancel();
        }
    }

    @Override
    public void initVars() {
        super.initVars();

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        mGroupId = getIntent().getIntExtra(GROUP_ID, 0);

        mType = getIntent().getIntExtra(TYPE, TYPE_INCOME);

        mBundle = (TransactionsBundle) getIntent().getSerializableExtra(DATA);

        if (mType == TYPE_INCOME) {
            for (int i = 0; i < mBundle.getTheyOwe().size(); i++) {
                if (mBundle.getTheyOwe().get(i).getBalance() > 0)
                    mList.add(mBundle.getTheyOwe().get(i));
            }
        } else {
            for (int i = 0; i < mBundle.getiOwe().size(); i++) {
                if (mBundle.getiOwe().get(i).getBalance() > 0)
                    mList.add(mBundle.getiOwe().get(i));
            }
        }

        mAdapter = new TransactionListRVAdapter(getContext(), mList, mType);

        mAdapter.setOnItemClickListener(position -> {
            BalanceBottomSheedDialogFragment fragment = BalanceBottomSheedDialogFragment.getInstance(mList.get(position), mType);
            fragment.setClickListener(amount -> {
                addTransaction(position, amount);
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

    /**
     * UI
     */

    void showProgress(boolean show) {
        mProgressLayout.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    /**
     * HTTP
     */

    private void addTransaction(int position, float amount) {
        showProgress(true);

        Call<SimpleDataWrapper> call = mApiService.addTransaction(Auth.getInstance().getToken(getContext()),
                mType == TransactionListActivity.TYPE_INCOME ? mBundle.getTheyOwe().get(position).getFrom().getId() :
                        mBundle.getiOwe().get(position).getTo().getId(),
                amount,
                mGroupId);

        mCalls.add(call);

        call.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.successful_submitted_transaction, Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> showProgress(false));

                    if (response.errorBody() != null) {
                        try {
                            String errorString = response.errorBody().string();
                            Log.d(TAG, errorString);

                            runOnUiThread(() -> Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                    }
                    Log.d(TAG, response.raw().toString());
                    Log.e(TAG, "onResponse: Request Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<SimpleDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    Log.e(TAG, "onFailure: Request Failed");

                    runOnUiThread(() -> {
                        showProgress(false);
                        Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
