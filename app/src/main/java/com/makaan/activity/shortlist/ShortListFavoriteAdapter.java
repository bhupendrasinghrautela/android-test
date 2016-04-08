package com.makaan.activity.shortlist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.project.Project;
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
        this.mContext = mContext;
        this.wishList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.card_shortlist_favorite, parent, false);
        ShortListFavoriteViewHolder shortListFavoriteViewHolder = new ShortListFavoriteViewHolder(view);
        return shortListFavoriteViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (wishList.get(position) == null) {
            return;
        }

        ShortListFavoriteViewHolder shortListFavoriteViewHolder = (ShortListFavoriteViewHolder) holder;
        if (null != wishList.get(position).listingId) {

            if (null != wishList.get(position).listing && null != wishList.get(position).listing.currentListingPrice) {
                if (wishList.get(position).listing.currentListingPrice.price > 0) {
                    shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
                    String price = StringUtil.getDisplayPrice(wishList.get(position).listing.currentListingPrice.price);
                    shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);
                }
            }

        } else if (wishList.get(position).project != null && wishList.get(position).project.minResaleOrPrimaryPrice != null) {
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.VISIBLE);
            String price = StringUtil.getDisplayPrice(wishList.get(position).project.minResaleOrPrimaryPrice);
            shortListFavoriteViewHolder.mTextViewPriceValue.setText(price);

        } else {
            shortListFavoriteViewHolder.mLinearLayoutDetails.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(wishList.get(position).projectName)) {
            shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.VISIBLE);
            if (wishList.get(position).projectName != null) {
                if (wishList.get(position).builderName != null) {
                    shortListFavoriteViewHolder.mTextViewArea.setText(String.format("%s %s",
                            wishList.get(position).builderName, wishList.get(position).projectName).toLowerCase());
                } else {
                    shortListFavoriteViewHolder.mTextViewArea.setText(wishList.get(position).projectName.toLowerCase());
                }
            }
        } else {
            Project project = wishList.get(position).project;
            if(project == null) {
                if(wishList.get(position).listing != null && wishList.get(position).listing.property != null) {
                    project = wishList.get(position).listing.property.project;
                }
            }
            if (project != null && !TextUtils.isEmpty(project.name)) {
                if(project.activeStatus == null || !"dummy".equalsIgnoreCase(project.activeStatus)) {
                    shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.VISIBLE);
                    if (project.name != null) {
                        if (project.builder != null && !TextUtils.isEmpty(project.builder.name)) {
                            shortListFavoriteViewHolder.mTextViewArea.setText(String.format("%s %s",
                                    project.builder.name, project.name).toLowerCase());
                        } else {
                            shortListFavoriteViewHolder.mTextViewArea.setText(project.name.toLowerCase());
                        }
                    } else {
                        shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.GONE);
                    }
                }
            } else {
                shortListFavoriteViewHolder.mTextViewArea.setVisibility(View.GONE);
            }
        }
        String imageUrl = null;
        if (wishList.get(position).listing != null && !TextUtils.isEmpty(wishList.get(position).listing.mainImageURL)) {
            imageUrl = wishList.get(position).listing.mainImageURL;
        } else if (wishList.get(position).project != null && !TextUtils.isEmpty(wishList.get(position).project.imageURL)) {
            imageUrl = wishList.get(position).project.imageURL;
        }
        /*if(imageUrl != null && !imageUrl.contains("https")){
            imageUrl=imageUrl.replace("http","https");
        }*/
        if (imageUrl != null) {
            int height = (int) Math.ceil(mContext.getResources().getDimension(R.dimen.fav_card_height));
            int width = (int) (mContext.getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
            shortListFavoriteViewHolder.mImageViewBackground.setDefaultImageResId(
                    wishList.get(position).project != null ? R.drawable.project_placeholder
                            : R.drawable.property_placeholder);
            shortListFavoriteViewHolder.mImageViewBackground.setImageUrl(ImageUtils.getImageRequestUrl(imageUrl, width, height, false),
                    MakaanNetworkClient.getInstance().getImageLoader());
        }

        Project project = wishList.get(position).project;
        if(project == null) {
            if(wishList.get(position).listing != null && wishList.get(position).listing.property != null) {
                project = wishList.get(position).listing.property.project;
            }
        }
        if (project != null && project.address != null) {
            shortListFavoriteViewHolder.mTextViewLocality.setText(project.address.toLowerCase());
        } else if (project != null && project.locality != null) {
            if (project.locality.suburb != null
                    && project.locality.suburb.city != null
                    && !TextUtils.isEmpty(project.locality.suburb.city.label)) {
                if (!TextUtils.isEmpty(project.locality.label)) {
                    shortListFavoriteViewHolder.mTextViewLocality.setText(String.format("%s, %s",
                            project.locality.label, project.locality.suburb.city.label).toLowerCase());
                } else if (!TextUtils.isEmpty(project.locality.suburb.label)) {
                    shortListFavoriteViewHolder.mTextViewLocality.setText(String.format("%s, %s",
                            project.locality.suburb.label, project.locality.suburb.city.label).toLowerCase());
                } else {
                    shortListFavoriteViewHolder.mTextViewLocality.setText(project.locality.suburb.city.label.toLowerCase());
                }
            } else {
                if (!TextUtils.isEmpty(project.locality.label)) {
                    shortListFavoriteViewHolder.mTextViewLocality.setText(project.locality.label.toLowerCase());
                } else if (project.locality.suburb != null
                        && !TextUtils.isEmpty(project.locality.suburb.label)) {
                    shortListFavoriteViewHolder.mTextViewLocality.setText(project.locality.suburb.label.toLowerCase());
                }
            }
        }

        shortListFavoriteViewHolder.mImageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != wishList.get(position)) {
                    if (wishList.get(position).listingId != null) {
                        Intent intent = new Intent(mContext, OverviewActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putLong(OverviewActivity.ID, wishList.get(position).listingId);
                        bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROPERTY.ordinal());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else if (wishList.get(position).projectId != null) {
                        Intent intent = new Intent(mContext, OverviewActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putLong(OverviewActivity.ID, wishList.get(position).projectId);
                        bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        shortListFavoriteViewHolder.mTextViewGetCallBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*----- track events--------*/
                if (wishList.get(position).project != null && wishList.get(position).project.projectId != null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", wishList.get(position).project.projectId,
                            MakaanTrackerConstants.Label.getCallBack));
                    MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickShortListFavourite);
                }
                /*-------------------------*/
                Intent intent = new Intent(mContext, LeadFormActivity.class);
                try {
                    WishList wishListValue = wishList.get(position);
                    if(wishListValue.listing!=null && wishListValue.listing.companySeller!=null) {
                        if(wishListValue.listing.companySeller.user!=null && wishListValue.listing.companySeller.user.id!=null) {
                            intent.putExtra(KeyUtil.USER_ID, wishList.get(position).listing.companySeller.user.id);
                        }
                        if(wishListValue.listing.companySeller.company!=null) {
                            intent.putExtra(KeyUtil.NAME_LEAD_FORM, wishList.get(position).listing.companySeller.company.name);
                            if (wishListValue.listing.companySeller.company.score != null) {
                                intent.putExtra(KeyUtil.SCORE_LEAD_FORM, wishList.get(position).listing.companySeller.company.score.toString());
                            }
                            intent.putExtra(KeyUtil.SINGLE_SELLER_ID, wishList.get(position).listing.companySeller.company.id.toString());
                        }
                        if (wishListValue.listing.companySeller.user != null && wishListValue.listing.companySeller.user.contactNumbers != null
                                && wishListValue.listing.companySeller.user.contactNumbers.size() > 0) {
                            intent.putExtra(KeyUtil.PHONE_LEAD_FORM, wishListValue.listing.companySeller.user.contactNumbers.get(0).contactNumber);
                        }
                    }

                    if (wishListValue.listing != null && wishListValue.listing.listingCategory != null &&
                            !TextUtils.isEmpty(wishListValue.listing.listingCategory)) {
                        if (wishListValue.listing.listingCategory.equalsIgnoreCase("primary") ||
                                wishListValue.listing.listingCategory.equalsIgnoreCase("resale")) {
                            intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
                        } else if (wishListValue.listing.listingCategory.equalsIgnoreCase("rental")) {
                            intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "rent");
                        }
                    }

                    intent.putExtra(KeyUtil.SOURCE_LEAD_FORM, ShortListFavoriteAdapter.class.getName());
                    if (wishList.get(position).listingId != null) {
                        intent.putExtra(KeyUtil.LISTING_ID_LEAD_FORM, wishList.get(position).listingId);
                    }
                    if (wishList.get(position).projectId != null) {
                        intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM, wishList.get(position).projectId);
                    }
                    if (wishList.get(position).projectName != null) {
                        intent.putExtra(KeyUtil.PROJECT_NAME_LEAD_FORM, wishList.get(position).projectName);
                    }

                    Project project = wishList.get(position).project;
                    if(project == null) {
                        if(wishList.get(position).listing != null && wishList.get(position).listing.property != null) {
                            project = wishList.get(position).listing.property.project;
                        }
                    }
                    if (project != null) {
                        if (project.locality != null && project.locality.cityId != null) {
                            intent.putExtra(KeyUtil.CITY_ID_LEAD_FORM, project.locality.cityId);
                        }
                        if (project.locality != null && project.localityId != null) {
                            intent.putExtra(KeyUtil.LOCALITY_ID_LEAD_FORM, project.localityId);
                        }
                    }

                    if (project.locality != null && project.locality.suburb != null &&
                            project.locality.suburb.city != null && project.locality.suburb.city.label != null) {
                        intent.putExtra(KeyUtil.CITY_NAME_LEAD_FORM, project.locality.suburb.city.label);
                    }

                    if (wishList.get(position).listing != null && wishList.get(position).listing.companySeller != null &&
                            wishList.get(position).listing.companySeller.company != null && wishList.get(position).listing.companySeller.company.logo != null) {
                        intent.putExtra(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM, wishList.get(position).listing.companySeller.company.logo);
                    }
                    mContext.startActivity(intent);
                } catch (NullPointerException npe) {
                    Toast.makeText(mContext, "Seller data not available", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(mContext, "Seller data not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // shortListFavoriteViewHolder.mTextViewArea.setText();
    }

    @Override
    public int getItemCount() {
        if(wishList == null) {
            return 0;
        }
        return wishList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
