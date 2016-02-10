package com.makaan.ui.listing;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.listing.Listing;
import com.makaan.response.serp.ListingInfoMap;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Random;

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
        String text = mCallback.getOverviewText();
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
