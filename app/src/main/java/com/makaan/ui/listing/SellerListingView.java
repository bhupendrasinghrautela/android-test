package com.makaan.ui.listing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.response.listing.Listing;
import com.makaan.util.Blur;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class SellerListingView extends AbstractListingView {
    @Bind(R.id.serp_listing_item_seller_background_image_view)
    ImageView mSellerBackgroundImageView;

    private Listing mListing;

    public SellerListingView(Context context) {
        super(context);
    }

    public SellerListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SellerListingView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        mSellerBackgroundImageView.setImageBitmap(newImg);
    }
}