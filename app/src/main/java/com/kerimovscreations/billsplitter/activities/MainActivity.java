package com.kerimovscreations.billsplitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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
import com.kerimovscreations.billsplitter.adapters.TimelineRVAdapter;
import com.kerimovscreations.billsplitter.models.Timeline;
import com.kerimovscreations.billsplitter.tools.BaseActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.rvTimeline)
    RecyclerView mRVTimeline;
    @BindView(R.id.any_chart_view)
    AnyChartView mAnyChartView;

    private TimelineRVAdapter mTimelineAdapter;

    private ArrayList<Timeline> mTimeline;

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
    }

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

        pie.background().fill("#eeeeee 1.0");

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
}
