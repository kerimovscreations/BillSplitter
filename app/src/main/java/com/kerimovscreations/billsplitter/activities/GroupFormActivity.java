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
import com.kerimovscreations.billsplitter.adapters.GroupMembersListRVAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.InviteMemberBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.GroupMember;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalGroupMember;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupFormActivity extends BaseActivity {

    private final String TAG = "GROUP_FORM";

    public static final String INTENT_ITEM = "ITEM";
    public static final String INTENT_ITEM_ID = "ITEM_ID";

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

    GroupMembersListRVAdapter mAdapter;

    private Currency mSelectedCurrency;
    AppApiService mApiService;

    Call<SimpleDataWrapper> mCreateGroupCall;
    Call<SimpleDataWrapper> mUpdateGroupCall;

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

        int groupId = getIntent().getIntExtra(INTENT_ITEM_ID, 0);

        mCurrencies = new ArrayList<>();

        mCurrencies.addAll(GlobalApplication.getRealm().where(Currency.class).findAll());

        // No need
        mSwipeRefreshLayout.setEnabled(false);

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        setupData(groupId);
    }

    void setupData(int groupId) {
        if (groupId == 0) {
            mGroup = new Group();
            mSelectedCurrency = mCurrencies.get(0);
            mCurrency.setText(mSelectedCurrency.getName());
        } else {
            LocalGroup localGroup = GlobalApplication.getRealm().where(LocalGroup.class).equalTo("id", groupId).findFirst();

            if (localGroup != null)
                mGroup = new Group(localGroup);
            else
                return;

            RealmResults<LocalGroupMember> members = GlobalApplication.getRealm()
                    .where(LocalGroupMember.class)
                    .equalTo("groupId", mGroup.getId())
                    .findAll();
            for (LocalGroupMember localGroupMember : members) {
                mGroup.getGroupUsers().add(localGroupMember.getMember());
            }

            mTitle.setText(mGroup.getTitle());
            mSelectedCurrency = mGroup.getCurrency();
            mCurrency.setText(mSelectedCurrency.getName());
        }

        // Shared people list

        // fake last item
        mGroup.getGroupUsers().add(new GroupMember(-1, "Placeholder"));
        mAdapter = new GroupMembersListRVAdapter(getContext(), mGroup.getGroupUsers(), true);
        mAdapter.setOnItemClickListener(new GroupMembersListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                if (mGroup.getGroupUsers().get(position).getId() > 0) {

                } else {
                    InviteMemberBottomSheetDialogFragment fragment = InviteMemberBottomSheetDialogFragment.getInstance();
                    fragment.setClickListener(new InviteMemberBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSend(String email) {
                            for (GroupMember person : mGroup.getGroupUsers()) {
                                if (email.equals(person.getEmail())) {
                                    return;
                                }
                            }

                            mGroup.getGroupUsers().add(mGroup.getGroupUsers().size() - 1, new GroupMember(1, email, email));

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
        if (!isFormValid()) {
            return;
        }

        if (mGroup.getId() == 0) {
            submitForm();
        } else {
            updateGroup();
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

        mGroup.setTitle(mTitle.getText().toString());
        mGroup.setCurrency(mSelectedCurrency);

        mCreateGroupCall = mApiService.createGroup(Auth.getInstance().getToken(getContext()), data);

        mCreateGroupCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        GlobalApplication.getRealm().executeTransaction(realm -> {
                            LocalGroup localGroup = new LocalGroup(mGroup);
                            realm.copyToRealmOrUpdate(localGroup);

                            realm.where(LocalGroupMember.class).equalTo("groupId", localGroup.getId()).findAll().deleteAllFromRealm();

                            for (int i = 0; i < mGroup.getGroupUsers().size() - 1; i++) {
                                realm.copyToRealm(new LocalGroupMember(mGroup.getGroupUsers().get(i), localGroup.getId()));
                            }
                        });

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

    private void updateGroup() {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("CurrencyId", String.valueOf(mSelectedCurrency.getId()));
        data.put("Name", mTitle.getText().toString());

        for (int i = 0; i < mGroup.getGroupUsers().size() - 1; i++) {
            data.put("members[" + i + "]", mGroup.getGroupUsers().get(i).getEmail());
        }

        mGroup.setTitle(mTitle.getText().toString());
        mGroup.setCurrency(mSelectedCurrency);

        mUpdateGroupCall = mApiService.updateGroup(Auth.getInstance().getToken(getContext()), mGroup.getId(), data);

        mUpdateGroupCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        GlobalApplication.getRealm().executeTransaction(realm -> {
                            LocalGroup localGroup = new LocalGroup(mGroup);
                            realm.copyToRealmOrUpdate(localGroup);

                            realm.where(LocalGroupMember.class).equalTo("groupId", localGroup.getId()).findAll().deleteAllFromRealm();

                            for (int i = 0; i < mGroup.getGroupUsers().size() - 1; i++) {
                                realm.copyToRealm(new LocalGroupMember(mGroup.getGroupUsers().get(i), localGroup.getId()));
                            }
                        });
                        Toast.makeText(getContext(), R.string.successful_update_group, Toast.LENGTH_SHORT).show();
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