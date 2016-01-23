package com.makaan.fragment.locality;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.locality.TrendingSearchLocalityEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.LocalityService;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.PriceTrendView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

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

    @Bind(R.id.price_trend_view)
    public PriceTrendView priceTrendView;
    @Bind(R.id.price_range_tabs)
    public TabLayout tabLayout;
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
    private LinearLayoutManager mLayoutManager;
    private PopularSearchesAdapter mAdapter;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_price_trends;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        new LocalityService().getTrendingSearchesInLocality(localityId);
        fetchData(12);
    }

    private void fetchData(int months) {
        ArrayList<Long> localityIds = new ArrayList<>();
        localityIds.add(localityId);
        localityIds.add((long) 53250);
        new PriceTrendService().getPriceTrendForLocalities(localityIds, months, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                priceTrendView.bindView(localityPriceTrendDto);
            }
        });

    }

    @Subscribe
    public void onResults(TrendingSearchLocalityEvent searchResultEvent){
        initPopularSearches(searchResultEvent.trendingSearches);
    }

    private void initView() {
        localityId = getArguments().getLong("localityId");
        title = getArguments().getString("title");
        localityId = getArguments().getLong("localityId");
        primaryMedian = getArguments().getInt("primaryMedian");
        primaryRise = getArguments().getDouble("primaryRise");
        secondaryAverage = getArguments().getDouble("secondaryAverage");
        secondaryMedian = getArguments().getInt("secondaryMedian");

        titleTv.setText(title);
        trendsMedianTv.setText("the median home value in electronic city is "+primaryMedian+" / sq ft.");
        trendsGrowthTv.setText("electronic city home values have gone up +" + primaryRise + " over the past year and makaan predicts they will rise around 0% withing the next year.");
        trendsRentGrowthTv.setText("the average rent price in electronic city is " + secondaryAverage + " which is higher than city median of " + secondaryMedian);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fetchData(getMonths(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PopularSearchesAdapter(searchResponseItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class PopularSearchesAdapter extends RecyclerView.Adapter<PopularSearchesAdapter.ViewHolder> {
        private List<SearchResponseItem> list;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView descriptionTv;

            public ViewHolder(View v) {
                super(v);
                descriptionTv = (TextView) v.findViewById(R.id.tv_localities_recent_searches_desc);
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
            //TODO: Picasso load imgurl
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }
}