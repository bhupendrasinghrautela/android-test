package com.makaan.activity.listing;

import android.os.Bundle;

import com.makaan.request.selector.Selector;

/**
 * Created by rohitgarg on 1/20/16.
 */
public interface SerpRequestCallback {
    void serpRequest(int type, Selector selector);
    void serpRequest(int type, Long id);
    void serpRequest(int type, Selector selector, String gpId);
    void serpRequest(int typeSuggestion, String filters);

    void requestApi(int request, String key);

    boolean needSellerInfoInSerp();

    Selector getGroupSelector();

    void requestDetailPage(int type, Bundle bundle);

    void loadMoreItems();

    String getOverviewText();
    Long getLastUsedClusterId();

    void requestOverviewPage();

    void trackScroll(int request, int position);
}
