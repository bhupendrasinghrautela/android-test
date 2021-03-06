package com.makaan.fragment.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.trend.ProjectPriceTrendEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.response.trend.PriceTrendData;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.MakaanLineChartView;
import com.makaan.util.DateUtil;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
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
    private String projectName;
    private Long localityId;
    private Boolean hasIncreased;
    private Integer pricePerUnit;

    @Bind(R.id.line_chart_layout)
    public MakaanLineChartView priceTrendView;
    @Bind(R.id.header_text)
    public TextView titleTv;
    @Bind(R.id.price_change)
    ImageView priceChange;
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
//        ((LocalityService)MakaanServiceFactory.getInstance().getService(LocalityService.class)).getTrendingSearchesInLocality(SerpObjects.isBuyContext(getContext()), localityId);
        fetchData(12);
    }

    private void fetchData(final int months) {
        if(localities!=null)
            localities.add(localityId);
        else{
            localities = new ArrayList<>();
            localities.add(localityId);
        }
        final String minTime = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getDateMonthsBack(0));
        final String maxTime = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getDateMonthsBack(13));
        ((PriceTrendService) MakaanServiceFactory.getInstance().getService(PriceTrendService.class)).getPriceTrendForLocalities(localities, minTime,maxTime, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                if(localityPriceTrendDto!=null && localityPriceTrendDto.data!=null && !localityPriceTrendDto.data.isEmpty()) {
                    ((PriceTrendService) MakaanServiceFactory.getInstance().getService(PriceTrendService.class)).getPriceTrendForProject(projectId,minTime,maxTime);
                    priceTrendViewl.setVisibility(View.VISIBLE);
                    priceTrendView.setProjectId(projectId);
                    priceTrendView.bindView(localityPriceTrendDto.data);
                    priceTrendView.changeDataBasedOnTime(13);
                    localityPriceTrendsData = localityPriceTrendDto.data;
                }
            }});

    }

    @Subscribe
    public void onResult(ProjectPriceTrendEvent projectPriceTrendEvent){
        if(!isVisible()) {
            return;
        }
        if(projectPriceTrendEvent == null || projectPriceTrendEvent.error!=null){
            return;
        }
        if(localityPriceTrendsData == null){
            return;
        }
        if(projectPriceTrendEvent.projectPriceTrendDto!=null && projectPriceTrendEvent.projectPriceTrendDto.data!=null) {
            Set<PriceTrendKey> priceTrendKeySet = projectPriceTrendEvent.projectPriceTrendDto.data.keySet();
            for (PriceTrendKey key : priceTrendKeySet) {
                key.label = projectName;
                localityPriceTrendsData.put(key, projectPriceTrendEvent.projectPriceTrendDto.data.get(key));
            }
            priceTrendView.bindView(localityPriceTrendsData);
            priceTrendView.changeDataBasedOnTime(13);
        }
    }
    private void initView() {
        if(getArguments() == null) {
            return;
        }
        localityId = getArguments().getLong("localityId");
        projectId = getArguments().getLong("projectId");
        title = getArguments().getString("title");
        projectName = getArguments().getString("projectName");
        localities = (ArrayList<Long>) getArguments().getSerializable("localities");
        pricePerUnit = getArguments().getInt("price");
        hasIncreased = getArguments().getBoolean("increased");
        if(!TextUtils.isEmpty(title)) {
            titleTv.setText(title.toLowerCase());
        } else {
            titleTv.setText("");
        }
        if(pricePerUnit!=null && pricePerUnit>0) {
            priceTv.setText(pricePerUnit == null ? "" : "\u20B9 " + pricePerUnit + " / sq ft");
            if (hasIncreased != null && hasIncreased) {
                priceChange.setImageResource(R.drawable.bottom_arrow_circle);
            } else {
                priceChange.setImageResource(R.drawable.bottom_arrow_circle_green);
            }
        }
        priceTrendViewl.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        if(priceTrendView != null) {
            priceTrendView.onDestroyView();
        }
        super.onDestroyView();
    }
}
