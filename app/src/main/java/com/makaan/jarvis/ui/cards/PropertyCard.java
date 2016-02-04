package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.view.BaseView;
import com.makaan.util.KeyUtil;
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
    public void bindView(final Context context, final Message item) {
        textViewTitle.setText(StringUtil.getSpannedPrice(item.chatObj.price));
        textViewSubTitle.setText(item.chatObj.locality);
        textViewSubTitle2.setText(item.chatObj.area);
        imageView.setImageUrl(item.chatObj.imageURL, MakaanNetworkClient.getInstance().getImageLoader());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong(KeyUtil.LISTING_ID, item.chatObj.propertyId);
                Intent intent = new Intent(context, PropertyActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });

    }


}
