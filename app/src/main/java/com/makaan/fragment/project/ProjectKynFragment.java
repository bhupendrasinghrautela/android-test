package com.makaan.fragment.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.event.project.OnSeeOnMapClicked;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.amenity.AmenityCardView;
import com.makaan.util.AppBus;
import com.makaan.util.KeyUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectKynFragment extends MakaanBaseFragment{
    @Bind(R.id.rv_localities_kyn)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_localities_kyn_title)
    TextView titleTv;
    @Bind(R.id.tv_localities_kyn_description)
    TextView descriptionTv;
    @Bind(R.id.locality_score_text)
    TextView scoreTv;
    @Bind(R.id.locality_score_progress)
    ProgressBar scoreProgress;
    @Bind(R.id.rl_locality_score_container)
    RelativeLayout scoreRl;
    @Bind(R.id.kyn_see_on_map_container)
    LinearLayout seeOnMapLl;

    private LinearLayoutManager mLayoutManager;
    private KnowYourNeighbourhoodAdapter mAdapter;
    private String title;
    private String description;
    private Double score;
    private Context context;
    private List<AmenityCluster> amenityClusters;
    private long localityId;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project_kyn;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
    }

    @OnClick(R.id.more_about_locality)
    public void openLocality(){
        Intent intent = new Intent(getActivity(), LocalityActivity.class);
        intent.putExtra(KeyUtil.LOCALITY_ID, localityId);
        startActivity(intent);
    }

    private void initView() {
        if(getArguments() == null){
            return;
        }
        title = getArguments().getString("title");
        localityId = getArguments().getLong("localityId");
        description = getArguments().getString("description");
        score = getArguments().getDouble("score");
        if(score==null || score==0)
            scoreRl.setVisibility(View.GONE);
        else {
            scoreTv.setText(""+score);
            scoreProgress.setProgress((int) (score*10));
        }
        if(description!=null) {
            descriptionTv.setText(Html.fromHtml(description));
        }
        titleTv.setText(title);
        if(amenityClusters!=null && amenityClusters.size()>0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void setData(List<AmenityCluster> amenityClusters) {
        this.amenityClusters = amenityClusters;
        if(amenityClusters.size()==0) {
            seeOnMapLl.setVisibility(View.GONE);
        }
        mAdapter = new KnowYourNeighbourhoodAdapter(this.amenityClusters);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }


    private class KnowYourNeighbourhoodAdapter extends RecyclerView.Adapter<KnowYourNeighbourhoodAdapter.ViewHolder> {
        private List<AmenityCluster> amenityClusters;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public AmenityCardView amenityCardView;

            public ViewHolder(View v) {
                super(v);
                amenityCardView = (AmenityCardView) v.findViewById(R.id.amenity_card_view);
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
            holder.amenityCardView.bindView(context,amenityCluster);
        }

        @Override
        public int getItemCount() {
            return amenityClusters.size();
        }

    }

    @OnClick(R.id.amenity_see_on_map)
    public void showMap(){
        OnSeeOnMapClicked onSeeOnMapClicked = new OnSeeOnMapClicked();
        onSeeOnMapClicked.amenityClusters = amenityClusters;
        AppBus.getInstance().post(onSeeOnMapClicked);
    }


}
