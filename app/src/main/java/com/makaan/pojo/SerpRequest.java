package com.makaan.pojo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.makaan.activity.listing.SerpActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.util.KeyUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/3/16.
 */
public class SerpRequest implements Parcelable {

    private static final int UNEXPECTED_VALUE = Integer.MIN_VALUE;
    public static final int CONTEXT_RENT = 0x01;
    public static final int CONTEXT_PRIMARY = 0x02;
    public static final int CONTEXT_RESALE = 0x04;
    public static final int CONTEXT_BUY = CONTEXT_PRIMARY | CONTEXT_RESALE;

    long cityId = UNEXPECTED_VALUE;
    long localityId = UNEXPECTED_VALUE;
    long projectId = UNEXPECTED_VALUE;
    long minBudget = UNEXPECTED_VALUE;
    long maxBudget = UNEXPECTED_VALUE;
    int context = CONTEXT_BUY;

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public void setLocalityId(long localityId) {
        this.localityId = localityId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setMinBudget(long minBudget) {
        this.minBudget = minBudget;
    }

    public void setMaxBudget(long maxBudget) {
        this.maxBudget = maxBudget;
    }

    public void setContext(int context) {
        this.context = context;
    }

    public SerpRequest() { }

    public SerpRequest(Parcel source) {
        setCityId(source.readLong());
        setLocalityId(source.readLong());
        setProjectId(source.readLong());
        setMinBudget(source.readLong());
        setMaxBudget(source.readLong());
        setContext(source.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cityId);
        dest.writeLong(this.localityId);
        dest.writeLong(this.projectId);
        dest.writeLong(this.minBudget);
        dest.writeLong(this.maxBudget);
        dest.writeInt(this.context);
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
        if(cityId != UNEXPECTED_VALUE) {
            selector.term(KeyUtil.CITY_ID, String.valueOf(cityId));
        }
        if(localityId != UNEXPECTED_VALUE) {
            selector.term(KeyUtil.LOCALITY_ID, String.valueOf(localityId));
        }
        if(projectId != UNEXPECTED_VALUE) {
            selector.term(KeyUtil.PROJECT_ID, String.valueOf(projectId));
        }

        if(context != UNEXPECTED_VALUE) {
            if((context & CONTEXT_PRIMARY) > 0) {
                selector.term("listingCategory", "Primary");
            }
            if((context & CONTEXT_RESALE) > 0) {
                selector.term("listingCategory", "Resale");
            }
            if((context & CONTEXT_RENT) > 0) {
                selector.term("listingCategory", "Rental");
            }
        }

        if (minBudget != UNEXPECTED_VALUE
                && maxBudget != UNEXPECTED_VALUE) {
            selector.range("price", minBudget, maxBudget);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && "price".equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                    grp.rangeFilterValues.get(0).selectedMaxValue = maxBudget;
                    grp.isSelected = true;
                }
            }
        } else if (minBudget != UNEXPECTED_VALUE) {
            selector.range("price", minBudget, null);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && "price".equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
                    grp.rangeFilterValues.get(0).selectedMinValue = minBudget;
                    grp.isSelected = true;
                }
            }
        } else if (maxBudget != UNEXPECTED_VALUE) {
            selector.range("price", null, maxBudget);

            for (FilterGroup grp : filterGroup) {
                if (grp.rangeFilterValues.size() > 0 && "price".equalsIgnoreCase(grp.rangeFilterValues.get(0).fieldName)) {
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
        context.startActivity(intent);

    }
}
