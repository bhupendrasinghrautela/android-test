package com.makaan.ui.property;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.image.Image;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aishwarya on 14/01/16.
 */
public class PropertyImageCardView extends CardView {
    @Bind(R.id.property_image_detail)
    RelativeLayout mPropertyImageDetailLayout;
    @Bind(R.id.property_imageview)
    FadeInNetworkImageView mPropertyImageView;
    @Bind(R.id.price_text)
    TextView price;

    public PropertyImageCardView(Context context) {
        super(context);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindView(Context mContext, Image listingDetailImage, int position) {
        ButterKnife.bind(this);
/*        if(position != 1){
            mPropertyImageDetailLayout.setVisibility(GONE);
        }else{
            mPropertyImageDetailLayout.setVisibility(VISIBLE);
        }*/
        price.setText(String.valueOf(position));
        mPropertyImageView.setImageUrl(listingDetailImage.absolutePath, MakaanNetworkClient.getInstance().getImageLoader());
    }
}
