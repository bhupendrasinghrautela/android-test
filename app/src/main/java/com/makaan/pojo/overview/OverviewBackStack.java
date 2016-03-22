package com.makaan.pojo.overview;

import java.util.Stack;

/**
 * Created by rohitgarg on 3/21/16.
 */
public class OverviewBackStack {
    Stack<SingleOverviewItem> mOverviewBackStack = new Stack<>();

    public void addToBackStack(OverviewItemType type, Long id) {
        if(isNull(id)) {
            return;
        }
        mOverviewBackStack.add(new SingleOverviewItem(type, id));
    }

    private boolean isNull(Number id) {
        if(id == null || id.equals(0)) {
            return true;
        }
        if(id instanceof Double) {
            if(Double.isNaN((Double)id)) {
                return true;
            }
        }
        return false;
    }

    public void addToBackStack(OverviewItemType type, Long id, Double latitude, Double longitude, String placeName) {
        if(isNull(id)) {
            return;
        }
        mOverviewBackStack.add(new SingleOverviewItem(type, id, latitude, longitude, placeName));
    }

    public SingleOverviewItem pop() {
        if(mOverviewBackStack.size() <= 0) {
            return null;
        }
        return mOverviewBackStack.pop();
    }

    public SingleOverviewItem peek() {
        if(mOverviewBackStack.size() <= 0) {
            return null;
        }
        return mOverviewBackStack.peek();
    }

    public class SingleOverviewItem {
        public OverviewItemType type;
        public Long id;
        public Double latitude, longitude;
        public String placeName;

        public SingleOverviewItem(OverviewItemType type, Long id) {
            this.type = type;
            this.id = id;
        }

        public SingleOverviewItem(OverviewItemType type, Long id, Double latitude, Double longitude, String placeName) {
            this.type = type;
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.placeName = placeName;
        }
    }
}
