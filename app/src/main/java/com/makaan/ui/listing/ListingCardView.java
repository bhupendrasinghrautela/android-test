package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.ui.listing.BaseCardView;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.listing.Listing;
import com.makaan.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunil on 04/01/16.
 */
public class ListingCardView extends BaseCardView<Listing> {


    @Bind(R.id.thumbnail)FadeInNetworkImageView mThumbnail;
    @Bind(R.id.price)TextView mPriceTextView;
    @Bind(R.id.unit)TextView mUnitNameTextView;
    @Bind(R.id.size)TextView mSizeTextView;
    @Bind(R.id.name)TextView mNameTextView;

    public ListingCardView(Context context) {
        super(context);
    }

    public ListingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, Listing item) {

        String price = StringUtil.getDisplayPrice(item.price);
        mPriceTextView.setText(price);
        mUnitNameTextView.setText(item.bhkInfo);

        //TODO this is just a dummy image
        String url = "https://im.proptiger-ws.com/1/644953/6/imperial-project-image-460007.jpeg";
        mThumbnail.setImageUrl(url, MakaanNetworkClient.getInstance().getImageLoader());

        if(item.size>0) {
            mSizeTextView.setText(item.sizeInfo);
        }

        mNameTextView.setText(item.project.fullName);

    }
}
