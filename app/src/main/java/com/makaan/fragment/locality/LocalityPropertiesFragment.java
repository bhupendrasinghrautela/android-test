package com.makaan.fragment.locality;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.TaxonomyCard;
import com.segment.analytics.Properties;

import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalityPropertiesFragment extends MakaanBaseFragment {
    private LinearLayoutManager mLayoutManager;
    private PropertiesAdapter mAdapter;
    private String title;
    @Bind(R.id.tv_localities_props_title)
    public TextView titleTv;
    @Bind(R.id.rv_localities_props)
    public  RecyclerView mRecyclerView;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_properties;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        title = getArguments().getString("title");
        if(title != null) {
            titleTv.setText(title.toLowerCase());
        }
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<TaxonomyCard> taxonomyCardList, Context context) {
        mAdapter = new PropertiesAdapter(taxonomyCardList, context);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

    private static class ClickListener implements View.OnClickListener {

        private final List<TaxonomyCard> taxonomyCardList;
        private final Context context;
        private int mPosition;

        public ClickListener(List<TaxonomyCard> taxonomyCardList, Context context) {
            super();
            this.taxonomyCardList = taxonomyCardList;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            if(taxonomyCardList != null && taxonomyCardList.size() > mPosition) {
                TaxonomyCard taxonomyCard = taxonomyCardList.get(mPosition);
                taxonomyCard.serpRequest.launchSerp(context);
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                properties.put(MakaanEventPayload.LABEL, taxonomyCard.label1+"_"+(mPosition+1));
                MakaanEventPayload.endBatch(context, MakaanTrackerConstants.Action.selectPropertiesLocality);
            }
        }

        public void setPosition(int position) {
            mPosition = position;
        }
    }

    private static class PropertiesAdapter
            extends RecyclerView.Adapter<PropertiesAdapter.ViewHolder> {
        private final Context mContext;
        private List<TaxonomyCard> taxonomyCardList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView descriptionTv;
            public TextView typeTv;
            public ImageView localityIv;
            ClickListener clickListener;

            public ViewHolder(View view) {
                super(view);
                descriptionTv = (TextView) view.findViewById(R.id.tv_localities_props_label);
                typeTv = (TextView) view.findViewById(R.id.tv_localities_props_label_type);
                localityIv = (ImageView) view.findViewById(R.id.iv_localitites_props);

                clickListener = new ClickListener(taxonomyCardList, mContext);
                view.setOnClickListener(clickListener);
            }

            public void setPosition(int position) {
                clickListener.setPosition(position);
            }
        }

        public PropertiesAdapter(List<TaxonomyCard> taxonomyCardList, Context context) {
            this.taxonomyCardList = taxonomyCardList;
            mContext = context;
        }

        @Override
        public PropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_props, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TaxonomyCard taxonomyCard = taxonomyCardList.get(position);
            holder.descriptionTv.setText(taxonomyCard.label2);
            holder.typeTv.setText(taxonomyCard.label1);

/*            // todo handling Out of memory error, need to recheck this, jira issue id 262
            if(holder.localityIv.getDrawable() != null && holder.localityIv.getDrawable() instanceof BitmapDrawable) {
                ((BitmapDrawable)holder.localityIv.getDrawable()).getBitmap().recycle();
            }*/



            holder.localityIv.setImageBitmap(getImage(position));
            holder.setPosition(position);
        }
        private Bitmap getImage(int position) {
            int id = R.drawable.locality_placeholder;
            String imageName = "locality_placeholder";
            switch (position) {
                case 0:
                    id = R.drawable.luxury_properties;
                    imageName = "luxury_properties";
                    break;
                case 1:
                    id = R.drawable.recent_properties;
                    imageName = "recent_properties";
                    break;
                case 2:
                    id = R.drawable.budget_homes;
                    imageName = "budget_homes";
                    break;
                case 3:
                    id = R.drawable.popular_properties;
                    imageName = "popular_properties";
                    break;
                case 4:
                    id = R.drawable.new_rental_properties;
                    imageName = "new_rental_properties";
                    break;
            }

            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(imageName);
            if (bitmap != null) {
                return bitmap;
            } else {
                Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), id);
                MakaanBuyerApplication.bitmapCache.putBitmap(imageName, b);
                return b;
            }
        }

            @Override
        public int getItemCount() {
            return taxonomyCardList.size();
        }

    }
}
