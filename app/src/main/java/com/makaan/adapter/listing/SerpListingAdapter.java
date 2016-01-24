package com.makaan.adapter.listing;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.PaginatedBaseAdapter;
import com.makaan.adapter.RecycleViewMode;
import com.makaan.response.listing.GroupListing;
import com.makaan.response.listing.Listing;
import com.makaan.ui.listing.ListingViewHolderFactory;
import com.makaan.ui.listing.BaseListingAdapterViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpListingAdapter extends PaginatedBaseAdapter<Listing> {

    private final Context mContext;
    private final SerpRequestCallback mCallback;
    private int mRequestType = SerpActivity.TYPE_UNKNOWN;
    protected ArrayList<GroupListing> mGroupListings;

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks) {
        mContext = context;
        mCallback = callbacks;
        recycleViewMode = RecycleViewMode.DATA;
    }

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks, List<Listing> listings, int requestType) {
        mContext = context;
        mCallback = callbacks;
        setData((ArrayList<Listing>) listings, requestType);
        recycleViewMode = RecycleViewMode.DATA;
    }

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks, int requestType) {
        mContext = context;
        mCallback = callbacks;
        recycleViewMode = RecycleViewMode.DATA;
        this.mRequestType = requestType;
    }

    @Override
    public int getItemViewType(int position) {
        int superItemViewType = super.getItemViewType(position);
        if(superItemViewType == RecycleViewMode.DATA.getValue()) {
            if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                if(position == 0) {
                    if(mRequestType == SerpActivity.TYPE_BUILDER) {
                        superItemViewType = RecycleViewMode.DATA_TYPE_BUILDER.getValue();
                    } else {
                        superItemViewType = RecycleViewMode.DATA_TYPE_SELLER.getValue();
                    }
                } else {
                    superItemViewType = RecycleViewMode.DATA_TYPE_LISTING.getValue();
                }
            } else {
                if(position == 3 && mGroupListings != null && mGroupListings.size() > 0) {
                    superItemViewType =  RecycleViewMode.DATA_TYPE_CLUSTER.getValue();
                } else {
                    superItemViewType = RecycleViewMode.DATA_TYPE_LISTING.getValue();
                }
            }
        }
        return superItemViewType;
    }

    @Override
    public int getItemCount() {
        if(mRequestType == SerpActivity.TYPE_CLUSTER) {
            if(mItems == null) {
                return 0;
            }
            return mItems.size();
        } else if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
            if(mItems == null) {
                return 1;
            }
            return (mItems.size() + 1);
        } else {
            if (mItems == null && mGroupListings == null) {
                return 0;
            } else if (mGroupListings == null || mGroupListings.size() == 0) {
                return mItems.size();
            } else if (mItems == null || mItems.size() == 0) {
                return 0;
                // TODO check for this case
//                return (mGroupListings.size() / 3);
            } else {
                // TODO need to check for the cluster logic
                return (mItems.size() + 1);
//                return (mItems.size() + (mGroupListings.size() / 3));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        return ListingViewHolderFactory.createViewHolder(mContext, parent, viewType);
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterLoadingViewHolder(ViewGroup parent) {
        return new FooterHeaderLoaderViewAdapter(LayoutInflater.from(mContext).inflate(R.layout.recycler_view_loading_footer, parent, false));
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterErrorViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseListingAdapterViewHolder viewHolder = (BaseListingAdapterViewHolder) holder;
        if(mGroupListings != null && mGroupListings.size() > 0 && position == 3) {
            viewHolder.populateData(mGroupListings, mCallback);
        } else {
            if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                if(position == 0) {
                    viewHolder.populateData(null, mCallback);
                } else {
                    viewHolder.populateData(mItems.get(position - 1), mCallback);
                }
            } else {
                if (mRequestType != SerpActivity.TYPE_CLUSTER && mGroupListings != null && mGroupListings.size() > 0) {
                    if (position > 3) {
                        viewHolder.populateData(mItems.get(position - 1), mCallback);
                    } else {
                        viewHolder.populateData(mItems.get(position), mCallback);
                    }
                } else {
                    viewHolder.populateData(mItems.get(position), mCallback);
                }
            }
        }
    }

    @Override
    public void onBindFooterLoadingViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindFooterErrorViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void setData(List<Listing> listings, @Nullable List<GroupListing> groupListings, int requestType) {

        if (mGroupListings == null) {
            mGroupListings = new ArrayList<>();
        } else {
            this.mGroupListings.clear();
        }

        if(groupListings != null) {
            mGroupListings.addAll(groupListings);
        }
        setData(listings, requestType);
    }

    @Override
    public void setData(List<Listing> listings, int requestType) {
        if(this.mItems == null) {
            this.mItems = new ArrayList<Listing>();
        }
        this.mItems.clear();

        mRequestType = requestType;
        if(listings == null) {
            return;
        }

        this.mItems.addAll(listings);
        notifyDataSetChanged();
    }
}
