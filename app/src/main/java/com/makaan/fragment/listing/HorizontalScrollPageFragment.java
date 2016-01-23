package com.makaan.fragment.listing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.response.listing.GroupListing;
import com.makaan.ui.listing.ChildSerpGroupListingItemView;
import com.makaan.ui.listing.GroupListingItemView;

public class HorizontalScrollPageFragment extends Fragment {

    public static final String IS_CHILD_SERP_CLUSTER_VIEW = "isChildSerpClusterView";
    private ViewGroup rootView;
    private Object obj;
    private boolean isChildSerpClusterView;
    private SerpRequestCallback mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(IS_CHILD_SERP_CLUSTER_VIEW)) {
            isChildSerpClusterView = bundle.getBoolean(IS_CHILD_SERP_CLUSTER_VIEW);
        }
        if(isChildSerpClusterView) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.child_serp_cluster_item_view, container, false);
            if (obj != null && obj instanceof GroupListing) {
                ((ChildSerpGroupListingItemView) rootView).populateView((GroupListing) obj, mCallback);
            }
        } else {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.cluster_item_view, container, false);
            if (obj != null && obj instanceof GroupListing) {
                ((GroupListingItemView) rootView).populateView((GroupListing) obj, mCallback);
            }
        }

        return rootView;
    }

    public static HorizontalScrollPageFragment init(boolean isChildSerpClusterAdapter) {
        // create listing fragment to show listing of the request we are receiving
        HorizontalScrollPageFragment fragment = new HorizontalScrollPageFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_CHILD_SERP_CLUSTER_VIEW, isChildSerpClusterAdapter);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void populateData(Object o, SerpRequestCallback callback) {
        this.obj = o;
        this.mCallback =callback;
    }
}