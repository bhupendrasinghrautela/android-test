package com.makaan.fragment.project;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.trend.ProjectPriceTrendEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.response.trend.PriceTrendData;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.MakaanLineChartView;
import com.makaan.ui.PriceTrendView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    @Bind(R.id.price_trend_view)
    LinearLayout priceTrendViewl;
    private ArrayList<Long> localities;
    private long projectId;
    private HashMap<PriceTrendKey, List<PriceTrendData>> localityPriceTrendsData;


    @Override
    protected int getContentViewId() {
        return R.layout.project_price_trends;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        new LocalityService().getTrendingSearchesInLocality(localityId);
        fetchData(12);
    }

    private void fetchData(final int months) {
        if(localities!=null)
            localities.add(localityId);
        else{
            localities = new ArrayList<>();
            localities.add(localityId);
        }
        ((PriceTrendService) MakaanServiceFactory.getInstance().getService(PriceTrendService.class)).getPriceTrendForLocalities(localities, months, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                ((PriceTrendService) MakaanServiceFactory.getInstance().getService(PriceTrendService.class)).getPriceTrendForProject(projectId, months);
                priceTrendViewl.setVisibility(View.VISIBLE);
                priceTrendView.bindView(localityPriceTrendDto.data);
                localityPriceTrendsData = localityPriceTrendDto.data;
            }});

    }

    @Subscribe
    public void onResult(ProjectPriceTrendEvent projectPriceTrendEvent){
        Set<PriceTrendKey> priceTrendKeySet = projectPriceTrendEvent.projectPriceTrendDto.data.keySet();
        for(PriceTrendKey key : priceTrendKeySet){
            localityPriceTrendsData.put(key,projectPriceTrendEvent.projectPriceTrendDto.data.get(key));
        }
        priceTrendView.bindView(localityPriceTrendsData);
    }
    private void initView() {
        localityId = getArguments().getLong("localityId");
        projectId = getArguments().getLong("projectId");
        title = getArguments().getString("title");
        localities = (ArrayList<Long>) getArguments().getSerializable("localities");
        pricePerUnit = getArguments().getInt("price");
        titleTv.setText(title);
        priceTv.setText(pricePerUnit == null ? "" : "\u20B9 " + pricePerUnit + " / sq ft");
    }

}
