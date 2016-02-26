package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.PageVisitType;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 20/02/16.
 */
public class SimpleCtaCard extends BaseCtaView<ExposeMessage> {

    private Context mContext;


    @Bind(R.id.txt_message)
    TextView mMessage;

    @Bind(R.id.btn_yes)
    Button mApplyButton;

    @OnClick(R.id.btn_no)
    public void OnCancelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }

    }

    public SimpleCtaCard(Context context) {
        super(context);
    }

    public SimpleCtaCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleCtaCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(final Context context, ExposeMessage item) {
        if(TextUtils.isEmpty(item.properties.message_type)){
            setVisibility(View.GONE);
        }

        Map<String, BuyerJourneyMessage> jarvisBuyerJourneyMessageMap =
                MasterDataCache.getInstance().getJarvisBuyerJourneyMessageMap();

        if(null==jarvisBuyerJourneyMessageMap ||jarvisBuyerJourneyMessageMap.isEmpty() ||
                !jarvisBuyerJourneyMessageMap.containsKey(item.properties.message_type)){
            setVisibility(View.GONE);
        }

        final BuyerJourneyMessage buyerJourneyMessage = jarvisBuyerJourneyMessageMap.get(item.properties.message_type);
        mMessage.setText(buyerJourneyMessage.message);

        mApplyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BuyerDashboardActivity.class);
                intent.putExtra(BuyerDashboardActivity.KEY_PHASE_ID, buyerJourneyMessage.phaseId);
                context.startActivity(intent);

                if(null!=mOnApplyClickListener){
                    mOnApplyClickListener.onApplyClick();
                }

            }
        });

    }


}