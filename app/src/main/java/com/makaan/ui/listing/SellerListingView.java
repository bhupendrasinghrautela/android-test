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
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Text;

import java.util.Calendar;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/7/16.
 */
public class SellerListingView extends AbstractCardListingView {
    @Bind(R.id.serp_listing_item_seller_background_image_view)
    ImageView mSellerBackgroundImageView;

    @Bind(R.id.serp_listing_item_seller_image_view)
    CircularImageView mSellerImageView;
    @Bind(R.id.serp_listing_item_seller_name_text_view)
    TextView mSellerNameTextView;
    @Bind(R.id.serp_listing_item_seller_company_name_text_view)
    TextView mSellerCompanyNameTextView;
    @Bind(R.id.serp_listing_item_seller_not_rated_text_view)
    TextView mNotRatedTextView;

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
    }

    @Subscribe
    public void onResults(SellerByIdEvent sellerByIdEvent) {
        if(null== sellerByIdEvent || null!=sellerByIdEvent.error){
            //TODO handle error
            return;
        }
        CompanySeller seller = sellerByIdEvent.seller;
        if(seller.sellers != null && seller.sellers.size() > 0 && seller.sellers.get(0).companyUser != null
                && seller.sellers.get(0).companyUser.sellerListingData != null) {
            mSellerNameTextView.setText(seller.sellers.get(0).companyUser.user.fullName);
            if(seller.sellers.get(0).cities != null) {
                if(seller.sellers.get(0).cities.size() > 1) {
                    mSellerOperatesInTextView.setText(String.format("operates in - %d localities in %d cities",
                            seller.sellers.get(0).companyUser.sellerListingData.localityCount, seller.sellers.get(0).cities.size()));
                } else if(seller.sellers.get(0).cities.size() == 1) {
                    mSellerOperatesInTextView.setText(String.format("operates in - %d localities in %s",
                            seller.sellers.get(0).companyUser.sellerListingData.localityCount, seller.sellers.get(0).cities.get(0).label));
                } else {
                    mSellerOperatesInTextView.setText(String.format("operates in - %d localities", seller.sellers.get(0).companyUser.sellerListingData.localityCount));
                }
            } else {
                mSellerOperatesInTextView.setText(String.format("operates in - %d localities", seller.sellers.get(0).companyUser.sellerListingData.localityCount));
            }

            if(seller.sellers.get(0).companyUser.sellerListingData.categoryWiseCount != null
                    && seller.sellers.get(0).companyUser.sellerListingData.categoryWiseCount.size() > 0) {
                String amp = "";
                int properties = 0;
                StringBuilder builder = new StringBuilder("");
                for(CompanySeller.Seller.CompanyUser.SellerListingData.CategoryWiseCount categoryWiseCount
                        : seller.sellers.get(0).companyUser.sellerListingData.categoryWiseCount) {
                    properties += categoryWiseCount.listingCount;
                    builder.append(amp);
                    builder.append(categoryWiseCount.listingCategoryType.toLowerCase());
                    amp = " & ";
                }
                mSellerPropertiesCountTextView.setText(String.format("%d properties", properties));
                mSellerTypeTextView.setText(builder.toString());
            }
            mSellerProjectCountTextView.setText(String.format("%d projects", seller.sellers.get(0).companyUser.sellerListingData.projectCount));
        }

        mSellerCompanyNameTextView.setText(seller.name);

        if(seller.activeSince != null) {
            mSellerExperienceTextView.setVisibility(View.VISIBLE);
            mSellerExperienceTextView.setText(StringUtil.getAgeFromTimeStamp(seller.activeSince, Calendar.YEAR));
        } else {
            mSellerExperienceTextView.setVisibility(View.INVISIBLE);
        }

        if(seller.score > 0) {
            mSellerRatingBar.setVisibility(View.VISIBLE);
            mNotRatedTextView.setVisibility(View.GONE);
            mSellerRatingBar.setRating(seller.score / 2.0f);
        } else {
            mSellerRatingBar.setVisibility(View.GONE);
            mNotRatedTextView.setVisibility(View.VISIBLE);
        }

        if(seller.logo != null) {
            // get seller image
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(
                    seller.logo, mSellerImageView.getWidth(), mSellerImageView.getHeight(), false), new ImageLoader.ImageListener() {
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
            // TODO discuss request size
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(
                    seller.coverPicture, mSellerBackgroundImageView.getWidth(), mSellerBackgroundImageView.getHeight(), false), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    final Bitmap newImg = Blur.fastblur(mContext, image, 5);
                    mSellerBackgroundImageView.setImageBitmap(newImg);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
        } else {
            // TODO need to use original data
            Bitmap bitmap = null;

            final Drawable image = mContext.getResources().getDrawable(R.drawable.temp_bulding);
            if(image.getIntrinsicWidth() <= 0 || image.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444);
            } else {
                bitmap = Bitmap.createBitmap(image.getIntrinsicWidth(), image.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
            }
            Canvas canvas = new Canvas(bitmap);
            image.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            image.draw(canvas);

            final Bitmap newImg = Blur.fastblur(mContext, bitmap, 25);

            mSellerBackgroundImageView.setImageBitmap(newImg);
        }
        this.setVisibility(View.VISIBLE);

        AppBus.getInstance().unregister(this);
    }
}