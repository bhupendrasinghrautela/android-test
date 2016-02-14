package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpRequest;
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

    private Message message;

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

        message = item;
        if(MessageType.localityOverview==item.messageType){
            textViewTitle.setVisibility(View.GONE);
        }else if(MessageType.localityBuy==item.messageType) {
            textViewTitle.setText("property for buy near");
        }
        else if(MessageType.localityRent==item.messageType) {
            textViewTitle.setText("property for rent near");
        }

        textViewSubTitle.setText(item.chatObj.localityName);
        textViewSubTitle2.setText(item.chatObj.cityName);

        item.chatObj.image = item.chatObj.imageURL;//TODO this temp due to api

        if(!TextUtils.isEmpty(item.chatObj.image)){
            imageView.setImageUrl(item.chatObj.image, MakaanNetworkClient.getInstance().getImageLoader());
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message==null){
                    return;
                }
                if(MessageType.localityOverview==message.messageType) {
                    Intent localityIntent = new Intent(context, LocalityActivity.class);
                    localityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(item.chatObj.localityId));
                    context.startActivity(localityIntent);
                }else if(MessageType.localityBuy==message.messageType) {
                    SerpRequest serpRequest = new SerpRequest(SerpActivity.TYPE_LOCALITY);
                    serpRequest.setSerpContext(SerpActivity.SERP_CONTEXT_BUY);
                    serpRequest.setLocalityId(message.chatObj.localityId);
                    serpRequest.launchSerp(context);
                }
                else if(MessageType.localityRent==message.messageType) {
                    SerpRequest serpRequest = new SerpRequest(SerpActivity.TYPE_LOCALITY);
                    serpRequest.setSerpContext(SerpActivity.SERP_CONTEXT_RENT);
                    serpRequest.setLocalityId(message.chatObj.localityId);
                    serpRequest.launchSerp(context);
                }
            }
        });
    }


}
