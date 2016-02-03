package com.makaan.fragment.property;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.detail.ListingDetail;

import java.util.List;

import butterknife.Bind;

/**
 * Created by aishwarya on 1/2/16.
 */
public class SimilarPropertyFragment extends MakaanBaseFragment implements View.OnClickListener{
    private LinearLayoutManager mLayoutManager;
    private SimilarPropertiesAdapter mAdapter;
    private String title;
    @Bind(R.id.tv_similar_project_title)
    public TextView titleTv;
    @Bind(R.id.rv_similar_projects)
    public RecyclerView mRecyclerView;


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
    }

    public void setData(List<ListingDetail> similarProjects) {
        mAdapter = new SimilarPropertiesAdapter(similarProjects, this);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {

    }


    private class SimilarPropertiesAdapter
            extends RecyclerView.Adapter<SimilarPropertiesAdapter.ViewHolder> {
        private final View.OnClickListener onClickListener;
        private List<ListingDetail> similarPropertiesList;

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

        public SimilarPropertiesAdapter(List<ListingDetail> similarProjectsList,View.OnClickListener onClickListener) {
            this.similarPropertiesList = similarProjectsList;
            this.onClickListener = onClickListener;
        }

        @Override
        public SimilarPropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_project_similar_property, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ListingDetail listing = similarPropertiesList.get(position);
            holder.cardView.setTag(listing.id);
            holder.cardView.setOnClickListener(onClickListener);
            holder.nameTv.setText(listing.property.project.getFullName());
            holder.priceTv.setText(String.valueOf(listing.minResaleOrPrimaryPrice));
            if(listing.property.project.address == null)
                holder.addressTv.setVisibility(View.GONE);
            else {
                holder.addressTv.setVisibility(View.VISIBLE);
                holder.addressTv.setText(listing.property.project.address );
            }
            MakaanNetworkClient.getInstance().getImageLoader().get(listing.property.project.imageURL.replace("http", "https"), new ImageLoader.ImageListener() {
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
            return similarPropertiesList.size();
        }

    }
}
