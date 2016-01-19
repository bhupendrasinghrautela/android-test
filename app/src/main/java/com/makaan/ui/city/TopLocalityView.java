package com.makaan.ui.city;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.response.locality.Locality;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.util.CommonUtil;

import java.util.List;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/01/16.
 */
public class TopLocalityView extends BaseLinearLayout<List<Locality>> {
    @Bind(R.id.locality_container)
    LinearLayout mLocalityContainer;
    public TopLocalityView(Context context) {
        super(context);
    }

    public TopLocalityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopLocalityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void bindView(List<Locality> item) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        for(Locality locality:item){
            View localityItem = inflater.inflate(R.layout.top_locality_item, null);
            TextView localityLabel = (TextView) localityItem.findViewById(R.id.city_locality_label);
            TextView localityAvgPrice = (TextView) localityItem.findViewById(R.id.city_avg_price);
            TextView localityAvgPriceRise = (TextView) localityItem.findViewById(R.id.city_avg_percent_rise);
            TextView localityScore = (TextView) localityItem.findViewById(R.id.locality_score_text);
            ProgressBar localityScoreProgress = (ProgressBar) localityItem.findViewById(R.id.locality_score_progress);
            localityLabel.setText(locality.label);
            localityAvgPrice.setText(mContext.getString(R.string.avg_price_prefix)+" "+String.valueOf(locality.avgPricePerUnitArea)+mContext.getString(R.string.avg_price_postfix));
            localityAvgPriceRise.setText(String.valueOf(locality.avgPriceRisePercentage) +"%");
            localityScore.setText(String.valueOf(locality.livabilityScore));
            localityScoreProgress.setProgress((int) (locality.livabilityScore * 10));
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
}
