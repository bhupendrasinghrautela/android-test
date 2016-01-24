package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;

/**
 * Created by sunil on 22/01/16.
 */
public class LocalityCard extends BaseView<Message> {

    @Bind(R.id.image)
    FadeInNetworkImageView imageView;

    @Bind(R.id.text_line1)
    TextView textViewTitle;

    @Bind(R.id.text_line2)
    TextView textViewSubTitle;

    @Bind(R.id.text_line3)
    TextView textViewSubTitle2;

    public LocalityCard(Context context) {
        super(context);
    }

    public LocalityCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalityCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {
        textViewTitle.setText("Properties in");
        textViewSubTitle.setText(item.chatObj.localityName);
        textViewSubTitle2.setText(item.chatObj.cityName);

    }
}
