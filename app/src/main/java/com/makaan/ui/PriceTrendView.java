package com.makaan.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.makaan.R;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.segment.analytics.Properties;

import butterknife.Bind;
import butterknife.OnClick;

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
                if (getContext() instanceof LocalityActivity) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    properties.put(MakaanEventPayload.LABEL,TRENDS_MONTH.values()[tab.getPosition()].toString().replace("\n","") );
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickLocalityPriceTrends);
                }
                else if (getContext() instanceof CityActivity) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                    properties.put(MakaanEventPayload.LABEL,TRENDS_MONTH.values()[tab.getPosition()].toString().replace("\n","") );
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickCityPriceTrends);
                }
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
