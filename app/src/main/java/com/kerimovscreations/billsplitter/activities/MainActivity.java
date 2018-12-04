package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.activities.auth.LoginActivity;
import com.kerimovscreations.billsplitter.adapters.ShoppingListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.TimelineRVAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupEditBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.MenuBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.models.Timeline;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final int SHOPPING_ITEM_EDIT_REQUEST = 1;
    private static final int GROUP_CREATE_REQUEST = 2;
    private static final int EDIT_PROFILE_REQUEST = 3;

    @BindView(R.id.rvTimeline)
    RecyclerView mRVTimeline;
    //    @BindView(R.id.any_chart_view)
//    AnyChartView mAnyChartView;
    @BindView(R.id.pie_chart)
    PieChart mPieChart;
    @BindView(R.id.rvActiveList)
    RecyclerView mRVActiveList;
    @BindView(R.id.rvCompletedList)
    RecyclerView mRVCompletedList;
    @BindView(R.id.completed_list_drop_down_ic)
    ImageView mCompletedListDropDownIc;

    private TimelineRVAdapter mTimelineAdapter;
    private ShoppingListRVAdapter mActiveShoppingListAdapter;
    private ShoppingListRVAdapter mCompletedShoppingListAdapter;
    private MenuBottomSheetDialogFragment mMenuBottomDialogFragment;

    private ArrayList<Timeline> mTimeline;
    private ArrayList<ShoppingItem> mActiveShoppingList;
    private ArrayList<ShoppingItem> mCompletedShoppingList;

    private boolean mIsCompletedListOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_main);

//        if (Auth.getInstance().isLogged(getContext()))
//            initVars();
//        else {
//            finish();
//            startActivity(new Intent(getContext(), LoginActivity.class));
//        }
    }

    @Override
    public void initVars() {
        super.initVars();

        mTimeline = new ArrayList<>();

        mTimeline.add(new Timeline(new Date(), new Date(), "Today"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Week"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Month"));
        mTimeline.add(new Timeline(new Date(), new Date(), "6 Months"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Year"));

        mTimelineAdapter = new TimelineRVAdapter(getContext(), mTimeline);
        mTimelineAdapter.setOnItemClickListener(new TimelineRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRVTimeline.setAdapter(mTimelineAdapter);
        mRVTimeline.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupPieChart1();

        setupActiveList();
        setupCompletedList();
        setupMenuBottomDialog();
    }

    /**
     * UI
     */

    void setupPieChart1() {

        List<PieEntry> data = new ArrayList<>();
        data.add(new PieEntry(100, "Apple"));
        data.add(new PieEntry(200, "Pears"));
        data.add(new PieEntry(120, "Grapes"));
        data.add(new PieEntry(210, "Banana"));
        data.add(new PieEntry(300, "Oranges"));

        PieDataSet dataSet = new PieDataSet(data, "");
        dataSet.setColors(new int[]{
                R.color.colorRed,
                R.color.colorBlue,
                R.color.colorAccent,
                R.color.colorPink,
                R.color.colorGreen
        }, getContext());
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

        PieData pieData = new PieData();
        pieData.setDataSet(dataSet);

        mPieChart.setData(pieData);
        Description description = new Description();
        description.setText("Cost statistics by categories");
        description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorLightGray));
        mPieChart.setDescription(description);

    }

    void setupActiveList() {
        mActiveShoppingList = new ArrayList<>();

        ArrayList<Person> sharedPeople = new ArrayList<>();

        sharedPeople.add(new Person(1, "Karim Karimov", "user@gmail.com"));
        sharedPeople.add(new Person(1, "Shamil Omarov", "user@gmail.com"));
        sharedPeople.add(new Person(1, "Parvana Isgandarova", "user@gmail.com"));

        mActiveShoppingList.add(new ShoppingItem("Item 1", "13 November 2018", false, sharedPeople, true));
        mActiveShoppingList.add(new ShoppingItem("Item 2", "13 November 2018", false, sharedPeople, false));
        mActiveShoppingList.add(new ShoppingItem("Item 3", "13 November 2018", false, sharedPeople, false));
        mActiveShoppingList.add(new ShoppingItem("Item 4", "13 November 2018", false, sharedPeople, false));
        mActiveShoppingList.add(new ShoppingItem("Item 5", "13 November 2018", false, sharedPeople, false));

        mActiveShoppingListAdapter = new ShoppingListRVAdapter(getContext(), mActiveShoppingList);
        mActiveShoppingListAdapter.setOnItemClickListener(new ShoppingListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toShoppingItemDetails(mActiveShoppingList.get(position));
            }

            @Override
            public void onCheckClick(int position) {

            }
        });

        mRVActiveList.setAdapter(mActiveShoppingListAdapter);
        mRVActiveList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void setupCompletedList() {
        mCompletedShoppingList = new ArrayList<>();

        ArrayList<Person> sharedPeople = new ArrayList<>();

        mCompletedShoppingList.add(new ShoppingItem("Item 1", "13 November 2018", true, sharedPeople, true));
        mCompletedShoppingList.add(new ShoppingItem("Item 2", "13 November 2018", true, sharedPeople, false));
        mCompletedShoppingList.add(new ShoppingItem("Item 3", "13 November 2018", true, sharedPeople, false));
        mCompletedShoppingList.add(new ShoppingItem("Item 4", "13 November 2018", true, sharedPeople, false));
        mCompletedShoppingList.add(new ShoppingItem("Item 5", "13 November 2018", true, sharedPeople, false));

        mCompletedShoppingListAdapter = new ShoppingListRVAdapter(getContext(), mCompletedShoppingList);
        mCompletedShoppingListAdapter.setOnItemClickListener(new ShoppingListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toShoppingItemDetails(mCompletedShoppingList.get(position));
            }

            @Override
            public void onCheckClick(int position) {

            }
        });

        mRVCompletedList.setAdapter(mCompletedShoppingListAdapter);
        mRVCompletedList.setLayoutManager(new LinearLayoutManager(getContext()));

        updateCompletedListVisibility();
    }

    void setupMenuBottomDialog() {

    }

    void updateCompletedListVisibility() {
        if (mIsCompletedListOpen) {
            mCompletedListDropDownIc.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drop_up, null));
            mRVCompletedList.setVisibility(View.VISIBLE);
        } else {
            mCompletedListDropDownIc.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drop_down, null));
            mRVCompletedList.setVisibility(View.GONE);
        }
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.completed_list_layout)
    void onCompletedLayout(View view) {
        mIsCompletedListOpen = !mIsCompletedListOpen;
        updateCompletedListVisibility();
    }

    @OnClick(R.id.bottom_tab_menu_ic)
    void onTabMenu(View view) {
        mMenuBottomDialogFragment = MenuBottomSheetDialogFragment.getInstance();
        mMenuBottomDialogFragment.setClickListener(new MenuBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onGroup(Group group) {
            }

            @Override
            public void onCreateGroup() {
                toGroupForm(null);
            }

            @Override
            public void editProfile() {
                Log.e("TAB", "EDIT PROFILE");
                toProfileEdit();
            }
        });

        mMenuBottomDialogFragment.show(getSupportFragmentManager(), "MENU_TAG");
    }

    @OnClick(R.id.bottom_tab_more_ic)
    void onTabMore(View view) {
        GroupEditBottomSheetDialogFragment fragment = GroupEditBottomSheetDialogFragment.getInstance();
        fragment.setClickListener(new GroupEditBottomSheetDialogFragment.OnClickListener() {

            @Override
            public void onDelete() {
                // TODO: API Integration
            }

            @Override
            public void onEdit() {
                toGroupForm(new Group("Group name", new ArrayList<>()));
            }

            @Override
            public void onDeleteItems() {
                // TODO: API Integration
            }
        });

        fragment.show(getSupportFragmentManager(), "MORE_TAG");
    }

    @OnClick(R.id.add_item_btn)
    void onAddItem(View view) {
        toShoppingItemDetails(null);
    }

    /**
     * Navigation
     */

    void toShoppingItemDetails(ShoppingItem item) {
        Intent intent = new Intent(getContext(), ShoppingItemDetailsActivity.class);
        intent.putExtra(ShoppingItemDetailsActivity.INTENT_ITEM, item);
        startActivityForResult(intent, SHOPPING_ITEM_EDIT_REQUEST);
    }

    void toGroupForm(Group group) {
        Intent intent = new Intent(getContext(), GroupFormActivity.class);
        intent.putExtra(GroupFormActivity.INTENT_ITEM, group);
        startActivityForResult(intent, GROUP_CREATE_REQUEST);
    }

    void toProfileEdit() {
        Intent intent = new Intent(getContext(), ProfileEditActivity.class);
        startActivityForResult(intent, GROUP_CREATE_REQUEST);
    }

    /**
     * Permission and results
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SHOPPING_ITEM_EDIT_REQUEST:
                // TODO: Update UI
                break;
            default:
                break;
        }
    }
}
