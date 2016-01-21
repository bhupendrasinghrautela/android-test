package com.makaan.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.PreferenceConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.Listing;
import com.makaan.util.Blur;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class BuilderListingView extends AbstractListingView {
    @Bind(R.id.serp_listing_item_builder_background_image_view)
    ImageView mBuilderBackgroundImageView;

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
    public void populateData(Object data, SerpRequestCallback callback) {
        super.populateData(data, callback);

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
}