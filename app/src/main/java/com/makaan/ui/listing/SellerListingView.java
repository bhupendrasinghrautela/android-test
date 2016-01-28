package com.makaan.ui.listing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.event.builder.BuilderByIdEvent;
import com.makaan.event.seller.SellerByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.image.Image;
import com.makaan.response.listing.Listing;
import com.makaan.response.project.Builder;
import com.makaan.response.project.CompanySeller;
import com.makaan.util.AppBus;
import com.makaan.util.Blur;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Text;

import java.util.Calendar;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class SellerListingView extends AbstractListingView {
    @Bind(R.id.serp_listing_item_seller_background_image_view)
    ImageView mSellerBackgroundImageView;

    @Bind(R.id.serp_listing_item_seller_image_view)
    CircularImageView mSellerImageView;
    @Bind(R.id.serp_listing_item_seller_name_text_view)
    TextView mSellerNameTextView;
    @Bind(R.id.serp_listing_item_seller_company_name_text_view)
    TextView mSellerCompanyNameTextView;

    @Bind(R.id.serp_listing_item_seller_operates_in_text_view)
    TextView mSellerOperatesInTextView;
    @Bind(R.id.serp_listing_item_seller_address_text_view)
    TextView mSellerAddressTextView;
    @Bind(R.id.serp_listing_item_seller_experience_text_view)
    TextView mSellerExperienceTextView;
    @Bind(R.id.serp_listing_item_seller_type_text_view)
    TextView mSellerTypeTextView;

    @Bind(R.id.serp_listing_item_seller_properties_count_text_view)
    TextView mSellerPropertiesCountTextView;
    @Bind(R.id.serp_listing_item_seller_project_count_text_view)
    TextView mSellerProjectCountTextView;

    @Bind(R.id.serp_listing_item_seller_rating_bar)
    RatingBar mSellerRatingBar;

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

        try {
            AppBus.getInstance().register(this);
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        callback.requestApi(SerpActivity.REQUEST_SELLER_API, "listingCompanyId");
        this.setVisibility(View.GONE);

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

    @Subscribe
    public void onResults(SellerByIdEvent sellerByIdEvent) {
        CompanySeller seller = sellerByIdEvent.seller;
        mSellerNameTextView.setText(seller.sellers.get(0).companyUser.user.fullName);
        mSellerCompanyNameTextView.setText(seller.name);

        mSellerOperatesInTextView.setText(String.format("operates in - %d localities", seller.sellers.get(0).companyUser.sellerListingData.localityCount));
        mSellerExperienceTextView.setText(StringUtil.getAgeFromTimeStamp(seller.activeSince, Calendar.YEAR));

        mSellerRatingBar.setRating(seller.score / 2.0f);

        mSellerPropertiesCountTextView.setText(String.format("%d properties",
                seller.sellers.get(0).companyUser.sellerListingData.categoryWiseCount.get(0).listingCount));

        mSellerProjectCountTextView.setText(String.format("%d projects", seller.sellers.get(0).companyUser.sellerListingData.projectCount));

        if(seller.logo != null) {
            // get seller image
            MakaanNetworkClient.getInstance().getImageLoader().get(seller.logo, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    mSellerImageView.setImageBitmap(image);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }

        if(seller.coverPicture != null) {
            // get seller cover image
            MakaanNetworkClient.getInstance().getImageLoader().get(seller.coverPicture, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    final Bitmap newImg = Blur.fastblur(mContext, image, 25);
                    mSellerBackgroundImageView.setImageBitmap(newImg);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        }
        this.setVisibility(View.VISIBLE);

        AppBus.getInstance().unregister(this);
    }
}