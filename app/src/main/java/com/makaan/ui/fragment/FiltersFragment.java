package com.makaan.ui.fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * To show filter/sort by and map on top of listings
 * Created by rohitgarg on 1/8/16.
 */
public class FiltersFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.filters_fragment_filter_relative_layout)
    RelativeLayout mFilterRelativeLayout;
    @Bind(R.id.filters_fragment_sort_by_relative_layout)
    RelativeLayout mSortByRelativeLayout;
    @Bind(R.id.filters_fragment_map_linear_layout)
    LinearLayout mMapRelativeLayout;

    @Bind(R.id.filters_fragment_filter_setting_text_view)
    TextView mFilterSettingTextView;
    @Bind(R.id.filters_fragment_sort_by_setting_text_view)
    TextView mSortBySettingTextView;


    public static FiltersFragment init() {
        return new FiltersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filters, container, false);
        // bind view to ButterKnife
        ButterKnife.bind(this, view);

        // get listing from parent activity
        Activity activity = getActivity();
        if(activity != null && activity instanceof FiltersFragmentCallbacks) {
            // update that there is one working fragment available for filters
            ((FiltersFragmentCallbacks)activity).updateFiltersFragment(this);

        }
        mFilterRelativeLayout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == mFilterRelativeLayout) {
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            FiltersDialogFragment filterFragment = FiltersDialogFragment.init();
            filterFragment.show(ft, "Filters");
        }
    }

    public interface FiltersFragmentCallbacks {
        void updateFiltersFragment(FiltersFragment filtersFragment);
    }
}
