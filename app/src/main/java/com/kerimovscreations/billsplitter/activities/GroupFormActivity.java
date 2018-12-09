package com.kerimovscreations.billsplitter.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.InviteMemberBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFormActivity extends BaseActivity {

    private final String TAG = "GROUP_FORM";

    public static final String INTENT_ITEM = "ITEM";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

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

    private Currency mSelectedCurrency;
    AppApiService mApiService;

    Call<SimpleDataWrapper> mCreateGroupCall;

    private ArrayList<Currency> mCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_group_form);
    }

    @Override
    public void onBackPressed() {
        if (mCreateGroupCall != null && !mCreateGroupCall.isExecuted()) {
            showProgress(false);
            mCreateGroupCall.cancel();
            mCreateGroupCall = null;
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCreateGroupCall != null && !mCreateGroupCall.isExecuted())
            mCreateGroupCall.cancel();
    }

    @Override
    public void initVars() {
        super.initVars();

        mGroup = (Group) getIntent().getSerializableExtra(INTENT_ITEM);
        mCurrencies = new ArrayList<>();

        mCurrencies.addAll(GlobalApplication.getRealm().where(Currency.class).findAll());

        // No need
        mSwipeRefreshLayout.setEnabled(false);

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        setupData();
    }

    void setupData() {
        if (mGroup == null) {
            mGroup = new Group();
            mActionBtn.setVisibility(View.VISIBLE);
            mSelectedCurrency = mCurrencies.get(0);
            mCurrency.setText(mSelectedCurrency.getName());
        } else {
            mActionBtn.setVisibility(View.GONE);
            mSelectedCurrency = mGroup.getCurrency();
            mCurrency.setText(mSelectedCurrency.getName());
        }


        // Shared people list

        // fake last item
        mGroup.getGroupUsers().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mGroup.getGroupUsers(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                if (mGroup.getGroupUsers().get(position).getId() > 0) {

                } else {
                    InviteMemberBottomSheetDialogFragment fragment = InviteMemberBottomSheetDialogFragment.getInstance();
                    fragment.setClickListener(new InviteMemberBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSend(String email) {
                            mGroup.getGroupUsers().add(mGroup.getGroupUsers().size() - 1, new Person(1, email, email));
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
                }
            }

            @Override
            public void onDelete(int position) {
                mGroup.getGroupUsers().remove(position);
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
        if (mGroup.getId() == 0) {
            if (!isFormValid()) {
                return;
            }

            submitForm();
        } else {
            promptDeleteDialog();
        }
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

    boolean isFormValid() {
        if (mTitle.getText().length() == 0) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.prompt_fill_inputs, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.okay, view -> snackbar.dismiss());
            snackbar.show();
            return false;
        }

        return true;
    }

    void promptDeleteDialog() {
        // TODO: Complete method
    }

    void promptCurrencyPickerDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.ic_select_currency);
        builderSingle.setTitle(getString(R.string.select_currency));

        final ArrayAdapter<Currency> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item);
//        arrayAdapter.addAll(Objects.requireNonNull(Currency.loadArrayFromAsset(getContext(), "currency.json")));

        arrayAdapter.addAll(mCurrencies);

        builderSingle.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builderSingle.setAdapter(arrayAdapter, (dialog, position) -> {
            Currency code = arrayAdapter.getItem(position);

            if (code != null) {
                mCurrency.setText(code.toString());
                mSelectedCurrency = code;
            }
        });
        builderSingle.show();
    }

    /**
     * HTTP
     */

    private void submitForm() {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("CurrencyId", String.valueOf(mSelectedCurrency.getId()));
        data.put("Name", mTitle.getText().toString());

        for (int i = 0; i < mGroup.getGroupUsers().size() - 1; i++) {
            data.put("members[" + i + "]", mGroup.getGroupUsers().get(i).getEmail());
        }

        mCreateGroupCall = mApiService.createGroup(Auth.getInstance().getToken(getContext()), data);

        mCreateGroupCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.successful_create_group, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
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