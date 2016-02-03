package com.makaan.activity.shortlist;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.response.wishlist.WishList;

import java.util.List;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<WishList> wishList;

    public ShortListFavoriteAdapter(Context mContext, List<WishList> data) {
        this.mContext=mContext;
        this.wishList= data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_shortlist_favorite, parent, false);
        ShortListFavoriteViewHolder shortListFavoriteViewHolder = new ShortListFavoriteViewHolder(view);
        return shortListFavoriteViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ShortListFavoriteViewHolder shortListFavoriteViewHolder = (ShortListFavoriteViewHolder)holder;
        shortListFavoriteViewHolder.mTextViewRupees.setText(mContext.getString(R.string.rupee));
        shortListFavoriteViewHolder.mTextViewPriceValue.setText(String.valueOf(wishList.get(position).project.maxPrice));
       // shortListFavoriteViewHolder.mTextViewPriceUnit.setText();
        shortListFavoriteViewHolder.mTextViewLocality.setText(wishList.get(position).project.address);
       // shortListFavoriteViewHolder.mTextViewArea.setText();

    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
