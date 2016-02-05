package com.makaan.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.makaan.R;
import com.makaan.response.city.CityTrendData;
import com.makaan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by aishwarya on 24/01/16.
 */
public class MakaanBarChartView extends BaseLinearLayout<List<CityTrendData>>{

    @Bind(R.id.bar_chart)
    ColumnChartView mColumnChartView;
    private List<CityTrendData> mChartData;
    private List<Float> mAxisXValues;
    private List<AxisValue> mAxisYValues;
    private List<String> mAxisXLabels, mAxisYLabels;
    private List<Column> mColumns = new ArrayList<Column>();
    private ColumnChartData mColumnChartData;
    private Long mMaxListings;
    private OnBarTouchListener mListener;
    public interface OnBarTouchListener{
        public void onBarTouched(CityTrendData cityTrendData);
    }

    public MakaanBarChartView(Context context) {
        super(context);
    }

    public MakaanBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MakaanBarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(List<CityTrendData> item) {
        this.mChartData = item;
        init();
        generateDataForChart();
    }

    public void setListener(OnBarTouchListener listener){
        this.mListener = listener;
    }

    private void init() {
        mAxisXLabels = new ArrayList<>();
        mAxisYLabels = new ArrayList<>();
        mAxisXValues = new ArrayList<>();
        mAxisYValues = new ArrayList<>();
        mColumnChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    private void generateDataForChart() {
        if(mColumns == null || mAxisXValues == null || mAxisXLabels == null){
            return;
        }
        mColumns.clear();
        mAxisXLabels.clear();
        mAxisXValues.clear();
        List<SubcolumnValue> values;
        float id =0;
        mMaxListings = 0l;
        for(CityTrendData cityTrendData:mChartData){
            values = new ArrayList<>();
            values.add(new SubcolumnValue(cityTrendData.noOfListings, Color.parseColor("#FBA511")));
            if(mMaxListings<cityTrendData.noOfListings){
                mMaxListings = Long.valueOf(cityTrendData.noOfListings);
            }
            mAxisXValues.add(id++);
            mAxisXLabels.add(StringUtil.getDisplayPrice(cityTrendData.minPrice));
            Column column = new Column(values);
            mColumns.add(column);
        }
        mColumnChartView.setValueSelectionEnabled(false);
        mColumnChartData = new ColumnChartData(mColumns);
        Axis axisX = Axis.generateAxisFromCollection(mAxisXValues,mAxisXLabels);
        axisX.setHasTiltedLabels(true);
        axisX.setInside(false);
        axisX.setMaxLabelChars(6);
        Float gap = (float)mMaxListings/5;
        Float value;
        for(value = 0f;value<mMaxListings+gap;value=value+gap){
            AxisValue axisValue = new AxisValue(value);
            axisValue.setLabel(StringUtil.getDisplayPriceForChart(value));
            mAxisYValues.add(axisValue);
        }
        Axis axisY = new Axis(mAxisYValues);
        axisY.setHasLines(true);
        axisY.setHasTiltedLabels(true);
        axisY.setMaxLabelChars(5);
        axisY.setName("No. of Properties");
        mColumnChartData.setAxisYLeft(axisY);
        mColumnChartData.setAxisXBottom(axisX);
        mColumnChartView.setColumnChartData(mColumnChartData);
        Viewport v = mColumnChartView.getMaximumViewport();
        v.top = v.top+gap;
        mColumnChartView.setMaximumViewport(v);
        mColumnChartView.setCurrentViewport(v);
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {
        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            if(mListener!=null) {
                mListener.onBarTouched(mChartData.get(columnIndex));
            }
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
