package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;

/**
 * Created by sunil on 16/01/16.
 */
public class SellerOverviewCard extends BaseView<Message> {
    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.properties)
    TextView properties;

    @Bind(R.id.locations)
    TextView locations;

    public SellerOverviewCard(Context context) {
        super(context);
    }

    public SellerOverviewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bindView(Context context, Message item) {

        if(!TextUtils.isEmpty(item.chatObj.name)) {
            name.setText(item.chatObj.name);
        }
        if(item.chatObj.listingCount>0) {
            properties.setText(item.chatObj.listingCount + (item.chatObj.listingCount > 1 ? " properties" : " property"));
        }
        if(!TextUtils.isEmpty(item.chatObj.localityName + item.chatObj.cityName)) {
            StringBuilder builder = new StringBuilder();
            if(item.chatObj.localitiesCount>0){
                builder.append("in " + item.chatObj.localitiesCount + " localities ");
            }

            if(!TextUtils.isEmpty(item.chatObj.cityCountText)){
                builder.append(item.chatObj.cityCountText);
            }

            if(!TextUtils.isEmpty(builder.toString())){
                locations.setText(builder.toString());
            }

        }

        //item.chatObj.image = item.chatObj.imageURL;//TODO this temp due to api

/*        if(!TextUtils.isEmpty(item.chatObj.image)){
            imageView.setImageUrl(item.chatObj.image, MakaanNetworkClient.getInstance().getImageLoader());
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localityIntent = new Intent(context, LocalityActivity.class);
                localityIntent.putExtra(LocalityActivity.LOCALITY_ID, Long.valueOf(item.chatObj.localityId));
                context.startActivity(localityIntent);
            }
        });*/

    }
}
