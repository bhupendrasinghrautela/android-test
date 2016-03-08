package com.makaan.ui.property;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.image.Image;
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;

import java.text.DecimalFormat;

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
    @Bind(R.id.price_change)
    ImageView priceChange;
    @Bind(R.id.rupee_icon)
    ImageView rupeeIcon;
    @Bind(R.id.area_text)
    TextView sizeText;
    @Bind(R.id.emi_text)
    TextView emiText;

    public PropertyImageCardView(Context context) {
        super(context);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindView(Context mContext, Image listingDetailImage, int position, Double price, Double size, boolean hasIncreased, String category) {
        ButterKnife.bind(this);
        /*if(position != 1){
            mPropertyImageDetailLayout.setVisibility(GONE);
        }else{*/
            mPropertyImageDetailLayout.setVisibility(VISIBLE);
//        }
        if(price!=null && price > 0) {
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
            if(!TextUtils.isEmpty(priceUnit) && category != null) {
                priceUnit = priceUnit.concat(" +");
            }
            priceText.setText(String.valueOf(priceString));
            rupeeIcon.setVisibility(VISIBLE);
            priceUnitText.setText(priceUnit);
        } else {
            rupeeIcon.setVisibility(GONE);
        }

        mPropertyImageView.setDefaultImageResId(R.drawable.luxury_project);
        int width = (int) (getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
        int height = (int)Math.ceil(getResources().getDimension(R.dimen.property_page_stack_like_pager_height));
        mPropertyImageView.setImageUrl(ImageUtils.getImageRequestUrl(listingDetailImage.absolutePath, width, height, false),
                MakaanNetworkClient.getInstance().getImageLoader());
        if(category == null){
            initData(size,price,hasIncreased,mContext);
        }
        else if(!TextUtils.isEmpty(category) && !category.equals("Rental")) {
            initData(size,price,hasIncreased,mContext);
        }
    }

    public Long calculateEmi(Double principal,Double ratePerAnuminFraction,Integer tenure) {
        Double emi = principal * (ratePerAnuminFraction / 12) * Math.pow(1 + ratePerAnuminFraction / 12, tenure * 12) / (Math.pow(1 + ratePerAnuminFraction / 12, tenure * 12) - 1);
        return Math.round(emi);
    }

    public void initData(Double size,Double price,boolean hasIncreased,Context mContext){
        if (size != null && size > 0) {
            sizeText.setText(StringUtil.getFormattedNumber(size) + " " + mContext.getString(R.string.avg_price_postfix));
            priceChange.setVisibility(VISIBLE);
        } else {
            priceChange.setVisibility(INVISIBLE);
        }
        if (price != null) {
            String formatted = new DecimalFormat("##,##,##0").format(calculateEmi(price, 9.55 / 100, 20));
            emiText.setText(mContext.getString(R.string.emi_rs) + " " + formatted);
        }
        if (hasIncreased) {
            priceChange.setImageResource(R.drawable.bottom_arrow_circle);
        } else {
            priceChange.setImageResource(R.drawable.bottom_arrow_circle_green);
        }
    }
}
