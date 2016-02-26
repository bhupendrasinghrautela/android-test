package com.makaan.pojo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.constants.RequestConstants;
import com.makaan.request.selector.Selector;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.util.KeyUtil;

import java.security.Key;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by rohitgarg on 2/3/16.
 */
public class SerpRequest implements Parcelable, Cloneable {

    public static final int UNEXPECTED_VALUE = Integer.MIN_VALUE;
    public static final int CONTEXT_RENT = 0x01;
    public static final int CONTEXT_PRIMARY = 0x02;
    public static final int CONTEXT_RESALE = 0x04;
    public static final int CONTEXT_BUY = CONTEXT_PRIMARY | CONTEXT_RESALE;

    public static final int LISTED_BY_SELLER = 0x01;
    public static final int LISTED_BY_BUILDER = 0x02;
    public static final int LISTED_BY_OWNER = 0x04;


    public enum Sort {
        RELEVANCE, PRICE_HIGH_TO_LOW, PRICE_LOW_TO_HIGH, SELLER_RATING_ASC, SELLER_RATING_DESC,
        DATE_POSTED_ASC, DATE_POSTED_DESC, LIVABILITY_SCORE_ASC, LIVABILITY_SCORE_DESC,
        QUALITY_SCORE_ASC, QUALITY_SCORE_DESC, DISTANCE_ASC, DISTANCE_DESC, GEO_ASC, GEO_DESC
    }

    private ArrayList<Long> cityIds = new ArrayList<Long>();
    private ArrayList<Long> localityIds = new ArrayList<Long>();
    private ArrayList<Long> suburbIds = new ArrayList<Long>();
    private ArrayList<Long> projectIds = new ArrayList<Long>();
    private ArrayList<Long> builderIds = new ArrayList<Long>();
    private ArrayList<Long> sellerIds = new ArrayList<Long>();
    private ArrayList<Integer> bedrooms = new ArrayList<Integer>();
    private ArrayList<Integer> bathrooms = new ArrayList<Integer>();
    private ArrayList<Integer> propertyTypes = new ArrayList<Integer>();
    private ArrayList<String> gpIds = new ArrayList<String>();
    private ArrayList<SearchResponseItem> searchItems = new ArrayList<>();

    private HashMap<String, ArrayList<String>> termMap = new HashMap<>();
    private HashMap<String, Long[]> rangeMap = new HashMap<>();
    private HashMap<String, Long[]> minMaxRangeMap = new HashMap<>();

    private long minBudget = UNEXPECTED_VALUE;
    private long maxBudget = UNEXPECTED_VALUE;
    private long minArea = UNEXPECTED_VALUE;
    private long maxArea = UNEXPECTED_VALUE;

    private int serpContext = UNEXPECTED_VALUE;
    private int sort = UNEXPECTED_VALUE;
    private int type = UNEXPECTED_VALUE;
    private int backstackType = UNEXPECTED_VALUE;
    private int listedBy = UNEXPECTED_VALUE;

    private double latitude = Double.MIN_VALUE;
    private double longitude = Double.MIN_VALUE;
    private double fromLatitude = Double.MIN_VALUE;
    private double fromLongitude = Double.MIN_VALUE;
    private double toLatitude = Double.MIN_VALUE;
    private double toLongitude = Double.MIN_VALUE;

    private boolean isFromBackstack = false;
    private boolean mPlus = false;

    private String displayText = "";

    public SerpRequest(int type) {
        this.type = type;
    }

    public SerpRequest(String json) {
        SelectorParser.parse(json).applySelections(this);
    }

    public int getType() {
        return this.type;
    }

    public boolean isFromBackstack() {
        return isFromBackstack;
    }

    public void setIsFromBackstack(boolean isFromBackstack) {
        this.isFromBackstack = isFromBackstack;
    }

    public void setMPlus(boolean mPlus) {
        this.mPlus = mPlus;
    }

    public void setBackStackType(int type) {
        backstackType = type;
    }

    public int getBackStackType() {
        return backstackType;
    }

    public void setSort(Sort sort) {
        this.sort = sort.ordinal();
    }

    private void setSort(int sort) {
        this.sort = sort;
    }

    public void setCityId(long cityId) {
        if(!this.cityIds.contains(cityId)) {
            this.cityIds.add(cityId);
        }
    }

    public long getCityId() {
        if(this.cityIds.size() > 0) {
            return this.cityIds.get(0);
        }
        return 0;
    }

    public long getLocalityId() {
        if(this.localityIds.size() > 0) {
            return this.localityIds.get(0);
        }
        return getSuburbId();
    }

    private long getSuburbId() {
        if(this.suburbIds.size() > 0) {
            return this.suburbIds.get(0);
        }
        return 0;
    }

    public void setLocalityId(long localityId) {
        if(!this.localityIds.contains(localityId)) {
            this.localityIds.add(localityId);
        }
    }

    public void setSuburbId(long suburbId) {
        if(!this.suburbIds.contains(suburbId)) {
            this.suburbIds.add(suburbId);
        }
    }

    public void setProjectId(long projectId) {
        if(!this.projectIds.contains(projectId)) {
            this.projectIds.add(projectId);
        }
    }

    public long getProjectId() {
        if(this.projectIds.size() > 0) {
            return this.projectIds.get(0);
        }
        return 0;
    }

    public void setBuilderId(long builderId) {
        if(!this.builderIds.contains(builderId)) {
            this.builderIds.add(builderId);
        }
    }

    public void setSellerId(long sellerId) {
        if(!this.sellerIds.contains(sellerId)) {
            this.sellerIds.add(sellerId);
        }
    }

    public void setBedrooms(int bedroom) {
        if(!this.bedrooms.contains(bedroom)) {
            this.bedrooms.add(bedroom);
        }
    }

    public void setBathroom(int bathroom) {
        if(!this.bathrooms.contains(bathroom)) {
            this.bathrooms.add(bathroom);
        }
    }

    public void setPropertyType(int propertyType) {
        if(!this.propertyTypes.contains(propertyType)) {
            this.propertyTypes.add(propertyType);
        }
    }

    public void setGpId(String gpId) {
        if(!this.gpIds.contains(gpId)) {
            this.gpIds.add(gpId);
        }
    }

    public void setTitle(String displayText) {
        this.displayText = displayText;
    }

    public String getTitle() {
        return this.displayText;
    }

    public void setMinBudget(long minBudget) {
        this.minBudget = minBudget;
    }

    public void setMaxBudget(long maxBudget) {
        this.maxBudget = maxBudget;
    }

    public void setMinArea(long minBudget) {
        this.minArea = minBudget;
    }

    public void setMaxArea(long maxBudget) {
        this.maxArea = maxBudget;
    }

    public void setSerpContext(int context) {
        this.serpContext = context;
    }

    public void addSerpContext(int context) {
        if(this.serpContext == UNEXPECTED_VALUE) {
            this.serpContext = 0;
        }
        this.serpContext |= context;
    }

    public void setListedBy(int listedBy) {
        this.listedBy = listedBy;
    }

    public void addListedBy(int listedBy) {
        if(this.listedBy == UNEXPECTED_VALUE) {
            this.listedBy = 0;
        }
        this.listedBy |= listedBy;
    }

    public void setSearch(SearchResponseItem item) {
        searchItems.add(item);
    }

    public ArrayList<SearchResponseItem> getSearches() {
        return searchItems;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFromToGeo(double fromLat, double toLat, double fromLon, double toLon) {
        this.fromLatitude = fromLat;
        this.fromLongitude = fromLon;
        this.toLatitude = toLat;
        this.toLongitude = toLon;
    }

    public int selectedLocalitiesAndSuburbs() {
        return this.localityIds.size() + this.suburbIds.size();
    }

    public void addTerms(String fieldName, ArrayList<String> values) {
        ArrayList<String> terms = termMap.get(fieldName);
        if(terms == null) {
            termMap.put(fieldName, values);
        } else {
            terms.addAll(values);
        }
    }

    public void addTerm(String fieldName, String value) {
        ArrayList<String> terms = termMap.get(fieldName);
        if(terms == null) {
            ArrayList<String> values = new ArrayList<String>();
            values.add(value);
            termMap.put(fieldName, values);
        } else {
            terms.add(value);
        }
    }

    public void addRange(String fieldName, Long from, Long to) {
        HashMap<String, Long[]> map;
        if(from == null || to == null) {
            map = minMaxRangeMap;
        } else {
            map = rangeMap;
        }
        Long[] terms = map.get(fieldName);
        if(terms != null) {
            map.remove(fieldName);
        }
        terms = new Long[2];
        terms[0] = from;
        terms[1] = to;
        map.put(fieldName, terms);
    }

    public SerpRequest(Parcel source) {
        this.type = source.readInt();
        readLongList(source, this.cityIds);
        readLongList(source, this.localityIds);
        readLongList(source, this.suburbIds);
        readLongList(source, this.projectIds);
        readLongList(source, this.builderIds);
        readLongList(source, this.sellerIds);
        readIntList(source, this.bedrooms);
        readIntList(source, this.bathrooms);
        readIntList(source, this.propertyTypes);
        readStringList(source, this.gpIds);
        readParceableList(source, this.searchItems);

        setMinBudget(source.readLong());
        setMaxBudget(source.readLong());

        setMinArea(source.readLong());
        setMaxArea(source.readLong());

        setSerpContext(source.readInt());
        setSort(source.readInt());
        setBackStackType(source.readInt());
        setListedBy(source.readInt());

        setLatitude(source.readDouble());
        setLongitude(source.readDouble());
        setFromToGeo(source.readDouble(), source.readDouble(), source.readDouble(), source.readDouble());

        setIsFromBackstack(source.readByte() == 1);
        setMPlus(source.readByte() == 1);

        setTitle(source.readString());

        readTermMap(source);
        readRangeMap(source);
        readRangeMap(source);
    }

    public SerpRequest(SerpRequest request) {
        this.type = request.type;
        this.cityIds.addAll(request.cityIds);
        this.localityIds.addAll(request.localityIds);
        this.suburbIds.addAll(request.suburbIds);
        this.projectIds.addAll(request.projectIds);
        this.builderIds.addAll(request.builderIds);
        this.sellerIds.addAll(request.sellerIds);
        this.bedrooms.addAll(request.bedrooms);
        this.bathrooms.addAll(request.bathrooms);
        this.propertyTypes.addAll(request.propertyTypes);
        this.gpIds.addAll(request.gpIds);
        this.searchItems.addAll(request.searchItems);

        this.setMinBudget(request.minBudget);
        this.setMaxBudget(request.maxBudget);

        setMinArea(request.minArea);
        setMaxArea(request.maxArea);

        setSerpContext(request.serpContext);
        setSort(request.sort);
        setBackStackType(request.backstackType);
        setListedBy(request.listedBy);

        setLatitude(request.latitude);
        setLongitude(request.longitude);
        setFromToGeo(request.fromLatitude, request.toLatitude, request.fromLongitude, request.toLongitude);

        setIsFromBackstack(request.isFromBackstack);
        setMPlus(request.mPlus);

        setTitle(request.getTitle());

        this.termMap = new HashMap<> (request.termMap);
        this.rangeMap = new HashMap<> (request.rangeMap);
        this.minMaxRangeMap = new HashMap<> (request.minMaxRangeMap);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);

        writeLongList(dest, this.cityIds);
        writeLongList(dest, this.localityIds);
        writeLongList(dest, this.suburbIds);
        writeLongList(dest, this.projectIds);
        writeLongList(dest, this.builderIds);
        writeLongList(dest, this.sellerIds);
        writeIntList(dest, this.bedrooms);
        writeIntList(dest, this.bathrooms);
        writeIntList(dest, this.propertyTypes);
        writeStringList(dest, this.gpIds);
        writeParceableList(dest, this.searchItems, flags);

        dest.writeLong(this.minBudget);
        dest.writeLong(this.maxBudget);

        dest.writeLong(this.minArea);
        dest.writeLong(this.maxArea);

        dest.writeInt(this.serpContext);
        dest.writeInt(this.sort);
        dest.writeInt(this.backstackType);
        dest.writeInt(this.listedBy);

        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.fromLatitude);
        dest.writeDouble(this.toLatitude);
        dest.writeDouble(this.fromLongitude);
        dest.writeDouble(this.toLongitude);

        dest.writeByte((byte) (isFromBackstack ? 1 : 0));
        dest.writeByte((byte) (mPlus ? 1 : 0));

        dest.writeString(displayText);

        writeTermMap(dest);
        writeRangeMap(dest);
    }

    private void writeRangeMap(Parcel dest) {
        int keySize = rangeMap.keySet().size();
        dest.writeInt(keySize);
        for(String key : rangeMap.keySet()) {
            dest.writeString(key);
            Long[] values = rangeMap.get(key);
            if(values[0] == null) {
                dest.writeLong(Long.MIN_VALUE);
            } else {
                dest.writeLong(values[0]);
            }
            if(values[1] == null) {
                dest.writeLong(Long.MIN_VALUE);
            } else {
                dest.writeLong(values[1]);
            }
        }

        keySize = minMaxRangeMap.keySet().size();
        dest.writeInt(keySize);
        for(String key : minMaxRangeMap.keySet()) {
            dest.writeString(key);
            Long[] values = minMaxRangeMap.get(key);
            if(values[0] == null) {
                dest.writeLong(Long.MIN_VALUE);
            } else {
                dest.writeLong(values[0]);
            }
            if(values[1] == null) {
                dest.writeLong(Long.MIN_VALUE);
            } else {
                dest.writeLong(values[1]);
            }
        }
    }

    private void readRangeMap(Parcel source) {
        int keySize = source.readInt();
        for(int i = 0; i < keySize; i++) {
            String key = source.readString();
            Long[] values = new Long[2];
            values[0] = source.readLong();
            values[1] = source.readLong();
            if(values[0] == Long.MIN_VALUE) {
                values[0] = null;
            }
            if(values[1] == Long.MIN_VALUE) {
                values[1] = null;
            }
            if(values[0] == null || values[1] == null) {
                minMaxRangeMap.put(key, values);
            } else {
                rangeMap.put(key, values);
            }
        }
    }

    private void writeTermMap(Parcel dest) {
        int keySize = termMap.keySet().size();
        dest.writeInt(keySize);
        for(String key : termMap.keySet()) {
            dest.writeString(key);
            ArrayList<String> values = termMap.get(key);
            writeStringList(dest, values);
        }
    }

    private void readTermMap(Parcel source) {
        int keySize = source.readInt();
        for(int i = 0; i < keySize; i++) {
            String key = source.readString();
            ArrayList<String> values = new ArrayList<>();
            readStringList(source, values);
            termMap.put(key, values);
        }
    }

    private void writeParceableList(Parcel dest, ArrayList<SearchResponseItem> list, int flags) {
        dest.writeInt(list.size());
        for(int i = 0; i < list.size(); i++) {
            dest.writeParcelable(list.get(i), flags);
        }
    }

    private void writeStringList(Parcel dest, ArrayList<String> list) {
        dest.writeInt(list.size());
        for(int i = 0; i < list.size(); i++) {
            dest.writeString(list.get(i));
        }
    }

    private void writeLongList(Parcel dest, ArrayList<Long> list) {
        dest.writeInt(list.size());
        for(int i = 0; i < list.size(); i++) {
            dest.writeLong(list.get(i));
        }
    }

    private void writeIntList(Parcel dest, ArrayList<Integer> list) {
        dest.writeInt(list.size());
        for(int i = 0; i < list.size(); i++) {
            dest.writeInt(list.get(i));
        }
    }

    private void readParceableList(Parcel source, ArrayList<SearchResponseItem> list) {
        int size = source.readInt();
        for(int i = 0; i < size; i++) {
            list.add(source.<SearchResponseItem>readParcelable(SearchResponseItem.class.getClassLoader()));
        }
    }

    private void readStringList(Parcel source, ArrayList<String> list) {
        int size = source.readInt();
        for(int i = 0; i < size; i++) {
            list.add(source.readString());
        }
    }

    private void readLongList(Parcel source, ArrayList<Long> list) {
        int size = source.readInt();
        for(int i = 0; i < size; i++) {
            list.add(source.readLong());
        }
    }

    private void readIntList(Parcel source, ArrayList<Integer> list) {
        int size = source.readInt();
        for(int i = 0; i < size; i++) {
            list.add(source.readInt());
        }
    }

    public static final Parcelable.Creator<SerpRequest> CREATOR = new Parcelable.Creator<SerpRequest>() {
        public SerpRequest createFromParcel(Parcel source) {
            return new SerpRequest(source);
        }

        public SerpRequest[] newArray(int size) {
            return new SerpRequest[size];
        }
    };
    public void applySelector(Selector selector, ArrayList<FilterGroup> filterGroup) {
        applySelector(selector, filterGroup, true);
    }

    public void applySelector(Selector selector, ArrayList<FilterGroup> filterGroup, boolean needFacets) {
        applySelector(selector, filterGroup, needFacets, false);
    }

    public void applySelector(Selector selector, ArrayList<FilterGroup> filterGroup, boolean needFacets, boolean needViewPort) {
        // city ids
        if(this.cityIds.size() > 0){
            selector.removeTerm(KeyUtil.CITY_ID);
            for (Long cityId : this.cityIds) {
                selector.term(KeyUtil.CITY_ID, String.valueOf(cityId));
            }
            if(needFacets) {
                selector.facet("cityId");
                selector.facet("cityLabel");
            }
        }

        // locality and suburb ids
        if(this.localityIds.size() > 0 && this.suburbIds.size() > 0) {
            selector.removeTerm(KeyUtil.LOCALITY_ID);
            selector.removeTerm(KeyUtil.SUBURB_ID);
            selector.removeTerm(KeyUtil.LOCALITY_OR_SUBURB_ID);

            for (Long localityId : this.localityIds) {
                selector.term(KeyUtil.LOCALITY_OR_SUBURB_ID, String.valueOf(localityId));
            }

            for (Long suburbId : this.suburbIds) {
                selector.term(KeyUtil.LOCALITY_OR_SUBURB_ID, String.valueOf(suburbId));
            }
            if(needFacets) {
                selector.facet("localityId");
                selector.facet("localityLabel");
                selector.facet("suburbId");
                selector.facet("suburbLabel");
            }
        } else if(this.localityIds.size() > 0) {
            selector.removeTerm(KeyUtil.LOCALITY_ID);
            selector.removeTerm(KeyUtil.SUBURB_ID);
            selector.removeTerm(KeyUtil.LOCALITY_OR_SUBURB_ID);
            for (Long localityId : this.localityIds) {
                selector.term(KeyUtil.LOCALITY_ID, String.valueOf(localityId));
            }
            if(needFacets) {
                selector.facet("localityId");
                selector.facet("localityLabel");
            }
        } else if(this.suburbIds.size() > 0) {
            selector.removeTerm(KeyUtil.LOCALITY_ID);
            selector.removeTerm(KeyUtil.SUBURB_ID);
            selector.removeTerm(KeyUtil.LOCALITY_OR_SUBURB_ID);
            for (Long suburbId : this.suburbIds) {
                selector.term(KeyUtil.SUBURB_ID, String.valueOf(suburbId));
            }
            if(needFacets) {
                selector.facet("suburbId");
                selector.facet("suburbLabel");
            }
        }

        // project ids
        if(this.projectIds.size() > 0){
            selector.removeTerm(KeyUtil.PROJECT_ID);
            for (Long projectId : this.projectIds) {
                selector.term(KeyUtil.PROJECT_ID, String.valueOf(projectId));
            }
            if(needFacets) {
                selector.facet("projectId");
            }
        }

        // builder ids
        if(this.builderIds.size() > 0) {
            selector.removeTerm(KeyUtil.BUILDER_ID);
            for (Long builderId : this.builderIds) {
                selector.term(KeyUtil.BUILDER_ID, String.valueOf(builderId));
            }
            if(needFacets) {
                selector.facet("builderId");
                selector.facet("builderLabel");
            }
        }

        // seller ids
        if(this.sellerIds.size() > 0) {
            selector.removeTerm(KeyUtil.SELLER_ID);
            for (Long sellerId : this.sellerIds) {
                selector.term(KeyUtil.SELLER_ID, String.valueOf(sellerId));
            }
        }

        // gp ids
        if(this.gpIds.size() > 0) {
            // TODO
        }

        // bedrooms
        if(this.bedrooms.size() > 0) {

            selector.removeTerm(KeyUtil.BEDROOM);
            for (Integer bedroom : this.bedrooms) {
                selector.term(KeyUtil.BEDROOM, String.valueOf(bedroom));
            }
        }

        // bathrooms
        if(this.bathrooms.size() > 0) {

            selector.removeTerm(KeyUtil.BATHROOM);
            for (Integer bathroom : this.bathrooms) {
                selector.term(KeyUtil.BATHROOM, String.valueOf(bathroom));
            }
        }

        // property types
        if(this.propertyTypes.size() > 0) {

            selector.removeTerm(KeyUtil.PROPERTY_TYPES);
            for (Integer propertyType : this.propertyTypes) {
                selector.term(KeyUtil.PROPERTY_TYPES, String.valueOf(propertyType));
            }
        }

        // buy/rent context
        if(serpContext != UNEXPECTED_VALUE) {
            selector.removeTerm(KeyUtil.LISTING_CATEGORY);
            if((serpContext & CONTEXT_PRIMARY) > 0) {
                selector.term(KeyUtil.LISTING_CATEGORY, "Primary");
            }
            if((serpContext & CONTEXT_RESALE) > 0) {
                selector.term(KeyUtil.LISTING_CATEGORY, "Resale");
            }
            if((serpContext & CONTEXT_RENT) > 0) {
                selector.term(KeyUtil.LISTING_CATEGORY, "Rental");
            }
        }

        // budget
        if (minBudget != UNEXPECTED_VALUE
                && maxBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minBudget, maxBudget);
        } else if (minBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minBudget, null);
        } else if (maxBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, null, maxBudget);
        }

        // area
        if (minArea != UNEXPECTED_VALUE
                && maxArea != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minArea, maxArea);
        } else if (minArea != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minArea, null);
        } else if (maxArea != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, null, maxArea);
        }

        // listed by
        if(listedBy != UNEXPECTED_VALUE) {
            selector.removeTerm(KeyUtil.LISTING_SELLER_COMPANY_TYPE);
            if((listedBy & LISTED_BY_BUILDER) > 0) {
                selector.term(KeyUtil.LISTING_SELLER_COMPANY_TYPE, "Builder");
            }
            if((listedBy & LISTED_BY_SELLER) > 0) {
                selector.term(KeyUtil.LISTING_SELLER_COMPANY_TYPE, "Broker");
            }
            if((listedBy & LISTED_BY_OWNER) > 0) {
                selector.term(KeyUtil.LISTING_SELLER_COMPANY_TYPE, "Owner");
            }
        }

        // m plus
        if(mPlus) {
            selector.removeTerm(KeyUtil.LISTING_SELLER_COMPANY_ASSIST);
            selector.term(KeyUtil.LISTING_SELLER_COMPANY_ASSIST, "true");
        }

        if(Double.compare(latitude, Double.MIN_VALUE) != 0 && Double.compare(longitude, Double.MIN_VALUE) != 0) {
            selector.nearby(RequestConstants.GEO_REQUEST_DISTANCE, latitude, longitude, needViewPort);
        }

        // view port
        if(Double.compare(fromLatitude, Double.MIN_VALUE) != 0 && Double.compare(toLatitude, Double.MIN_VALUE) != 0
                && Double.compare(fromLongitude, Double.MIN_VALUE) != 0 && Double.compare(toLongitude, Double.MIN_VALUE) != 0) {
            selector.range(RequestConstants.LATITUDE, fromLatitude, toLatitude);
            selector.range(RequestConstants.LONGITUDE, fromLongitude, toLongitude);
        }

        // sorting
        if(this.sort != UNEXPECTED_VALUE) {
            if (this.sort == Sort.RELEVANCE.ordinal()) {
                selector.sort(null, null);
            } else if(this.sort == Sort.PRICE_HIGH_TO_LOW.ordinal()) {
                selector.sort("price", "DESC");
            } else if(this.sort == Sort.PRICE_LOW_TO_HIGH.ordinal()) {
                selector.sort("price", "ASC");
            } else if(this.sort == Sort.SELLER_RATING_ASC.ordinal()) {
                selector.sort("listingSellerCompanyScore", "ASC");
            } else if(this.sort == Sort.SELLER_RATING_DESC.ordinal()) {
                selector.sort("listingSellerCompanyScore", "DESC");
            } else if(this.sort == Sort.DATE_POSTED_ASC.ordinal()) {
                selector.sort("listingPostedDate", "ASC");
            } else if(this.sort == Sort.DATE_POSTED_DESC.ordinal()) {
                selector.sort("listingPostedDate", "DESC");
            } else if(this.sort == Sort.LIVABILITY_SCORE_ASC.ordinal()) {
                selector.sort("listingLivabilityScore", "ASC");
            } else if(this.sort == Sort.LIVABILITY_SCORE_DESC.ordinal()) {
                selector.sort("listingLivabilityScore", "DESC");
            } else if(this.sort == Sort.QUALITY_SCORE_ASC.ordinal()) {
                selector.sort("listingQualityScore", "ASC");
            } else if(this.sort == Sort.QUALITY_SCORE_DESC.ordinal()) {
                selector.sort("listingQualityScore", "DESC");
            } else if(this.sort == Sort.DISTANCE_ASC.ordinal()) {
//                selector.sort("price", "DESC");
            } else if(this.sort == Sort.DISTANCE_DESC.ordinal()) {
//                selector.sort("price", "DESC");
            } else if(this.sort == Sort.GEO_ASC.ordinal()) {
                if(!needViewPort) {
                    selector.sort("geoDistance", "ASC");
                }
            } else if(this.sort == Sort.GEO_DESC.ordinal()) {
                if(!needViewPort) {
                    selector.sort("geoDistance", "DESC");
                }
            }
        }

        // apply filters
        if(filterGroup != null) {
            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && KeyUtil.PRICE.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    if (minBudget != UNEXPECTED_VALUE) {
                        grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                        grp.isSelected = true;
                    }
                    if (maxBudget != UNEXPECTED_VALUE) {
                        grp.rangeFilterValues.get(0).selectedMaxValue = maxBudget;
                        grp.isSelected = true;
                    }
                } else if (grp.rangeFilterValues.size() > 0 && KeyUtil.AREA.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    if (minArea != UNEXPECTED_VALUE) {
                        grp.rangeFilterValues.get(0).selectedMinValue = minArea;
                        grp.isSelected = true;
                    }
                    if (maxArea != UNEXPECTED_VALUE) {
                        grp.rangeFilterValues.get(0).selectedMaxValue = maxArea;
                        grp.isSelected = true;
                    }
                } else if (grp.termFilterValues.size() > 0 && KeyUtil.BATHROOM.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                    if (this.bathrooms.size() > 0) {
                        applyIntTermFilter(grp, bathrooms);
                    }
                } else if (grp.termFilterValues.size() > 0 && KeyUtil.BEDROOM.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                    if (this.bedrooms.size() > 0) {
                        applyIntTermFilter(grp, bedrooms);
                    }
                } else if (grp.termFilterValues.size() > 0 && KeyUtil.PROPERTY_TYPES.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                    if (this.propertyTypes.size() > 0) {
                        applyIntTermFilter(grp, propertyTypes);
                    }
                } else if (grp.termFilterValues.size() > 0 && KeyUtil.LISTING_SELLER_COMPANY_TYPE.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                    if (this.listedBy != UNEXPECTED_VALUE) {
                        if ((listedBy & LISTED_BY_BUILDER) > 0) {
                            for (TermFilter filter : grp.termFilterValues) {
                                if ("Builder".equalsIgnoreCase(filter.value)) {
                                    filter.selected = true;
                                    grp.isSelected = true;
                                }
                            }
                        }
                        if ((listedBy & LISTED_BY_SELLER) > 0) {
                            for (TermFilter filter : grp.termFilterValues) {
                                if ("Broker".equalsIgnoreCase(filter.value)) {
                                    filter.selected = true;
                                    grp.isSelected = true;
                                }
                            }
                        }
                        if ((listedBy & LISTED_BY_OWNER) > 0) {
                            for (TermFilter filter : grp.termFilterValues) {
                                if ("Owner".equalsIgnoreCase(filter.value)) {
                                    filter.selected = true;
                                    grp.isSelected = true;
                                }
                            }
                        }
                    }
                } else if (grp.termFilterValues.size() > 0 && KeyUtil.LISTING_SELLER_COMPANY_ASSIST.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                    if (mPlus) {
                        grp.termFilterValues.get(0).selected = true;
                        grp.isSelected = true;
                    }
                }
            }
        }

        if(termMap.keySet().size() > 0) {
            for (String key : termMap.keySet()) {
                boolean isApplied = false;
                if(filterGroup != null) {
                    for (FilterGroup grp : filterGroup) {
                        if (grp.termFilterValues.size() > 0 && grp.termFilterValues.get(0).fieldName.equals(key)) {
                            isApplied = true;
                            for (String value : termMap.get(key)) {
                                for (TermFilter filter : grp.termFilterValues) {
                                    if (filter.value.equalsIgnoreCase(value)) {
                                        filter.selected = true;
                                        grp.isSelected = true;
                                    }
                                }

                                if (value.indexOf(FiltersViewAdapter.MIN_MAX_SEPARATOR) > 0) {
                                    String[] values = value.split(FiltersViewAdapter.MIN_MAX_SEPARATOR);
                                    if (values.length == 2) {
                                        try {
                                            int minValue = Integer.parseInt(values[0]);
                                            int maxValue = Integer.parseInt(values[1]);
                                            for (int i = minValue; i <= maxValue; i++) {
                                                selector.term(key, String.valueOf(i));
                                            }
                                        } catch (NumberFormatException ex) {
                                            // TODO
                                        }
                                    }
                                } else {
                                    selector.term(key, value);
                                }
                            }
                        }
                    }
                }
                if(!isApplied) {
                    selector.term(key, termMap.get(key));
                    if(KeyUtil.CITY_ID.equalsIgnoreCase(key)) {
                        selector.facet("cityId");
                        selector.facet("cityLabel");
                    } else if(KeyUtil.LOCALITY_ID.equalsIgnoreCase(key)) {
                        selector.facet("localityId");
                        selector.facet("localityLabel");
                    } else if(KeyUtil.SUBURB_ID.equalsIgnoreCase(key)) {
                        selector.facet("suburbId");
                        selector.facet("suburbLabel");
                    } else if(KeyUtil.LOCALITY_OR_SUBURB_ID.equalsIgnoreCase(key)) {
                        selector.facet("localityId");
                        selector.facet("localityLabel");
                        selector.facet("suburbId");
                        selector.facet("suburbLabel");
                    } else if(KeyUtil.PROJECT_ID.equalsIgnoreCase(key)) {
                        selector.facet("projectId");
                    } else if(KeyUtil.BUILDER_ID.equalsIgnoreCase(key)) {
                        selector.facet("builderId");
                        selector.facet("builderLabel");
                    }
                }
            }
        }

        if(rangeMap.keySet().size() > 0) {
            for (String key : rangeMap.keySet()) {
                if(filterGroup != null) {
                    for (FilterGroup grp : filterGroup) {
                        if (grp.rangeFilterValues.size() > 0 && grp.rangeFilterValues.get(0).fieldName.equals(key)) {
                            Long[] value = rangeMap.get(key);
                            if (FiltersViewAdapter.RADIO_BUTTON_RANGE == grp.layoutType) {
                                for (RangeFilter filter : grp.rangeFilterValues) {
                                    if (value[0] == filter.minValue && value[1] == filter.maxValue) {
                                        filter.selected = true;
                                        grp.isSelected = true;

                                        if (filter.minValue != FiltersViewAdapter.UNEXPECTED_VALUE || filter.maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                            Long minValue = (long) FiltersViewAdapter.UNEXPECTED_VALUE;
                                            Long maxValue = (long) FiltersViewAdapter.UNEXPECTED_VALUE;
                                            if (grp.type.equalsIgnoreCase(FiltersViewAdapter.TYPE_YEAR)) {
                                                Calendar cal = Calendar.getInstance();
                                                if (filter.minValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    cal.add(Calendar.YEAR, (int) filter.minValue);
                                                    minValue = cal.getTimeInMillis();
                                                    cal.add(Calendar.YEAR, -(int) filter.minValue);
                                                }

                                                if (filter.maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    cal.add(Calendar.YEAR, (int) filter.maxValue);
                                                    maxValue = cal.getTimeInMillis();
                                                }
                                                if (minValue == FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    minValue = null;
                                                }
                                                if (maxValue == FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    maxValue = null;
                                                }

                                                selector.range(filter.fieldName, minValue, maxValue);
                                            } else if (grp.type.equalsIgnoreCase(FiltersViewAdapter.TYPE_DAY)) {
                                                Calendar cal = Calendar.getInstance();
                                                if (filter.minValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    cal.add(Calendar.DAY_OF_MONTH, (int) filter.minValue);
                                                    minValue = cal.getTimeInMillis();
                                                    cal.add(Calendar.DAY_OF_MONTH, -(int) filter.minValue);
                                                }

                                                if (filter.maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    cal.add(Calendar.DAY_OF_MONTH, (int) filter.maxValue);
                                                    maxValue = cal.getTimeInMillis();
                                                }
                                                if (minValue == FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    minValue = null;
                                                }
                                                if (maxValue == FiltersViewAdapter.UNEXPECTED_VALUE) {
                                                    maxValue = null;
                                                }

                                                selector.range(filter.fieldName, minValue, maxValue);
                                            } else {
                                                selector.range(filter.fieldName, filter.minValue, filter.maxValue);
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (RangeFilter filter : grp.rangeFilterValues) {
                                    filter.selectedMinValue = value[0];
                                    filter.selectedMaxValue = value[1];
                                    if (filter.selectedMinValue != filter.minValue || filter.selectedMaxValue != filter.maxValue) {
                                        filter.selected = true;
                                        grp.isSelected = true;
                                    }
                                }

                                selector.range(key, value[0], value[1]);
                            }
                        }
                    }
                }
            }
        }

        if(minMaxRangeMap.keySet().size() > 0) {
            ArrayList<Double> minFieldMap = new ArrayList<>();
            ArrayList<Double> maxFieldMap = new ArrayList<>();
            for (String key : minMaxRangeMap.keySet()) {
                if(filterGroup != null) {
                    for (FilterGroup grp : filterGroup) {
                        if (grp.rangeMinMaxFilterValues.size() > 0) {
                            if (grp.rangeMinMaxFilterValues.get(0).minFieldName.equals(key)) {
                                Long[] value = minMaxRangeMap.get(key);
                                for (RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                                    if (filter.minValue == value[0]) {
                                        if (maxFieldMap.contains(filter.maxValue)) {
                                            maxFieldMap.remove(filter.maxValue);
                                            filter.selected = true;
                                            grp.isSelected = true;
                                        } else {
                                            minFieldMap.add(filter.minValue);
                                        }
                                    }
                                }
                            } else if (grp.rangeMinMaxFilterValues.get(0).maxFieldName.equals(key)) {
                                Long[] value = minMaxRangeMap.get(key);
                                for (RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                                    if (filter.maxValue == value[1]) {
                                        if (minFieldMap.contains(filter.minValue)) {
                                            maxFieldMap.remove(filter.minValue);
                                            filter.selected = true;
                                            grp.isSelected = true;
                                        } else {
                                            maxFieldMap.add(filter.maxValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(filterGroup != null) {
                for (FilterGroup grp : filterGroup) {
                    if (grp.isSelected && grp.rangeMinMaxFilterValues.size() > 0) {
                        for (RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                            if (filter.selected) {
                                if (grp.type.equalsIgnoreCase(FiltersViewAdapter.TYPE_YEAR)) {
                                    Calendar cal = Calendar.getInstance();
                                    long minValue = FiltersViewAdapter.UNEXPECTED_VALUE;
                                    long maxValue = FiltersViewAdapter.UNEXPECTED_VALUE;
                                    if (filter.minValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        cal.add(Calendar.YEAR, (int) filter.minValue);
                                        minValue = cal.getTimeInMillis();
                                        cal.add(Calendar.YEAR, -(int) filter.minValue);
                                    }
                                    if (filter.maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        cal.add(Calendar.YEAR, (int) filter.maxValue);
                                        maxValue = cal.getTimeInMillis();
                                    }
                                    if (minValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        selector.range(filter.minFieldName, minValue, null);
                                    }
                                    if (maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        selector.range(filter.maxFieldName, null, maxValue);
                                    }
                                } else {
                                    if (filter.minValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        selector.term(filter.minFieldName, String.valueOf(filter.minValue));
                                    }
                                    if (filter.maxValue != FiltersViewAdapter.UNEXPECTED_VALUE) {
                                        selector.term(filter.maxFieldName, String.valueOf(filter.maxValue));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyIntTermFilter(FilterGroup grp, ArrayList<Integer> list) {
        for(int item : list) {
            for(TermFilter filter : grp.termFilterValues) {
                try {
                    int val = Integer.parseInt(filter.value);
                    if(val == item) {
                        filter.selected = true;
                        grp.isSelected = true;
                    }
                } catch (NumberFormatException ex) {
                    if(filter.value.contains("-")) {
                        String[] val = filter.value.split("-");
                        if(val.length == 2) {
                            if ((Integer.parseInt(val[0]) <= item) && (Integer.parseInt(val[1]) >= item)) {
                                filter.selected = true;
                                grp.isSelected = true;
                            }
                        } else if(val.length == 1) {
                            if (Integer.parseInt(val[0]) <= item) {
                                filter.selected = true;
                                grp.isSelected = true;
                            }
                        }
                    }
                }
            }
        }
    }

    public String getGpId() {
        if(gpIds != null && gpIds.size() == 1) {
            return gpIds.get(0);
        }
        return null;
    }

    public void launchSerp(Context context) {
        Intent intent = new Intent(context, SerpActivity.class);
        intent.putExtra(SerpActivity.REQUEST_TYPE, type);
        intent.putExtra(SerpActivity.REQUEST_DATA, this);

        if(this.serpContext != UNEXPECTED_VALUE) {
            if((this.serpContext & CONTEXT_RENT) > 0) {
                intent.putExtra(SerpActivity.REQUEST_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_RENT);
            } else {
                intent.putExtra(SerpActivity.REQUEST_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
            }
        }
        context.startActivity(intent);

    }

    public void launchSerp(Context context, String selector) {
        SelectorParser.parse(selector, this);

        Intent intent = new Intent(context, SerpActivity.class);
        intent.putExtra(SerpActivity.REQUEST_TYPE, type);
        intent.putExtra(SerpActivity.REQUEST_DATA, this);

        if(this.serpContext != UNEXPECTED_VALUE) {
            if((this.serpContext & CONTEXT_RENT) > 0) {
                intent.putExtra(SerpActivity.REQUEST_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_RENT);
            } else {
                intent.putExtra(SerpActivity.REQUEST_CONTEXT, MakaanBaseSearchActivity.SERP_CONTEXT_BUY);
            }
        }
        context.startActivity(intent);

    }

    @Override
    protected SerpRequest clone() throws CloneNotSupportedException {
        return new SerpRequest(this);
    }
}
