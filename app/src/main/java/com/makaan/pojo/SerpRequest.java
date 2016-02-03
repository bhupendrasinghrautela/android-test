package com.makaan.pojo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
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

    ArrayList<Long> cityIds = new ArrayList<Long>();
    ArrayList<Long> localityIds = new ArrayList<Long>();
    ArrayList<Long> suburbIds = new ArrayList<Long>();
    ArrayList<Long> projectIds = new ArrayList<Long>();
    ArrayList<Long> builderIds = new ArrayList<Long>();
    ArrayList<Long> sellerIds = new ArrayList<Long>();
    ArrayList<String> gpIds = new ArrayList<String>();

    long minBudget = UNEXPECTED_VALUE;
    long maxBudget = UNEXPECTED_VALUE;
    int serpContext = UNEXPECTED_VALUE;

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
        readStringList(source, this.gpIds);

        setMinBudget(source.readLong());
        setMaxBudget(source.readLong());
        setSerpContext(source.readInt());
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
        writeStringList(dest, this.gpIds);

        dest.writeLong(this.minBudget);
        dest.writeLong(this.maxBudget);
        dest.writeInt(this.serpContext);
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

        if (minBudget != UNEXPECTED_VALUE
                && maxBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minBudget, maxBudget);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && KeyUtil.PRICE.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                    grp.rangeFilterValues.get(0).selectedMaxValue = maxBudget;
                    grp.isSelected = true;
                }
            }
        } else if (minBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, minBudget, null);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && KeyUtil.PRICE.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                    grp.isSelected = true;
                }
            }
        } else if (maxBudget != UNEXPECTED_VALUE) {
            selector.removeRange(KeyUtil.PRICE);
            selector.range(KeyUtil.PRICE, null, maxBudget);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && KeyUtil.PRICE.equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    grp.rangeFilterValues.get(0).selectedMaxValue = maxBudget;
                    grp.isSelected = true;
                }
            }
        }
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
}
