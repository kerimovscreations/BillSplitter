package com.kerimovscreations.billsplitter.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.recyclerView.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.spinner.CategorySpinnerAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.DeleteItemBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupMemberPickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.PricePickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.GroupMember;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalGroupMember;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemDataWrapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingItemDetailsActivity extends BaseActivity {

    public static final String INTENT_GROUP_ID = "GROUP_ID";
    public static final String INTENT_ITEM_ID = "ITEM_ID";
    private final String TAG = "SHOPPING_FORM";

    public static final int REQUEST_BAR_CODE_READ = 4;
    public static final int REQUEST_BAR_CODE_SEARCH = 5;
    public static final int PERMISSION_REQUEST_CAMERA = 6;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.layout_progress)
    View mProgressLayout;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.rvSharedPeople)
    RecyclerView mRVSharedPeople;

    @BindView(R.id.group_spinner)
    Spinner mGroupSpinner;

    @BindView(R.id.title)
    EditText mTitle;

    @BindView(R.id.action_btn)
    ImageView mActionBtn;

    @BindView(R.id.category_spinner)
    Spinner mCategorySpinner;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.price)
    TextView mPrice;

    @BindView(R.id.buyer)
    TextView mBuyer;

    @BindView(R.id.qr_code_action_btn)
    ImageView mQRCodeActionBtn;

    @BindView(R.id.qr_code)
    TextView mQRCode;

    @BindView(R.id.qr_code_layout)
    View mQRCodeLayout;

    @BindView(R.id.qr_scan_btn)
    View mQRSearchBtn;

    ShoppingItem mShoppingItem;

    SharedPeopleListRVAdapter mAdapter;

    private Calendar myCalendar;

    boolean mShouldOpenQRSearch = false;
    boolean mShouldOpenQRRead = false;

    private Group mGroup;
    private Category mSelectedCategory;
    private Call<ShoppingItemDataWrapper> mCreateItemCall;
    private Call<ShoppingItemDataWrapper> mUpdateItemCall;
    private Call<ShoppingItemDataWrapper> mSearchItemCall;

    AppApiService mApiService;

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            String selectedDate = sdf.format(myCalendar.getTime());
            mShoppingItem.setDate(selectedDate);
            updateDateText();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_shopping_item_details);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mResultBarCode != null && !mResultBarCode.isEmpty()) {
            GlobalApplication.getRealm().executeTransaction(realm -> mShoppingItem.getProduct().setBarCode(mResultBarCode));
            updateBarCodeText();
            mResultBarCode = "";
        }

        if (mShouldOpenQRSearch) {
            mShouldOpenQRSearch = false;
            onQRScan();
        } else if (mShouldOpenQRRead) {
            mShouldOpenQRRead = false;
            onQRCodeLayout();
        }
    }

    @Override
    public void onBackPressed() {
        if (mCreateItemCall != null && !mCreateItemCall.isExecuted()) {
            showProgress(false);
            mCreateItemCall.cancel();
            mCreateItemCall = null;
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mCreateItemCall != null && !mCreateItemCall.isExecuted())
            mCreateItemCall.cancel();

        if (mShoppingItem.getId() > 0) {
            GlobalApplication.getRealm().executeTransaction(realm -> {
                mShoppingItem.getSharedMembers().where().equalTo("id", -1).findAll().deleteAllFromRealm();
            });
        }

        super.onDestroy();
    }

    @Override
    public void initVars() {
        super.initVars();

        int itemId = getIntent().getIntExtra(INTENT_ITEM_ID, 0);

        if (itemId != 0)
            mShoppingItem = new ShoppingItem(Objects.requireNonNull(GlobalApplication
                    .getRealm()
                    .where(ShoppingItem.class)
                    .equalTo("id", itemId)
                    .findFirst()));

        int groupId = getIntent().getIntExtra(INTENT_GROUP_ID, 0);

        mGroup = new Group(Objects.requireNonNull(GlobalApplication
                .getRealm()
                .where(LocalGroup.class)
                .equalTo("id", groupId)
                .findFirst()));

        RealmResults<LocalGroupMember> members = GlobalApplication.getRealm()
                .where(LocalGroupMember.class)
                .equalTo("groupId", mGroup.getId())
                .findAll();
        for (LocalGroupMember localGroupMember : members) {
            mGroup.getGroupUsers().add(localGroupMember.getMember());
        }

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

        myCalendar = Calendar.getInstance();

        setupData();
    }

    void setupData() {
        if (mShoppingItem == null) {
            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_black_24dp, null));
            mActionBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            mShoppingItem = new ShoppingItem();
            mQRCodeLayout.setVisibility(View.GONE);
            mQRSearchBtn.setVisibility(View.VISIBLE);
        } else {
            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_black_24dp, null));
            mActionBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            // TODO: Delete btn
//            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
            mQRCodeLayout.setVisibility(View.VISIBLE);
            mQRSearchBtn.setVisibility(View.GONE);
        }

        // title

        if (mShoppingItem.getProduct() != null)
            mTitle.setText(mShoppingItem.getProduct().getName());

        // Date

        if (mShoppingItem.getDate() == null || mShoppingItem.getDate().isEmpty()) {
            Date myDate = new Date();

            GlobalApplication.getRealm().executeTransaction(realm -> mShoppingItem.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(myDate)));
        } else {
            GlobalApplication.getRealm().executeTransaction(realm -> mShoppingItem.setDate(mShoppingItem.getDate().split("T")[0]));
        }

        updateDateText();

        // Bar code

        updateBarCodeText();

        // Shopping group

        List<Group> shoppingGroups = new ArrayList<>();

        RealmResults<LocalGroup> localGroups = GlobalApplication.getRealm().where(LocalGroup.class).findAll();

        for (LocalGroup localGroup : localGroups) {
            shoppingGroups.add(new Group(localGroup));
        }

        ArrayAdapter<Group> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_shopping_group_text, shoppingGroups);

        dataAdapter.addAll();
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGroupSpinner.setAdapter(dataAdapter);

        mGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mGroup = shoppingGroups.get(i);

                RealmResults<LocalGroupMember> members = GlobalApplication.getRealm()
                        .where(LocalGroupMember.class)
                        .equalTo("groupId", mGroup.getId())
                        .findAll();
                for (LocalGroupMember localGroupMember : members) {
                    mGroup.getGroupUsers().add(localGroupMember.getMember());
                }

                updatePriceText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        for (int i = 0; i < shoppingGroups.size(); i++) {
            if (mGroup.getId() == shoppingGroups.get(i).getId()) {
                mGroupSpinner.setSelection(i);
                break;
            }
        }

        mGroupSpinner.setEnabled(false);

        // Category spinner

        RealmResults<Category> realmResults = GlobalApplication.getRealm().where(Category.class).findAll();
        List<Category> categories = new ArrayList<>(realmResults);

        CategorySpinnerAdapter categoryDataAdapter = new CategorySpinnerAdapter(getActivity(),
                R.layout.spinner_category_text,
                R.id.title,
                categories);

        mCategorySpinner.setAdapter(categoryDataAdapter);

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedCategory = categories.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (mShoppingItem.getProduct() != null) {
            int selectedCategoryId = mShoppingItem.getProduct().getCategory().getId();
//            mSelectedCategory = mShoppingItem.getProduct().getCategory();

            for (int i = 0; i < categories.size(); i++) {
                if (selectedCategoryId == categories.get(i).getId()) {
                    mCategorySpinner.setSelection(i);
                    break;
                }
            }
        } else {
            mSelectedCategory = categories.get(0);
        }

        // Price

        updatePriceText();

        // Buyer

        mBuyer.setText(mShoppingItem.getBuyer() == null ? getString(R.string.select_buyer) : mShoppingItem.getBuyer().getFullName());

        // Shared people list

        // fake user
        if (mShoppingItem.getSharedMembers().size() < mGroup.getGroupUsers().size()) {
            GlobalApplication.getRealm().executeTransaction(realm ->
                    mShoppingItem.getSharedMembers().add(new GroupMember(-1, "Placeholder")));
        }

        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedMembers(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                if (mShoppingItem.getSharedMembers().get(position).getId() > 0) {

                } else {
                    ArrayList<GroupMember> people = new ArrayList<>();

                    boolean hasFoundFlag;

                    for (int i = 0; i < mGroup.getGroupUsers().size(); i++) {
                        hasFoundFlag = false;

                        for (int j = 0; j < mShoppingItem.getSharedMembers().size() - 1; j++) {
                            if (mGroup.getGroupUsers().get(i).getId() == mShoppingItem.getSharedMembers().get(j).getId()) {
                                hasFoundFlag = true;
                                break;
                            }
                        }

                        if (!hasFoundFlag)
                            people.add(mGroup.getGroupUsers().get(i));
                    }

                    GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance(people);
                    fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSelect(GroupMember person) {
                            GlobalApplication.getRealm().executeTransaction(realm ->
                                    mShoppingItem.getSharedMembers().add(mShoppingItem.getSharedMembers().size() - 1, person));

                            if (mGroup.getGroupUsers().size() < mShoppingItem.getSharedMembers().size())
                                GlobalApplication.getRealm().executeTransaction(realm ->
                                        mShoppingItem.getSharedMembers().remove(mShoppingItem.getSharedMembers().size() - 1));

                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onRemove() {
                            // not used
                        }
                    });

                    fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
                }
            }

            @Override
            public void onDelete(int position) {
                if (mGroup.getGroupUsers().size() == mShoppingItem.getSharedMembers().size())
                    GlobalApplication.getRealm().executeTransaction(realm -> {
                        mShoppingItem.getSharedMembers().add(new GroupMember(-1, "Placeholder"));
                        mShoppingItem.getSharedMembers().remove(position);
                    });
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelect(int position) {
                if (mShoppingItem.getSharedMembers().get(position).getId() == -1) {
                    GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance(mGroup.getGroupUsers());
                    fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSelect(GroupMember person) {
                            GlobalApplication.getRealm().executeTransaction(realm -> {
                                mShoppingItem.getSharedMembers().remove(position);
                                mShoppingItem.getSharedMembers().add(position, person);
                            });

                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onRemove() {
                            GlobalApplication.getRealm().executeTransaction(realm -> mShoppingItem.getSharedMembers().remove(position));
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                    fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
                }
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

    @OnClick(R.id.action_btn)
    void onAction(View view) {
        if (!isFormValid()) {
            return;
        }

        if (mShoppingItem.getId() == 0) {
            createItem();
        } else {
            // TODO: Delete option
//            promptDeleteDialog();


            updateItem();
        }
    }

    @OnClick(R.id.date_layout)
    void onDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.price_layout)
    void onPrice(View view) {
        PricePickerBottomSheetDialogFragment fragment = PricePickerBottomSheetDialogFragment.getInstance(mShoppingItem.getPrice());
        fragment.setClickListener(price -> {
            GlobalApplication.getRealm().executeTransaction(realm -> {
                mShoppingItem.setPrice(price);
            });
            updatePriceText();
        });

        fragment.show(getSupportFragmentManager(), "PRICE_TAG");
    }

    @OnClick(R.id.qr_scan_btn)
    void onQRScan() {
        if (!hasCameraPermission()) {
            mShouldOpenQRSearch = true;
            requestCameraAccess();
            return;
        }

        Intent intent = new Intent(getContext(), BarScannerActivity.class);
        startActivityForResult(intent, REQUEST_BAR_CODE_SEARCH);
    }

    @OnClick(R.id.buyer_layout)
    void onBuyer(View view) {
        GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance(mGroup.getGroupUsers());
        fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onSelect(GroupMember member) {
                GlobalApplication.getRealm().executeTransaction(realm -> mShoppingItem.setBuyer(new Person(member)));
                mBuyer.setText(member.getFullName());
            }

            @Override
            public void onRemove() {
                GlobalApplication.getRealm().executeTransaction(realm -> {
                    mShoppingItem.setBuyer(null);
                });
                mBuyer.setText("");
            }
        });

        fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
    }

    @OnClick(R.id.qr_code_layout)
    void onQRCodeLayout() {
        if (!hasCameraPermission()) {
            mShouldOpenQRRead = true;
            requestCameraAccess();
            return;
        }

        Intent intent = new Intent(getContext(), BarScannerActivity.class);
        startActivityForResult(intent, REQUEST_BAR_CODE_READ);
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

    void updatePriceText() {
        mPrice.setText(String.format(Locale.getDefault(), "%.2f %s", mShoppingItem.getPrice(), mGroup.getCurrency().getName()));
    }

    void updateDateText() {
        mDate.setText(mShoppingItem.getDate());
    }

    void updateBarCodeText() {
        if (mShoppingItem.getProduct() == null)
            return;

        if (mShoppingItem.getProduct().getBarCode() != null && !mShoppingItem.getProduct().getBarCode().isEmpty()) {
//            mQRCode.setText(getString(R.string.provided));
            mQRCode.setText(mShoppingItem.getProduct().getBarCode());
            mQRCodeActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_black_24dp, null));
        } else {
            mQRCode.setText(getString(R.string.not_provided));
            mQRCodeActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_plus, null));
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
        DeleteItemBottomSheetDialogFragment fragment = DeleteItemBottomSheetDialogFragment.getInstance();
        fragment.setClickListener(() -> {
            // TODO: API Integration
        });

        fragment.show(getSupportFragmentManager(), "MORE_TAG");
    }

    boolean hasCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    void requestCameraAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        }
    }

    /**
     * HTTP
     */

    private void createItem() {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("name", mTitle.getText().toString());
        data.put("groupId", String.valueOf(mGroup.getId()));
        data.put("categoryId", String.valueOf(mSelectedCategory.getId()));
        data.put("barCode", "");
        data.put("price", String.valueOf(mShoppingItem.getPrice()));
        data.put("date", mShoppingItem.getDate());

        if (mShoppingItem.getBuyer() != null) {
            data.put("paidById", String.valueOf(mShoppingItem.getBuyer().getId()));
        }

        for (int i = 0; i < mShoppingItem.getSharedMembers().size(); i++) {
            if (mShoppingItem.getSharedMembers().get(i).getId() != -1)
                data.put("shares[" + i + "]", String.valueOf(mShoppingItem.getSharedMembers().get(i).getId()));
        }

        mCreateItemCall = mApiService.createShoppingItem(Auth.getInstance().getToken(getContext()), data);

        mCreateItemCall.enqueue(new Callback<ShoppingItemDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Response<ShoppingItemDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        GlobalApplication.getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(response.body().getShoppingItem()));
                        Toast.makeText(getContext(), R.string.successful_create_shopping_item, Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
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
            public void onFailure(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Throwable t) {
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

    private void updateItem() {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("name", mTitle.getText().toString());
        data.put("groupId", String.valueOf(mGroup.getId()));
        data.put("categoryId", String.valueOf(mSelectedCategory.getId()));
        data.put("barCode", mShoppingItem.getProduct().getBarCode());
        data.put("price", String.valueOf(mShoppingItem.getPrice()));
        data.put("date", mShoppingItem.getDate());
        data.put("isComplete", String.valueOf(mShoppingItem.isComplete()));

        if (mShoppingItem.getBuyer() != null) {
            data.put("paidById", String.valueOf(mShoppingItem.getBuyer().getId()));
        }

        for (int i = 0; i < mShoppingItem.getSharedMembers().size(); i++) {
            if (mShoppingItem.getSharedMembers().get(i).getId() != -1)
                data.put("shares[" + i + "]", String.valueOf(mShoppingItem.getSharedMembers().get(i).getId()));
        }

        mUpdateItemCall = mApiService.updateShoppingItem(Auth.getInstance().getToken(getContext()), mShoppingItem.getId(), data);

        mUpdateItemCall.enqueue(new Callback<ShoppingItemDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Response<ShoppingItemDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        response.body().getShoppingItem().setGroupId(mGroup.getId());
                        GlobalApplication.getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(response.body().getShoppingItem()));
                        Toast.makeText(getContext(), R.string.successful_update_shopping_item, Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
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
            public void onFailure(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Throwable t) {
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

    private void searchProduct(String barCode) {
        showProgress(true);

        mSearchItemCall = mApiService.searchProduct(Auth.getInstance().getToken(getContext()), mGroup.getId(), barCode);

        mSearchItemCall.enqueue(new Callback<ShoppingItemDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Response<ShoppingItemDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        mShoppingItem = response.body().getShoppingItem();
                        mShoppingItem.setId(0); // not to be accessible in onDestroy delegate
                        setupData();
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
            public void onFailure(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Throwable t) {
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

    /**
     * Permission and results
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

    private String mResultBarCode;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BAR_CODE_READ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mResultBarCode = data.getStringExtra(BarScannerActivity.BAR_CODE);
//                    Toast.makeText(getContext(), data.getStringExtra(BarScannerActivity.BAR_CODE), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_BAR_CODE_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    searchProduct(data.getStringExtra(BarScannerActivity.BAR_CODE));
//                    Toast.makeText(getContext(), data.getStringExtra(BarScannerActivity.BAR_CODE), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
