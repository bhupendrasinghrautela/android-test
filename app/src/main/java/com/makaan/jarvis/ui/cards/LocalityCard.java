package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.ui.FadeInNetworkImageView;
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

        if(item==null || item.chatObj==null){
            return;
        }

        message = item;
/*        if(MessageType.localityOverview==item.messageType){
            textViewTitle.setVisibility(View.GONE);
        }else if(MessageType.localityBuy==item.messageType ||
                MessageType.suburbBuy==item.messageType) {
            textViewTitle.setText("property for buy near");
        }
        else if(MessageType.localityRent==item.messageType ||
                MessageType.suburbRent==item.messageType) {
            textViewTitle.setText("property for rent near");
        }*/

        if(!TextUtils.isEmpty(item.chatObj.label)) {
            textViewTitle.setText(item.chatObj.label);
        }else {
            textViewTitle.setVisibility(View.GONE);
        }

        textViewSubTitle.setVisibility(View.GONE);
        textViewSubTitle2.setVisibility(View.GONE);

        /*if(item.chatObj.localityName != null) {
            textViewSubTitle.setText(item.chatObj.localityName.toLowerCase());
        }
        if(item.chatObj.cityName != null) {
            textViewSubTitle2.setText(item.chatObj.cityName.toLowerCase());
        }*/

        if(!TextUtils.isEmpty(item.chatObj.image)) {
            imageView.setImageUrl(item.chatObj.image + "?WIDTH=220&HEIGHT=120", MakaanNetworkClient.getInstance().getImageLoader());
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message==null){
                    return;
                }
                if(MessageType.localityOverview==message.messageType) {
                    Intent localityIntent = new Intent(context, OverviewActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putLong(OverviewActivity.ID, Long.valueOf(item.chatObj.localityId));
                    bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

                    localityIntent.putExtras(bundle);
                    context.startActivity(localityIntent);
                }else if(MessageType.localityBuy==message.messageType ||
                        MessageType.cityBuy==message.messageType ||
                        MessageType.suburbBuy==message.messageType ||
                        MessageType.suburbResidentialBuy==message.messageType) {

                    SerpRequest serpRequest = null;
                    if(MessageType.localityBuy==message.messageType) {
                        serpRequest = new SerpRequest(SerpActivity.TYPE_LOCALITY);
                        serpRequest.setLocalityId(message.chatObj.localityId);

                    }else if(MessageType.cityBuy==message.messageType){
                        serpRequest = new SerpRequest(SerpActivity.TYPE_CITY);
                        serpRequest.setCityId(message.chatObj.cityId);

                    }else{
                        serpRequest = new SerpRequest(SerpActivity.TYPE_SUBURB);
                        serpRequest.setSuburbId(message.chatObj.suburbId);
                    }

                    if(null!=serpRequest) {
                        serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);
                        serpRequest.launchSerp(context);
                    }
                }
                else if(MessageType.localityRent==message.messageType ||
                        MessageType.cityRent==message.messageType ||
                        MessageType.suburbRent==message.messageType) {

                    SerpRequest serpRequest = null;
                    if(MessageType.localityRent==message.messageType) {
                        serpRequest = new SerpRequest(SerpActivity.TYPE_LOCALITY);
                        serpRequest.setLocalityId(message.chatObj.localityId);

                    }else if(MessageType.cityRent==message.messageType){
                        serpRequest = new SerpRequest(SerpActivity.TYPE_CITY);
                        serpRequest.setCityId(message.chatObj.cityId);

                    }else{
                        serpRequest = new SerpRequest(SerpActivity.TYPE_SUBURB);
                        serpRequest.setSuburbId(message.chatObj.suburbId);
                    }

                    if(null!=serpRequest) {
                        serpRequest.setSerpContext(SerpRequest.CONTEXT_RENT);
                        serpRequest.launchSerp(context);
                    }
                }
            }
        });
    }


}
