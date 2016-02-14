package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
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

    private Message message;

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
        message = item;

        if(MessageType.propertyOverview==item.messageType) {
            textViewTitle.setText(StringUtil.getSpannedPrice(item.chatObj.price));
        }else if(MessageType.projectOverview==item.messageType) {

            SpannableString price = StringUtil.getSpannedPrice(item.chatObj.price);
            if(!Double.isNaN(item.chatObj.price) && item.chatObj.price>0){
                TextUtils.concat(price, " onwards");
                textViewTitle.setText(TextUtils.concat(price, " onwards"));
            }else {
                textViewTitle.setText(price);
            }
        }
        textViewSubTitle.setText(item.chatObj.locality);
        textViewSubTitle2.setText(item.chatObj.area);
        imageView.setImageUrl(item.chatObj.imageUrl, MakaanNetworkClient.getInstance().getImageLoader());

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(MessageType.propertyOverview==message.messageType) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(KeyUtil.LISTING_ID, message.chatObj.propertyId);
                    Intent intent = new Intent(context, PropertyActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }else if(MessageType.projectOverview==message.messageType) {
                    Intent intent = new Intent(context,ProjectActivity.class);
                    intent.putExtra(ProjectActivity.PROJECT_ID, Long.valueOf(message.chatObj.projectId));
                    context.startActivity(intent);
                }

            }
        });

    }


}
