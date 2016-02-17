package com.makaan.fragment.neighborhood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.amenity.AmenityCluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 24/01/16.
 */
public class NeighborhoodCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AmenityCluster> mItems = new ArrayList<>();
    private CategoryClickListener mCategoryClickListener;
    private CheckBox lastAmenityView;
    private int selectedPosition=0;

    private final static String URL = "http://content.makaan.com.s3.amazonaws.com/app/icons/amenities";

    public NeighborhoodCategoryAdapter(Context context,
                                       CategoryClickListener categoryClickListener){
        mContext = context;
        mCategoryClickListener = categoryClickListener;
    }

    public void setData(List<AmenityCluster> items) {
        if (items == null) {
            mItems.clear();
        } else {
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.neighbor_category_view, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        categoryViewHolder.bind(mItems.get(position));
        if(position==selectedPosition){
            categoryViewHolder.checkBox.setChecked(true);
            setLastAmenityView(categoryViewHolder.checkBox);
        }
        else{
            categoryViewHolder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView label;
        FadeInNetworkImageView icon;
        CheckBox checkBox;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.neighborhood_category_name);
            icon = (FadeInNetworkImageView) itemView.findViewById(R.id.neighborhood_category_icon);
            checkBox=(CheckBox)itemView.findViewById(R.id.amenity_selector);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setClickable(false);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mCategoryClickListener) {
                        mCategoryClickListener.onItemClick(getAdapterPosition(), view);
                        if (checkBox.isChecked()) {
                        } else {
                            selectedPosition = getAdapterPosition();
                            uncheckLastAmenity();
                            checkBox.setChecked(true);
                            setLastAmenityView(checkBox);
                        }
                    }
                }
            });
        }

        public void bind(AmenityCluster amenityCluster){
            StringBuilder finalImageUrl = new StringBuilder();
            finalImageUrl.append(URL.toString());
            finalImageUrl.append("/");
            finalImageUrl.append(amenityCluster.amenityId);
            finalImageUrl.append(".png");
            icon.setImageUrl(finalImageUrl.toString(), MakaanNetworkClient.getInstance().getImageLoader());
            label.setText(amenityCluster.name);
        }

        //TODO remove this listener 
        @Override
        public void onClick(View view) {
            if(null!=mCategoryClickListener) {
                mCategoryClickListener.onItemClick(getAdapterPosition(), view);
            }
        }
    }

    public interface CategoryClickListener {
        void onItemClick(int position, View v);
    }

    public void uncheckLastAmenity(){
        if(lastAmenityView!=null) {
            lastAmenityView.setChecked(false);
        }
    }

    public void setLastAmenityView(CheckBox checkBox){
        lastAmenityView=checkBox;
    }
}
