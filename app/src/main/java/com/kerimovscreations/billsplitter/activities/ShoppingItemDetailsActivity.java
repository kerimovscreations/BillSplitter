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
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.spinner.CategorySpinnerAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.DeleteItemBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupMemberPickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.PricePickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalGroupMember;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;

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
    private final String TAG = "SHOPPING_FORM";

    public static final String INTENT_ITEM = "ITEM";
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

    private String mSelectedDate;
    private Calendar myCalendar;

    boolean mShouldOpenQRSearch = false;
    boolean mShouldOpenQRRead = false;

    private Group mGroup;
    private Category mSelectedCategory;
    private Call<SimpleDataWrapper> mCreateItemCall;

    AppApiService mApiService;

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            mSelectedDate = sdf.format(myCalendar.getTime());
            mDate.setText(mSelectedDate);
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

        if (mShouldOpenQRSearch) {
            mShouldOpenQRSearch = false;
            onQRScan();
        } else if (mShouldOpenQRRead) {
            mShouldOpenQRRead = false;
            onQRCodeLayout();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (mCreateGroupCall != null && !mCreateGroupCall.isExecuted()) {
//            showProgress(false);
//            mCreateGroupCall.cancel();
//            mCreateGroupCall = null;
//            return;
//        }
//
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (mCreateGroupCall != null && !mCreateGroupCall.isExecuted())
//            mCreateGroupCall.cancel();
//    }

    @Override
    public void initVars() {
        super.initVars();

        mShoppingItem = (ShoppingItem) getIntent().getSerializableExtra(INTENT_ITEM);
        int groupId = getIntent().getIntExtra(INTENT_GROUP_ID, 0);

        mGroup = new Group(Objects.requireNonNull(GlobalApplication.getRealm().where(LocalGroup.class).equalTo("id", groupId).findFirst()));

        RealmResults<LocalGroupMember> members = GlobalApplication.getRealm()
                .where(LocalGroupMember.class)
                .equalTo("groupId", mGroup.getId())
                .findAll();
        for (LocalGroupMember localGroupMember : members) {
            mGroup.getGroupUsers().add(localGroupMember.getPerson());
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
            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
            mQRCodeLayout.setVisibility(View.VISIBLE);
            mQRSearchBtn.setVisibility(View.GONE);
        }

        // Date

        Date myDate = new Date();

        mDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(myDate));

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
            mSelectedCategory = mShoppingItem.getProduct().getCategory();

            for (int i = 0; i < categories.size(); i++) {
                if (mSelectedCategory.getId() == categories.get(i).getId()) {
                    mCategorySpinner.setSelection(i);
                    break;
                }
            }
        } else {
            mSelectedCategory = categories.get(0);
        }

        // Price

        mPrice.setText(String.format(Locale.getDefault(), "%.2f %s", mShoppingItem.getPrice(), mGroup.getCurrency().getName()));

        // Buyer

        mBuyer.setText(mShoppingItem.getBuyer() == null ? getString(R.string.select_buyer) : mShoppingItem.getBuyer().getFullName());

        // Shared people list

        // fake user
        mShoppingItem.getSharedMembers().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedMembers(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                if (mShoppingItem.getSharedMembers().get(position).getId() > 0) {

                } else {
                    ArrayList<Person> people = new ArrayList<>();

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
                        public void onSelect(Person person) {
                            mShoppingItem.getSharedMembers().add(mShoppingItem.getSharedMembers().size() - 1, person);

                            if (mGroup.getGroupUsers().size() < mShoppingItem.getSharedMembers().size())
                                mShoppingItem.getSharedMembers().remove(mShoppingItem.getSharedMembers().size() - 1);

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
                    mShoppingItem.getSharedMembers().add(new Person(-1, "Placeholder"));

                mShoppingItem.getSharedMembers().remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelect(int position) {
                if(mShoppingItem.getSharedMembers().get(position).getId() == -1){
                    GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance(mGroup.getGroupUsers());
                    fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                        @Override
                        public void onSelect(Person person) {
                            mShoppingItem.getSharedMembers().remove(position);
                            mShoppingItem.getSharedMembers().add(position, person);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onRemove() {
                            mShoppingItem.getSharedMembers().remove(position);
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
        if (mShoppingItem.getId() == 0) {
            if (!isFormValid()) {
                return;
            }

            createItem();
        } else {
            promptDeleteDialog();
        }
    }

    @OnClick(R.id.date_layout)
    void onDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.price_layout)
    void onPrice(View view) {
        PricePickerBottomSheetDialogFragment fragment = PricePickerBottomSheetDialogFragment.getInstance();
        fragment.setClickListener(new PricePickerBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onSubmit(Float price) {
                mPrice.setText(String.format(Locale.getDefault(), "%.2f %s", price, mGroup.getCurrency().getName()));
            }
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
            public void onSelect(Person person) {
                mShoppingItem.setBuyer(person);
                mBuyer.setText(person.getFullName());
            }

            @Override
            public void onRemove() {
                mShoppingItem.setBuyer(null);
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
        fragment.setClickListener(new DeleteItemBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onDelete() {
                // TODO: API Integration
            }
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
        data.put("categoryId", String.valueOf(mSelectedCategory.getId()));
        data.put("barCode", "");
        data.put("price", mPrice.getText().toString().split(" ")[0]);
        data.put("paidById", mShoppingItem.getBuyer() == null ? getString(R.string.select_buyer) : String.valueOf(mShoppingItem.getBuyer().getId()));
        data.put("date", mDate.getText().toString());

        for (int i = 0; i < mShoppingItem.getSharedMembers().size() - 1; i++) {
            data.put("shares[" + i + "]", String.valueOf(mShoppingItem.getSharedMembers().get(i).getId()));
        }

        mCreateItemCall = mApiService.createPurchase(Auth.getInstance().getToken(getContext()), data);

        mCreateItemCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.successful_create_shopping_item, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_BAR_CODE_READ) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mQRCode.setText(data.getStringExtra(BarScannerActivity.BAR_CODE));
//                    Toast.makeText(getContext(), data.getStringExtra(BarScannerActivity.BAR_CODE), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_BAR_CODE_SEARCH) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
//                    Toast.makeText(getContext(), data.getStringExtra(BarScannerActivity.BAR_CODE), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
