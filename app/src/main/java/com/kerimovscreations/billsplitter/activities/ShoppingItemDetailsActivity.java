package com.kerimovscreations.billsplitter.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.SharedPeopleListRVAdapter;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public class ShoppingItemDetailsActivity extends BaseActivity {

    public static final String INTENT_ITEM = "ITEM";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rvSharedPeople)
    RecyclerView mRVSharedPeople;
    @BindView(R.id.group_spinner)
    Spinner mGroupSpinner;
    @BindView(R.id.title)
    EditText mTitle;
    @BindView(R.id.delete_ic)
    ImageView mDeleteIc;
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
    public void initVars() {
        super.initVars();

        mShoppingItem = (ShoppingItem) getIntent().getSerializableExtra(INTENT_ITEM);

        mSwipeRefreshLayout.setOnRefreshListener(() -> mSwipeRefreshLayout.setRefreshing(false));

        setupData();
    }

    void setupData() {
        if (mShoppingItem == null) {
            mDeleteIc.setVisibility(View.GONE);
            mShoppingItem = new ShoppingItem("", "", false, new ArrayList<>(), false);
        } else {
            mDeleteIc.setVisibility(View.VISIBLE);
        }

        // Shopping group

        List<String> shoppingGroups = new ArrayList<>();
        shoppingGroups.add("Home shopping list");
        shoppingGroups.add("Office shopping list");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_shopping_group_text, shoppingGroups);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mGroupSpinner.setAdapter(dataAdapter);

        // Shopping group

        List<String> categories = new ArrayList<>();
        categories.add("Drinks");
        categories.add("Soups");
        categories.add("Dairy");
        categories.add("Electronic");

        ArrayAdapter<String> categoryDataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_category_text, categories);

        dataAdapter.setDropDownViewResource(R.layout.spinner_shopping_group_text);

        mCategorySpinner.setAdapter(categoryDataAdapter);

        // Shared people list

        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedPeople());

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

    @OnClick(R.id.date_layout)
    void onDate(View view) {
        // TODO: complete method
    }

    @OnClick(R.id.price_layout)
    void onPrice(View view) {
        // TODO: complete method
    }

    @OnClick(R.id.buyer_layout)
    void onBuyer(View view) {
        // TODO: complete method
    }

    /**
     * UI
     */
    void promptDeleteDialog() {
        // TODO: Complete method
    }
}
