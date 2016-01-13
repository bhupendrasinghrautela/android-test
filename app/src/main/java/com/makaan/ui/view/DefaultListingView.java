package com.makaan.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.response.listing.Listing;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;

import org.w3c.dom.Text;

import java.util.Locale;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class DefaultListingView extends AbstractListingView implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public CheckBox mPropertyShortlistCheckbox;

    @Bind(R.id.serp_default_listing_property_image_image_view)
    ImageView mPropertyImage_View;
    @Bind(R.id.serp_default_listing_property_price_difference_image_view)
    ImageView mPropertyPriceDifferenceImage_View;
    @Bind(R.id.serp_default_listing_badge_Image_view)
    ImageView mBadgeImageView;

    @Bind(R.id.serp_default_listing_seller_image_view)
    CircularImageView mSellerImageView;

    @Bind(R.id.serp_default_listing_property_price_text_view)
    TextView mPropertyPriceTextView;
    @Bind(R.id.serp_default_listing_property_price_unit_text_view)
    TextView mPropertyPriceUnitTextView;
    @Bind(R.id.serp_default_listing_property_price_sq_ft_text_view)
    TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.serp_default_listing_property_info_text_view)
    TextView mPropertyInfoTextView;
    @Bind(R.id.serp_default_listing_property_address_text_view)
    TextView mPropertyAddressTextView;
    @Bind(R.id.serp_default_listing_property_possession_date_text_view)
    TextView mPropertyPossessionDateTextView;
    @Bind(R.id.serp_default_listing_property_floor_info_text_view)
    TextView mPropertyFloorInfoTextView;
    @Bind(R.id.serp_default_listing_property_bathroom_number_date_text_view)
    TextView mPropertyBathroomNumberTextView;
    @Bind(R.id.serp_default_listing_property_tagline_text_view)
    TextView mPropertyTaglineTextView;
    @Bind(R.id.serp_default_listing_seller_name_text_view)
    TextView mPropertySellerNameTextView;
    @Bind(R.id.serp_default_listing_badge_text_view)
    TextView mBadgeTextView;

    @Bind(R.id.serp_default_listing_assist_button)
    Button mAssistButton;
    @Bind(R.id.serp_default_listing_call_button)
    Button mCallButton;


    private SharedPreferences mPreferences;
    private Listing mListing;

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
    public void populateData(Object data) {
        super.populateData(data);
        if(!(data instanceof Listing)) {
            return;
        }
        mListing = (Listing)data;
        mPropertyShortlistCheckbox.setOnCheckedChangeListener(null);
        mPreferences = mContext.getSharedPreferences(
                PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, Context.MODE_PRIVATE);

        if(MakaanBuyerApplication.isBuySearch) {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId);
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
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId);
            mPropertyShortlistCheckbox.setChecked(isShortlisted);
        }
        mPropertyShortlistCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
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

        // TODO set rating for the seller

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


        mPropertyPriceTextView.setText(priceString);
        mPropertyPriceUnitTextView.setText(priceUnit);
        mPropertyPriceSqFtTextView.setText(String.format(Locale.ENGLISH, "%d/sqft", mListing.pricePerUnitArea));
        mPropertyInfoTextView.setText(String.format(Locale.ENGLISH, "%s\n%s", mListing.bhkInfo, mListing.sizeInfo));
        mPropertyAddressTextView.setText(String.format(Locale.ENGLISH, "%s, %s", mListing.localityName, mListing.cityName));
        mPropertyPossessionDateTextView.setText(mListing.possessionDate);
        mPropertyFloorInfoTextView.setText(String.format(Locale.ENGLISH, "%drd of %dth", mListing.floor, mListing.totalFloors));
        mPropertyBathroomNumberTextView.setText(String.valueOf(mListing.bathrooms));
        mPropertyTaglineTextView.setText(mListing.description);

        // TODO check seller name
        mPropertySellerNameTextView.setText(mListing.project.builderName);

        // TODO diff image view

        mPropertyShortlistCheckbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            MasterDataCache.getInstance().addShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, mListing.lisitingId);
        } else {
            MasterDataCache.getInstance().removeShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, mListing.lisitingId);
        }
    }
}
