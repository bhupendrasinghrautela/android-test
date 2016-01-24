package com.makaan.activity.listing;

import com.makaan.request.selector.Selector;

/**
 * Created by rohitgarg on 1/20/16.
 */
public interface SerpRequestCallback {
    public void serpRequest(int type, Selector selector);
    public void serpRequest(int type, Long id);

    void requestApi(int request, String key);
}
