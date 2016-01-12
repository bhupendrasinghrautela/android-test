package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.makaan.constants.UIConstants;
import com.makaan.fragment.listing.SerpListFragment;
import com.makaan.response.listing.Listing;
import com.makaan.ui.listing.ListingViewHolderFactory;
import com.makaan.ui.listing.BaseListingAdapterViewHolder;

import java.util.ArrayList;
import java.util.List;

import com.makaan.pojo.TempClusterItem;

/**
 * Created by rohitgarg on 1/6/16.
 */
public class SerpListingAdapter extends RecyclerView.Adapter<BaseListingAdapterViewHolder> {


    private final Context mContext;
    private final ListingAdapterCallbacks mCallbacks;
    private final boolean mIsChildSerp;
    List<Listing> mListings;
    SparseArray<List<TempClusterItem>> clusterItems;

    public SerpListingAdapter(Context context, ListingAdapterCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;
        mIsChildSerp = false;
    }

    public SerpListingAdapter(Context context, ListingAdapterCallbacks callbacks, List<Listing> listings) {
        mContext = context;
        mCallbacks = callbacks;
        setData((ArrayList<Listing>) listings);
        mIsChildSerp = false;
    }

    public SerpListingAdapter(Context context, ListingAdapterCallbacks callbacks, boolean isChildSerp) {
        mContext = context;
        mCallbacks = callbacks;
        mIsChildSerp = isChildSerp;
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
            if(!mIsChildSerp) {
                if (position > 3) {
                    holder.populateData(mListings.get(position - 1));
                } else {
                    holder.populateData(mListings.get(position));
                }
            } else {
                holder.populateData(mListings.get(position));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(clusterItems != null && clusterItems.get(position) != null) {
            return UIConstants.LISTING_ADAPTER_ITEM_TYPE_CLUSTER;
        }
        return UIConstants.LISTING_ADAPTER_ITEM_TYPE_LISTING;
    }

    @Override
    public int getItemCount() {
        if(mIsChildSerp) {
            return mListings.size();
        } else {
            return (mListings.size() + clusterItems.size());
        }
    }

    public void setData(ArrayList<Listing> listings) {
        if(this.mListings == null) {
            this.mListings = new ArrayList<Listing>();
        }
        this.mListings.clear();
        if(!mIsChildSerp) {
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
        this.mListings.addAll(listings);
        notifyDataSetChanged();
    }

    /**
     * this interface will be implemented by activity/fragment
     * which is housing recycler view which is using this class as its adapter
     */
    public interface ListingAdapterCallbacks {

    }
}
