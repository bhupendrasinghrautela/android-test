package com.makaan.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.makaan.response.listing.Listing;
import com.makaan.ui.adapter.constants.Constant;
import com.makaan.ui.adapter.factory.ListingViewHolderFactory;
import com.makaan.ui.adapter.holder.BaseListingAdapterViewHolder;

import java.util.ArrayList;
import java.util.List;

import pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class ListingAdapter extends RecyclerView.Adapter<BaseListingAdapterViewHolder> {


    private final Context mContext;
    private final ListingAdapterCallbacks mCallbacks;
    List<Listing> listings;
    SparseArray<List<TempClusterItem>> clusterItems;

    public ListingAdapter(Context context, ListingAdapterCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
    }

    public ListingAdapter(Context context, ListingAdapterCallbacks callbacks, List<Listing> listings) {
        mContext = context;
        mCallbacks = callbacks;
        setData((ArrayList<Listing>) listings);
    }

    @Override
    public BaseListingAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ListingViewHolderFactory.createViewHolder(mContext, parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseListingAdapterViewHolder holder, int position) {
        if(clusterItems != null && clusterItems.get(position) != null) {
            holder.populateData(clusterItems.get(position));
        } else {
            if(position > 3) {
                holder.populateData(listings.get(position - 1));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(clusterItems != null && clusterItems.get(position) != null) {
            return Constant.LISTING_ADAPTER_ITEM_TYPE_CLUSTER;
        }
        return Constant.LISTING_ADAPTER_ITEM_TYPE_LISTING;
    }

    @Override
    public int getItemCount() {
        return (listings.size() + clusterItems.size());
    }

    public void setData(ArrayList<Listing> listings) {
        if(this.listings == null) {
            this.listings = new ArrayList<Listing>();
        }
        this.listings.clear();
        if(clusterItems == null) {
            clusterItems = new SparseArray<>();
        }
        if(listings.size() >= 3) {
            List<TempClusterItem> items = new ArrayList<>();
            items.add(new TempClusterItem("\u20B9 40l to \u20B945l", "2 bhk apartment", "sector 2, sohna road", "view 100 listings"));
            items.add(new TempClusterItem("\u20B9 50l to \u20B955l", "3 bhk apartment", "sector 3, sohna road", "view 200 listings"));
            items.add(new TempClusterItem("\u20B9 60l to \u20B965l", "3 bhk apartment", "sector 4, sohna road", "view 300 listings"));
            items.add(new TempClusterItem("\u20B9 70l to \u20B975l", "4 bhk apartment", "sector 5, sohna road", "view 400 listings"));
            clusterItems.append(3, items);
        }
        this.listings.addAll(listings);
        notifyDataSetChanged();
    }

    /**
     * this interface will be implemented by activity/fragment
     * which is housing recycler view which is using this class as its adapter
     */
    public interface ListingAdapterCallbacks {

    }
}
