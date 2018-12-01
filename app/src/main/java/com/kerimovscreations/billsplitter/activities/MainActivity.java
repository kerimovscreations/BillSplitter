package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.adapters.ShoppingListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.TimelineRVAdapter;
import com.kerimovscreations.billsplitter.fragments.dialogs.MenuBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.models.Timeline;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private static final int SHOPPING_ITEM_EDIT_REQUEST = 1;
    private static final int GROUP_CREATE_REQUEST = 2;

    @BindView(R.id.rvTimeline)
    RecyclerView mRVTimeline;
    @BindView(R.id.any_chart_view)
    AnyChartView mAnyChartView;
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
//            startActivity(new Intent(getContext(), AuthActivity.class));
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

        setupPieChart();

        setupActiveList();
        setupCompletedList();
        setupMenuBottomDialog();
    }

    /**
     * UI
     */

    void setupPieChart() {
        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
//                Toast.makeText(getContext(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Apples", 100));
        data.add(new ValueDataEntry("Pears", 200));
        data.add(new ValueDataEntry("Bananas", 120));
        data.add(new ValueDataEntry("Grapes", 210));
        data.add(new ValueDataEntry("Oranges", 230));

        pie.data(data);

        pie.title().enabled(false);

        pie.labels().position("outside");

        pie.legend().enabled(true);
        pie.legend().title()
                .text("Categories")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

//        pie.background().fill("#eeeeee 1.0");

        String[] colors = new String[7];
        colors[0] = "#FF7675 1.0";
        colors[1] = "#5E77FF 1.0";
        colors[2] = "#74B9FF 1.0";
        colors[3] = "#A29BFE 1.0";
        colors[4] = "#3AD29F 1.0";
        colors[5] = "#81ECEC 1.0";
        colors[6] = "#FFCA75 1.0";

        pie.palette(colors);

        mAnyChartView.setChart(pie);
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
        });

        mMenuBottomDialogFragment.show(getSupportFragmentManager(), "MENU_TAG");
    }

    @OnClick(R.id.bottom_tab_menu_ic)
    void onTabMore(View view) {
        // TODO: Complete method
    }

    @OnClick(R.id.add_item_btn)
    void onAddItem(View view) {
        // TODO: Complete method
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

    /**
     * Permission and results
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SHOPPING_ITEM_EDIT_REQUEST:
                break;
            default:
                break;
        }
    }
}
