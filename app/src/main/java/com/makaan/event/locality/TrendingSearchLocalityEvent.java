package com.makaan.event.locality;

import com.makaan.event.MakaanEvent;
import com.makaan.response.search.Search;

import java.util.ArrayList;

/**
 * Created by vaibhav on 19/01/16.
 */
public class TrendingSearchLocalityEvent extends MakaanEvent {

    public ArrayList<Search> trendingSearches;

    public TrendingSearchLocalityEvent(ArrayList<Search> trendingSearches) {
        this.trendingSearches = trendingSearches;
    }

    public TrendingSearchLocalityEvent() {
    }
}
