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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.spinner.CategorySpinnerAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.DeleteItemBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupMemberPickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.PricePickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.utils.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ShoppingItemDetailsActivity extends BaseActivity {

    public static final String INTENT_ITEM = "ITEM";
    public static final int REQUEST_BAR_CODE_READ = 4;
    public static final int REQUEST_BAR_CODE_SEARCH = 5;
    public static final int PERMISSION_REQUEST_CAMERA = 6;

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

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

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
        } else if(mShouldOpenQRRead) {
            mShouldOpenQRRead = false;
            onQRCodeLayout();
        }
    }

    @Override
    public void initVars() {
        super.initVars();

        mShoppingItem = (ShoppingItem) getIntent().getSerializableExtra(INTENT_ITEM);

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

        // Shopping group

        List<String> shoppingGroups = new ArrayList<>();
        shoppingGroups.add("Home shopping list");
        shoppingGroups.add("Office shopping list");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_shopping_group_text, shoppingGroups);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGroupSpinner.setAdapter(dataAdapter);

        // Category spinner

        List<Category> categories = new ArrayList<>();

        categories.add(new Category("Grocery", "#FF7675"));
        categories.add(new Category("Meat", "#5E77FF"));
        categories.add(new Category("Cereals", "#74B9FF"));
        categories.add(new Category("Electronics", "#A29BFE"));
        categories.add(new Category("Home items", "#3AD29F"));
        categories.add(new Category("Pastry", "#81ECEC"));

        CategorySpinnerAdapter categoryDataAdapter = new CategorySpinnerAdapter(getActivity(),
                R.layout.spinner_category_text,
                R.id.title,
                categories);

        mCategorySpinner.setAdapter(categoryDataAdapter);

        // Shared people list

        // fake user
        mShoppingItem.getSharedMembers().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedMembers(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSelect(Person person) {
                        mShoppingItem.getSharedMembers().add(mShoppingItem.getSharedMembers().size() - 1, person);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRemove() {
                        // not used
                    }
                });

                fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
            }

            @Override
            public void onDelete(int position) {
                mShoppingItem.getSharedMembers().remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelect(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
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
    void onDelete(View view) {
        if (mShoppingItem == null) {
            // TODO: save content
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
                mPrice.setText(String.format(Locale.getDefault(), "%.2f", price));
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
        GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
        fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onSelect(Person person) {

            }

            @Override
            public void onRemove() {
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
