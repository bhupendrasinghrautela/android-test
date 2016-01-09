package com.makaan.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class TempClusterItemView extends RelativeLayout {
    @Bind(R.id.cluster_item_view_property_price_range_text_view)
    TextView propertyPriceRangeTextView;
    @Bind(R.id.cluster_item_view_property_type_text_view)
    TextView propertyTypeTextView;
    @Bind(R.id.cluster_item_view_property_address_text_view)
    TextView propertyAddressTextView;
    @Bind(R.id.cluster_item_view_click_button)
    Button clickButton;
    public TempClusterItemView(Context context) {
        super(context);
    }

    public TempClusterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TempClusterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TempClusterItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // TODO need to map data from web based data
    public void populateView(TempClusterItem item) {
        ButterKnife.bind(this, this);
        propertyPriceRangeTextView.setText(item.getPropertyPriceRange());
        propertyTypeTextView.setText(item.getPropertyType());
        propertyAddressTextView.setText(item.getPropertyAddress());
        clickButton.setText(item.getPropertyCount());
    }
}
