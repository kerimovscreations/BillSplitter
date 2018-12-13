package com.kerimovscreations.billsplitter.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.activities.auth.LoginActivity;
import com.kerimovscreations.billsplitter.activities.enums.StatisticsPeriod;
import com.kerimovscreations.billsplitter.adapters.recyclerView.ShoppingListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.recyclerView.TimelineRVAdapter;
import com.kerimovscreations.billsplitter.application.GlobalApplication;
import com.kerimovscreations.billsplitter.fragments.dialogs.GroupEditBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.fragments.dialogs.MenuBottomSheetDialogFragment;
import com.kerimovscreations.billsplitter.interfaces.AppApiService;
import com.kerimovscreations.billsplitter.models.Category;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalGroupMember;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.models.Timeline;
import com.kerimovscreations.billsplitter.models.Transaction;
import com.kerimovscreations.billsplitter.models.TransactionsBundle;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.GroupListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.StatisticsDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.TransactionsBundleDataWrapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private final String TAG = "MAIN_ACT";

    private static final int SHOPPING_ITEM_EDIT_REQUEST = 1;
    private static final int GROUP_CREATE_REQUEST = 2;
    private static final int GROUP_EDIT_REQUEST = 3;
    private static final int EDIT_PROFILE_REQUEST = 4;
    private static final int TRANSACTION_UPDATE_REQUEST = 5;

    @BindView(R.id.rvTimeline)
    RecyclerView mRVTimeline;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.statistics_layout)
    View mStatisticsLayout;

    @BindView(R.id.statistics_no_data)
    View mStatisticsNoData;

    @BindView(R.id.group_title)
    TextView mGroupTitle;

    @BindView(R.id.pie_chart)
    PieChart mPieChart;

    @BindView(R.id.rvActiveList)
    RecyclerView mRVActiveList;

    @BindView(R.id.shopping_list_title)
    View mActiveShoppingListTitle;

    @BindView(R.id.rvCompletedList)
    RecyclerView mRVCompletedList;

    @BindView(R.id.completed_list_layout)
    View mCompletedListLayout;

    @BindView(R.id.completed_list_drop_down_ic)
    ImageView mCompletedListDropDownIc;

    @BindView(R.id.group_content)
    View mGroupContent;

    @BindView(R.id.add_item_btn)
    View mAddItemBtn;

    @BindView(R.id.empty_content_placeholder)
    View mEmptyContentPlaceholder;

    @BindView(R.id.empty_list_placeholder)
    View mEmptyListPlaceholder;

    @BindView(R.id.transactions_layout)
    View mTransactionsLayout;

    @BindView(R.id.transactions_income_layout)
    View mTransactionsIncomeLayout;

    @BindView(R.id.transactions_income_text)
    TextView mTransactionsIncomeText;

    @BindView(R.id.transactions_outcome_layout)
    View mTransactionsOutcomeLayout;

    @BindView(R.id.transactions_outcome_text)
    TextView mTransactionsOutcomeText;

    private TimelineRVAdapter mTimelineAdapter;
    private ShoppingListRVAdapter mActiveShoppingListAdapter;
    private ShoppingListRVAdapter mCompletedShoppingListAdapter;
    private MenuBottomSheetDialogFragment mMenuBottomDialogFragment;

    private ArrayList<Timeline> mTimeline;
    private ArrayList<ShoppingItem> mActiveShoppingList;
    private ArrayList<ShoppingItem> mCompletedShoppingList;

    private boolean mIsCompletedListOpen = false;

    AppApiService mApiService;

    private LocalProfile mLocalProfile;
    private ArrayList<LocalGroup> mLocalGroups = new ArrayList<>();
    private LocalGroup mSelectedGroup;

    private ArrayList<Category> mStatistics = new ArrayList<>();

    private TransactionsBundle mTransactionBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreateSetContentView(R.layout.activity_main);

        mLocalProfile = getRealm().where(LocalProfile.class).findFirst();
        mLocalGroups.addAll(getRealm().where(LocalGroup.class).findAll());

        if (Auth.getInstance().isLogged(getContext()) && mLocalProfile != null)
            setupData();
        else {
            toLogin();
        }
    }

    @Override
    public void initVars() {
        super.initVars();

        mApiService = GlobalApplication.getRetrofit().create(AppApiService.class);

        mSwipeRefreshLayout.setOnRefreshListener(this::getData);

        mTimeline = new ArrayList<>();

        mTimeline.add(new Timeline(new Date(), new Date(), getString(R.string.today), StatisticsPeriod.DAY));
        mTimeline.add(new Timeline(new Date(), new Date(), getString(R.string.week), StatisticsPeriod.WEEK));
        mTimeline.add(new Timeline(new Date(), new Date(), getString(R.string.month), StatisticsPeriod.MONTH));
        mTimeline.add(new Timeline(new Date(), new Date(), getString(R.string.six_month), StatisticsPeriod.SIX_MONTH));
        mTimeline.add(new Timeline(new Date(), new Date(), getString(R.string.year), StatisticsPeriod.YEAR));

        mTimelineAdapter = new TimelineRVAdapter(getContext(), mTimeline);
        mTimelineAdapter.setOnItemClickListener(position -> {
            getStatistics();
        });
        mRVTimeline.setAdapter(mTimelineAdapter);
        mRVTimeline.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        mPieChart.setTouchEnabled(false);

        setupContent();
    }

    void setupData() {
        if (mLocalProfile.getLastSelectedGroupId() == -1) {
            if (mLocalGroups.size() > 0) {
                getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(0));

                for (LocalGroup group : mLocalGroups) {
                    if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                        mSelectedGroup = group;
                        break;
                    }
                }

                mGroupContent.setVisibility(View.VISIBLE);
                mAddItemBtn.setVisibility(View.VISIBLE);
                mEmptyContentPlaceholder.setVisibility(View.GONE);
                mEmptyListPlaceholder.setVisibility(View.GONE);
            } else {
                mGroupContent.setVisibility(View.GONE);
                mAddItemBtn.setVisibility(View.GONE);
                mEmptyContentPlaceholder.setVisibility(View.VISIBLE);
                mEmptyListPlaceholder.setVisibility(View.GONE);
            }
        } else {
            for (LocalGroup group : mLocalGroups) {
                if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                    mSelectedGroup = group;
                    break;
                }
            }
        }

        if (mSelectedGroup != null) {
            mGroupTitle.setText(mSelectedGroup.getTitle());
        }

        getGroups();

        getData();
    }

    void getData() {
        if (mSelectedGroup != null) {
            mGroupContent.setVisibility(View.GONE);
            mAddItemBtn.setVisibility(View.GONE);
            mEmptyContentPlaceholder.setVisibility(View.GONE);
            mEmptyListPlaceholder.setVisibility(View.GONE);
            getGroupItems();
            getTransactions();
            getStatistics();
        } else {
            showProgress(false);
        }
    }

    /**
     * UI
     */

    void showProgress(boolean show) {
        mSwipeRefreshLayout.setRefreshing(show);
    }

    void setupContent() {

        setupStatistics();

        setupActiveList();
        setupCompletedList();
    }

    void setupStatistics() {
        if (mStatistics.size() == 0) {
            mStatisticsLayout.setVisibility(View.VISIBLE);
            mPieChart.setVisibility(View.GONE);
            mStatisticsNoData.setVisibility(View.VISIBLE);
            return;
        } else {
            mStatisticsLayout.setVisibility(View.VISIBLE);
            mPieChart.setVisibility(View.VISIBLE);
            mStatisticsNoData.setVisibility(View.GONE);
        }

        List<PieEntry> data = new ArrayList<>();
        int[] colors = new int[mStatistics.size()];

        for (int i = 0; i < mStatistics.size(); i++) {
            data.add(new PieEntry(mStatistics.get(i).getAmountSpent(), mStatistics.get(i).getTitle()));
            colors[i] = Color.parseColor("#" + mStatistics.get(i).getHexColor());
        }

        PieDataSet dataSet = new PieDataSet(data, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));

        PieData pieData = new PieData();
        pieData.setDataSet(dataSet);

        mPieChart.setData(pieData);
        Description description = new Description();
        description.setText(getString(R.string.cost_statistics_chart_legend));
        description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorLightGray));
        mPieChart.setDescription(description);
    }

    void setupActiveList() {
        mActiveShoppingList = new ArrayList<>();

        mActiveShoppingListAdapter = new ShoppingListRVAdapter(getContext(), mActiveShoppingList);
        mActiveShoppingListAdapter.setOnItemClickListener(new ShoppingListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toShoppingItemDetails(mActiveShoppingList.get(position));
            }

            @Override
            public void onCheckClick(int position) {
                mActiveShoppingList.get(position).toggleComplete();
                updateCompleteStatus(mActiveShoppingList.get(position));
                mActiveShoppingListAdapter.notifyDataSetChanged();
            }
        });

        mRVActiveList.setAdapter(mActiveShoppingListAdapter);
        mRVActiveList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    void setupCompletedList() {
        mCompletedShoppingList = new ArrayList<>();

        mCompletedShoppingListAdapter = new ShoppingListRVAdapter(getContext(), mCompletedShoppingList);
        mCompletedShoppingListAdapter.setOnItemClickListener(new ShoppingListRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toShoppingItemDetails(mCompletedShoppingList.get(position));
            }

            @Override
            public void onCheckClick(int position) {
                mCompletedShoppingList.get(position).toggleComplete();
                updateCompleteStatus(mCompletedShoppingList.get(position));
                mCompletedShoppingListAdapter.notifyDataSetChanged();
            }
        });

        mRVCompletedList.setAdapter(mCompletedShoppingListAdapter);
        mRVCompletedList.setLayoutManager(new LinearLayoutManager(getContext()));

        updateCompletedListVisibility();
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

    void loadLocalShoppingList() {
        RealmResults<ShoppingItem> realmResults = getRealm()
                .where(ShoppingItem.class)
                .equalTo("groupId", mSelectedGroup.getId())
                .sort("date", Sort.DESCENDING)
                .findAll();

        mCompletedShoppingList.clear();
        mActiveShoppingList.clear();

        for (ShoppingItem shoppingItem : realmResults) {
            if (shoppingItem.isComplete()) {
                mCompletedShoppingList.add(new ShoppingItem(shoppingItem));
            } else {
                mActiveShoppingList.add(new ShoppingItem(shoppingItem));
            }
        }

        formatShoppingLists();

        mGroupContent.setVisibility(View.VISIBLE);
        mAddItemBtn.setVisibility(View.VISIBLE);
        mEmptyContentPlaceholder.setVisibility(View.GONE);
        mEmptyListPlaceholder.setVisibility(View.GONE);

        mActiveShoppingListTitle.setVisibility(mActiveShoppingList.size() == 0 ? View.GONE : View.VISIBLE);
        mCompletedListLayout.setVisibility(mCompletedShoppingList.size() == 0 ? View.GONE : View.VISIBLE);

        mActiveShoppingListAdapter.notifyDataSetChanged();
        mCompletedShoppingListAdapter.notifyDataSetChanged();
    }

    void formatShoppingLists() {
        String tempDate;

        // Active

        if (mActiveShoppingList.size() > 0) {
            tempDate = mActiveShoppingList.get(0).getDate();

            mActiveShoppingList.add(0, new ShoppingItem(-1, tempDate));

            for (int i = 0; i < mActiveShoppingList.size() - 1; i++) {
                if (!tempDate.equals(mActiveShoppingList.get(i + 1).getDate())) {
                    tempDate = mActiveShoppingList.get(i + 1).getDate();
                    mActiveShoppingList.add(i + 1
                            , new ShoppingItem(-1, tempDate));
                }
            }
        }

        // Completed
        if (mCompletedShoppingList.size() > 0) {
            tempDate = mCompletedShoppingList.get(0).getDate();

            mCompletedShoppingList.add(0, new ShoppingItem(-1, tempDate));

            for (int i = 0; i < mCompletedShoppingList.size() - 1; i++) {
                if (!tempDate.equals(mCompletedShoppingList.get(i + 1).getDate())) {
                    tempDate = mCompletedShoppingList.get(i + 1).getDate();
                    mCompletedShoppingList.add(i + 1
                            , new ShoppingItem(-1, tempDate));
                }
            }
        }
    }

    /**
     * Click handlers
     */

    @OnClick(R.id.completed_list_header)
    void onCompletedLayout(View view) {
        mIsCompletedListOpen = !mIsCompletedListOpen;
        updateCompletedListVisibility();
    }

    @OnClick(R.id.bottom_tab_menu_ic)
    void onTabMenu(View view) {
        mMenuBottomDialogFragment = MenuBottomSheetDialogFragment.getInstance(mSelectedGroup);
        mMenuBottomDialogFragment.setClickListener(new MenuBottomSheetDialogFragment.OnClickListener() {
            @Override
            public void onGroup(Group group) {
                GlobalApplication.getRealm().executeTransaction(realm ->
                        mLocalProfile.setLastSelectedGroupId(group.getId()));
                mSelectedGroup = new LocalGroup(group);
                mGroupTitle.setText(mSelectedGroup.getTitle());
                getData();
                mMenuBottomDialogFragment.dismiss();
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

            @Override
            public void onLogout() {
                Auth.getInstance().logout(getContext());
                toLogin();
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
                deleteGroup();
            }

            @Override
            public void onEdit() {
                toGroupForm(new Group(mSelectedGroup));
            }

            @Override
            public void onDeleteItems() {
                deleteCompletedItems();
            }
        });

        fragment.show(getSupportFragmentManager(), "MORE_TAG");
    }

    @OnClick(R.id.add_item_btn)
    void onAddItem(View view) {
        toShoppingItemDetails(null);
    }

    @OnClick(R.id.create_group_btn)
    void onCreateGroup() {
        toGroupForm(new Group(mSelectedGroup));
    }

    @OnClick(R.id.transactions_income_layout)
    void onIncome(View view) {
        toIncome();
    }

    @OnClick(R.id.transactions_outcome_layout)
    void onOutcome(View view) {
        toOutcome();
    }

    /**
     * HTTP
     */

    private void getGroups() {
        showProgress(true);

        Call<GroupListDataWrapper> groupsCall = mApiService.getGroups(Auth.getInstance().getToken(getContext()), "", 1);

        groupsCall.enqueue(new Callback<GroupListDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<GroupListDataWrapper> call, @NonNull Response<GroupListDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        getRealm().executeTransaction(realm -> {
                            realm.where(LocalGroup.class).findAll().deleteAllFromRealm();

                            for (Group tempGroup : response.body().getList()) {
                                realm.copyToRealmOrUpdate(new LocalGroup(tempGroup));

                                realm.where(LocalGroupMember.class).equalTo("groupId", tempGroup.getId()).findAll().deleteAllFromRealm();

                                for (int i = 0; i < tempGroup.getGroupUsers().size(); i++) {
                                    realm.copyToRealm(new LocalGroupMember(tempGroup.getGroupUsers().get(i), tempGroup.getId()));
                                }
                            }
                        });

                        mLocalGroups.clear();
                        mLocalGroups.addAll(getRealm().where(LocalGroup.class).findAll());

                        for (LocalGroup group : mLocalGroups) {
                            if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                                mSelectedGroup = group;
                                mGroupTitle.setText(mSelectedGroup.getTitle());
                                break;
                            }
                        }

                        if (mSelectedGroup == null && mLocalGroups.size() > 0) {
                            getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(mLocalGroups.get(0).getId()));

                            for (LocalGroup group : mLocalGroups) {
                                if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                                    mSelectedGroup = group;
                                    break;
                                }
                            }

                            mGroupTitle.setText(mSelectedGroup.getTitle());

                            if (mSelectedGroup != null) {
                                getData();
                            }
                        }
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
            public void onFailure(@NonNull Call<GroupListDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void deleteGroup() {
        showProgress(true);

        Call<SimpleDataWrapper> deleteCall = mApiService.deleteGroup(Auth.getInstance().getToken(getContext()), mSelectedGroup.getId());

        deleteCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        getRealm().executeTransaction(realm -> {
                            realm.where(ShoppingItem.class).equalTo("groupId", mSelectedGroup.getId()).findAll().deleteAllFromRealm();
                            realm.where(LocalGroupMember.class).equalTo("groupId", mSelectedGroup.getId()).findAll().deleteAllFromRealm();
                            Objects.requireNonNull(realm.where(LocalGroup.class).equalTo("id", mSelectedGroup.getId()).findFirst()).deleteFromRealm();
                            mLocalGroups.clear();
                            mLocalGroups.addAll(getRealm().where(LocalGroup.class).findAll());
                            mSelectedGroup = null;
                        });

                        if (mSelectedGroup == null && mLocalGroups.size() > 0) {
                            getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(mLocalGroups.get(0).getId()));

                            for (LocalGroup group : mLocalGroups) {
                                if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                                    mSelectedGroup = group;
                                    break;
                                }
                            }
                        }

                        mGroupTitle.setText(mSelectedGroup.getTitle());

                        if (mSelectedGroup != null) {
                            getData();
                        }
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
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getGroupItems() {
        showProgress(true);

        Call<ShoppingItemListDataWrapper> groupItems = mApiService.getShoppingItems(Auth.getInstance().getToken(getContext()),
                mSelectedGroup.getId(), 1);

        groupItems.enqueue(new Callback<ShoppingItemListDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ShoppingItemListDataWrapper> call, @NonNull Response<ShoppingItemListDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        if (response.body().getList().size() == 0) {
                            mGroupContent.setVisibility(View.GONE);
                            mAddItemBtn.setVisibility(View.VISIBLE);
                            mEmptyContentPlaceholder.setVisibility(View.GONE);
                            mEmptyListPlaceholder.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < response.body().getList().size(); i++)
                                response.body().getList().get(i).setGroupId(mSelectedGroup.getId());

                            GlobalApplication.getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(response.body().getList()),
                                    () -> {
                                        mCompletedShoppingList.clear();
                                        mActiveShoppingList.clear();

                                        for (ShoppingItem shoppingItem : response.body().getList()) {
                                            if (shoppingItem.isComplete()) {
                                                mCompletedShoppingList.add(shoppingItem);
                                            } else {
                                                mActiveShoppingList.add(shoppingItem);
                                            }
                                        }

                                        formatShoppingLists();

                                        mGroupContent.setVisibility(View.VISIBLE);
                                        mAddItemBtn.setVisibility(View.VISIBLE);
                                        mEmptyContentPlaceholder.setVisibility(View.GONE);
                                        mEmptyListPlaceholder.setVisibility(View.GONE);

                                        mActiveShoppingListTitle.setVisibility(mActiveShoppingList.size() == 0 ? View.GONE : View.VISIBLE);
                                        mCompletedListLayout.setVisibility(mCompletedShoppingList.size() == 0 ? View.GONE : View.VISIBLE);

                                        mActiveShoppingListAdapter.notifyDataSetChanged();
                                        mCompletedShoppingListAdapter.notifyDataSetChanged();
                                    });
                        }
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
            public void onFailure(@NonNull Call<ShoppingItemListDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateCompleteStatus(ShoppingItem shoppingItem) {
        showProgress(true);

        HashMap<String, String> data = new HashMap<>();

        data.put("name", shoppingItem.getProduct().getName());
        data.put("groupId", String.valueOf(shoppingItem.getGroupId()));
        data.put("categoryId", String.valueOf(shoppingItem.getProduct().getCategory().getId()));
        data.put("barCode", shoppingItem.getProduct().getBarCode());
        data.put("price", String.valueOf(shoppingItem.getPrice()));
        data.put("date", shoppingItem.getDate());
        data.put("isComplete", String.valueOf(shoppingItem.isComplete()));

        if (shoppingItem.getBuyer() != null) {
            data.put("paidById", String.valueOf(shoppingItem.getBuyer().getId()));
        }

        for (int i = 0; i < shoppingItem.getSharedMembers().size(); i++) {
            if (shoppingItem.getSharedMembers().get(i).getId() != -1)
                data.put("shares[" + i + "]", String.valueOf(shoppingItem.getSharedMembers().get(i).getId()));
        }

        Call<ShoppingItemDataWrapper> mUpdateItemCall = mApiService.updateShoppingItem(Auth.getInstance().getToken(getContext()), shoppingItem.getId(), data);

        mUpdateItemCall.enqueue(new Callback<ShoppingItemDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ShoppingItemDataWrapper> call, @NonNull Response<ShoppingItemDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        GlobalApplication.getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(response.body().getShoppingItem()));
                        getStatistics();
                        getTransactions();
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

    private void deleteCompletedItems() {
        showProgress(true);

        Call<SimpleDataWrapper> deleteCall = mApiService.hideCompletedShoppingItems(Auth.getInstance().getToken(getContext()), mSelectedGroup.getId());

        deleteCall.enqueue(new Callback<SimpleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<SimpleDataWrapper> call, @NonNull Response<SimpleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        getRealm().executeTransaction(realm -> {
                            realm.where(ShoppingItem.class).equalTo("groupId", mSelectedGroup.getId()).equalTo("isComplete", true).findAll().deleteAllFromRealm();
                        });

                        loadLocalShoppingList();
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
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getStatistics() {
        showProgress(true);

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        switch (mTimeline.get(mTimelineAdapter.getSelectedIndex()).getPeriod()) {
            case DAY:
                startCalendar.add(Calendar.DATE, -1);
                break;
            case WEEK:
                startCalendar.add(Calendar.DATE, -7);
                break;
            case MONTH:
                startCalendar.add(Calendar.MONTH, -1);
                break;
            case SIX_MONTH:
                startCalendar.add(Calendar.MONTH, -6);
                break;
            case YEAR:
                startCalendar.add(Calendar.YEAR, -1);
                break;
        }

        String startDate = dateFormat.format(startCalendar.getTime());
        String endDate = dateFormat.format(endCalendar.getTime());

        Call<StatisticsDataWrapper> statisticsCall = mApiService.getStatistics(Auth.getInstance().getToken(getContext()),
                mSelectedGroup.getId(), startDate, endDate);

        statisticsCall.enqueue(new Callback<StatisticsDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<StatisticsDataWrapper> call, @NonNull Response<StatisticsDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        mStatistics.clear();
                        mStatistics.addAll(response.body().getList());
                        setupStatistics();
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
            public void onFailure(@NonNull Call<StatisticsDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getTransactions() {
        showProgress(true);

        Call<TransactionsBundleDataWrapper> transactionsCall = mApiService.getTransactions(Auth.getInstance().getToken(getContext()),
                mSelectedGroup.getId());

        transactionsCall.enqueue(new Callback<TransactionsBundleDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<TransactionsBundleDataWrapper> call, @NonNull Response<TransactionsBundleDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> {
                        mTransactionBundle = response.body().getBunle().processData(mSelectedGroup);
                        mTransactionsLayout.setVisibility(View.VISIBLE);

                        float sum1 = 0;

                        for (Transaction transaction : mTransactionBundle.getTheyOwe()) {
                            sum1 += transaction.getBalance();
                        }

                        mTransactionsIncomeText.setText(String.format("%s %s", sum1, mSelectedGroup.getCurrency().getName()));

                        float sum2 = 0;

                        for (Transaction transaction : mTransactionBundle.getiOwe()) {
                            sum2 += transaction.getBalance();
                        }

                        mTransactionsOutcomeText.setText(String.format("%s %s", sum2, mSelectedGroup.getCurrency().getName()));
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
            public void onFailure(@NonNull Call<TransactionsBundleDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    /**
     * Navigation
     */

    void toShoppingItemDetails(ShoppingItem item) {
        Intent intent = new Intent(getContext(), ShoppingItemDetailsActivity.class);
        if (item != null) {
            intent.putExtra(ShoppingItemDetailsActivity.INTENT_ITEM_ID, item.getId());
        }
        intent.putExtra(ShoppingItemDetailsActivity.INTENT_GROUP_ID, mSelectedGroup.getId());
        startActivityForResult(intent, SHOPPING_ITEM_EDIT_REQUEST);
    }

    void toGroupForm(Group group) {
        Intent intent = new Intent(getContext(), GroupFormActivity.class);
        if (group != null) {
            intent.putExtra(GroupFormActivity.INTENT_ITEM_ID, group.getId());
            startActivityForResult(intent, GROUP_EDIT_REQUEST);
        } else {
            startActivityForResult(intent, GROUP_CREATE_REQUEST);
        }
    }

    void toProfileEdit() {
        Intent intent = new Intent(getContext(), ProfileEditActivity.class);
        startActivityForResult(intent, EDIT_PROFILE_REQUEST);
    }

    void toLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void toIncome() {
        Intent intent = new Intent(getContext(), TransactionListActivity.class);
        intent.putExtra(TransactionListActivity.TYPE, TransactionListActivity.TYPE_INCOME);
        intent.putExtra(TransactionListActivity.DATA, mTransactionBundle);
        intent.putExtra(TransactionListActivity.GROUP_ID, mSelectedGroup.getId());
        startActivityForResult(intent, TRANSACTION_UPDATE_REQUEST);
    }

    void toOutcome() {
        Intent intent = new Intent(getContext(), TransactionListActivity.class);
        intent.putExtra(TransactionListActivity.TYPE, TransactionListActivity.TYPE_OUTCOME);
        intent.putExtra(TransactionListActivity.DATA, mTransactionBundle);
        intent.putExtra(TransactionListActivity.GROUP_ID, mSelectedGroup.getId());
        startActivityForResult(intent, TRANSACTION_UPDATE_REQUEST);
    }

    /**
     * Permission and results
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SHOPPING_ITEM_EDIT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    getStatistics();
                    loadLocalShoppingList();
                    getTransactions();
                }
                break;
            case GROUP_CREATE_REQUEST:
                mLocalGroups.clear();
                mLocalGroups.addAll(getRealm().where(LocalGroup.class).findAll());

                for (LocalGroup group : mLocalGroups) {
                    if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                        mSelectedGroup = group;
                        mGroupTitle.setText(mSelectedGroup.getTitle());
                        break;
                    }
                }

                if (mSelectedGroup == null && mLocalGroups.size() > 0) {
                    getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(mLocalGroups.get(0).getId()));

                    for (LocalGroup group : mLocalGroups) {
                        if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                            mSelectedGroup = group;
                            break;
                        }
                    }

                    mGroupTitle.setText(mSelectedGroup.getTitle());

                    if (mSelectedGroup != null) {
                        getData();
                    }
                }

                break;
            case GROUP_EDIT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.e("MAIN", "EDIT GROUP RESULT");
                    if (mLocalProfile.getLastSelectedGroupId() < 0 && mLocalGroups.size() > 0) {
                        getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(mLocalGroups.get(0).getId()));
                    }

                    for (LocalGroup group : mLocalGroups) {
                        if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                            mSelectedGroup = group;
                            mGroupTitle.setText(mSelectedGroup.getTitle());
                            break;
                        }
                    }

                    getTransactions();
                }
                break;
            case EDIT_PROFILE_REQUEST:
                // no need update ui
                break;
            case TRANSACTION_UPDATE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    getTransactions();
                }
                break;
            default:
                break;
        }
    }
}
