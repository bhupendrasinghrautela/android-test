package com.makaan.network;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.apache.http.HttpStatus;

/**
 * Created by rohitgarg on 3/8/16.
 */
public abstract class CustomImageLoaderListener implements ImageLoader.ImageListener {

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if(volleyError != null && volleyError.networkResponse != null) {
            if((volleyError.networkResponse.statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (volleyError.networkResponse.statusCode == HttpStatus.SC_MOVED_TEMPORARILY)) {
                if(volleyError.networkResponse.headers != null) {
                    String url = volleyError.networkResponse.headers.get("Location");
                    if(url != null) {
                        MakaanNetworkClient.getInstance().getImageLoader().get(url, this);
                    }
                }
            }
        }
    }
}
