package com.makaan.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.makaan.activity.listing.SerpActivity;
import com.makaan.pojo.TempClusterItem;
import com.makaan.service.ListingService;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class TempClusterItemView extends RelativeLayout {
    @Bind(R.id.cluster_item_view_property_price_range_text_view)
    TextView mPropertyPriceRangeTextView;
    @Bind(R.id.cluster_item_view_property_type_text_view)
    TextView mPropertyTypeTextView;
    @Bind(R.id.cluster_item_view_click_button)
    TextView mPropertyCountTextView;
    public TempClusterItemView(Context context) {
        super(context);
    }

    public TempClusterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TempClusterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // TODO need to map data from web based data
    public void populateView(TempClusterItem item) {
        ButterKnife.bind(this, this);
        mPropertyPriceRangeTextView.setText(item.getPropertyPriceRange());
        mPropertyTypeTextView.setText(item.getPropertyType());
        mPropertyCountTextView.setText(item.getPropertyCount());

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SerpActivity.isChildSerp = true;
                new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
            }
        });
    }
}
