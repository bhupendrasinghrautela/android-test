package com.makaan.ui.listing;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class CountListingView extends RelativeLayout {
    @Bind(R.id.fragment_listing_total_properties_overview_button)
    Button mOverviewButton;

    @Bind(R.id.fragment_listing_total_properties_text_view)
    TextView mTotalPropertiesTextView;
    private SerpRequestCallback mCallback;

    public CountListingView(Context context) {
        super(context);
    }

    public CountListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void populateData(Object data, SerpRequestCallback callback) {
        ButterKnife.bind(this, this);
        if(!(data instanceof String)) {
            return;
        }
        mCallback = callback;
        mTotalPropertiesTextView.setText(String.valueOf(data));
        String text = null;
        if(mCallback != null) {
            text = mCallback.getOverviewText();
        }
        if(TextUtils.isEmpty(text)) {
            mOverviewButton.setVisibility(View.GONE);
        } else {
            mOverviewButton.setVisibility(View.VISIBLE);
            mOverviewButton.setText(text);
        }
    }

    @OnClick(R.id.fragment_listing_total_properties_set_alert_button)
    public void onSetAlertClicked(View view) {
        if(mCallback != null) {
            mCallback.requestDetailPage(SerpActivity.REQUEST_SET_ALERT, null);
        }
    }

    @OnClick(R.id.fragment_listing_total_properties_overview_button)
    public void onOverviewClicked(View view) {
        if(mCallback != null) {
            mCallback.requestOverviewPage();
        }
    }
}
