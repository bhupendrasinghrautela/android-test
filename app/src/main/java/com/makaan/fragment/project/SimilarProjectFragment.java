package com.makaan.fragment.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Project;
import com.makaan.util.AppBus;
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;
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
        if(!TextUtils.isEmpty(title)) {
            titleTv.setText(title.toLowerCase());
        } else {
            titleTv.setText("");
        }
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
            public TextView pricePreTv;
            public TextView pricePostTv;
            public ImageView projectIv;
            public CardView cardView;

            public ViewHolder(View v) {
                super(v);
                nameTv = (TextView) v.findViewById(R.id.tv_similar_project_name);
                addressTv = (TextView) v.findViewById(R.id.tv_similar_project_address);
                priceTv = (TextView) v.findViewById(R.id.tv_similar_project_price);
                projectIv = (ImageView) v.findViewById(R.id.iv_similar_project);
                pricePreTv = (TextView) v.findViewById(R.id.tv_similar_project_price_prefix);
                pricePostTv = (TextView) v.findViewById(R.id.tv_similar_project_price_postfix);
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
                    .inflate(R.layout.row_similar_property, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Project project = similarProjectsList.get(position);
            holder.cardView.setTag(project.projectId);
            holder.cardView.setTag(R.string.similar_project_clicked_position ,position+1);
            holder.cardView.setOnClickListener(onClickListener);
            if(!TextUtils.isEmpty(project.getFullName())) {
                holder.nameTv.setText(project.getFullName().toLowerCase());
            } else {
                holder.nameTv.setText("");
            }
            if(project.minResaleOrPrimaryPrice != null && project.minResaleOrPrimaryPrice > 0) {
                    Double price = project.minResaleOrPrimaryPrice;
                    String priceString = StringUtil.getDisplayPrice(price);
                    String priceUnit = "";
                    if(priceString.indexOf("\u20B9") == 0) {
                        priceString = priceString.replace("\u20B9", "");
                    }
                    String[] priceParts = priceString.split(" ");
                    priceString = priceParts[0];
                    if(priceParts.length > 1) {
                        priceUnit = priceParts[1];
                    }
                    holder.priceTv.setText(String.valueOf(priceString)+" ");
                    holder.pricePostTv.setText(priceUnit+" onwards");
                    holder.pricePreTv.setText("\u20B9 ");
                } else {
                holder.priceTv.setText("");
                holder.pricePostTv.setText("");
                holder.pricePreTv.setText("");
                }

            if(project.address == null) {
                if(project.locality != null) {
                    if(!TextUtils.isEmpty(project.locality.label)) {
                        if(project.locality.suburb != null && project.locality.suburb.city != null) {
                            if (!TextUtils.isEmpty(project.locality.suburb.city.label)) {
                                holder.addressTv.setVisibility(View.VISIBLE);
                                holder.addressTv.setText(String.format("%s, %s", project.locality.label.toLowerCase(), project.locality.suburb.city.label.toLowerCase()));
                            } else {
                                holder.addressTv.setVisibility(View.VISIBLE);
                                holder.addressTv.setText(project.locality.label.toLowerCase());
                            }
                        } else {
                            holder.addressTv.setVisibility(View.VISIBLE);
                            holder.addressTv.setText(project.locality.label.toLowerCase());
                        }
                    } else if(project.locality.suburb != null && project.locality.suburb.city != null) {
                        if(!TextUtils.isEmpty(project.locality.suburb.city.label)) {
                            holder.addressTv.setVisibility(View.VISIBLE);
                            holder.addressTv.setText(project.locality.suburb.city.label.toLowerCase());
                        } else {
                            holder.addressTv.setVisibility(View.GONE);
                        }
                    } else {
                        holder.addressTv.setVisibility(View.GONE);
                    }
                } else {
                    holder.addressTv.setVisibility(View.GONE);
                }
            } else {
                holder.addressTv.setVisibility(View.VISIBLE);
                holder.addressTv.setText(project.address.toLowerCase());
            }

            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("project_placeholder");
            if (bitmap == null) {
                int id = R.drawable.project_placeholder;
                bitmap = BitmapFactory.decodeResource(getResources(), id);
                MakaanBuyerApplication.bitmapCache.putBitmap("project_placeholder", bitmap);
            }

            holder.projectIv.setImageBitmap(bitmap);
            int width = getResources().getDimensionPixelSize(R.dimen.project_similar_project_card_width);
            int height = getResources().getDimensionPixelSize(R.dimen.project_similar_project_card_height);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(project.imageURL, width, height, false),
                    new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if(!isVisible()){
                        return;
                    }
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    holder.projectIv.setImageBitmap(image);
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
