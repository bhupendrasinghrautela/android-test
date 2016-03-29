package com.makaan.response.listing;

import com.makaan.request.selector.Selector;
import com.makaan.util.KeyUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by rohitgarg on 2/11/16.
 */
public class ListingFacets {
    ArrayList<Map<String,Integer>> localityId;
    ArrayList<Map<String,String>> localityLabel;
    ArrayList<Map<String,Integer>> cityId;
    ArrayList<Map<String,String>> cityLabel;
    ArrayList<Map<String,Integer>> suburbId;
    ArrayList<Map<String,String>> suburbLabel;
    ArrayList<Map<String,Integer>> builderId;
    ArrayList<Map<String,String>> builderLabel;

    public String buildDisplayName() {
        StringBuilder builder = new StringBuilder();
        String separator = "";
        if(localityId != null && localityLabel != null) {
            for(Map localityMap : localityLabel) {
                for(Object locality : localityMap.keySet()) {
                    builder.append(separator);
                    builder.append(String.valueOf(locality));
                    separator = ", ";
                }
            }
        }

        if(suburbId != null && suburbLabel != null) {
            for(Map suburbMap : suburbLabel) {
                for(Object suburb : suburbMap.keySet()) {
                    builder.append(separator);
                    builder.append(String.valueOf(suburb));
                    separator = ", ";
                }
            }
        }

        if(cityId != null && cityLabel != null) {
            for (Map cityMap : cityLabel) {
                for (Object city : cityMap.keySet()) {
                    builder.append(separator);
                    builder.append(String.valueOf(city));
                    separator = ", ";

                }
            }
        }
        // todo handle seller and builder
        return builder.toString();
    }

    public void applySelector(Selector selector) {
        if(localityId != null && localityLabel != null) {
            for(Map localityMap : localityId) {
                for(Object locality : localityMap.keySet()) {
                    selector.term(KeyUtil.LOCALITY_ID, String.valueOf(locality));
                }
            }
        }

        if(suburbId != null && suburbLabel != null) {
            for(Map suburbMap : suburbId) {
                for(Object suburb : suburbMap.keySet()) {
                    selector.term(KeyUtil.SUBURB_ID, String.valueOf(suburb));
                }
            }
        }

        if(cityId != null && cityLabel != null) {
            for (Map cityMap : cityId) {
                for (Object city : cityMap.keySet()) {
                    selector.term(KeyUtil.CITY_ID, String.valueOf(city));
                }
            }
        }

        if(builderId != null && builderLabel != null) {
            for (Map builderMap : builderId) {
                for (Object builder : builderMap.keySet()) {
                    selector.term(KeyUtil.BUILDER_ID, String.valueOf(builder));
                }
            }
        }
        // TODO handle seller
    }

    public String getSearchName() {
        // if locality/suburb is selected
        if((localityLabel != null && localityLabel.size() > 0) || (suburbLabel != null && suburbLabel.size() > 0)) {
            int localityCount = localityLabel != null ? localityLabel.size() : 0;
            int suburbCount = suburbLabel != null ? suburbLabel.size() : 0;
            if(localityCount + suburbCount > 1) {
                for(Map localityMap : localityLabel) {
                    for(Object locality : localityMap.keySet()) {
                        return String.valueOf(locality) + " + " + (localityCount + suburbCount - 1);
                    }
                }

                for(Map suburbMap : suburbLabel) {
                    for(Object suburb : suburbMap.keySet()) {
                        return String.valueOf(suburb) + " + " + (localityCount + suburbCount - 1);
                    }
                }
            } else if(localityCount + suburbCount == 1){
                if(localityCount > 0) {
                    for(Map localityMap : localityLabel) {
                        for(Object locality : localityMap.keySet()) {
                            return String.valueOf(locality);
                        }
                    }
                } else {
                    for(Map suburbMap : suburbLabel) {
                        for(Object suburb : suburbMap.keySet()) {
                            return String.valueOf(suburb);
                        }
                    }
                }
            }
        }

        if(cityLabel != null && cityLabel.size() > 0) {
            Set<String> set = cityLabel.get(0).keySet();
            for(String city : set) {
                return city;
            }
        }
        return null;
    }
}
