package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.response.listing.GroupListing;
import com.makaan.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class ChildSerpGroupListingItemView extends RelativeLayout implements View.OnClickListener {
    @Bind(R.id.child_serp_cluster_card_price_range_text_view)
    TextView mPropertyPriceRangeTextView;
    @Bind(R.id.child_serp_cluster_card_address_text_view)
    TextView mPropertyAddressTextView;

    private Long mMinBudget;
    private Long mMaxBudget;
    private Integer mBedrooms;
    private String mLocality;
    private Integer mUnitId;

    private static final String PRICE_RANGE_STRING = "%s | %s";
    private static final String PROPERTY_ADDRESS_STRING = "%s, %s";
    private SerpRequestCallback mCallback;
    private GroupListing mItem;

    public ChildSerpGroupListingItemView(Context context) {
        super(context);
    }

    public ChildSerpGroupListingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildSerpGroupListingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        // TODO check for city name
        mPropertyAddressTextView.setText(String.format(PROPERTY_ADDRESS_STRING, mLocality, ""));

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mCallback != null && mItem != null) {
            mCallback.serpRequest(SerpActivity.TYPE_CLUSTER, mItem.listing.id);
        }
    }
}
