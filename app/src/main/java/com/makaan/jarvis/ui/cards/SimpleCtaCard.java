package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.jarvis.message.ExposeMessage;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 20/02/16.
 */
public class SimpleCtaCard extends BaseCtaView<ExposeMessage> {

    private Context mContext;


    @Bind(R.id.txt_message)
    TextView mMessage;

    @OnClick(R.id.btn_no)
    public void OnCancelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }

    }

    @OnClick(R.id.btn_yes)
    public void OnApplyClick(){
        if(null!=mOnApplyClickListener){
            mOnApplyClickListener.onApplyClick();
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
    public void bindView(Context context, ExposeMessage item) {

    }


}