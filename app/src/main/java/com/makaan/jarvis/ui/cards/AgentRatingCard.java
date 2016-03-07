package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.jarvis.JarvisClient;
import com.makaan.jarvis.message.Message;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.view.BaseView;
import com.makaan.util.ImageUtils;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sunil on 31/01/16.
 */
public class AgentRatingCard extends BaseView<Message> {

    @Bind(R.id.ratingbar)
    RatingBar mRatingBar;

    @Bind(R.id.agent_image)
    CircleImageView mAgentImage;

    @Bind(R.id.btn_submit)
    Button mSubmitButton;

    private float mRating = 0;

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

                mRating = rating;
            }
        });

        if(!TextUtils.isEmpty(item.chatObj.image)) {
            int width = getResources().getDimensionPixelSize(R.dimen.jarvis_seller_card_profile_pic_dimen);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(item.chatObj.image, width, width, false),
                    new ImageLoader.ImageListener() {
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

    @OnClick(R.id.btn_submit)
    public void onSubmitClick(){
        JarvisClient.getInstance().rateAgent(mRating);
        mRatingBar.setIsIndicator(true);
        mSubmitButton.setVisibility(View.GONE);
    }
}
