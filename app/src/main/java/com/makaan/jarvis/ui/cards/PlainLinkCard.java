package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.deeplink.DeepLinkingIntentParserActivity;
import com.makaan.jarvis.message.Message;
import com.makaan.ui.view.BaseView;

import butterknife.Bind;

/**
 * Created by sunil on 25/01/16.
 */
public class PlainLinkCard extends BaseView<Message> {

    private Context mContext;

    @Bind(R.id.plain_link)
    TextView mPlainLink;

    public PlainLinkCard(Context context) {
        super(context);
        mContext = context;
    }

    public PlainLinkCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PlainLinkCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(final Context context, final Message item) {
        if(null!=item || null!=item.message){
            mPlainLink.setText(Html.fromHtml(item.message).toString().toLowerCase());
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse(item.chatObj.link);
                    if (!item.chatObj.link.startsWith("http://") && !item.chatObj.link.startsWith("https://")) {
                        uri = Uri.parse("http://" + item.chatObj.link);
                    }
                    Intent deepLinkResolverIntent = new Intent(context, DeepLinkingIntentParserActivity.class);
                    deepLinkResolverIntent.setData(uri);
                    mContext.startActivity(deepLinkResolverIntent);
                }catch (Exception e){
                    Crashlytics.logException(e);
                }
            }
        });
    }

}
