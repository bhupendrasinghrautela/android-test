package com.makaan.fragment.project;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.locality.OnNearByLocalityClickEvent;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Project;
import com.makaan.ui.view.ParallexScrollview;
import com.makaan.util.AppBus;
import com.segment.analytics.Properties;

import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class SimilarProjectFragment extends MakaanBaseFragment implements View.OnClickListener{
    private LinearLayoutManager mLayoutManager;
    private SimilarProjectsAdapter mAdapter;
    private String title;
    @Bind(R.id.tv_similar_project_title)
    public TextView titleTv;
    @Bind(R.id.rv_similar_projects)
    public RecyclerView mRecyclerView;
    public static final int CLICKED_POSITION=1;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_similar_projects;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        title = getArguments().getString("title");
        titleTv.setText(title);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new scrollChange());
    }

    public void setData(List<Project> similarProjects) {
        mAdapter = new SimilarProjectsAdapter(similarProjects, this);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        AppBus.getInstance().post(new OnSimilarProjectClickedEvent(((Long) v.getTag()), (Integer) v.getTag(R.string.similar_project_clicked_position)) );
    }


    private class SimilarProjectsAdapter
            extends RecyclerView.Adapter<SimilarProjectsAdapter.ViewHolder> {
        private final View.OnClickListener onClickListener;
        private List<Project> similarProjectsList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView nameTv;
            public TextView addressTv;
            public TextView priceTv;
            public ImageView projectIv;
            public CardView cardView;

            public ViewHolder(View v) {
                super(v);
                nameTv = (TextView) v.findViewById(R.id.tv_similar_project_name);
                addressTv = (TextView) v.findViewById(R.id.tv_similar_project_address);
                priceTv = (TextView) v.findViewById(R.id.tv_similar_project_price);
                projectIv = (ImageView) v.findViewById(R.id.iv_similar_project);
                cardView = (CardView) v.findViewById(R.id.card_similar_projects);
            }
        }

        public SimilarProjectsAdapter(List<Project> similarProjectsList,View.OnClickListener onClickListener) {
            this.similarProjectsList = similarProjectsList;
            this.onClickListener = onClickListener;
        }

        @Override
        public SimilarProjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_project_similar_property, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Project project = similarProjectsList.get(position);
            holder.cardView.setTag(project.projectId);
            holder.cardView.setTag(R.string.similar_project_clicked_position ,position+1);
            holder.cardView.setOnClickListener(onClickListener);
            holder.nameTv.setText(project.getFullName());
            holder.priceTv.setText(project.getMinPriceOnwards());
            if(project.address == null)
                holder.addressTv.setVisibility(View.GONE);
            else {
                holder.addressTv.setVisibility(View.VISIBLE);
                holder.addressTv.setText(project.address);
            }
            MakaanNetworkClient.getInstance().getImageLoader().get(project.imageURL.replace("http", "https"), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    holder.projectIv.setImageBitmap(image);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return similarProjectsList.size();
        }


    }

    private class scrollChange extends RecyclerView.OnScrollListener {
        int flingCoordinate;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState==RecyclerView.SCROLL_STATE_IDLE){
                if(flingCoordinate>0){
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.right);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectSimilarProjects);
                }
                else if(flingCoordinate<0){
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.left);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectSimilarProjects);
                }
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            flingCoordinate=dx;
        }
    }

}
