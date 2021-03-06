package com.makaan.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.segment.analytics.Properties;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/01/16.
 */
public class PriceTrendView extends BaseLinearLayout<LocalityPriceTrendDto> {
    @Bind(R.id.price_range_tabs)
    TabLayout mPriceRangeTabs;
    @Bind(R.id.line_chart_layout)
    MakaanLineChartView mTrendsChart;
    private Long cityId, localityId;

    public void onDestroyView() {
        if(mPriceRangeTabs != null) {
            mPriceRangeTabs.setOnTabSelectedListener(null);
        }
        if(mTrendsChart != null) {
            mTrendsChart.onDestroyView();
        }
    }


    private enum TRENDS_MONTH{
        SIX("6 \n months",7),
        TWELVE("12 \n months",13),
        THIRTY_SIX("3 \n years",37);

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
                /*----- track events----------------------------*/
                if (ScreenNameConstants.SCREEN_NAME_LOCALITY.equalsIgnoreCase(((BaseJarvisActivity)getContext()).getScreenName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    properties.put(MakaanEventPayload.LABEL, TRENDS_MONTH.values()[tab.getPosition()].toString().replace("\n", ""));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickLocalityPriceTrends);
                } else if (ScreenNameConstants.SCREEN_NAME_CITY.equalsIgnoreCase(((BaseJarvisActivity)getContext()).getScreenName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                    properties.put(MakaanEventPayload.LABEL, TRENDS_MONTH.values()[tab.getPosition()].toString().replace("\n", ""));
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickCityPriceTrends);
                }
                /*----------------------------------------------*/

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
        if(cityId!=null&& cityId!=0){
            mTrendsChart.setCityId(cityId);
        }
        if(localityId!=null&& localityId!=0){
            mTrendsChart.setCityId(localityId);
        }
    }

    private void initView() {
        for(TRENDS_MONTH priceTabText:TRENDS_MONTH.values()){
            TextView text = new TextView(mContext);
            SpannableString ss1=  new SpannableString(priceTabText.toString());
            ss1.setSpan(new RelativeSizeSpan(1.5f), 0,2, 0);
            text.setText(ss1);
            text.setGravity(Gravity.CENTER);
            Tab tab = mPriceRangeTabs.newTab();
            tab.setCustomView(text);
            mPriceRangeTabs.addTab(tab);
        }
    }

    public void setCityId(Long cityId){
        this.cityId=cityId;
    }

    public void setLocalityId(Long localityId){
        this.localityId=localityId;
    }
}
