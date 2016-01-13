package com.makaan.fragment.listing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.view.TempClusterItemView;

import com.makaan.pojo.TempClusterItem;

public class HorizontalScrollPageFragment extends Fragment {

    public static final String IS_CHILD_SERP_CLUSTER_VIEW = "isChildSerpClusterView";
    private ViewGroup rootView;
    private Object o;
    private boolean isChildSerpClusterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(IS_CHILD_SERP_CLUSTER_VIEW)) {
            isChildSerpClusterView = bundle.getBoolean(IS_CHILD_SERP_CLUSTER_VIEW);
        }
        if(isChildSerpClusterView) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.cluster_item_view, container, false);
            if (o != null && o instanceof TempClusterItem) {
                ((TempClusterItemView) rootView).populateView((TempClusterItem) o);
            }
        } else {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.cluster_item_view, container, false);
            if (o != null && o instanceof TempClusterItem) {
                ((TempClusterItemView) rootView).populateView((TempClusterItem) o);
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

    public void populateData(Object o) {
        this.o = o;
    }
}