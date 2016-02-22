package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.jarvis.message.ExposeMessage;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 22/02/16.
 */
public class RichContentCard extends BaseCtaView<ExposeMessage> {

    @Bind(R.id.txt_heading)
    TextView mHeader;

    @Bind(R.id.txt_details)
    TextView mDetails;

    @Bind(R.id.image)
    TextView mImage;


    @OnClick(R.id.btn_no)
    public void OnCancelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }
    }
    public RichContentCard(Context context) {
        super(context);
    }

    public RichContentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, ExposeMessage item) {

    }
}
