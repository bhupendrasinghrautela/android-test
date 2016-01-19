package com.makaan.fragment.locality;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.locality.LocalityApartmentsPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 18/1/16.
 */
public class LocalityApartmentsPagerFragment extends Fragment {
    private LocalityApartmentsPagerAdapter mApartmentsPagerAdapter;
    @Bind(R.id.localities_apartments_header)TextView mApartmentsHeader;
    @Bind(R.id.localities_apartments_view_pager)ViewPager mAprtmentsViewPager;

    public LocalityApartmentsPagerFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.locality_apartments_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApartmentsPagerAdapter=new LocalityApartmentsPagerAdapter(getActivity());
        mAprtmentsViewPager.setAdapter(mApartmentsPagerAdapter);
        mAprtmentsViewPager.setClipToPadding(false);
        mAprtmentsViewPager.setPageMargin(30);
        mAprtmentsViewPager.setPadding(0, 0, 60, 0);
        mAprtmentsViewPager.setOffscreenPageLimit(2);
    }

}

