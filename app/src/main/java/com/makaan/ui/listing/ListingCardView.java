package com.makaan.ui.listing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;

import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;

import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.response.listing.Listing;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    public void bindView(final Context context, Listing item) {
        mListing = (Listing)item;
        mPreferences = context.getSharedPreferences(
                PreferenceConstants.PREF_SHORTLISTED_PROPERTIES, Context.MODE_PRIVATE);
        mShortlistPropertyCheckbox.setOnCheckedChangeListener(null);
        boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(mListing.lisitingId);
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


        if(mListing.mainImageUrl != null && !TextUtils.isEmpty(mListing.mainImageUrl)) {
            mPropertyImageView.setImageUrl(mListing.mainImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        } else {
            //TODO this is just a dummy image
            String url = "https://im.proptiger-ws.com/1/644953/6/imperial-project-image-460007.jpeg";
            mPropertyImageView.setImageUrl(url, MakaanNetworkClient.getInstance().getImageLoader());
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListing != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KeyUtil.LISTING_ID, mListing.id);
                    bundle.putDouble(KeyUtil.LISTING_LAT, mListing.latitude);
                    bundle.putDouble(KeyUtil.LISTING_LON, mListing.longitude);
                    bundle.putString(KeyUtil.LISTING_Image, mListing.mainImageUrl);

                    Intent intent = new Intent(context, PropertyActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    //mCallback.requestDetailPage(SerpActivity.REQUEST_PROPERTY_PAGE, bundle);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        WishListService wishListService =
                (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
        if(isChecked) {
            wishListService.addListing(mListing.lisitingId, mListing.projectId);
        } else {
            wishListService.delete(mListing.lisitingId);
        }
    }

    @OnClick(R.id.listing_brief_view_layout_address_relative_view)
    public void onProjectClicked(View view) {
        if(mListing.projectId != null && mListing.projectId != 0) {
            Bundle bundle = new Bundle();
            bundle.putLong(ProjectActivity.PROJECT_ID, mListing.projectId);

            Intent intent = new Intent(getContext(), ProjectActivity.class);
            intent.putExtras(bundle);
            getContext().startActivity(intent);
        }
    }
}
