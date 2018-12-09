package com.kerimovscreations.billsplitter.activities;

import android.content.Intent;
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
import com.kerimovscreations.billsplitter.models.Currency;
import com.kerimovscreations.billsplitter.models.Group;
import com.kerimovscreations.billsplitter.models.LocalGroup;
import com.kerimovscreations.billsplitter.models.LocalProfile;
import com.kerimovscreations.billsplitter.models.Person;
import com.kerimovscreations.billsplitter.models.ShoppingItem;
import com.kerimovscreations.billsplitter.models.Timeline;
import com.kerimovscreations.billsplitter.utils.Auth;
import com.kerimovscreations.billsplitter.utils.BaseActivity;
import com.kerimovscreations.billsplitter.wrappers.CurrencyListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.GroupListDataWrapper;
import com.kerimovscreations.billsplitter.wrappers.ShoppingItemListDataWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
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

    @BindView(R.id.pie_chart)
    PieChart mPieChart;

    @BindView(R.id.rvActiveList)
    RecyclerView mRVActiveList;

    @BindView(R.id.rvCompletedList)
    RecyclerView mRVCompletedList;

    @BindView(R.id.completed_list_drop_down_ic)
    ImageView mCompletedListDropDownIc;

    @BindView(R.id.group_content)
    View mGroupContent;

    @BindView(R.id.add_item_btn)
    View mAddItemBtn;

    @BindView(R.id.empty_content_placeholder)
    View mEmptyContentPlaceholder;

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
            } else {
                mGroupContent.setVisibility(View.GONE);
                mAddItemBtn.setVisibility(View.GONE);
                mEmptyContentPlaceholder.setVisibility(View.VISIBLE);
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
        getCurrencies();

        getData();
    }

    void getData() {
        if (mSelectedGroup != null) {
            getGroupItems();
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

        setupPieChart();

        setupActiveList();
        setupCompletedList();
    }

    void setupPieChart() {
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
                // TODO: API Integration
            }

            @Override
            public void onEdit() {
                toGroupForm(new Group());
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

    @OnClick(R.id.create_group_btn)
    void onCreateGroup() {
        toGroupForm(null);
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
                            realm.delete(LocalGroup.class);
                            for (Group tempGroup : response.body().getList()) {
                                realm.copyToRealm(new LocalGroup(tempGroup));
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
                                mGroupContent.setVisibility(View.VISIBLE);
                                mAddItemBtn.setVisibility(View.VISIBLE);
                                mEmptyContentPlaceholder.setVisibility(View.GONE);
                                getGroupItems();
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
                        for (ShoppingItem shoppingItem : response.body().getList()) {
                            if (shoppingItem.isComplete()) {
                                mActiveShoppingList.add(shoppingItem);
                            } else {
                                mCompletedShoppingList.add(shoppingItem);
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
            public void onFailure(@NonNull Call<ShoppingItemListDataWrapper> call, @NonNull Throwable t) {
                t.printStackTrace();

                if (!call.isCanceled()) {
                    runOnUiThread(() -> Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void getCurrencies() {
        Call<CurrencyListDataWrapper> call = mApiService.getCurrencies(Auth.getInstance().getToken(getContext()), "", 1);

        call.enqueue(new Callback<CurrencyListDataWrapper>() {
            @Override
            public void onResponse(@NonNull Call<CurrencyListDataWrapper> call, @NonNull Response<CurrencyListDataWrapper> response) {
                runOnUiThread(() -> showProgress(false));

                if (response.isSuccessful() && response.body() != null) {
                    getRealm().executeTransaction(realm -> {
                        realm.delete(Currency.class);
                        realm.copyToRealm(response.body().getList());
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
            public void onFailure(@NonNull Call<CurrencyListDataWrapper> call, @NonNull Throwable t) {
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
                // TODO: Update UI
                break;
            default:
                break;
        }
    }
}
