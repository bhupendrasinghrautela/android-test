package com.makaan.activity.deeplink;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.HomeActivity;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.response.seo.SeoUrlResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SeoService;
import com.makaan.util.JsonParser;


/**
 * Created by sunil on 10/03/16.
 */
public class DeepLinkingIntentParserActivity extends AppCompatActivity {

    private static final String TAG = DeepLinkingIntentParserActivity.class.getSimpleName();
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deeplinking_intent_parsing_activity_layout);
        try {
            mUri = getIntent().getData();
            resolveIntent(mUri);
        } catch (Exception e) {
            Crashlytics.logException(e);
            finish();
        }

    }
    
    private void resolveIntent(Uri data){
        if (data == null  || TextUtils.isEmpty(data.getHost())) {
            throw new RuntimeException();
        }

        if(data.getHost().contains("makaan")){
            if(TextUtils.isEmpty(data.getPath())){
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }else {
                makeSeoUrlApiRequest(data);
            }
        }
    }

    private void makeSeoUrlApiRequest(Uri data){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(data.getPath().substring("/".length()));
        if(!TextUtils.isEmpty(data.getQuery())){
            queryBuilder.append("?");
            queryBuilder.append(data.getQuery());
        }
        SeoService seoService = (SeoService) MakaanServiceFactory.getInstance().getService(SeoService.class);
        seoService.getPageAttributes(queryBuilder.toString(), new SeoUrlRequestCallback());
    }


    /**
     * Listener for seo api request
     * */
    private class SeoUrlRequestCallback extends StringRequestCallback {

        @Override
        public void onSuccess(String response) {
            if(DeepLinkingIntentParserActivity.this == null || isFinishing()) {
                return;
            }

            SeoUrlResponse seoUrlResponse = (SeoUrlResponse) JsonParser.parseJson(response, SeoUrlResponse.class);

            if(!DeepLinkHelper.resolveDeepLink(DeepLinkingIntentParserActivity.this, seoUrlResponse)){
                try {
                    Uri uri = Uri.parse(mUri.toString());
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    DeepLinkingIntentParserActivity.this.startActivity(browserIntent);
                }catch (ActivityNotFoundException e){}
            }

            finish();
        }

        @Override
        public void onError(ResponseError error) {
            finish();
        }
    }

}
