package com.makaan.ui.listing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.event.builder.BuilderByIdEvent;
import com.makaan.response.project.Builder;
import com.makaan.util.AppBus;
import com.makaan.util.Blur;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class BuilderListingView extends AbstractListingView {
    @Bind(R.id.serp_listing_item_builder_background_image_view)
    ImageView mBuilderBackgroundImageView;
    @Bind(R.id.serp_listing_item_builder_image_view)
    ImageView mBuilderImageView;

    @Bind(R.id.serp_listing_item_builder_name_text_view)
    TextView mBuilderNameTextView;
    @Bind(R.id.serp_listing_item_builder_experience_text_view)
    TextView mBuilderExperienceTextView;
    @Bind(R.id.serp_listing_item_builder_ongoing_projects_text_view)
    TextView mOngoingProjectsTextView;
    @Bind(R.id.serp_listing_item_builder_past_projects_text_view)
    TextView mBuilderPastProjectsTextView;
    @Bind(R.id.serp_listing_item_builder_avg_delay_text_view)
    TextView mBuilderAvgDelayTextView;


    public BuilderListingView(Context context) {
        super(context);
    }

    public BuilderListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuilderListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);

        AppBus.getInstance().register(this);

        callback.requestApi(SerpActivity.REQUEST_BUILDER_API, "builderId");

        // TODO need to use original data
        Bitmap bitmap = null;

        final Drawable image = mContext.getResources().getDrawable(R.drawable.temp_bulding);
        if(image.getIntrinsicWidth() <= 0 || image.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(image.getIntrinsicWidth(), image.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        image.draw(canvas);

        final Bitmap newImg = Blur.fastblur(mContext, bitmap, 25);

        mBuilderBackgroundImageView.setImageBitmap(newImg);
    }

    @Subscribe
    public void onResults(BuilderByIdEvent builderByIdEvent) {
        Builder builder = builderByIdEvent.builder;
        mBuilderNameTextView.setText(builder.name);
        mBuilderExperienceTextView.setText(builder.establishedDate);
        mOngoingProjectsTextView.setText(String.valueOf(builder.projectStatusCount.underConstruction));
        mBuilderPastProjectsTextView.setText(String.valueOf(builder.projectStatusCount.completed));
        // TODO check for avg delay
        mBuilderAvgDelayTextView.setText(String.format("%d%s", builder.projectStatusCount.cancelled, "%"));

        AppBus.getInstance().unregister(this);
    }
}