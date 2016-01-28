package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.view.BaseView;
import com.makaan.util.StringUtil;

import butterknife.Bind;

/**
 * Created by sunil on 22/01/16.
 */
public class PropertyCard extends BaseView<Message> {

    @Bind(R.id.image)
    FadeInNetworkImageView imageView;

    @Bind(R.id.text_line1)
    TextView textViewTitle;

    @Bind(R.id.text_line2)
    TextView textViewSubTitle;

    @Bind(R.id.text_line3)
    TextView textViewSubTitle2;

    public PropertyCard(Context context) {
        super(context);
    }

    public PropertyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PropertyCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {
        textViewTitle.setText(StringUtil.getSpannedPrice(item.chatObj.minPrice));
        textViewSubTitle.setText(item.chatObj.builderName + " " + item.chatObj.projectName);
        textViewSubTitle2.setText(item.chatObj.localityName + " " + item.chatObj.cityName);
        imageView.setImageUrl(item.chatObj.image, MakaanNetworkClient.getInstance().getImageLoader());

    }
}
