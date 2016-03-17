package com.makaan.ui.listing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;

import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.constants.PreferenceConstants;

import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.Listing;
import com.makaan.ui.CustomNetworkImageView;
import com.makaan.ui.view.WishListButton;
import com.makaan.ui.view.WishListButton.WishListDto;
import com.makaan.ui.view.WishListButton.WishListType;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by sunil on 04/01/16.
 */
public class ListingCardView extends AbstractListingView {


    @Bind(R.id.listing_brief_view_layout_property_image_view)CustomNetworkImageView mPropertyImageView;
    @Bind(R.id.listing_brief_view_layout_property_price_text_view)TextView mPropertyPriceTextView;
    @Bind(R.id.listing_brief_view_layout_property_price_unit_text_view)TextView mPropertyPriceUnitTextView;
    @Bind(R.id.listing_brief_view_layout_property_shortlist_checkbox)public WishListButton mPropertyWishListCheckbox;
    @Bind(R.id.listing_brief_view_layout_property_price_sq_ft_text_view)TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.listing_brief_view_layout_property_price_difference_image_view)ImageView mDifferenceImageView;
    @Bind(R.id.listing_brief_view_layout_property_bhk_info_text_view)TextView mPropertyBhkInfoTextView;
    @Bind(R.id.listing_brief_view_layout_property_size_info_text_view)TextView mPropertySizeInfoTextView;
    @Bind(R.id.listing_brief_view_layout_property_address_text_view)TextView mPropertyAddressTextView;
    @Bind(R.id.listing_brief_view_layout_badge_Image_view)ImageView mBadgeImageView;
    @Bind(R.id.listing_brief_view_layout_text_view)TextView mBadgeTextView;

    @Bind(R.id.listing_brief_view_layout_assist_button) Button mAssistButton;

    private SharedPreferences mPreferences;
    private Listing mListing;

    public ListingCardView(Context context) {
        super(context);
    }

    public ListingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void populateData(Object item, SerpRequestCallback callback, int position) {
        mListing = (Listing)item;

        if(RecentPropertyProjectManager.getInstance(mContext.getApplicationContext()).containsProperty(mListing.id)) {
            mBadgeImageView.setVisibility(View.VISIBLE);
            mBadgeTextView.setVisibility(View.VISIBLE);

            mBadgeImageView.setImageResource(R.drawable.badge_seen);
            mBadgeTextView.setText("seen");
        } else {
            mBadgeImageView.setVisibility(View.GONE);
            mBadgeTextView.setVisibility(View.GONE);
        }
        // TODO implement new
        mPreferences = mContext.getSharedPreferences(
                PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, Context.MODE_PRIVATE);
        mPropertyWishListCheckbox.bindView(new WishListDto(mListing.lisitingId.longValue(), mListing.projectId.longValue(), WishListType.listing));

        // TODO check for unit info
        String priceString = StringUtil.getDisplayPrice(mListing.price);
        String priceUnit = "";
        if(priceString.indexOf("\u20B9") == 0) {
            priceString = priceString.replace("\u20B9", "");
        }
        String[] priceParts = priceString.split(" ");
        priceString = priceParts[0];
        if(priceParts.length > 1) {
            priceUnit = priceParts[1];
        }

        mPropertyPriceTextView.setText(priceString.toLowerCase());
        mPropertyPriceUnitTextView.setText(priceUnit);
        mPropertyPriceSqFtTextView.setText(String.format("%s%s/sqft", "\u20B9", StringUtil.getFormattedNumber(mListing.pricePerUnitArea)).toLowerCase());

        // set property bhk and size info
        if(mListing.bhkInfo == null) {
            mPropertyBhkInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertyBhkInfoTextView.setVisibility(View.VISIBLE);
            mPropertyBhkInfoTextView.setText(mListing.bhkInfo.toLowerCase());
        }

        if(mListing.sizeInfo == null) {
            mPropertySizeInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertySizeInfoTextView.setVisibility(View.VISIBLE);
            mPropertySizeInfoTextView.setText(mListing.sizeInfo.toLowerCase());
        }

        // set property address info {project_name},{localityName}_{cityName}
        if(!TextUtils.isEmpty(mListing.project.name)
                && (mListing.project.activeStatus == null || !"dummy".equalsIgnoreCase(mListing.project.activeStatus))) {
            if(!TextUtils.isEmpty(mListing.project.builderName)) {
                mPropertyAddressTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s %s</font>, %s, %s", mListing.project.builderName,
                        mListing.project.name, mListing.localityName, mListing.cityName).toLowerCase()));
            } else {
                mPropertyAddressTextView.setText(Html.fromHtml(String.format("<font color=\"#E71C28\">%s</font>, %s, %s", mListing.project.name, mListing.localityName, mListing.cityName).toLowerCase()));
            }
        } else {
            mPropertyAddressTextView.setText(String.format("%s, %s", mListing.localityName, mListing.cityName).toLowerCase());
        }


        if(mListing.mainImageUrl != null && !TextUtils.isEmpty(mListing.mainImageUrl)) {
            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_property_image_height);
            mPropertyImageView.setDefaultImageResId(R.drawable.serp_placeholder);
            mPropertyImageView.setImageUrl(ImageUtils.getImageRequestUrl(mListing.mainImageUrl, width, height, false), MakaanNetworkClient.getInstance().getImageLoader());
        } else {
            // show placeholder
            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("serp_placeholder");
            if (bitmap == null) {
                int id = R.drawable.serp_placeholder;
                bitmap = BitmapFactory.decodeResource(getResources(), id);
                MakaanBuyerApplication.bitmapCache.putBitmap("serp_placeholder", bitmap);
            }

            mPropertyImageView.setImageBitmap(bitmap);
        }

        if(mListing.lisitingPostedBy == null || !mListing.lisitingPostedBy.assist) {
            mAssistButton.setVisibility(View.GONE);
        } else {
            mAssistButton.setVisibility(View.VISIBLE);
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListing != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KeyUtil.LISTING_ID, mListing.id);
                    if(mListing.latitude != null) {
                        bundle.putDouble(KeyUtil.LISTING_LAT, mListing.latitude);
                    }
                    if(mListing.longitude != null) {
                        bundle.putDouble(KeyUtil.LISTING_LON, mListing.longitude);
                    }
                    bundle.putString(KeyUtil.LISTING_Image, mListing.mainImageUrl);

                    Intent intent = new Intent(mContext, PropertyActivity.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    //mCallback.requestDetailPage(SerpActivity.REQUEST_PROPERTY_PAGE, bundle);
                }
            }
        });
    }

    @OnClick(R.id.listing_brief_view_layout_address_relative_view)
    public void onProjectClicked(View view) {
        if(!TextUtils.isEmpty(mListing.project.name)
                && (mListing.project.activeStatus == null || !"dummy".equalsIgnoreCase(mListing.project.activeStatus))) {
            if (mListing.projectId != null && mListing.projectId != 0) {
                Bundle bundle = new Bundle();
                bundle.putLong(ProjectActivity.PROJECT_ID, mListing.projectId);

                Intent intent = new Intent(getContext(), ProjectActivity.class);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        }
    }
}
