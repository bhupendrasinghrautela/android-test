package com.makaan.ui.listing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.event.builder.BuilderByIdEvent;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Builder;
import com.makaan.util.AppBus;
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class BuilderListingView extends AbstractCardListingView {
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

    @Bind(R.id.serp_listing_item_builder_content_linear_layout)
    LinearLayout mSellerContentLinearLayout;


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

        try {
            AppBus.getInstance().register(this);
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        callback.requestApi(SerpActivity.REQUEST_BUILDER_API, "builderId");
        mSellerContentLinearLayout.setVisibility(View.GONE);

        /*Bitmap bitmap = null;

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

        mBuilderBackgroundImageView.setImageBitmap(newImg);*/
    }

    @Subscribe
    public void onResults(BuilderByIdEvent builderByIdEvent) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity)mContext;
            if (activity.isFinishing() ) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if(activity.isDestroyed()) {
                    return;
                }
            }
        }
        Builder builder = builderByIdEvent.builder;
        if(builder == null) {
            return;
        }
        if(!TextUtils.isEmpty(builder.name)) {
            mBuilderNameTextView.setText(builder.name.toLowerCase());
        }
        try {
            mBuilderExperienceTextView.setText(StringUtil.getAgeFromTimeStamp(Long.valueOf(builder.establishedDate), Calendar.YEAR));
        } catch(NumberFormatException ex) {
            ex.printStackTrace();
        }

        if(builder.projectStatusCount.underConstruction == 0) {
            mOngoingProjectsTextView.setText("na");
        } else {
            mOngoingProjectsTextView.setText(String.valueOf(builder.projectStatusCount.underConstruction));
        }
        if(builder.projectStatusCount.completed == 0) {
            mBuilderPastProjectsTextView.setText("na");
        } else {
            mBuilderPastProjectsTextView.setText(String.valueOf(builder.projectStatusCount.completed));
        }
        // TODO check for avg delay
        mBuilderAvgDelayTextView.setText(String.format("%d%s", builder.projectStatusCount.cancelled, "%"));

        if(builder.imageURL != null) {
            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_item_builder_image_card_view_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_item_builder_image_card_view_height);
            // get seller image
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(builder.imageURL, width, height, false),
                    new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    if(mBuilderImageView!=null) {
                        mBuilderImageView.setImageBitmap(image);
                    }
                }
            });
        }

        mSellerContentLinearLayout.setVisibility(View.VISIBLE);
        AppBus.getInstance().unregister(this);
    }
}