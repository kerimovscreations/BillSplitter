package com.kerimovscreations.billsplitter.activities;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.spinner.CategorySpinnerAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupMemberPickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ShoppingItemDetailsActivity extends BaseActivity {

    public static final String INTENT_ITEM = "ITEM";
    public static final int REQUEST_BAR_CODE_READ = 4;
    public static final int PERMISSION_REQUEST_CAMERA = 5;

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

    ShoppingItem mShoppingItem;

    SharedPeopleListRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_shopping_item_details);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mShouldOpenScan) {
            mShouldOpenScan = false;
            onQRScan();
        }
    }

    @Override
    public void initVars() {
        super.initVars();

        mShoppingItem = (ShoppingItem) getIntent().getSerializableExtra(INTENT_ITEM);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

        setupData();
    }

    void setupData() {
        if (mShoppingItem == null) {
            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_black_24dp, null));
            mActionBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorGreen), android.graphics.PorterDuff.Mode.SRC_IN);
            mShoppingItem = new ShoppingItem("", "", false, new ArrayList<>(), false);
        } else {
            mActionBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete, null));
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
        mShoppingItem.getSharedPeople().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedPeople(), true);
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSelect(Person person) {
                        mShoppingItem.getSharedPeople().add(mShoppingItem.getSharedPeople().size() - 1, person);
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
                mShoppingItem.getSharedPeople().remove(position);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSelect(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSelect(Person person) {
                        mShoppingItem.getSharedPeople().remove(position);
                        mShoppingItem.getSharedPeople().add(position, person);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRemove() {
                        mShoppingItem.getSharedPeople().remove(position);
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
        // TODO: complete method
    }

    @OnClick(R.id.price_layout)
    void onPrice(View view) {
        // TODO: complete method
    }

    @OnClick(R.id.qr_scan_btn)
    void onQRScan() {
        if (!hasCameraPermission()) {
            requestCameraAccess();
            return;
        }

        Intent intent = new Intent(getContext(), BarScannerActivity.class);
        startActivityForResult(intent, REQUEST_BAR_CODE_READ);
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

    /**
     * UI
     */
    void promptDeleteDialog() {
        // TODO: Complete method
    }

    boolean mShouldOpenScan = false;

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
                    mShouldOpenScan = true;
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
//                    Toast.makeText(getContext(), data.getStringExtra(BarScannerActivity.BAR_CODE), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
