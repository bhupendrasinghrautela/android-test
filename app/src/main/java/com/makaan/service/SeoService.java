package com.makaan.service;

import com.makaan.constants.ApiConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.StringRequestCallback;

/**
 * Created by sunil on 11/03/16.
 */
public class SeoService implements MakaanService {

    private static final String TAG = SeoService.class.getSimpleName();

    public void getPageAttributes(String path, StringRequestCallback callback){
        String requestUrl = buildSeoApiUrl(path);
        MakaanNetworkClient.getInstance().get(requestUrl, callback, TAG);
    }


    private String buildSeoApiUrl(String path){
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(ApiConstants.BASE_URL);
        requestBuilder.append("/dawnstar/data/v2/url?url=");
        requestBuilder.append(path);

        return requestBuilder.toString();
    }
}
