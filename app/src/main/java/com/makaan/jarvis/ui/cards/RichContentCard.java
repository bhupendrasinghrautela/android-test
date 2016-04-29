package com.makaan.jarvis.ui.cards;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.util.CommonUtil;
import com.makaan.util.JsonBuilder;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 22/02/16.
 */
public class RichContentCard extends BaseCtaView<ExposeMessage> {


    @Bind(R.id.txt_details)
    TextView mDetails;

    @Bind(R.id.image)
    FadeInNetworkImageView mImage;
    private Context mContext;

    public RichContentCard(Context context) {
        super(context);
    }

    public RichContentCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichContentCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(final Context context, final ExposeMessage item) {
        try {
            mContext=context;
            JSONArray jsonArray = JsonBuilder.toJsonArray(item.properties.content);

            List<Content> contents = MakaanBuyerApplication.gson.fromJson(jsonArray.toString(), new TypeToken<List<Content>>(){}.getType());

            if(contents==null || contents.isEmpty()){
                setVisibility(View.GONE);
                return;
            }

            final Content content = contents.get(0);

            if(content.postTitle != null) {
                mDetails.setText(content.postTitle.toLowerCase());
            }
            mImage.setImageUrl(content.primaryImageUrl, MakaanNetworkClient.getInstance().getImageLoader());

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(content.guid!=null && !TextUtils.isEmpty(content.guid)) {
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoClick, content.guid));
                        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.click);
                    }
                    try {
                        String link = content.guid;
                        Uri uri = Uri.parse(link);
                        if (!link.startsWith("http://") && !link.startsWith("https://")) {
                            uri = Uri.parse("http://" + link);
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(browserIntent);
                    }catch (ActivityNotFoundException e){}
                }
            });
        } catch (JSONException e) {
            CommonUtil.TLog("exception", e);
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCacelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }
    }

    private static class Content {
        public String guid;
        public String postTitle;
        public String primaryImageUrl;

    }
}


