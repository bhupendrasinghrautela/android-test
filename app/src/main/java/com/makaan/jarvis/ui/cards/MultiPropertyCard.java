package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;

import com.makaan.R;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.ui.pager.PropertyPager;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 27/01/16.
 */
public class MultiPropertyCard extends BaseCtaView<ExposeMessage> {

    @Bind(R.id.property_pager)
    PropertyPager mPropertyPager;

    public MultiPropertyCard(Context context) {
        super(context);
    }

    public MultiPropertyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiPropertyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, ExposeMessage item) {
        mPropertyPager.bindView(context, item.properties.content);
    }

    @OnClick(R.id.btn_cancel)
    public void onCacelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }
    }
}
