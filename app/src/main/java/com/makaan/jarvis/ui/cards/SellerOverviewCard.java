package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpRequest;
import com.makaan.ui.view.BaseView;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.util.ImageUtils;

import java.util.Random;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sunil on 16/01/16.
 */
public class SellerOverviewCard extends BaseView<Message> {
    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.properties)
    TextView properties;

    @Bind(R.id.locations)
    TextView locations;

    @Bind(R.id.seller_image_view)
    CircleImageView mSellerImageView;

    @Bind(R.id.seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.seller_rating)
    CustomRatingBar sellerRatingBar;

    public SellerOverviewCard(Context context) {
        super(context);
    }

    public SellerOverviewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bindView(final Context context, final Message item) {

        sellerRatingBar.setRating(item.chatObj.score);

        if(!TextUtils.isEmpty(item.chatObj.name)) {
            name.setText(item.chatObj.name.toLowerCase());
        }

        if(item.chatObj.listingCount>0) {
            properties.setText(item.chatObj.listingCount + (item.chatObj.listingCount > 1 ? " properties" : " property"));
        }

        if(!TextUtils.isEmpty(item.chatObj.localityName + item.chatObj.cityName)) {
            StringBuilder builder = new StringBuilder();
            if(item.chatObj.localitiesCount>0){
                builder.append("in " + item.chatObj.localitiesCount + " localities ");
            }

            if(!TextUtils.isEmpty(item.chatObj.cityCountText)){
                builder.append(item.chatObj.cityCountText);
            }

            if(!TextUtils.isEmpty(builder.toString())){
                locations.setText(builder.toString().toLowerCase());
            }

        }

        if(!TextUtils.isEmpty(item.chatObj.image)) {
            int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
            int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(item.chatObj.image, width, height, false),
                    new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showTextAsImage(item.chatObj.name);
                }
            });
        } else {
            showTextAsImage(item.chatObj.name);
        }

        //item.chatObj.image = item.chatObj.imageURL;//TODO this temp due to api

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SerpRequest request = new SerpRequest(SerpActivity.TYPE_SELLER);
                request.setSellerId(item.chatObj.id);
                request.launchSerp(getContext());
            }
        });

    }

    private void showTextAsImage(String sellerName) {
        if(TextUtils.isEmpty(sellerName)){
            return;
        }
        mSellerLogoTextView.setText(String.valueOf(sellerName.charAt(0)));
        mSellerLogoTextView.setVisibility(View.VISIBLE);
        mSellerImageView.setVisibility(View.GONE);
        // show seller first character as logo

        int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        // int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerLogoTextView.setBackground(drawable);
        } else {
            mSellerLogoTextView.setBackgroundDrawable(drawable);
        }
    }
}
