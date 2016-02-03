package com.makaan.activity.shortlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListEnquiredAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;

    public ShortListEnquiredAdapter(Context mContext){
        this.mContext=mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_shortlist_enquired, parent, false);
        ShortListEnquiredViewHolder shortListEnquiredViewHolder = new ShortListEnquiredViewHolder(view);
        return shortListEnquiredViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
