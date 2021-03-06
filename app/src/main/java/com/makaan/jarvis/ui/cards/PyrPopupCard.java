package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.jarvis.message.ExposeMessage;
import com.segment.analytics.Properties;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 19/02/16.
 */
public class PyrPopupCard extends BaseCtaView<ExposeMessage> {

    private Context mContext;

    @Bind(R.id.title)
    TextView mTitle;

    @Bind(R.id.message)
    TextView mMessage;

    @Bind(R.id.btn_yes)
    Button mApplyButton;

    @OnClick(R.id.btn_no)
    public void OnCancelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }

    }


    public PyrPopupCard(Context context) {
        super(context);
    }

    public PyrPopupCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PyrPopupCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(final Context context, final ExposeMessage item) {
        if(item.city != null) {
            mTitle.setText("interested in " + item.city.toLowerCase());
        }

        mApplyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {



                /*----track -------------event--------- */
                if(item.city!=null && !TextUtils.isEmpty(item.city)) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",MakaanTrackerConstants.Label.mAutoViewPyrYes,item.city));
                    MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.click);
                }
                /*--------------------------------------*/

                if(null!=mOnApplyClickListener){
                    mOnApplyClickListener.onApplyClick();
                }
            }
        });
    }


}
