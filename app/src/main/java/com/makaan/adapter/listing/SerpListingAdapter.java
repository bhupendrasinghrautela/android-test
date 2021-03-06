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
import com.makaan.pojo.GroupCluster;
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
    protected ArrayList<GroupCluster> mGroupClusterListings;
    private String mCount;
    private boolean mBuilderSellerPopulated = false;
    private boolean mEventSent;

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks) {
        mContext = context;
        mCallback = callbacks;
        recycleViewMode = RecycleViewMode.DATA;
    }

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks, List<Listing> listings, int requestType) {
        mContext = context;
        mCallback = callbacks;
        setData(listings, requestType);
        recycleViewMode = RecycleViewMode.DATA;
    }

    public SerpListingAdapter(Context context, SerpRequestCallback callbacks, int requestType) {
        mContext = context;
        mCallback = callbacks;
        recycleViewMode = RecycleViewMode.DATA;
        this.mRequestType = requestType & SerpActivity.MASK_LISTING_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        int superItemViewType = super.getItemViewType(position);
        if(superItemViewType == RecycleViewMode.DATA.getValue()) {
            if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                if(position == 1) {
                    if(mRequestType == SerpActivity.TYPE_BUILDER) {
                        superItemViewType = RecycleViewMode.DATA_TYPE_BUILDER.getValue();
                    } else {
                        superItemViewType = RecycleViewMode.DATA_TYPE_SELLER.getValue();
                    }
                } else if(position == 0) {
                    superItemViewType = RecycleViewMode.DATA_TYPE_COUNT.getValue();
                } else {
                    if(position == 2 && (mItems == null || mItems.size() == 0)) {
                        superItemViewType = RecycleViewMode.DATA_TYPE_NO_LISTING.getValue();
                    } else {
                        superItemViewType = RecycleViewMode.DATA_TYPE_LISTING.getValue();
                    }
                }
            } else {
                if(position == 0) {
                    superItemViewType = RecycleViewMode.DATA_TYPE_COUNT.getValue();
                } else {
                    position--;
                    if ((position % 10) == GroupCluster.CLUSTER_POS_IN_SERP_PER_TEN && mGroupClusterListings != null
                            && mGroupClusterListings.size() > (position / 10)) {
                        superItemViewType = RecycleViewMode.DATA_TYPE_CLUSTER.getValue();
                    } else {
                        superItemViewType = RecycleViewMode.DATA_TYPE_LISTING.getValue();
                    }
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
            return mItems.size() + 1;
        } else if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
            if(mItems == null) {
                return 1;
            }
            if(mItems.size() == 0) {
                return 3; // 1 for showing item count, 2 for seller/builder card and 3 for no listing
            }
            return (mItems.size() + 2);
        } else {
            if (mItems == null && mGroupClusterListings == null) {
                return 0;
            } else if (mGroupClusterListings == null || mGroupClusterListings.size() == 0) {
                return (mItems.size() + 1);
            } else if (mItems == null || mItems.size() == 0) {
                return 0;
            } else {
                if(Math.ceil((mItems.size() - GroupCluster.CLUSTER_POS_IN_SERP_PER_TEN) / 10.0f) < mGroupClusterListings.size()) {
                    return ((mItems.size() + 1) + (int)(Math.ceil(mItems.size() - GroupCluster.CLUSTER_POS_IN_SERP_PER_TEN) / 10.0f));
                }
                return ((mItems.size() + 1) + mGroupClusterListings.size());
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
        int type = getItemViewType(position);
        if(type == RecycleViewMode.DATA_TYPE_CLUSTER.getValue()) {
            viewHolder.populateData(mGroupClusterListings.get(position / 10), mCallback, position);
        } else if(type == RecycleViewMode.DATA_TYPE_BUILDER.getValue()
                || type == RecycleViewMode.DATA_TYPE_SELLER.getValue()) {
            if(!mBuilderSellerPopulated) {
                viewHolder.populateData(null, mCallback, position);
                mBuilderSellerPopulated = true;
            }
        } else if(type == RecycleViewMode.DATA_TYPE_NO_LISTING.getValue()) {
            viewHolder.populateData(mContext.getString(R.string.no_content_serp_error), mCallback, position);
        } else {
            if(position == 0) {
                viewHolder.populateData(mCount, mCallback, position);
            } else {
                position--;
                if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
                    position--;
                }
                int extraCount = 0;
                int tens = position / 10;
                int digit = position % 10;
                if (tens >= mGroupClusterListings.size()) {
                    extraCount = mGroupClusterListings.size();
                } else {
                    if (digit > GroupCluster.CLUSTER_POS_IN_SERP_PER_TEN && tens < mGroupClusterListings.size()) {
                        extraCount = tens + 1;
                    } else {
                        extraCount = tens;
                    }
                }
                if(position - extraCount == 5 && !mEventSent) {
                    mEventSent = true;
                    mCallback.trackScroll(mRequestType, position - extraCount);
                }
                viewHolder.populateData(mItems.get(position - extraCount), mCallback, position - extraCount);
            }
        }
    }

    @Override
    public void onBindFooterLoadingViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onBindFooterErrorViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void setData(String count, List<Listing> listings, @Nullable List<GroupListing> groupListings, int requestType) {
        mCount = count;

        if (mGroupClusterListings == null) {
            mGroupClusterListings = new ArrayList<>();
        } else {
            this.mGroupClusterListings.clear();
        }

        if(groupListings != null) {
            mGroupClusterListings.addAll(GroupCluster.getGroupClusters((ArrayList<GroupListing>) groupListings));
        }
        setData(listings, requestType);
    }

    @Override
    public void setData(List<Listing> listings, int requestType) {
        if(this.mItems == null) {
            this.mItems = new ArrayList<Listing>();
        }
        this.mItems.clear();

        mRequestType = requestType & SerpActivity.MASK_LISTING_TYPE;
        if(listings == null) {
            return;
        }

        this.mItems.addAll(listings);
        mBuilderSellerPopulated = false;
        mEventSent = false;
        notifyDataSetChanged();
    }


}
