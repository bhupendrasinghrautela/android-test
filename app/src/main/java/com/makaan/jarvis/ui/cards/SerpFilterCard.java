package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;

import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 19/01/16.
 */
public class SerpFilterCard extends BaseView<Message> {

    public SerpFilterCard(Context context) {
        super(context);
    }

    public SerpFilterCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SerpFilterCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {

    }
}
