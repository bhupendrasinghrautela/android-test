package com.makaan.ui.locality;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.ui.listing.BaseCardView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 17/1/16.
 */
public class TopAgentsView extends BaseCardView {
    @Bind(R.id.agent_name)TextView mAgentName;
    @Bind(R.id.agent_info)TextView mAgentInfo;
    @Bind(R.id.agent_sale_count)TextView mAgentSaleCount;
    @Bind(R.id.agent_rent_count)TextView mAgentRentCount;
    @Bind(R.id.view_seller_details)TextView mViewSellerDetails;
    @Bind(R.id.agent_image)FadeInNetworkImageView mAgentImage;

    public TopAgentsView(Context context) {
        super(context);
    }

    public TopAgentsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopAgentsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    @OnClick(R.id.view_seller_details)
    public void viewSellerDeatils(){

    }

}
