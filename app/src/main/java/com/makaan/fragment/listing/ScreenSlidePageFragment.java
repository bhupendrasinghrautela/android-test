package com.makaan.fragment.listing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.ui.view.TempClusterItemView;

import com.makaan.pojo.TempClusterItem;

public class ScreenSlidePageFragment extends Fragment {

    private ViewGroup rootView;
    private Object o;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            rootView = (ViewGroup) inflater.inflate(
                    R.layout.cluster_item_view, container, false);
            if(o != null && o instanceof TempClusterItem) {
                ((TempClusterItemView)rootView).populateView((TempClusterItem)o);
            }

            return rootView;
        }

        public static ScreenSlidePageFragment init(int position) {
            // create listing fragment to show listing of the request we are receiving
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
            return fragment;
        }

    public void populateData(Object o) {
        this.o = o;
    }
}