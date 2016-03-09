package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;

/**
 * Created by sunil on 14/01/16.
 */
public class OutTextCard extends BaseView<Message> {
    @Bind(R.id.txt_message)
    TextView mTextMessage;

    public OutTextCard(Context context) {
        super(context);
    }

    public OutTextCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutTextCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {
        if(item.message != null) {
            mTextMessage.setText(item.message.toLowerCase());
        }
    }
}
