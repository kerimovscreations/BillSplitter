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
import com.kerimovscreations.billsplitter.adapters.spinner.CategorySpinnerAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupMemberPickerBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.MenuBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Group;
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

        // Category spinner

        List<Category> categories = new ArrayList<>();

        categories.add(new Category("Color 1", "#FF7675"));
        categories.add(new Category("Color 2", "#5E77FF"));
        categories.add(new Category("Color 3", "#74B9FF"));
        categories.add(new Category("Color 4", "#A29BFE"));
        categories.add(new Category("Color 5", "#3AD29F"));
        categories.add(new Category("Color 6", "#81ECEC"));
        categories.add(new Category("Color 7", "#FFCA75"));

        CategorySpinnerAdapter categoryDataAdapter = new CategorySpinnerAdapter(getActivity(),
                R.layout.spinner_category_text,
                R.id.title,
                categories);

        mCategorySpinner.setAdapter(categoryDataAdapter);

        // Shared people list

        // fake user
        mShoppingItem.getSharedPeople().add(new Person(-1, "Placeholder"));
        mAdapter = new SharedPeopleListRVAdapter(getContext(), mShoppingItem.getSharedPeople());
        mAdapter.setOnItemClickListener(new SharedPeopleListRVAdapter.OnItemClickListener() {
            @Override
            public void onAdd(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSelect(Person person) {

                    }

                    @Override
                    public void onRemove() {
                        // not used
                    }
                });

                fragment.show(getSupportFragmentManager(), "MEMBER_TAG");
            }

            @Override
            public void onSelect(int position) {
                GroupMemberPickerBottomSheetDialogFragment fragment = GroupMemberPickerBottomSheetDialogFragment.getInstance();
                fragment.setClickListener(new GroupMemberPickerBottomSheetDialogFragment.OnClickListener() {
                    @Override
                    public void onSelect(Person person) {

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
}
