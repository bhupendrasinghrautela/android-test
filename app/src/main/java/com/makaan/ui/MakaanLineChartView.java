package com.makaan.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.makaan.R;
import com.makaan.adapter.LegendAdapter;
import com.makaan.adapter.LegendAdapter.OnLegendsTouchListener;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.response.trend.PriceTrendData;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.ui.linechart.CustomLineChartView;
import com.makaan.ui.view.FontTextView;
import com.makaan.util.DateUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

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

/**
 * Created by aishwarya on 06/01/16.
 */
public class MakaanLineChartView extends BaseLinearLayout<HashMap<PriceTrendKey, List<PriceTrendData>>> {

    @Bind(R.id.line_chart)
    CustomLineChartView mLineChartView;
    @Bind(R.id.legends_grid)
    GridView mLegendsGrid;
    LineChartData mLineChartData;

    @Bind(R.id.header_text_no_data_graph)
    FontTextView mNoDataGraph;

    private List<Line> mLines = new ArrayList<Line>();
    private HashMap<PriceTrendKey, List<PriceTrendData>> mTrendsChartDataList;
    private LinkedHashSet<Float> mAxisXValues;
    private LinkedHashSet<String> mAxisXLabels;
    private ArrayList<AxisValue> mAxisYValues;
    private boolean setAxesForFirstTime = true;
    private Viewport mViewPort;
    private Long timeFrom;
    private Integer mMonths;
    private ArrayList<Long> mAllMonthsTime;
    private Long mMaxPrice;
    private Long projectId = null;
    private Long cityId, localityId;

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
        for (int i = 37; i >= 1; i--) {
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -i);
            c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_MONTH) + 1);
            mAllMonthsTime.add(c.getTimeInMillis());
        }
    }

    private void init() {
        mAxisXLabels = new LinkedHashSet<>();
        mAxisXValues = new LinkedHashSet<>();
        mAxisYValues = new ArrayList<>();
        generateDataForAllMonths();
        changeDataBasedOnTime(7);
    }

    private void generateDataForChart() {
        if (mLines == null || mAxisXValues == null || mAxisXLabels == null || mAllMonthsTime == null) {
            mNoDataGraph.setVisibility(VISIBLE);
            this.setVisibility(INVISIBLE);
            return;
        }
        this.setVisibility(VISIBLE);
        mNoDataGraph.setVisibility(GONE);

        mMaxPrice = 0l;
        mLines.clear();
        mAxisXLabels.clear();
        mAxisXValues.clear();
        mAxisYValues.clear();
        for (Map.Entry<PriceTrendKey, List<PriceTrendData>> entry : mTrendsChartDataList.entrySet()) {
            if (entry.getKey().isActive) {
                List<PointValue> pointValueList = new ArrayList<PointValue>();
                Collections.sort(entry.getValue());
                int current = 0;
                for (PriceTrendData trendsData : entry.getValue()) {
                    for (int i = current; i < mAllMonthsTime.size(); i++) {
                        if (mAllMonthsTime.get(i) > timeFrom) {
                            if (timeFrom < trendsData.date
                                    && DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)).equals(
                                    DateUtil.getMonthYearDisplayDate(trendsData.date))) {
                                pointValueList.add(new PointValue(mAllMonthsTime.get(i), trendsData.minPricePerUnitArea));
                                if (trendsData.minPricePerUnitArea > mMaxPrice) {
                                    mMaxPrice = trendsData.minPricePerUnitArea;
                                }
                                // if(setAxesForFirstTime) {
                                mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                                mAxisXValues.add((float) mAllMonthsTime.get(i));
                                current = i + 1;
                                break;
                            } else if (trendsData.date < mAllMonthsTime.get(i)) {
                                break;
                            }
                            pointValueList.add(new PointValue(mAllMonthsTime.get(i), -0.1f));
                            // if(setAxesForFirstTime) {
                            mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                            mAxisXValues.add((float) mAllMonthsTime.get(i));
                        }
                    }
                    //}
                }
                for (int i = current; i < mAllMonthsTime.size(); i++) {
                    if (mAllMonthsTime.get(i) > timeFrom) {
                        pointValueList.add(new PointValue(mAllMonthsTime.get(i), -0.1f));
                        // if(setAxesForFirstTime) {
                        mAxisXLabels.add(DateUtil.getMonthYearDisplayDate(mAllMonthsTime.get(i)));
                        mAxisXValues.add((float) mAllMonthsTime.get(i));
                    }
                }
                Line line = new Line(pointValueList);
                line.setColor(entry.getKey().colorId);
                line.setFilled(true);
                line.setCubic(true);
                line.setShape(ValueShape.CIRCLE);
                line.setPointRadius(2);
                mLines.add(line);
            }
        }
        List<PointValue> pointValueList = new ArrayList<PointValue>();
        pointValueList.add(new PointValue(mAllMonthsTime.get(mAllMonthsTime.size() - 1), 0));
        Line line = new Line(pointValueList);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            line.setColor(getResources().getColor(android.R.color.transparent, null));
        } else {
            line.setColor(getResources().getColor(android.R.color.transparent));
        }
        line.setFilled(true);
        line.setCubic(true);
        line.setShape(ValueShape.CIRCLE);
        line.setPointRadius(2);
        mLines.add(line);

        if (mLines.size() > 0) {

            mLineChartData = new LineChartData(mLines);
            List<Float> x = new ArrayList<>();
            x.addAll(mAxisXValues);
            List<String> y = new ArrayList<>();
            y.addAll(mAxisXLabels);
            Axis axisX = Axis.generateAxisFromCollection(x, y);
            axisX.setHasTiltedLabels(true);
            axisX.setTextSize(10);
            axisX.setMaxLabelChars(5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                axisX.setTextColor(getResources().getColor(R.color.listingBlack, null));
            } else {
                axisX.setTextColor(getResources().getColor(R.color.listingBlack));
            }
            mLineChartData.setAxisXBottom(axisX);

            Float gap = (float) findUpperLimitValue(mMaxPrice, 5) / 5;
            Float value;
            for (value = 0f; value < mMaxPrice + gap; value = value + gap) {
                AxisValue axisValue = new AxisValue(value);
                axisValue.setLabel(StringUtil.getDisplayPriceForChart(value));
                mAxisYValues.add(axisValue);
            }
            Axis axisY = new Axis(mAxisYValues);
            axisY.setHasLines(true);
            axisY.setHasTiltedLabels(true);
            axisY.setMaxLabelChars(5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                axisY.setTextColor(getResources().getColor(R.color.listingBlack, null));
            } else {
                axisY.setTextColor(getResources().getColor(R.color.listingBlack));
            }
            axisY.setName("price / sqft");
            mLineChartData.setAxisYLeft(axisY);
            mLineChartData.setBaseValue(Float.NEGATIVE_INFINITY);
            mLineChartView.setLineChartData(mLineChartData);
            Viewport v = mLineChartView.getMaximumViewport();
            v.top = v.top + gap;
            mLineChartView.setMaximumViewport(v);
            mLineChartView.setCurrentViewport(v);
            //if(!setAxesForFirstTime){
/*                mLineChartView.setMaximumViewport(mViewPort);
                mLineChartView.setCurrentViewport(mViewPort);*/
            //}
        }
        if (mLines == null || mLines.size() == 0) {
            mNoDataGraph.setVisibility(VISIBLE);
        } else {
            mNoDataGraph.setVisibility(GONE);
        }
    }

    private long findUpperLimitValue(Long price, int divisible) {
        if (price == 0) {
            return 0;
        }
        long temp = price;
        /*int count = 0;
        while(temp > 0) {
            if(temp > 100) {
                temp /= 10;
                count++;
            } else {
                temp++;
                while (temp % divisible != 0) {
                    temp++;
                }
                break;
            }
        }*/
        temp /= 1000;
        while (temp % divisible != 0) {
            temp++;
        }
        return (long) (temp * Math.pow(10, 3));
    }

    private void generateDataForGrid() {
        final List<PriceTrendKey> priceTrendKeyList = new ArrayList<>();
        int size = 0;
        for (Map.Entry<PriceTrendKey, List<PriceTrendData>> entry : mTrendsChartDataList.entrySet()) {
            PriceTrendKey priceTrendKey = entry.getKey();
            priceTrendKey.isActive = true;
            priceTrendKey.colorId = ChartUtils.COLORS[size++];
            priceTrendKeyList.add(entry.getKey());
        }
        final LegendAdapter adapter = new LegendAdapter(mContext, priceTrendKeyList);
        adapter.setLegendTouchListener(new OnLegendsTouchListener() {
            @Override
            public void legendTouched(View view, int position) {
                /*-------------------------------- track events------------------------------------*/
                if (projectId != null && projectId!=0) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL,
                                    String.format("%s_%s_%s",projectId ,priceTrendKeyList.get(position).label,
                                    (priceTrendKeyList.get(position).isActive?"select":"UnSelect")));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickProjectPriceTrends);
                }

                if(localityId!=null && localityId!=0){
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    properties.put(MakaanEventPayload.LABEL,
                            String.format("%s_%s_%s",localityId ,priceTrendKeyList.get(position).label,
                                    (priceTrendKeyList.get(position).isActive?"select":"UnSelect")));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickProjectPriceTrends);
                }

                if(cityId!=null && cityId!=0) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    properties.put(MakaanEventPayload.LABEL,
                            String.format("%s_%s_%s", cityId, priceTrendKeyList.get(position).label,
                                    (priceTrendKeyList.get(position).isActive ? "select" : "UnSelect")));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickProjectPriceTrends);
                }
                /*-----------------------------------------------------------------------------*/

                    generateDataForChart();
                if (mLines.size() == 1) {
                    view.performClick();

                }
            }
        });
        mLegendsGrid.setAdapter(adapter);
    }

    public void changeDataBasedOnTime(int months) {
        mMonths = months;
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -months);
        c.add(Calendar.DAY_OF_MONTH, -c.get(Calendar.DAY_OF_MONTH) + 1);
        timeFrom = c.getTimeInMillis();
        generateDataForChart();
    }

    public void setCityId(Long cityId){
        this.cityId=cityId;
    }

    public void setLocalityId(Long localityId){
        this.localityId=localityId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void onDestroyView() {
        if(mLegendsGrid != null && mLegendsGrid.getAdapter() instanceof LegendAdapter) {
            ((LegendAdapter)mLegendsGrid.getAdapter()).setLegendTouchListener(null);
        }

        if(mLineChartView != null) {
            mLineChartView.destroyView();
        }
    }
}
