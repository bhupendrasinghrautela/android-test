package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;

import com.makaan.jarvis.message.ExposeMessage;

/**
 * Created by sunil on 27/01/16.
 */
public class MultiPropertyCard extends BaseCtaView<ExposeMessage> {

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

    }
}
