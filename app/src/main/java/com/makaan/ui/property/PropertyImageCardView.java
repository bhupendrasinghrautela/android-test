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
import com.makaan.util.StringUtil;

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
    TextView priceText;
    @Bind(R.id.price_unit)
    TextView priceUnitText;

    public PropertyImageCardView(Context context) {
        super(context);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindView(Context mContext, Image listingDetailImage, int position,Double price) {
        ButterKnife.bind(this);
        if(position != 1){
            mPropertyImageDetailLayout.setVisibility(GONE);
        }else{
            mPropertyImageDetailLayout.setVisibility(VISIBLE);
        }
        if(price!=null) {
            String priceString = StringUtil.getDisplayPrice(price);
            String priceUnit = "";
            if(priceString.indexOf("\u20B9") == 0) {
                priceString = priceString.replace("\u20B9", "");
            }
            String[] priceParts = priceString.split(" ");
            priceString = priceParts[0];
            if(priceParts.length > 1) {
                priceUnit = priceParts[1];
            }
            priceText.setText(String.valueOf(priceString));
            priceUnitText.setText(priceUnit);
        }
        mPropertyImageView.setImageUrl(listingDetailImage.absolutePath, MakaanNetworkClient.getInstance().getImageLoader());
    }
}
