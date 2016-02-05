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
import com.makaan.util.StringUtil;

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
import lecho.lib.hellocharts.model.AxisValue;
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
    private LinkedHashSet<Float> mAxisXValues;
    private LinkedHashSet<String> mAxisXLabels;
    private ArrayList<AxisValue>mAxisYValues;
    private boolean setAxesForFirstTime = true;
    private Viewport mViewPort;
    private Long timeFrom = 6l;
    private Integer mMonths;
    private ArrayList<Long> mAllMonthsTime;
    private Long mMaxPrice;

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

    private void generateDataForAllMonths() {
        mAllMonthsTime = new ArrayList<>();
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        for(int i =59;i>=0;i--){
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -i);
            mAllMonthsTime.add(c.getTimeInMillis());
        }
    }

    private void init() {
        mAxisXLabels = new LinkedHashSet<>();
        mAxisXValues = new LinkedHashSet<>();
        mAxisYValues = new ArrayList<>();
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -6);
        timeFrom =c.getTimeInMillis();
        generateDataForAllMonths();
    }

    private void generateDataForChart() {
        if(mLines == null || mAxisXValues == null || mAxisXLabels == null){
            return;
        }
        mMaxPrice = 0l;
        mLines.clear();
        mAxisXLabels.clear();
        mAxisXValues.clear();
        mAxisYValues.clear();
        for (Map.Entry<PriceTrendKey, List<PriceTrendData>> entry : mTrendsChartDataList.entrySet())
        {
            if (entry.getKey().isActive) {
                List<PointValue> pointValueList = new ArrayList<PointValue>();
                Collections.sort(entry.getValue());
                int current = 0;
                for (PriceTrendData trendsData : entry.getValue()) {
                    for(int i=current;i<mAllMonthsTime.size();i++){
                        if(mAllMonthsTime.get(i)>timeFrom){
                            if(timeFrom<trendsData.date
                                    &&  DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)).equals(
                                    DateUtil.getMonthYearDisplayDate(trendsData.date))) {
                                pointValueList.add(new PointValue(mAllMonthsTime.get(i), trendsData.minPricePerUnitArea));
                                if(trendsData.minPricePerUnitArea > mMaxPrice){
                                    mMaxPrice = trendsData.minPricePerUnitArea;
                                }
                                // if(setAxesForFirstTime) {
                                mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                                mAxisXValues.add((float) mAllMonthsTime.get(i));
                                current = i+1;
                                break;
                            }
                            else if(trendsData.date<mAllMonthsTime.get(i)){
                                break;
                            }
                            pointValueList.add(new PointValue(mAllMonthsTime.get(i),-0.1f));
                            // if(setAxesForFirstTime) {
                            mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                            mAxisXValues.add((float)mAllMonthsTime.get(i));
                        }
                    }
                    //}
                }
                for(int i=current;i<mAllMonthsTime.size();i++){
                    pointValueList.add(new PointValue(mAllMonthsTime.get(i),-0.1f));
                    // if(setAxesForFirstTime) {
                    mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                    mAxisXValues.add((float)mAllMonthsTime.get(i));
                }
                Line line = new Line(pointValueList);
                line.setColor(entry.getKey().colorId);
                line.setFilled(true);
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
            Axis axisX = Axis.generateAxisFromCollection(x, y);
            axisX.setHasTiltedLabels(true);
            axisX.setMaxLabelChars(5);
            mLineChartData.setAxisXBottom(axisX);
            Float gap = (float)mMaxPrice/5;
            Float value;
            for(value = 0f;value<mMaxPrice+gap;value=value+gap){
                AxisValue axisValue = new AxisValue(value);
                axisValue.setLabel(StringUtil.getDisplayPriceForChart(value));
                mAxisYValues.add(axisValue);
            }
            Axis axisY = new Axis(mAxisYValues);
            axisY.setHasLines(true);
            axisY.setHasTiltedLabels(true);
            axisY.setMaxLabelChars(5);
            mLineChartData.setAxisYLeft(axisY);
            mLineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
            mLineChartView.setLineChartData(mLineChartData);
            Viewport v = mLineChartView.getMaximumViewport();
            v.top = v.top+gap;
            mLineChartView.setMaximumViewport(v);
            mLineChartView.setCurrentViewport(v);
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
        mMonths = months;
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -months);
        timeFrom =c.getTimeInMillis();
        generateDataForChart();
    }
}
