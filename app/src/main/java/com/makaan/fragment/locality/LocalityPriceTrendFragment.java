package com.makaan.fragment.locality;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.locality.TrendingSearchLocalityEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.PriceTrendView;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/20/16.
 */
public class LocalityPriceTrendFragment extends MakaanBaseFragment{
    private String title;
    private Long localityId;
    private int primaryMedian;
    private Double primaryRise;
    private Double secondaryAverage;
    private int secondaryMedian;
    private ArrayList<Long> locality;

    @Bind(R.id.price_trend_view)
    public PriceTrendView priceTrendView;
    @Bind(R.id.tv_price_trends_median_desc)
    public TextView trendsMedianTv;
    @Bind(R.id.tv_price_trends_growth_desc)
    public TextView trendsGrowthTv;
    @Bind(R.id.tv_price_trends_rent_growth_desc)
    public TextView trendsRentGrowthTv;
    @Bind(R.id.header_text)
    public TextView titleTv;
    @Bind(R.id.rv_localities_recent_searches)
    RecyclerView mRecyclerView;
    @Bind(R.id.header_text_popular_searches)
    public TextView popularSearchesTv;
    @Bind(R.id.ll_price_trends_first)
    LinearLayout priceTrendsFirstLl;
    @Bind(R.id.ll_price_trends_second)
    LinearLayout priceTrendsSecondLl;
    @Bind(R.id.ll_price_trends_third)
    LinearLayout priceTrendsThirdLl;


    private LinearLayoutManager mLayoutManager;
    private PopularSearchesAdapter mAdapter;
    private String localityName;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_price_trends;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getTrendingSearchesInLocality(SerpObjects.isBuyContext(getContext()), localityId);
        fetchData(36);
    }

    private void fetchData(int months) {
        if(locality!=null)
            locality.add(localityId);
        else{
            locality = new ArrayList<>();
            locality.add(localityId);
        }
        new PriceTrendService().getPriceTrendForLocalities(locality, months, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                if (localityPriceTrendDto.data != null && localityPriceTrendDto.data.size() != 0) {
                    priceTrendView.setVisibility(View.VISIBLE);
                    priceTrendView.bindView(localityPriceTrendDto);
                } else
                    priceTrendView.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void onResults(TrendingSearchLocalityEvent searchResultEvent){
        if(null== searchResultEvent || null!=searchResultEvent.error){
            //TODO handle error
            return;
        }
        initPopularSearches(searchResultEvent.trendingSearches);
    }

    private void initView() {
        localityId = getArguments().getLong("localityId");
        title = getArguments().getString("title");
        locality = (ArrayList<Long>) getArguments().getSerializable("locality");
        primaryMedian = getArguments().getInt("primaryMedian");
        primaryRise = getArguments().getDouble("primaryRise");
        secondaryAverage = getArguments().getDouble("secondaryAverage");
        secondaryMedian = getArguments().getInt("secondaryMedian");
        localityName = getArguments().getString("localityName");

        titleTv.setText(title);
        if(primaryMedian != 0) {
            trendsMedianTv.setText("the average home value in " + localityName + " is " + primaryMedian + " / sq ft.");
        }else{
            priceTrendsFirstLl.setVisibility(View.GONE);
        }
        if(primaryRise != 0)
            trendsGrowthTv.setText(localityName+" home values have gone up +" + primaryRise + " over the past year");
        else{
            priceTrendsSecondLl.setVisibility(View.GONE);
        }
        if(secondaryAverage !=0 && secondaryMedian!=0)
            trendsRentGrowthTv.setText("the average rent price in "+localityName+" is " + secondaryAverage + " which is higher than city average of " + secondaryMedian);
        else{
            priceTrendsThirdLl.setVisibility(View.GONE);
        }
    }

    private int getMonths(int position) {
        int months = 12;
        switch (position){
            case 0:
                months = 6;
                break;
            case 1:
                months = 12;
                break;
            case 2:
                months = 36;
                break;
            case 3:
                months = 50;
                break;
        }
        return months;
    }

    private void initPopularSearches(List<SearchResponseItem> searchResponseItems) {
        if(searchResponseItems!=null && searchResponseItems.size()>0) {
            popularSearchesTv.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new PopularSearchesAdapter(searchResponseItems);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class PopularSearchesAdapter extends RecyclerView.Adapter<PopularSearchesAdapter.ViewHolder> {
        private List<SearchResponseItem> list;

        public class ViewHolder extends RecyclerView.ViewHolder{
            // each data item is just a string in this case
            public TextView descriptionTv;

            public ViewHolder(View view) {
                super(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SerpRequest request = new SerpRequest(SerpActivity.TYPE_SUGGESTION);
                        //TODO set serp request suggestion type
                        request.launchSerp(getActivity());
                    }
                });
                descriptionTv = (TextView) view.findViewById(R.id.tv_localities_recent_searches_desc);
            }
        }

        public PopularSearchesAdapter(List<SearchResponseItem> list) {
            this.list = list;
        }

        @Override
        public PopularSearchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_recent_searches, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final SearchResponseItem responseItem = list.get(position);
            holder.descriptionTv.setText(responseItem.displayText);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    @OnClick(R.id.button_show_properties)
    public void showAllPropertiesClick(){
        if(localityId!=null){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.propertiesForSale+String.valueOf(localityId));
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickLocalityPriceTrends);
        }
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_LOCALITY);
        request.setLocalityId(localityId);
        request.launchSerp(getActivity());
    }
}