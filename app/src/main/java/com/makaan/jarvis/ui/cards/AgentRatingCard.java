package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RatingBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.view.BaseView;
import com.pkmmte.view.CircularImageView;

import butterknife.Bind;

/**
 * Created by sunil on 31/01/16.
 */
public class AgentRatingCard extends BaseView<Message> {

    @Bind(R.id.rating_bar)
    RatingBar mRatingBar;

    @Bind(R.id.agent_image)
    CircularImageView mAgentImage;

    public AgentRatingCard(Context context) {
        super(context);
    }

    public AgentRatingCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgentRatingCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(Context context, Message item) {

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {


            }
        });

        if(!TextUtils.isEmpty(item.chatObj.image)) {
            MakaanNetworkClient.getInstance().getImageLoader().get(item.chatObj.image, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mAgentImage.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                }
            });
        }

    }
}
