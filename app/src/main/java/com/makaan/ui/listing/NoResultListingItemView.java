package com.makaan.ui.listing;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makaan.R;
import com.makaan.activity.HomeActivity;
import com.makaan.activity.listing.SerpRequestCallback;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 4/5/16.
 */
public class NoResultListingItemView extends AbstractListingView {
    @Bind(R.id.serp_listing_item_no_result_image_view)
    ImageView mNoResultsImageView;
    @Bind(R.id.serp_listing_item_no_result_text_view)
    TextView mNoResultsTextView;
    @Bind(R.id.serp_listing_item_no_result_action_button)
    Button mActionButton;
    public NoResultListingItemView(Context context) {
        super(context);
    }

    public NoResultListingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoResultListingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback, int position) {
        super.populateData(data, callback, position);

        if(data instanceof String) {
            mNoResultsTextView.setText((String)data);
        }
        Glide.with(mContext).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);
    }

    @OnClick(R.id.serp_listing_item_no_result_action_button)
    public void onProjectClicked(View view) {
        Intent intent = new Intent(mContext, HomeActivity.class);
        // as per discussion with Amit, do not clear stack
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
