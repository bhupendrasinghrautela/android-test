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
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by aishwarya on 24/01/16.
 */
public class MakaanBarChartView extends BaseLinearLayout<List<CityTrendData>>{

    @Bind(R.id.bar_chart)
    ColumnChartView mColumnChartView;
    private List<CityTrendData> mChartData;
    private List<Float> mAxisXValues, mAxisYValues;
    private List<String> mAxisXLabels, mAxisYLabels;
    private List<Column> mColumns = new ArrayList<Column>();
    private ColumnChartData mColumnChartData;

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

    private void init() {
        mAxisXLabels = new ArrayList<>();
        mAxisYLabels = new ArrayList<>();
        mAxisXValues = new ArrayList<>();
        mAxisYValues = new ArrayList<>();
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
        for(CityTrendData cityTrendData:mChartData){
            values = new ArrayList<>();
            values.add(new SubcolumnValue(cityTrendData.noOfListings, Color.parseColor("#FBA511")));
            mAxisXValues.add(id++);
            mAxisXLabels.add(StringUtil.getDisplayPrice(cityTrendData.minPrice)+"\n-\n"+StringUtil.getDisplayPrice(cityTrendData.maxPrice));
            Column column = new Column(values);
            mColumns.add(column);
        }
        mColumnChartView.setValueSelectionEnabled(false);
        mColumnChartData = new ColumnChartData(mColumns);
        Axis axisX = Axis.generateAxisFromCollection(mAxisXValues,mAxisXLabels);
        axisX.setHasTiltedLabels(true);
        axisX.setInside(false);
        Axis axisY = new Axis().setHasLines(true);
        mColumnChartData.setAxisXBottom(axisX);
        mColumnChartData.setAxisYLeft(axisY);
        mColumnChartView.setColumnChartData(mColumnChartData);
    }
}
