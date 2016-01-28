package com.makaan.ui.listing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.Listing;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;

import java.text.NumberFormat;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class DefaultListingView extends AbstractListingView implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public CheckBox mPropertyShortlistCheckbox;

    @Bind(R.id.serp_default_listing_property_image_image_view)
    FadeInNetworkImageView mPropertyImageView;
    @Bind(R.id.serp_default_listing_property_price_difference_image_view)
    ImageView mPropertyPriceDifferenceImageView;
    @Bind(R.id.serp_default_listing_badge_Image_view)
    ImageView mBadgeImageView;

    @Bind(R.id.serp_default_listing_property_possession_image_view)
    ImageView mPossessionImageView;

    @Bind(R.id.serp_default_listing_seller_image_view)
    CircularImageView mSellerImageView;

    @Bind(R.id.serp_default_listing_seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.serp_default_listing_property_price_text_view)
    TextView mPropertyPriceTextView;
    @Bind(R.id.serp_default_listing_property_price_unit_text_view)
    TextView mPropertyPriceUnitTextView;
    @Bind(R.id.serp_default_listing_property_price_sq_ft_text_view)
    TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.serp_default_listing_property_bhk_info_text_view)
    TextView mPropertyBhkInfoTextView;
    @Bind(R.id.serp_default_listing_property_size_info_text_view)
    TextView mPropertySizeInfoTextView;
    @Bind(R.id.serp_default_listing_property_address_text_view)
    TextView mPropertyAddressTextView;
    @Bind(R.id.serp_default_listing_property_possession_date_text_view)
    TextView mPropertyPossessionDateTextView;
    @Bind(R.id.serp_default_listing_property_floor_info_text_view)
    TextView mPropertyFloorInfoTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_number_date_text_view)
    TextView mPropertyBathroomNumberTextView;
    @Bind(R.id.serp_default_listing_property_tagline_text_view)
    TextView mPropertyDescriptionTextView;
    @Bind(R.id.serp_default_listing_seller_name_text_view)
    TextView mPropertySellerNameTextView;
    @Bind(R.id.serp_default_listing_badge_text_view)
    TextView mBadgeTextView;

    @Bind(R.id.serp_default_listing_property_possession_text_view)
    TextView mPossesionTextView;

    @Bind(R.id.serp_default_listing_assist_button)
    Button mAssistButton;
    @Bind(R.id.serp_default_listing_call_button)
    Button mCallButton;

    @Bind(R.id.serp_default_listing_seller_rating)
    RatingBar mSellerRatingBar;

    @Bind(R.id.serp_default_listing_property_seller_info_relative_layout)
    RelativeLayout mSellerInfoRelativeLayout;

    private SharedPreferences mPreferences;
    private Listing mListing;
    private SerpRequestCallback mCallback;

    public DefaultListingView(Context context) {
        super(context);
    }

    public DefaultListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);
        if(!(data instanceof Listing)) {
            return;
        }
        mCallback = callback;

        boolean isBuy = MakaanBuyerApplication.serpSelector.isBuyContext();

        mListing = (Listing)data;
        mPropertyShortlistCheckbox.setOnCheckedChangeListener(null);
        mPreferences = mContext.getSharedPreferences(
                PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, Context.MODE_PRIVATE);

        if(isBuy) {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId, isBuy);
            mPropertyShortlistCheckbox.setChecked(isShortlisted);
            // TODO temporary code to show badges
            /*if(isShortlisted) {
                if (mListing.lisitingId % 2 == 0) {
                    mBadgeImageView.setImageResource(R.drawable.badge_new);
                    mBadgeImageView.setVisibility(VISIBLE);
                    mBadgeTextView.setText("New");
                } else {
                    mBadgeImageView.setImageResource(R.drawable.badge_seen);
                    mBadgeImageView.setVisibility(VISIBLE);
                    mBadgeTextView.setText("Seen");
                }
                mBadgeTextView.setVisibility(VISIBLE);
            } else {
                mBadgeImageView.setVisibility(GONE);
                mBadgeImageView.setVisibility(GONE);
            }*/
        } else {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId, isBuy);
            mPropertyShortlistCheckbox.setChecked(isShortlisted);
        }

        if(mListing.mainImageUrl != null && !TextUtils.isEmpty(mListing.mainImageUrl)) {
            mPropertyImageView.setImageUrl(mListing.mainImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        } else {
            //TODO this is just a dummy image
            String url = "https://im.proptiger-ws.com/1/644953/6/imperial-project-image-460007.jpeg";
            mPropertyImageView.setImageUrl(url, MakaanNetworkClient.getInstance().getImageLoader());
        }

        if(mListing.images != null && mListing.images.size() > 0) {

           /* ImageRequest request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mPropertyImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mPropertyImageView.setImageResource(R.drawable.temp_bulding);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");

            request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            mSellerImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            mSellerImageView.setImageResource(R.drawable.temp_builder);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");*/
        }

        if(mListing.lisitingPostedBy == null) {
            mSellerRatingBar.setVisibility(View.GONE);
        } else {
            mSellerRatingBar.setRating(mListing.lisitingPostedBy.rating);
        }

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

        // check possession for under construction properties
        // or age of property for reseller properties
        // or furnished/unfurnished/semi-furnished for rental properties
        if(isBuy) {
            if (mListing.isReadyToMove && mListing.propertyAge != null && !TextUtils.isEmpty(mListing.propertyAge)) {
                mPossesionTextView.setText("Age of Property");
                mPropertyPossessionDateTextView.setText(mListing.propertyAge);
            } else if(mListing.possessionDate != null && !TextUtils.isEmpty(mListing.possessionDate)) {
                mPossesionTextView.setText("Possession");
                mPropertyPossessionDateTextView.setText(mListing.possessionDate);
            } else {
                mPossesionTextView.setVisibility(View.GONE);
                mPropertyPossessionDateTextView.setVisibility(View.GONE);
                mPossessionImageView.setVisibility(View.GONE);
            }
        } else {
            if(mListing.furnished != null && !TextUtils.isEmpty(mListing.furnished)) {
                mPossesionTextView.setText("Furnished");
                mPropertyPossessionDateTextView.setText(mListing.furnished);
            } else {
                mPossesionTextView.setVisibility(View.GONE);
                mPropertyPossessionDateTextView.setVisibility(View.GONE);
                mPossessionImageView.setVisibility(View.GONE);
            }
        }

        // set price info
        mPropertyPriceTextView.setText(priceString);
        mPropertyPriceUnitTextView.setText(priceUnit);
        mPropertyPriceSqFtTextView.setText(String.format("%s/sqft", StringUtil.getFormattedNumber(mListing.pricePerUnitArea)));

        // set property bhk and size info
        if(mListing.bhkInfo == null) {
            mPropertyBhkInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertyBhkInfoTextView.setVisibility(View.VISIBLE);
            mPropertyBhkInfoTextView.setText(mListing.bhkInfo);
        }

        if(mListing.sizeInfo == null) {
            mPropertySizeInfoTextView.setVisibility(View.GONE);
        } else {
            mPropertySizeInfoTextView.setVisibility(View.VISIBLE);
            mPropertySizeInfoTextView.setText(mListing.sizeInfo);
        }

        // set property address info {project_name},{localityName}_{cityName}
        if(mListing.project.name != null) {
            mPropertyAddressTextView.setText(String.format("%s, %s, %s", mListing.project.name, mListing.localityName, mListing.cityName));
        } else {
            mPropertyAddressTextView.setText(String.format("%s, %s", mListing.localityName, mListing.cityName));
        }

        // set value of floor info of property out of total floors in building
        if(mListing.floor == 1) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>st</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 2) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>nd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else if(mListing.floor == 3) {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>rd</sup> of %d", mListing.floor, mListing.totalFloors)));
        } else {
            mPropertyFloorInfoTextView.setText(Html.fromHtml(String.format("%d<sup>th</sup> of %d", mListing.floor, mListing.totalFloors)));
        }

        // set bathroom info of property
        // TODO check if need to be replaced by any other value if bathroom value is 0 or negative
        mPropertyBathroomNumberTextView.setText(String.valueOf(mListing.bathrooms));

        // set property tagline or detailed info
        mPropertyDescriptionTextView.setText(Html.fromHtml(mListing.description));

        if(callback.needSellerInfoInSerp()) {
            mSellerInfoRelativeLayout.setVisibility(View.VISIBLE);

            // TODO check seller name
            if(mListing.lisitingPostedBy != null) {
                mPropertySellerNameTextView.setText(String.format("%s(%s)",mListing.lisitingPostedBy.name, mListing.lisitingPostedBy.type));
            } else {
                mPropertySellerNameTextView.setText(mListing.project.builderName);
            }

            // todo need to show seller logo image if available
            mSellerLogoTextView.setText(String.valueOf(mListing.lisitingPostedBy.name.charAt(0)));
            mSellerLogoTextView.setVisibility(View.VISIBLE);
            mSellerImageView.setVisibility(View.GONE);
            // show seller first character as logo
            Random random = new Random();
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
            drawable.getPaint().setColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mSellerLogoTextView.setBackground(drawable);
            } else {
                mSellerLogoTextView.setBackgroundDrawable(drawable);
            }

            // show or hide assist button
            if(mListing.lisitingPostedBy == null || !mListing.lisitingPostedBy.assist) {
                mAssistButton.setVisibility(View.GONE);
            } else {
                mAssistButton.setVisibility(View.VISIBLE);
            }
        } else {
            mSellerInfoRelativeLayout.setVisibility(View.GONE);
        }

        // TODO diff image view
        mPropertyShortlistCheckbox.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if(MakaanBuyerApplication.serpSelector.isBuyContext()) {
                MasterDataCache.getInstance().addShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId, true);
            } else {
                MasterDataCache.getInstance().addShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId, false);
            }
        } else {
            if(MakaanBuyerApplication.serpSelector.isBuyContext()) {
                MasterDataCache.getInstance().removeShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId, true);
            } else {
                MasterDataCache.getInstance().removeShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId, false);
            }
        }
    }

    @OnClick({R.id.serp_default_listing_seller_image_view, R.id.serp_default_listing_seller_name_text_view})
    public void onSellerPressed(View view) {
        // TODO discuss what should be done if listing posted by is not present

        MakaanBuyerApplication.serpSelector.removeTerm("builderId");
        MakaanBuyerApplication.serpSelector.removeTerm("localityId");
        MakaanBuyerApplication.serpSelector.removeTerm("cityId");
        MakaanBuyerApplication.serpSelector.removeTerm("suburbId");

        MakaanBuyerApplication.serpSelector.term("listingCompanyId", String.valueOf(mListing.lisitingPostedBy.id), true);

        mCallback.serpRequest(SerpActivity.TYPE_SELLER, MakaanBuyerApplication.serpSelector);

//        MakaanBuyerApplication.serpSelector.term("sellerId", String.valueOf(mListing.sellerId));
    }

    @OnClick(R.id.serp_default_listing_property_bhk_info_text_view)
    public void onPropertyPressed(View view) {

        Bundle bundle = new Bundle();
        bundle.putLong(KeyUtil.LISTING_ID, mListing.id);
        bundle.putDouble(KeyUtil.LISTING_LAT, mListing.latitude);
        bundle.putDouble(KeyUtil.LISTING_LON, mListing.longitude);

        mCallback.requestDetailPage(SerpActivity.REQUEST_PROPERTY_PAGE, bundle);

//        MakaanBuyerApplication.serpSelector.term("sellerId", String.valueOf(mListing.sellerId));
    }
}