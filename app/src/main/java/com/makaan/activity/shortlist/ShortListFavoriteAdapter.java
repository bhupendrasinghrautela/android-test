package com.makaan.activity.shortlist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.wishlist.WishList;
import com.makaan.util.StringUtil;

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
        if(wishList.get(position).project.maxPrice!=null) {
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
            String price = StringUtil.getDisplayPrice(wishList.get(position).project.maxPrice);
            shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);
        }else{
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.GONE);
        }
        String imageUrl=null;
        if(wishList.get(position).project.imageURL.contains("http")){
            imageUrl=wishList.get(position).project.imageURL.replace("http","https");//TODO : handle it in volley
        }
        shortListFavoriteViewHolder.mImageViewBackground.setImageUrl(imageUrl, MakaanNetworkClient.getInstance().getImageLoader());

        shortListFavoriteViewHolder.mTextViewLocality.setText(wishList.get(position).project.address);
        shortListFavoriteViewHolder.mTextViewGetCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeadFormActivity.class);
                mContext.startActivity(intent);
            }
        });
       // shortListFavoriteViewHolder.mTextViewArea.setText();//todo

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
