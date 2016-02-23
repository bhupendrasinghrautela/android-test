package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

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
    public void bindView(Context context, ExposeMessage item) {
        mTitle.setText("interested in " + item.city);
    }


}