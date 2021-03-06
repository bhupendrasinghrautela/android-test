package com.makaan.fragment.locality;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.amenity.AmenityCardView;
import com.makaan.ui.amenity.AmenityCardView.AmenityCardViewCallBack;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

//import com.makaan.event.project.OnSeeOnMapClicked;

/**
 * Created by tusharchaudhary on 1/20/16.
 */
public class KynFragment extends MakaanBaseFragment {
    @Bind(R.id.rv_localities_kyn)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_localities_kyn_title)
    TextView titleTv;
    @Bind(R.id.kyn_see_on_map_container)
    LinearLayout seeOnMapLl;
    private LinearLayoutManager mLayoutManager;
    private KnowYourNeighbourhoodAdapter mAdapter;
    private String title;
    private Context context ;
    private List<AmenityCluster> amenityClusters;
    private OverviewFragment.OverviewActivityCallbacks mActivityCallbacks;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_kyn;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
    }

    @OnClick(R.id.amenity_see_on_map)
    public void onSeeMapClicked(){
        if(mActivityCallbacks != null) {
            mActivityCallbacks.showMapFragment();
        }
    }

    private void initView() {
        title = getArguments().getString("title");
        titleTv.setText(title);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<AmenityCluster> amenityClusters, OverviewFragment.OverviewActivityCallbacks overviewActivityCallbacks) {
        this.amenityClusters = amenityClusters;
        this.mActivityCallbacks = overviewActivityCallbacks;
        if(amenityClusters.size()==0)
            seeOnMapLl.setVisibility(View.GONE);
        mAdapter = new KnowYourNeighbourhoodAdapter(amenityClusters);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }



    private class KnowYourNeighbourhoodAdapter extends RecyclerView.Adapter<KnowYourNeighbourhoodAdapter.ViewHolder> {
        private List<AmenityCluster> amenityClusters;

        public class ViewHolder extends RecyclerView.ViewHolder implements AmenityCardViewCallBack {
            // each data item is just a string in this case
            public AmenityCardView amenityCardView;
            private int mPosition;
            public ViewHolder(View v) {
                super(v);
                amenityCardView = (AmenityCardView) v.findViewById(R.id.amenity_card_view);
                amenityCardView.setCallback(this);
            }

            public void setPosition(int position){
                mPosition = position;
            }

            @Override
            public void onAmenityDistanceClicked(String placeName) {
                AmenityCluster amenityCluster = amenityClusters.get(mPosition);
                mActivityCallbacks.showMapFragmentWithSpecificAmenity(mPosition,placeName);
            }
        }

        public KnowYourNeighbourhoodAdapter(List<AmenityCluster> amenityClusters) {
            this.amenityClusters = amenityClusters;
        }

        @Override
        public KnowYourNeighbourhoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_kyn, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final AmenityCluster amenityCluster = amenityClusters.get(position);
            holder.setPosition(position);
            holder.amenityCardView.bindView(context,amenityCluster);
        }

        @Override
        public int getItemCount() {
            return amenityClusters.size();
        }

    }

}

