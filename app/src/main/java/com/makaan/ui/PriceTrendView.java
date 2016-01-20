package com.makaan.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.util.AttributeSet;

import com.makaan.R;
import com.makaan.response.trend.LocalityPriceTrendDto;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/01/16.
 */
public class PriceTrendView extends BaseLinearLayout<LocalityPriceTrendDto> {
    @Bind(R.id.price_range_tabs)
    TabLayout mPriceRangeTabs;
    @Bind(R.id.line_chart_layout)
    MakaanLineChartView mTrendsChart;
    private enum TRENDS_MONTH{
        SIX("6 \n months",6),
        TWELVE("12 \n months",12),
        THIRTY_SIX("3 \n years",36),
        SIXTY("5 \n years",60);

        private  String name;
        private int value;
        TRENDS_MONTH(String string, int value) {
            this.name = string;
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getValue(){
            return value;
        }
    }

    public PriceTrendView(Context context) {
        super(context);
    }

    public PriceTrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PriceTrendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        initListener();
    }

    private void initListener() {
        mPriceRangeTabs.setOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                mTrendsChart.changeDataBasedOnTime(
                        TRENDS_MONTH.values()[tab.getPosition()].getValue());
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    @Override
    public void bindView(LocalityPriceTrendDto item) {
        mTrendsChart.bindView(item.data);
    }

    private void initView() {
        for(TRENDS_MONTH priceTabText:TRENDS_MONTH.values()){
            mPriceRangeTabs.addTab(mPriceRangeTabs.newTab().setText(priceTabText.toString()));
        }
    }
}
