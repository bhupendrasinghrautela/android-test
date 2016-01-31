package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
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
    public void bindView(final Context context, final Message item) {
        textViewTitle.setText("Properties in");
        textViewSubTitle.setText(item.chatObj.localityName);
        textViewSubTitle2.setText(item.chatObj.cityName);

        item.chatObj.image = item.chatObj.imageURL;//TODO this temp due to api

        if(!TextUtils.isEmpty(item.chatObj.image)){
            imageView.setImageUrl(item.chatObj.image, MakaanNetworkClient.getInstance().getImageLoader());
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localityIntent = new Intent(context, LocalityActivity.class);
                localityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(item.chatObj.localityId));
                context.startActivity(localityIntent);
            }
        });
    }


}
