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
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.kerimovscreations.billsplitter.R;
import com.kerimovscreations.billsplitter.activities.auth.LoginActivity;
import com.kerimovscreations.billsplitter.adapters.ShoppingListRVAdapter;
import com.kerimovscreations.billsplitter.adapters.TimelineRVAdapter;
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
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.GroupListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.SimpleDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.StatisticsDataWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    private final String TAG = "MAIN_ACT";

    private static final int SHOPPING_ITEM_EDIT_REQUEST = 1;
    private static final int GROUP_CREATE_REQUEST = 2;
    private static final int EDIT_PROFILE_REQUEST = 3;

    @BindView(R.id.rvTimeline)
    RecyclerView mRVTimeline;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.statistics_layout)
    View mStatisticsLayout;

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

        mTimeline.add(new Timeline(new Date(), new Date(), "Today"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Week"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Month"));
        mTimeline.add(new Timeline(new Date(), new Date(), "6 Months"));
        mTimeline.add(new Timeline(new Date(), new Date(), "Year"));

        mTimelineAdapter = new TimelineRVAdapter(getContext(), mTimeline);
        mTimelineAdapter.setOnItemClickListener(position -> {

        });
        mRVTimeline.setAdapter(mTimelineAdapter);
        mRVTimeline.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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
            mStatisticsLayout.setVisibility(View.GONE);
            return;
        } else {
            mStatisticsLayout.setVisibility(View.VISIBLE);
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
        description.setText("Cost statistics by categories");
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
        RealmResults<ShoppingItem> realmResults = GlobalApplication.getRealm().where(ShoppingItem.class).findAll();

        mCompletedShoppingList.clear();
        mActiveShoppingList.clear();

        for (ShoppingItem shoppingItem : realmResults) {
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
    }

    void formatShoppingLists() {
        // Active
        String tempDate = mActiveShoppingList.get(0).getDate();

        mActiveShoppingList.add(0, new ShoppingItem(-1, tempDate));

        for (int i = 0; i < mActiveShoppingList.size() - 1; i++) {
            if (!tempDate.equals(mActiveShoppingList.get(i + 1).getDate())) {
                tempDate = mActiveShoppingList.get(i + 1).getDate();
                mActiveShoppingList.add(i + 1
                        , new ShoppingItem(-1, tempDate));
            }
        }

        // Completed
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
                mSelectedGroup = new LocalGroup(group);
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
                            for (Group tempGroup : response.body().getList()) {
                                realm.copyToRealmOrUpdate(new LocalGroup(tempGroup));

                                realm.where(LocalGroupMember.class).equalTo("groupId", tempGroup.getId()).findAll().deleteAllFromRealm();

                                for (int i = 0; i < tempGroup.getGroupUsers().size(); i++) {
                                    realm.copyToRealm(new LocalGroupMember(tempGroup.getGroupUsers().get(i), tempGroup.getId()));
                                }
                            }
                            mLocalGroups.clear();
                            mLocalGroups.addAll(getRealm().where(LocalGroup.class).findAll());
                        });

                        if (mSelectedGroup == null && mLocalGroups.size() > 0) {
                            getRealm().executeTransaction(realm -> mLocalProfile.setLastSelectedGroupId(mLocalGroups.get(0).getId()));

                            for (LocalGroup group : mLocalGroups) {
                                if (mLocalProfile.getLastSelectedGroupId() == group.getId()) {
                                    mSelectedGroup = group;
                                    break;
                                }
                            }

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

        Call<StatisticsDataWrapper> statisticsCall = mApiService.getStatistics(Auth.getInstance().getToken(getContext()),
                mSelectedGroup.getId());

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
        if (group != null)
            intent.putExtra(GroupFormActivity.INTENT_ITEM_ID, group.getId());
        startActivityForResult(intent, GROUP_CREATE_REQUEST);
    }

    void toProfileEdit() {
        Intent intent = new Intent(getContext(), ProfileEditActivity.class);
        startActivityForResult(intent, GROUP_CREATE_REQUEST);
    }

    void toLogin() {
        finish();
        startActivity(new Intent(getContext(), LoginActivity.class));
    }

    /**
     * Permission and results
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SHOPPING_ITEM_EDIT_REQUEST:
                if (resultCode == Activity.RESULT_OK)
                    loadLocalShoppingList();
                break;
            default:
                break;
        }
    }
}
