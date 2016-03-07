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
import com.makaan.activity.listing.OpenListingListener;
import com.makaan.event.listing.SimilarListingGetEvent.ListingItems;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.property.Property;
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;

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
    private OpenListingListener listener;


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

    public void setData(List<ListingItems> similarProjects) {
        mAdapter = new SimilarPropertiesAdapter(similarProjects, this);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.openPropertyPage((Long) v.getTag(),null,null);
        }
    }

    public void setListener(OpenListingListener listener) {
        this.listener = listener;
    }


    private class SimilarPropertiesAdapter
            extends RecyclerView.Adapter<SimilarPropertiesAdapter.ViewHolder> {
        private final View.OnClickListener onClickListener;
        private List<ListingItems> similarPropertiesList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView nameTv;
            public TextView addressTv;
            public TextView priceTv;
            public TextView pricePreTv;
            public TextView pricePostTv;
            public ImageView projectIv;
            public CardView cardView;
            private int position;

            public ViewHolder(View v) {
                super(v);
                nameTv = (TextView) v.findViewById(R.id.tv_similar_project_name);
                addressTv = (TextView) v.findViewById(R.id.tv_similar_project_address);
                priceTv = (TextView) v.findViewById(R.id.tv_similar_project_price);
                pricePreTv = (TextView) v.findViewById(R.id.tv_similar_project_price_prefix);
                pricePostTv = (TextView) v.findViewById(R.id.tv_similar_project_price_postfix);
                projectIv = (ImageView) v.findViewById(R.id.iv_similar_project);
                cardView = (CardView) v.findViewById(R.id.card_similar_projects);
            }
        }

        public SimilarPropertiesAdapter(List<ListingItems> similarProjectsList,View.OnClickListener onClickListener) {
            this.similarPropertiesList = similarProjectsList;
            this.onClickListener = onClickListener;
        }

        @Override
        public SimilarPropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_similar_property, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ListingDetail listing = similarPropertiesList.get(position).listing;
            holder.cardView.setTag(listing.id);
            holder.cardView.setOnClickListener(onClickListener);
            if(listing.property!=null) {
                Property property = listing.property;
                if (listing.property.project != null && listing.property.project.name != null) {
                    String nameTv = listing.property.project.name;
                    if (listing.property.project.locality != null && listing.property.project.locality.label != null) {
                        holder.addressTv.setText(nameTv.toLowerCase().concat(", ").concat(listing.property.project.locality.label).toLowerCase());
                    }
                    else {
                        holder.addressTv.setText(nameTv);
                    }
                }
                StringBuilder bhkInfo = new StringBuilder();
                if (property.unitType != null && "plot".equalsIgnoreCase(property.unitType)) {
                    bhkInfo.append("residential plot");
                } else {
                    bhkInfo.append(property.bedrooms > 0 ? property.bedrooms.toString() + "bhk " : "");
                    bhkInfo.append(property.unitType != null ? property.unitType : "");
                }
                bhkInfo.append((property.size != null ? "- " +StringUtil.getFormattedNumber(property.size)+" sq ft" : ""));
                holder.nameTv.setText(bhkInfo.toString());

            }
            if(listing.currentListingPrice!=null && listing.currentListingPrice.price!=0) {
                Double price = listing.currentListingPrice.price;
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
            }
            if(listing.mainImageURL!=null) {
                int width = getResources().getDimensionPixelSize(R.dimen.project_similar_project_card_width);
                int height = getResources().getDimensionPixelSize(R.dimen.project_similar_project_card_height);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(listing.mainImageURL, width, height, false),
                        new ImageLoader.ImageListener() {
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
        }

        @Override
        public int getItemCount() {
            return similarPropertiesList.size();
        }

    }
}
