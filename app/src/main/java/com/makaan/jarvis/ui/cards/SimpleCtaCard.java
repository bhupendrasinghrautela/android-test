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
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.request.buyerjourney.PhaseChange;
import com.makaan.service.ClientEventsService;
import com.makaan.service.MakaanServiceFactory;
import com.segment.analytics.Properties;

import java.util.Date;
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
    public void bindView(final Context context, final ExposeMessage item) {
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
        if(buyerJourneyMessage.message != null) {
            mMessage.setText(buyerJourneyMessage.message.toLowerCase());
        }

        mApplyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                /*----track---------events------------*/
                if(buyerJourneyMessage.message != null && !TextUtils.isEmpty(buyerJourneyMessage.message)) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoClick, buyerJourneyMessage.message));
                    MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.click);
                }
                /*------------------------------------*/

                if(buyerJourneyMessage.phaseId < LeadPhaseConstants.LEAD_PHASE_BOOKING) {
                    Intent intent = new Intent(context, BuyerDashboardActivity.class);
                    intent.putExtra(BuyerDashboardActivity.KEY_PHASE_ID, buyerJourneyMessage.phaseId);
                    context.startActivity(intent);

                    if (null != mOnApplyClickListener) {
                        mOnApplyClickListener.onApplyClick();
                    }
                } else {
                    if(item.properties != null && item.properties.agentId != null && item.properties.leadId != null) {
                        PhaseChange change = new PhaseChange();
                        change.agentId = item.properties.agentId;
                        change.performTime = new Date().getTime();
                        if(buyerJourneyMessage.phaseId == LeadPhaseConstants.LEAD_PHASE_BOOKING) {
                            change.eventTypeId = LeadPhaseConstants.LEAD_EVENT_POSSESSION;
                        } else if(buyerJourneyMessage.phaseId == LeadPhaseConstants.LEAD_PHASE_POSSESSION) {
                            change.eventTypeId = LeadPhaseConstants.LEAD_EVENT_REGISTRATION;
                        }
                        ((ClientEventsService) (MakaanServiceFactory.getInstance().getService(ClientEventsService.class))).changePhase(item.properties.leadId, change);
                    }
                    if (null != mOnApplyClickListener) {
                        mOnApplyClickListener.onApplyClick();
                    }
                }
            }
        });

    }


}