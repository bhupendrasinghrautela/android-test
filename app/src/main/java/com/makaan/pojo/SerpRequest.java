package com.makaan.pojo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.TermFilter;
import com.makaan.util.KeyUtil;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by rohitgarg on 2/3/16.
 */
public class SerpRequest implements Parcelable {

    private static final int UNEXPECTED_VALUE = Integer.MIN_VALUE;
    public static final int CONTEXT_RENT = 0x01;
    public static final int CONTEXT_PRIMARY = 0x02;
    public static final int CONTEXT_RESALE = 0x04;
    public static final int CONTEXT_BUY = CONTEXT_PRIMARY | CONTEXT_RESALE;

    public enum Sort {
        RELEVANCE, PRICE_HIGH_TO_LOW, PRICE_LOW_TO_HIGH, SELLER_RATING_ASC, SELLER_RATING_DESC,
        DATE_POSTED_ASC, DATE_POSTED_DESC, LIVABILITY_SCORE_ASC, LIVABILITY_SCORE_DESC,
        QUALITY_SCORE_ASC, QUALITY_SCORE_DESC, DISTANCE_ASC, DISTANCE_DESC
    }

    ArrayList<Long> cityIds = new ArrayList<Long>();
    ArrayList<Long> localityIds = new ArrayList<Long>();
    ArrayList<Long> suburbIds = new ArrayList<Long>();
    ArrayList<Long> projectIds = new ArrayList<Long>();
    ArrayList<Long> builderIds = new ArrayList<Long>();
    ArrayList<Long> sellerIds = new ArrayList<Long>();
    ArrayList<Integer> bedrooms = new ArrayList<Integer>();
    ArrayList<Integer> bathrooms = new ArrayList<Integer>();
    ArrayList<Integer> propertyTypes = new ArrayList<Integer>();
    ArrayList<String> gpIds = new ArrayList<String>();
    ArrayList<SearchResponseItem> searchItems = new ArrayList<>();

    long minBudget = UNEXPECTED_VALUE;
    long maxBudget = UNEXPECTED_VALUE;
    long minArea = UNEXPECTED_VALUE;
    long maxArea = UNEXPECTED_VALUE;
    int serpContext = UNEXPECTED_VALUE;
    int sort = UNEXPECTED_VALUE;

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

    public SerpRequest() { }

    public SerpRequest(Parcel source) {
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        // city ids
        if(this.cityIds.size() > 0){
            selector.removeTerm(KeyUtil.CITY_ID);
            for (Long cityId : this.cityIds) {
                selector.term(KeyUtil.CITY_ID, String.valueOf(cityId));
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
        } else if(this.localityIds.size() > 0) {
            selector.removeTerm(KeyUtil.LOCALITY_ID);
            selector.removeTerm(KeyUtil.SUBURB_ID);
            selector.removeTerm(KeyUtil.LOCALITY_OR_SUBURB_ID);
            for (Long localityId : this.localityIds) {
                selector.term(KeyUtil.LOCALITY_ID, String.valueOf(localityId));
            }
        } else if(this.suburbIds.size() > 0) {
            selector.removeTerm(KeyUtil.LOCALITY_ID);
            selector.removeTerm(KeyUtil.SUBURB_ID);
            selector.removeTerm(KeyUtil.LOCALITY_OR_SUBURB_ID);
            for (Long suburbId : this.suburbIds) {
                selector.term(KeyUtil.LOCALITY_ID, String.valueOf(suburbId));
            }
        }

        // project ids
        if(this.projectIds.size() > 0){
            selector.removeTerm(KeyUtil.PROJECT_ID);
            for (Long projectId : this.projectIds) {
                selector.term(KeyUtil.PROJECT_ID, String.valueOf(projectId));
            }
        }

        // builder ids
        if(this.builderIds.size() > 0) {
            selector.removeTerm(KeyUtil.BUILDER_ID);
            for (Long builderId : this.builderIds) {
                selector.term(KeyUtil.BUILDER_ID, String.valueOf(builderId));
            }
        }

        // seller ids
        if(this.sellerIds.size() > 0) {
            selector.removeTerm(KeyUtil.SELLER_ID);
            for (Long sellerId : this.sellerIds) {
                selector.term(KeyUtil.SELLER_ID, String.valueOf(sellerId));
            }
        }

        // seller ids
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
            }
        }

        // apply filters
        for (FilterGroup grp : filterGroup) {
            if (grp.rangeFilterValues.size() > 0 && KeyUtil.PRICE.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                if(minBudget != UNEXPECTED_VALUE) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                    grp.isSelected = true;
                }
                if(maxBudget != UNEXPECTED_VALUE) {
                    grp.rangeFilterValues.get(0).selectedMaxValue = maxBudget;
                    grp.isSelected = true;
                }
            } else if(grp.rangeFilterValues.size() > 0 && KeyUtil.AREA.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                if(minArea != UNEXPECTED_VALUE) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minArea;
                    grp.isSelected = true;
                }
                if(maxArea != UNEXPECTED_VALUE) {
                    grp.rangeFilterValues.get(0).selectedMaxValue = maxArea;
                    grp.isSelected = true;
                }
            } else if(grp.termFilterValues.size() > 0 && KeyUtil.BATHROOM.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                if(this.bathrooms.size() > 0) {
                    applyIntTermFilter(grp, bathrooms);
                }
            } else if(grp.termFilterValues.size() > 0 && KeyUtil.BEDROOM.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                if(this.bedrooms.size() > 0) {
                    applyIntTermFilter(grp, bedrooms);
                }
            } else if(grp.termFilterValues.size() > 0 && KeyUtil.PROPERTY_TYPES.equalsIgnoreCase(grp.termFilterValues.get(0).fieldName)) {
                if(this.propertyTypes.size() > 0) {
                    applyIntTermFilter(grp, propertyTypes);
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

    public void launchSerp(Context context, int type) {
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

    public void setSearch(SearchResponseItem item) {
        searchItems.add(item);
    }
}
