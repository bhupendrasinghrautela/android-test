package com.makaan.activity.shortlist;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.wishlist.WishList;
import com.makaan.util.ImageUtils;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ShortListFavoriteViewHolder shortListFavoriteViewHolder = (ShortListFavoriteViewHolder)holder;
        if(null!=wishList.get(position).listingId){
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
            String price = StringUtil.getDisplayPrice(wishList.get(position).listing.currentListingPrice.price);
            shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);

        }else if(null!=wishList.get(position).project.minResaleOrPrimaryPrice) {
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
            String price = StringUtil.getDisplayPrice(wishList.get(position).project.minResaleOrPrimaryPrice);
            shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);

        }else{
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(wishList.get(position).projectName)){
            shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.VISIBLE);
            shortListFavoriteViewHolder.mTextViewArea.setText(wishList.get(position).builderName + " " + wishList.get(position).projectName);
        }else{
            shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.GONE);
        }
        String imageUrl=wishList.get(position).project != null ? wishList.get(position).project.imageURL : null;
        if(imageUrl != null && !imageUrl.contains("https")){
            imageUrl=imageUrl.replace("http","https");//TODO : handle it in volley
        }
        if(imageUrl != null) {
            shortListFavoriteViewHolder.mImageViewBackground.setImageUrl(imageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        }

        if(wishList.get(position).project != null) {
            shortListFavoriteViewHolder.mTextViewLocality.setText(wishList.get(position).project.address);
        }
        shortListFavoriteViewHolder.mTextViewGetCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeadFormActivity.class);
                try {
                    intent.putExtra("name", wishList.get(position).listing.companySeller.company.name);
                    intent.putExtra("score", wishList.get(position).listing.companySeller.company.score.toString());
                    intent.putExtra("phone", "9090909090");//todo: not available in pojo
                    intent.putExtra("id", wishList.get(position).listing.companySeller.company.id.toString());
                    if(wishList.get(position).project.locality!=null && wishList.get(position).project.locality.cityId!=null) {
                        intent.putExtra("cityId", wishList.get(position).project.locality.cityId);
                    }
                    if(wishList.get(position).project.locality!=null && wishList.get(position).project.projectId!=null) {
                        intent.putExtra("localityId", wishList.get(position).project.projectId);
                    }
                    mContext.startActivity(intent);
                }catch (NullPointerException npe){
                    Toast.makeText(mContext, "Seller data not available", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    Toast.makeText(mContext, "Seller data not available", Toast.LENGTH_SHORT).show();
                }
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
