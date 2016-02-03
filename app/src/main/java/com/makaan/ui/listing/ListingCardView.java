package com.makaan.ui.listing;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;

import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;

import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.response.listing.Listing;
import com.makaan.util.StringUtil;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by sunil on 04/01/16.
 */
public class ListingCardView extends BaseCardView<Listing> implements CompoundButton.OnCheckedChangeListener {


    @Bind(R.id.listing_brief_view_layout_property_image_view)FadeInNetworkImageView mPropertyImageView;
    @Bind(R.id.listing_brief_view_layout_property_price_text_view)TextView mPropertyPriceTextView;
    @Bind(R.id.listing_brief_view_layout_property_price_unit_text_view)TextView mPropertyPriceUnitTextView;
    @Bind(R.id.listing_brief_view_layout_property_shortlist_checkbox)CheckBox mShortlistPropertyCheckbox;
    @Bind(R.id.listing_brief_view_layout_property_price_sq_ft_text_view)TextView mPropertyPriceSqFtTextView;
    @Bind(R.id.listing_brief_view_layout_property_price_difference_image_view)ImageView mDifferenceImageView;
    @Bind(R.id.listing_brief_view_layout_property_bhk_info_text_view)TextView mPropertyBhkInfoTextView;
    @Bind(R.id.listing_brief_view_layout_property_size_info_text_view)TextView mPropertySizeInfoTextView;
    @Bind(R.id.listing_brief_view_layout_property_address_text_view)TextView mPropertyAddressTextView;
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
    public void bindView(Context context, Listing item) {
        mListing = (Listing)item;
        mPreferences = context.getSharedPreferences(
                PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, Context.MODE_PRIVATE);
        mShortlistPropertyCheckbox.setOnCheckedChangeListener(null);
        if(SerpObjects.isBuyContext(getContext())) {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, item.lisitingId, true);
            mShortlistPropertyCheckbox.setChecked(isShortlisted);
            // TODO code to show badges

        } else {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, item.lisitingId, false);
            mShortlistPropertyCheckbox.setChecked(isShortlisted);
        }
        mShortlistPropertyCheckbox.setOnCheckedChangeListener(this);

        // TODO check for unit info
        String priceString = StringUtil.getDisplayPrice(item.price);
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
        mPropertyPriceSqFtTextView.setText(String.format("%d/sqft", item.pricePerUnitArea));
        mPropertyBhkInfoTextView.setText(item.bhkInfo);
        mPropertySizeInfoTextView.setText(item.sizeInfo);
        mPropertyAddressTextView.setText(String.format("%s, %s", item.localityName, item.cityName));

        //TODO this is just a dummy image
        String url = "https://im.proptiger-ws.com/1/644953/6/imperial-project-image-460007.jpeg";
        mPropertyImageView.setImageUrl(url, MakaanNetworkClient.getInstance().getImageLoader());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            if(SerpObjects.isBuyContext(getContext())) {
                MasterDataCache.getInstance().addShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId, true);
            } else {
                MasterDataCache.getInstance().addShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId, false);
            }
        } else {
            if(SerpObjects.isBuyContext(getContext())) {
                MasterDataCache.getInstance().removeShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_BUY, mListing.lisitingId, true);
            } else {
                MasterDataCache.getInstance().removeShortlistedProperty(mPreferences, PreferenceConstants.PREF_SHORTLISTED_PROPERTIES_KEY_RENT, mListing.lisitingId, false);
            }
        }
    }
}
