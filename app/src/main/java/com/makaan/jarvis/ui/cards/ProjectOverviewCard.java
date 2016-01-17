package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;

import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

/**
 * Created by sunil on 12/01/16.
 */
public class ProjectOverviewCard extends BaseView<Message> {


    public ProjectOverviewCard(Context context) {
        super(context);
    }

    public ProjectOverviewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProjectOverviewCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {

    }
}
