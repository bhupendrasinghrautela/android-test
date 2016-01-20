package com.makaan.event.locality;

import com.makaan.event.MakaanEvent;
import com.makaan.response.search.SearchResponseItem;

import java.util.ArrayList;

/**
 * Created by vaibhav on 19/01/16.
 */
public class TrendingSearchLocalityEvent extends MakaanEvent {

    public ArrayList<SearchResponseItem> trendingSearches;

    public TrendingSearchLocalityEvent(ArrayList<SearchResponseItem> trendingSearches) {
        this.trendingSearches = trendingSearches;
    }

    public TrendingSearchLocalityEvent() {
    }
}
