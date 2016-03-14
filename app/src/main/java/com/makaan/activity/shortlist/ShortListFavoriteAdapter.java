package com.makaan.activity.shortlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.wishlist.WishList;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

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

            if(null!=wishList.get(position).listing && null!=wishList.get(position).listing.currentListingPrice ){
                if(wishList.get(position).listing.currentListingPrice.price>0) {
                    shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
                    String price = StringUtil.getDisplayPrice(wishList.get(position).listing.currentListingPrice.price);
                    shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);
                }
            }

        }else if(null!=wishList.get(position).project.minResaleOrPrimaryPrice) {
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
            String price = StringUtil.getDisplayPrice(wishList.get(position).project.minResaleOrPrimaryPrice);
            shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);

        }else{
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(wishList.get(position).projectName)){
            shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.VISIBLE);
            if(wishList.get(position).projectName != null) {
                if(wishList.get(position).builderName != null) {
                    shortListFavoriteViewHolder.mTextViewArea.setText(String.format("%s %s", wishList.get(position).builderName, wishList.get(position).projectName).toLowerCase());
                } else {
                    shortListFavoriteViewHolder.mTextViewArea.setText(wishList.get(position).projectName.toLowerCase());
                }
            }
        }else{
            shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.GONE);
        }
        String imageUrl=wishList.get(position).project != null ? wishList.get(position).project.imageURL : null;
        /*if(imageUrl != null && !imageUrl.contains("https")){
            imageUrl=imageUrl.replace("http","https");//TODO : handle it in volley
        }*/
        if(imageUrl != null) {
            int height = (int)Math.ceil(mContext.getResources().getDimension(R.dimen.fav_card_height));
            int width = (int) (mContext.getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
            shortListFavoriteViewHolder.mImageViewBackground.setDefaultImageResId(
                    wishList.get(position).project != null? R.drawable.project_placeholder
                            : R.drawable.property_placeholder);
            shortListFavoriteViewHolder.mImageViewBackground.setImageUrl(ImageUtils.getImageRequestUrl(imageUrl, width, height, false),
                    MakaanNetworkClient.getInstance().getImageLoader());
        }

        if(wishList.get(position).project != null && wishList.get(position).project.address != null) {
            shortListFavoriteViewHolder.mTextViewLocality.setText(wishList.get(position).project.address.toLowerCase());
        }
        shortListFavoriteViewHolder.mTextViewGetCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wishList.get(position).project!=null && wishList.get(position).project.projectId!=null) {
                    Properties properties= MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",wishList.get(position).project.projectId ,
                            MakaanTrackerConstants.Label.getCallBack ));
                    MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickShortListFavourite);
                }
                Intent intent = new Intent(mContext, LeadFormActivity.class);
                try {
                    intent.putExtra("name", wishList.get(position).listing.companySeller.company.name);
                    intent.putExtra("score", wishList.get(position).listing.companySeller.company.score.toString());
                    intent.putExtra("phone", "9090909090");//todo: not available in pojo
                    intent.putExtra("id", wishList.get(position).listing.companySeller.company.id.toString());
                    if(wishList.get(position).listingId != null) {
                        intent.putExtra("listingId", wishList.get(position).listingId);
                    } else if(wishList.get(position).projectId != null) {
                        intent.putExtra("listingId", wishList.get(position).projectId);
                    }
                    if(wishList.get(position).project.locality!=null && wishList.get(position).project.locality.cityId!=null) {
                        intent.putExtra("cityId", wishList.get(position).project.locality.cityId);
                    }
                    if(wishList.get(position).project.locality!=null && wishList.get(position).project.projectId!=null) {
                        intent.putExtra("localityId", wishList.get(position).project.projectId);
                    }
                    if(wishList.get(position).listing!=null && wishList.get(position).listing.companySeller!=null &&
                            wishList.get(position).listing.companySeller.company!=null && wishList.get(position).listing.companySeller.company.logo!=null) {
                        intent.putExtra("sellerImageUrl", wishList.get(position).listing.companySeller.company.logo);
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
