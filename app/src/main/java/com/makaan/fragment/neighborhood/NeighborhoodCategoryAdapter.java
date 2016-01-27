package com.makaan.fragment.neighborhood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.jarvis.ui.MessageViewHolder;
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

    public NeighborhoodCategoryAdapter(Context context,
                                       CategoryClickListener categoryClickListener){
        mContext = context;
        mCategoryClickListener = categoryClickListener;
    }

    public void setData(List<AmenityCluster> items) {
        if(items == null) {
            mItems.clear();
        } else {
            mItems.addAll(items);
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
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView label;
        ImageView icon;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.neighborhood_category_name);
            icon = (ImageView) itemView.findViewById(R.id.neighborhood_category_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null!=mCategoryClickListener) {
                        mCategoryClickListener.onItemClick(getAdapterPosition(), view);
                    }
                }
            });
        }

        public void bind(AmenityCluster amenityCluster){
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
}
