package com.makaan.request.selector;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/11/16.
 */
public class FacetsSelector implements MakaanReqSelector {
    ArrayList<String> facets = new ArrayList<>();

    @Override
    public String build() {
        if(facets.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("&facets=");
            String separator = "";
            for(String facet : facets) {
                builder.append(separator);
                builder.append(facet);
                separator = ",";
            }
            return builder.toString();
        }
        return "";
    }

    public void add(String facet) {
        this.facets.add(facet);
    }

    public void reset() {
        this.facets.clear();
    }
}
