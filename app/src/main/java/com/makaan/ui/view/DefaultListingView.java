package com.makaan.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.response.listing.Listing;
import com.pkmmte.view.CircularImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class DefaultListingView extends AbstractListingView {
    @Bind(R.id.default_listing_property_image_image_view)
    public ImageView propertyImageView;

    @Bind(R.id.default_listing_call_image_view)
    public ImageView callSellerImageView;
    @Bind(R.id.default_listing_quick_view_image_view)
    public ImageView quickViewImageView;

    @Bind(R.id.default_listing_seller_image_view)
    public CircularImageView sellerImageView;

    @Bind(R.id.default_listing_property_since_duration_text_view)
    public TextView propertySinceDurationTextView;
    @Bind(R.id.default_listing_property_type_text_view)
    public TextView propertyTypeTextView;
    @Bind(R.id.default_listing_property_rating_image_view)
    public ImageView propertyRatingImageView;
    @Bind(R.id.default_listing_property_price_tnc_text_view)
    public TextView propertyPriceTncTextView;
    @Bind(R.id.default_listing_property_price_text_view)
    public TextView propertyPriceTextView;
    @Bind(R.id.default_listing_property_address_text_view)
    public TextView propertyAddressTextView;
    @Bind(R.id.default_listing_property_floor_text_view)
    public TextView propertyFloorTextView;
    @Bind(R.id.default_listing_property_bathroom_text_view)
    public TextView propertyBathroomTextView;

    public DefaultListingView(Context context) {
        super(context);
    }

    public DefaultListingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultListingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        //ButterKnife.bind(this, this);
    }

    @Override
    public void populateData(Object data) {
        super.populateData(data);
        if(!(data instanceof Listing)) {
            return;
        }
        Listing listing = (Listing)data;
        if(listing.images != null && listing.images.size() > 0) {

            ImageRequest request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            propertyImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            propertyImageView.setImageResource(R.drawable.temp_bulding);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");

            request = new ImageRequest(listing.images.get(0).url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            sellerImageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            sellerImageView.setImageResource(R.drawable.temp_builder);
                        }
                    });
            MakaanBuyerApplication.getInstance().addToRequestQueue(request, "");
        }

        // TODO change to since
        propertySinceDurationTextView.setText(listing.postedDate);
        propertyTypeTextView.setText(listing.bhkInfo);

        // TODO set rating for the property

        propertyPriceTncTextView.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                // TODO show tool tip
                return false;
            }
        });
        propertyPriceTextView.setText(listing.price.toString());
        propertyAddressTextView.setText(listing.localityName);
        propertyFloorTextView.setText(String.valueOf(listing.floor));
        propertyBathroomTextView.setText(String.valueOf(listing.bathrooms));
    }
}
