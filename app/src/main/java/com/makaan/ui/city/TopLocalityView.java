package com.makaan.ui.city;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.locality.Locality;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.util.CommonUtil;
import com.makaan.util.LocalityUtil;
import com.makaan.util.StringUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/01/16.
 */
public class TopLocalityView extends BaseLinearLayout<List<Locality>> {
    @Bind(R.id.locality_container)
    LinearLayout mLocalityContainer;

    @Bind((R.id.show_properties_text))
    TextView mShowAllPropertiesButton;

    private Context mContext;

    public TopLocalityView(Context context) {
        super(context);
        mContext = context;
    }

    public TopLocalityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public TopLocalityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void bindView(List<Locality> item) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        for(final Locality locality:item){
            View localityItem = inflater.inflate(R.layout.top_locality_item, null);
            TextView localityLabel = (TextView) localityItem.findViewById(R.id.city_locality_label);
            TextView localityAvgPrice = (TextView) localityItem.findViewById(R.id.city_avg_price);
            TextView localityAvgPriceRise = (TextView) localityItem.findViewById(R.id.city_avg_percent_rise);
            TextView localityScore = (TextView) localityItem.findViewById(R.id.locality_score_text);
            ProgressBar localityScoreProgress = (ProgressBar) localityItem.findViewById(R.id.locality_score_progress);
            if(!TextUtils.isEmpty(locality.label)) {
                localityLabel.setText(locality.label.toLowerCase());
                localityLabel.setVisibility(VISIBLE);
            } else {
                localityLabel.setVisibility(INVISIBLE);
            }
            //TODO calculate avg price from aggregation
            Integer avg = LocalityUtil.calculateAveragePrice(locality.listingAggregations).intValue();
            if(avg != null && avg>0) {
                localityAvgPrice.setText(mContext.getString(R.string.avg_price_prefix) + " " + StringUtil.getFormattedNumber(avg) +
                        mContext.getString(R.string.avg_price_postfix));
            }else{
                localityAvgPrice.setVisibility(GONE);
            }
            if(null!=locality.avgPriceRisePercentage && locality.avgPriceRisePercentage>0) {
                localityAvgPriceRise.setText(String.valueOf(locality.avgPriceRisePercentage) + "%");
            }else{
                localityAvgPriceRise.setVisibility(View.GONE);
            }
            if(locality.livabilityScore != null && locality.livabilityScore > 10) {
                localityScore.setText(String.valueOf(10));
            } else {
                localityScore.setText(String.valueOf(locality.livabilityScore));
            }
            localityScoreProgress.setProgress((int) (locality.livabilityScore * 10));
            localityItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchLocalityScreen(locality.localityId);
                }
            });

            mLocalityContainer.addView(localityItem);
            if(locality != item.get(item.size()-1)){
                View view = new View(mContext);
                view.setBackgroundColor(mContext.getResources().getColor(R.color.city_empty_view_color));
                view.setMinimumHeight(CommonUtil.dpToPixel(mContext, 1f));
                int internalMargin = (int) mContext.getResources().getDimension(R.dimen.city_internal_margin);
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                lp.leftMargin = internalMargin;
                lp.rightMargin = internalMargin;

                mLocalityContainer.addView(view, lp);
            }
        }
    }

    public void setShowAllPropertiesClickListener(final long cityId, final boolean isRent){
        mShowAllPropertiesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                SerpRequest request = new SerpRequest(SerpActivity.TYPE_CITY);
                request.setCityId(cityId);
                request.setSerpContext(isRent ? SerpRequest.CONTEXT_RENT : SerpRequest.CONTEXT_BUY);
                request.launchSerp(mContext);
            }
        });

    }

    private void launchLocalityScreen(final long localityId){
        Intent intent = new Intent(mContext, LocalityActivity.class);
        intent.putExtra(LocalityActivity.LOCALITY_ID, localityId);
        mContext.startActivity(intent);
    }
}
