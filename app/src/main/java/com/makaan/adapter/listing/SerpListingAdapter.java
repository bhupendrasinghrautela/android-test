package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.PaginatedBaseAdapter;
import com.makaan.adapter.RecycleViewMode;
import com.makaan.response.listing.Listing;
import com.makaan.ui.listing.ListingViewHolderFactory;
import com.makaan.ui.listing.BaseListingAdapterViewHolder;

import java.util.ArrayList;
import java.util.List;

import com.makaan.pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpListingAdapter extends PaginatedBaseAdapter<Listing> {


    private final Context mContext;
    private final SerpRequestCallback mCallback;
    SparseArray<List<TempClusterItem>> clusterItems;
    private int mRequestType = SerpActivity.TYPE_UNKNOWN;

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
                if(clusterItems != null && clusterItems.get(position) != null) {
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
            return mItems.size();
        } else if(mRequestType == SerpActivity.TYPE_BUILDER || mRequestType == SerpActivity.TYPE_SELLER) {
            return (mItems.size() + 1);
        } else {
            return (mItems.size() + clusterItems.size());
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
        if(clusterItems != null && clusterItems.get(position) != null) {
            viewHolder.populateData(clusterItems.get(position), mCallback);
        } else {
            if(mRequestType == SerpActivity.TYPE_BUILDER) {
                if(position == 0) {
                    viewHolder.populateData(null, mCallback);
                } else {
                    viewHolder.populateData(mItems.get(position - 1), mCallback);
                }
            } else {
                if (mRequestType != SerpActivity.TYPE_CLUSTER) {
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
        if(mRequestType == SerpActivity.TYPE_UNKNOWN || mRequestType == SerpActivity.TYPE_SEARCH) {
            if (clusterItems == null) {
                clusterItems = new SparseArray<>();
            } else {
                this.clusterItems.clear();
            }
            if(listings.size() >= 3) {
                List<TempClusterItem> items = new ArrayList<>();
                items.add(new TempClusterItem("\u20B9 40l to \u20B945l", "2 bhk apartment", "sector 2, sohna road", "100 listings"));
                items.add(new TempClusterItem("\u20B9 50l to \u20B955l", "3 bhk apartment", "sector 3, sohna road", "200 listings"));
                items.add(new TempClusterItem("\u20B9 60l to \u20B965l", "3 bhk apartment", "sector 4, sohna road", "300 listings"));
                items.add(new TempClusterItem("\u20B9 70l to \u20B975l", "4 bhk apartment", "sector 5, sohna road", "400 listings"));
                clusterItems.append(3, items);
            }
        }
        this.mItems.addAll(listings);
        notifyDataSetChanged();
    }
}
