package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.cache.MasterDataCache;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.util.StringUtil;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class GroupListingItemView extends RelativeLayout implements View.OnClickListener {
    @Bind(R.id.cluster_item_view_property_price_range_text_view)
    TextView mPropertyPriceRangeTextView;
    @Bind(R.id.cluster_item_view_property_type_text_view)
    TextView mPropertyTypeTextView;
    @Bind(R.id.cluster_item_view_click_button)
    TextView mPropertyCountTextView;
    @Bind(R.id.cluster_item_view_property_size_text_view)
    TextView mPropertySizeTextView;

    private Long mMinBudget;
    private Long mMaxBudget;
    private Integer mBedrooms;
    private String mLocality;
    private Integer mUnitId;

    private static final String PRICE_RANGE_STRING = "%s to %s";
    private static final String PROPERTY_SIZE_STRING = "%dbhk ";
    private static final String LISTING_SIZE_STRING = "%s listings ";
    private SerpRequestCallback mCallback;
    private GroupListing mItem;

    public GroupListingItemView(Context context) {
        super(context);
    }

    public GroupListingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupListingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // TODO need to map data from web based data
    public void populateView(GroupListing item, final SerpRequestCallback callback) {
        ButterKnife.bind(this, this);
        this.mCallback = callback;
        this.mItem = item;

        for(GroupListing.GroupingAttributes attribute : item.groupingAttributes) {
            switch (attribute.type) {
                case "minBudget":
                    mMinBudget = Long.valueOf(attribute.value);
                    break;
                case "maxBudget":
                    mMaxBudget = Long.valueOf(attribute.value);
                    break;
                case "bedrooms":
                    mBedrooms = Integer.valueOf(attribute.value);
                    break;
                case "locality":
                    mLocality = attribute.value;
                    break;
                case "unitTypeId":
                    mUnitId = Integer.valueOf(attribute.value);
                    break;
            }
        }

        String minString = StringUtil.getDisplayPrice(mMinBudget);
        String maxString = StringUtil.getDisplayPrice(mMaxBudget);

        if(minString.indexOf("\u20B9") == 0) {
            minString = minString.replace("\u20B9", "");
        }

        if(maxString.indexOf("\u20B9") == 0) {
            maxString = maxString.replace("\u20B9", "");
        }

        mPropertyPriceRangeTextView.setText(String.format(PRICE_RANGE_STRING, minString, maxString));
        mPropertySizeTextView.setText(String.format(PROPERTY_SIZE_STRING, mBedrooms));
        for(ApiIntLabel intLabel : MasterDataCache.getInstance().getBuyPropertyTypes()) {
            if(intLabel.id.equals(mUnitId)) {
                if(intLabel.name != null) {
                    mPropertyTypeTextView.setText(intLabel.name.toLowerCase());
                }
                break;
            }
        }
//        mPropertyCountTextView.setText(String.format(LISTING_SIZE_STRING, String.valueOf(item.groupedListings.size() + 1)));
        mPropertyCountTextView.setText("view properties");

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mCallback != null && mItem != null) {
            mCallback.serpRequest(SerpActivity.TYPE_CLUSTER, mItem.listing.id);
        }
    }
}
