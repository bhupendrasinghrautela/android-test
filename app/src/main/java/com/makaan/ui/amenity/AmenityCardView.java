package com.makaan.ui.amenity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.amenity.Amenity;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.listing.BaseCardView;

import butterknife.Bind;

/**
 * Created by sunil on 17/01/16.
 */
public class AmenityCardView extends BaseCardView<AmenityCluster> {

    @Bind(R.id.amenity_title)TextView mAmenityTitle;
    @Bind(R.id.amenity_logo)FadeInNetworkImageView mAmenityLogo;
    @Bind(R.id.amenity_list)LinearLayout mAmenityListLayout;
    private final static String URL = ApiConstants.HOSTED_IMAGE_URL;

    private Context mContext;

    public AmenityCardView(Context context) {
        super(context);
        mContext = context;
    }

    public AmenityCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AmenityCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(Context context, AmenityCluster item) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        StringBuilder finalImageUrl = new StringBuilder();
        finalImageUrl.append(URL.toString());
        finalImageUrl.append("/");
        finalImageUrl.append(item.amenityId);
        finalImageUrl.append(".png");
        if(item.name != null) {
            mAmenityTitle.setText(item.name.toLowerCase());
        }
        int width = getResources().getDimensionPixelSize(R.dimen.amenity_type_logo_dimen);
        mAmenityLogo.setImageUrl(finalImageUrl.toString(),
                MakaanNetworkClient.getInstance().getImageLoader());
            for (int i = 0; i < 3; i++) {
                final View amenityItem =
                        mLayoutInflater.inflate(R.layout.amenity_item_layout, null);
                if(i < item.cluster.size()) {
                    Amenity amenity = item.cluster.get(i);

                    if(amenity.name!=null) {
                        ((TextView) amenityItem.findViewById(R.id.amenity_name)).setText(amenity.name.toLowerCase());
                    }
                    ((TextView) amenityItem.findViewById(R.id.amenity_distance)).setText(amenity.displayDistance);
                }
                mAmenityListLayout.addView(amenityItem);
        }

    }
}
