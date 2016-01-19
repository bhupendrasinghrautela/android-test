package com.makaan.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.makaan.R;
import com.makaan.adapter.LegendAdapter;
import com.makaan.adapter.LegendAdapter.OnLegendsTouchListener;
import com.makaan.response.trend.PriceTrendData;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by aishwarya on 06/01/16.
 */
public class MakaanLineChartView extends BaseLinearLayout<HashMap<PriceTrendKey, List<PriceTrendData>>>{

    @Bind(R.id.line_chart)
    LineChartView mLineChartView;
    @Bind(R.id.legends_grid)
    GridView mLegendsGrid;
    LineChartData mLineChartData;

    private List<Line> mLines = new ArrayList<Line>();
    private HashMap<PriceTrendKey, List<PriceTrendData>> mTrendsChartDataList;
    private LinkedHashSet<Float> mAxisXValues, mAxisYValues;
    private LinkedHashSet<String> mAxisXLabels, mAxisYLabels;
    private boolean setAxesForFirstTime = true;
    private Viewport mViewPort;
    private Long timeFrom = 6l;

    public MakaanLineChartView(Context context) {
        this(context, null);
    }

    public MakaanLineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MakaanLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void bindView(HashMap<PriceTrendKey, List<PriceTrendData>> item) {
        mTrendsChartDataList = item;
        init();
        generateDataForGrid();
        generateDataForChart();
        setAxesForFirstTime = false;
        mViewPort = new Viewport(mLineChartView.getCurrentViewport());
    }

    private void init() {
        mAxisXLabels = new LinkedHashSet<>();
        mAxisYLabels = new LinkedHashSet<>();
        mAxisXValues = new LinkedHashSet<>();
        mAxisYValues = new LinkedHashSet<>();
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -6);
        timeFrom =c.getTimeInMillis();
    }

    private void generateDataForChart() {
        mLines.clear();
        mAxisXLabels.clear();
        mAxisXValues.clear();
        for (Map.Entry<PriceTrendKey, List<PriceTrendData>> entry : mTrendsChartDataList.entrySet())
        {
            if (entry.getKey().isActive) {
                List<PointValue> pointValueList = new ArrayList<PointValue>();
                Collections.sort(entry.getValue());
                for (PriceTrendData trendsData : entry.getValue()) {
                    if(timeFrom<trendsData.date) {
                        pointValueList.add(new PointValue(trendsData.date, trendsData.minPricePerUnitArea));
                        // if(setAxesForFirstTime) {
                        mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(trendsData.date));
                        mAxisXValues.add((float) trendsData.date);
                    }
                    //}
                }
                Line line = new Line(pointValueList);
                line.setColor(entry.getKey().colorId);
                line.setFilled(true);
                line.setCubic(true);
                line.setShape(ValueShape.CIRCLE);
                mLines.add(line);
        }
        }
        if(mLines.size()>0) {
            mLineChartData = new LineChartData(mLines);
            List<Float> x = new ArrayList<>();
            x.addAll(mAxisXValues);
            List<String> y = new ArrayList<>();
            y.addAll(mAxisXLabels);
            mLineChartData.setAxisXBottom(
                    Axis.generateAxisFromCollection(x, y));
            Axis axisY = new Axis().setHasLines(true);
            mLineChartData.setAxisYLeft(axisY);
            mLineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
            mLineChartView.setLineChartData(mLineChartData);
            //if(!setAxesForFirstTime){
/*                mLineChartView.setMaximumViewport(mViewPort);
                mLineChartView.setCurrentViewport(mViewPort);*/
            //}
        }
    }

    private void generateDataForGrid() {
        List<PriceTrendKey> priceTrendKeyList = new ArrayList<>();
        int size = 0;
        for (Map.Entry<PriceTrendKey, List<PriceTrendData>> entry : mTrendsChartDataList.entrySet())
        {
            PriceTrendKey priceTrendKey = entry.getKey();
            priceTrendKey.isActive = true;
            priceTrendKey.colorId = ChartUtils.COLORS[size++];
            priceTrendKeyList.add(entry.getKey());
        }
        final LegendAdapter adapter = new LegendAdapter(mContext,priceTrendKeyList);
        adapter.setLegendTouchListener(new OnLegendsTouchListener() {
            @Override
            public void legendTouched(View view) {
                generateDataForChart();
                if(mLines.size()==0){
                    view.performClick();
                }
            }
        });
        mLegendsGrid.setAdapter(adapter);
    }

    public void changeDataBasedOnTime(int months){
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -months);
        timeFrom =c.getTimeInMillis();
        generateDataForChart();
    }
}
