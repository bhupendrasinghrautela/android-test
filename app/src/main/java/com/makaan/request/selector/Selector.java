package com.makaan.request.selector;

import com.crashlytics.android.Crashlytics;
import com.makaan.MakaanBuyerApplication;
import com.makaan.util.CommonUtil;
import com.makaan.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import static com.makaan.constants.RequestConstants.AND;
import static com.makaan.constants.RequestConstants.FIELDS;
import static com.makaan.constants.RequestConstants.FILTERS;
import static com.makaan.constants.RequestConstants.LATITUDE;
import static com.makaan.constants.RequestConstants.LONGITUDE;
import static com.makaan.constants.RequestConstants.PAGING;
import static com.makaan.constants.RequestConstants.SELECTOR;
import static com.makaan.constants.RequestConstants.SORT;

/**
 * Created by vaibhav on 08/01/16.
 */
public class Selector implements Cloneable {

    public static final String TAG = Selector.class.getSimpleName();
    private static final double RADIUS_EARTH = 6378137;


    private HashSet<String> fieldSelector = new HashSet<>();
    private LinkedHashMap<String, TermSelector> termSelectorHashMap = new LinkedHashMap<>();
    private LinkedHashMap<String, RangeSelector> rangeSelectorHashMap = new LinkedHashMap<>();
    private PagingSelector pagingSelector = new PagingSelector();
    private SortSelector sortSelector = new SortSelector();
    private GeoSelector geoSelector = new GeoSelector();
    private GroupBySelector groupBySelector = new GroupBySelector();
    private FacetsSelector facetsSelector = new FacetsSelector();

    public Selector() {
    }

    public Selector(Selector selector) {
        this.termSelectorHashMap.putAll(selector.termSelectorHashMap);
        this.rangeSelectorHashMap.putAll(selector.rangeSelectorHashMap);
        this.geoSelector.lat = selector.geoSelector.lat;
        this.geoSelector.lon = selector.geoSelector.lon;
        this.geoSelector.distance = selector.geoSelector.distance;
    }

    public Selector fields(String[] fields) {
        Collections.addAll(fieldSelector, fields);
        return this;
    }

    public Selector field(String fieldName) {
        fieldSelector.add(fieldName);
        return this;
    }

    public Selector facets(String[] facets) {
        for (String facet : facets) {
            facetsSelector.add(facet);
        }
        return this;
    }

    public Selector facet(String facet) {
        facetsSelector.add(facet);
        return this;
    }

    public Selector term(String fieldName, String value, boolean clearValues) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);
        if (null == termSelector) {
            termSelector = new TermSelector(fieldName, value);
            termSelectorHashMap.put(fieldName, termSelector);

        } else {
            if (clearValues) {
                termSelector.values.clear();
            }
            termSelector.values.add(value);
        }

        return this;
    }

    public Selector groupBy(String field, String min) {
        this.groupBySelector.field = field;
        this.groupBySelector.min = min;
        return this;
    }

    public Selector term(String fieldName, String[] values) {
        return term(fieldName, Arrays.asList(values));
    }

    public Selector term(String fieldName, Iterable<String> values) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);

        if (null == termSelector) {
            termSelector = new TermSelector();
            termSelectorHashMap.put(fieldName, termSelector);
            termSelector.name = fieldName;
        }
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            termSelector.values.add(iterator.next());
        }

        return this;
    }

    public Selector term(String fieldName, String value) {
        TermSelector termSelector = termSelectorHashMap.get(fieldName);
        if (null == termSelector) {
            termSelector = new TermSelector(fieldName, value);
            termSelectorHashMap.put(fieldName, termSelector);

        } else {
            termSelector.values.add(value);
        }

        return this;
    }


    public Selector range(String fieldName, Double from, Double to) {
        RangeSelector rangeSelector = rangeSelectorHashMap.get(fieldName);
        if (null == rangeSelector) {
            rangeSelector = new RangeSelector<Double>(fieldName, from, to);
            rangeSelectorHashMap.put(fieldName, rangeSelector);
        } else {
            rangeSelector.from = from;
            rangeSelector.to = to;
        }

        return this;
    }


    public Selector range(String fieldName, Long from, Long to) {
        RangeSelector rangeSelector = rangeSelectorHashMap.get(fieldName);
        if (null == rangeSelector) {
            rangeSelector = new RangeSelector<Long>(fieldName, from, to);
            rangeSelectorHashMap.put(fieldName, rangeSelector);
        } else {
            rangeSelector.from = from;
            rangeSelector.to = to;
        }

        return this;
    }

    public Selector page(int start, int rows) {
        pagingSelector.start = start;
        pagingSelector.rows = rows;
        return this;
    }

    public Selector sort(String sortField, String sortOrder) {
        sortSelector.fieldName = sortField;
        sortSelector.sortOrder = sortOrder;
        return this;
    }

    public Selector nearby(double distance, double lat, double lon, boolean isViewPort) {
        if(!isViewPort) {
            geoSelector.distance = distance;
            geoSelector.lat = lat;
            geoSelector.lon = lon;
        } else {

            // convert distance from KM to Meters
            double dispNorth = distance * 1000;
            double dispEast = distance * 1000;

            double dispLatRad = dispNorth / RADIUS_EARTH;
            double dispLonRad = dispEast / (RADIUS_EARTH * Math.cos(Math.PI * lat / 180));

            // bottom left point lat and lon
            double latTopLeft = lat - (dispLatRad * 180 / Math.PI);
            double lonTopLeft = lon - (dispLonRad * 180 / Math.PI);

            // top right point lat and lon
            double latBottomRight = lat + (dispLatRad * 180 / Math.PI);
            double lonBottomRight = lon + (dispLonRad * 180 / Math.PI);

            range(LATITUDE, latTopLeft, latBottomRight);
            range(LONGITUDE, lonTopLeft, lonBottomRight);
        }

        return this;
    }

    public void reset() {
        fieldSelector = new HashSet<>();
        termSelectorHashMap = new LinkedHashMap<>();
        rangeSelectorHashMap = new LinkedHashMap<>();
        pagingSelector = new PagingSelector();
        sortSelector = new SortSelector();
    }

    public void applySelection(String selector) {
        //TODO: build
        /**
         * http://marketplace-qa.proptiger-ws.com/columbus/app/v1/popular/suggestions?entityId=50175&sourceDomain=Makaan
         */
    }


    public String build() {
        try {
            boolean firstElementAdded = false;

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            if (fieldSelector.size() > 0) {
                firstElementAdded = true;
                StringBuilder fieldBuilder = new StringBuilder();
                fieldBuilder.append("\"").append(FIELDS).append("\"").append(":").append(MakaanBuyerApplication.gson.toJson(fieldSelector));
                jsonBuilder.append(fieldBuilder.toString());
            }

            if (termSelectorHashMap.size() > 0 || rangeSelectorHashMap.size() > 0 || null != geoSelector) {
                boolean firstFilterAdded = false;

                StringBuilder andStrBuilder = new StringBuilder();
                andStrBuilder.append("\"").append(AND).append("\"").append(":[");

                int i = 0;
                for (LinkedHashMap.Entry<String, TermSelector> entry : termSelectorHashMap.entrySet()) {

                    String termSelectorJson = entry.getValue().build();
                    if (!StringUtil.isBlank(termSelectorJson)) {

                        if (i != 0) {
                            andStrBuilder.append(",").append(termSelectorJson);
                        } else {
                            andStrBuilder.append(termSelectorJson);
                        }
                        i++;
                    }
                    firstFilterAdded = true;
                }


                int j = 0;
                for (LinkedHashMap.Entry<String, RangeSelector> entry : rangeSelectorHashMap.entrySet()) {

                    String rangeSelectorJson = entry.getValue().build();
                    if (!StringUtil.isBlank(rangeSelectorJson)) {
                        if (j != 0 || firstFilterAdded) {          // i > 0 means at least one term selector added
                            andStrBuilder.append(",").append(rangeSelectorJson);
                        } else {
                            andStrBuilder.append(rangeSelectorJson);
                        }
                        j++;
                    }
                    firstFilterAdded = true;
                }


                if ((null != geoSelector.lat && geoSelector.lat > 0) && (null != geoSelector.lon && geoSelector.lon > 0)) {

                    if (firstFilterAdded) {
                        andStrBuilder.append(",").append(geoSelector.build());
                    } else {
                        andStrBuilder.append(geoSelector.build());
                    }

                }

                andStrBuilder.append("]");
                if (firstElementAdded) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\"").append(FILTERS).append("\"").append(":{").append(andStrBuilder.toString()).append("}");
                firstElementAdded = true;
            }

            String pagingSelectorJson = pagingSelector.build();
            if (!StringUtil.isBlank(pagingSelectorJson)) {
                // TODO check for field values
                if (termSelectorHashMap.size() > 0 || rangeSelectorHashMap.size() > 0 || firstElementAdded) {
                    jsonBuilder.append(",\"");
                }
                jsonBuilder.append(PAGING).append("\"").append(":").append(pagingSelectorJson);
                firstElementAdded = true;
            }

            String sortSelectorJson = sortSelector.build();
            if (!StringUtil.isBlank(sortSelectorJson)) {
                // TODO check for field values
                if (termSelectorHashMap.size() > 0 || rangeSelectorHashMap.size() > 0 || !StringUtil.isBlank(pagingSelectorJson) || firstElementAdded) {
                    jsonBuilder.append(",\"");
                }
                jsonBuilder.append(SORT).append("\"").append(":").append(sortSelectorJson);
                firstElementAdded = true;
            }

            String groupBySelectorJson = groupBySelector.build();
            if (!StringUtil.isBlank(groupBySelectorJson)) {

                if (firstElementAdded) {
                    jsonBuilder.append(",\"");
                }
                jsonBuilder.append("groupBy").append("\"").append(":").append(groupBySelectorJson);
                firstElementAdded = true;
            }

            jsonBuilder.append("}");

            jsonBuilder.append(facetsSelector.build());

            return SELECTOR.concat("=").concat(jsonBuilder.toString());

        } catch (Exception e) {
            Crashlytics.logException(e);
            CommonUtil.TLog(TAG, "Error while building Selector", e);
        }


        return null;
    }

    public Selector removeGeo() {
        this.geoSelector.distance = null;
        this.geoSelector.lat = null;
        this.geoSelector.lon = null;
        if(this.sortSelector.fieldName != null && this.sortSelector.fieldName.equalsIgnoreCase("geoDistance")) {
            this.sortSelector = new SortSelector();
        }
        return this;
    }

    public Selector removeFacets() {
        this.facetsSelector.reset();
        return this;
    }

    public Selector removeTerm(String fieldName) {
        if (this.termSelectorHashMap.containsKey(fieldName)) {
            this.termSelectorHashMap.remove(fieldName);
        }
        return this;
    }

    public Selector removeRange(String fieldName) {
        if (this.rangeSelectorHashMap.containsKey(fieldName)) {
            this.rangeSelectorHashMap.remove(fieldName);
        }
        return this;
    }

    public Selector removePaging() {
        pagingSelector.start = null;
        pagingSelector.rows = null;
        return this;
    }

    public Selector clone() {
        return new Selector(this);
    }

    public HashSet<String> getTerm(String key) {

        TermSelector termSelector = termSelectorHashMap.get(key);

        if (null == termSelector) {
            return null;
        } else {
            return new TermSelector(termSelector).values;
        }
    }

    public String getUniqueName(){
        StringBuilder uniqueString = new StringBuilder();
        for(TermSelector termSelector:termSelectorHashMap.values()){
            for(String string:termSelector.values) {
                uniqueString.append(string);
            }
        }
        for(RangeSelector rangeSelector:rangeSelectorHashMap.values()){
            uniqueString.append(rangeSelector.to);
            uniqueString.append("-");
            uniqueString.append(rangeSelector.from);
        }
        return uniqueString.toString();
    }
}
