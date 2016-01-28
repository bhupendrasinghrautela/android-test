package com.makaan.fragment.project;

import android.os.Bundle;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.LocalityService;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.MakaanLineChartView;
import com.makaan.ui.PriceTrendView;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectPriceTrendsFragment extends MakaanBaseFragment {
    private String title;
    private Long localityId;
    private Integer pricePerUnit;

    @Bind(R.id.line_chart_layout)
    public MakaanLineChartView priceTrendView;
    @Bind(R.id.header_text)
    public TextView titleTv;
    @Bind(R.id.tv_price_trend_price)
    public TextView priceTv;


    @Override
    protected int getContentViewId() {
        return R.layout.project_price_trends;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        new LocalityService().getTrendingSearchesInLocality(localityId);
        fetchData(60);
    }

    private void fetchData(int months) {
        ArrayList<Long> localityIds = new ArrayList<>();
        localityIds.add(localityId);
        new PriceTrendService().getPriceTrendForLocalities(localityIds, months, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                priceTrendView.bindView(localityPriceTrendDto.data);
            }
        });

    }
    private void initView() {
        localityId = getArguments().getLong("localityId");
        title = getArguments().getString("title");
        pricePerUnit = getArguments().getInt("price");
        titleTv.setText(title);
        priceTv.setText(pricePerUnit == null ? "" : "\u20B9 "+pricePerUnit+" / sq ft");
    }

}
