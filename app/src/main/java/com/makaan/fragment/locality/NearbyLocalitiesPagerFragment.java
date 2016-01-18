package com.makaan.fragment.locality;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.locality.NearbyLocalitiesPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 17/1/16.
 */
public class NearbyLocalitiesPagerFragment extends Fragment {
    private NearbyLocalitiesPagerAdapter mLocalityPagerAdapter;
    @Bind(R.id.nearby_localities_header)TextView mLocalityHeader;
    @Bind(R.id.nearby_localities_view_pager)ViewPager mLocalityViewPager;

    public NearbyLocalitiesPagerFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.nearby_localities_pager_fragment_layout, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocalityPagerAdapter=new NearbyLocalitiesPagerAdapter(getActivity());
        mLocalityViewPager.setAdapter(mLocalityPagerAdapter);
        mLocalityViewPager.setClipToPadding(false);
        mLocalityViewPager.setPageMargin(30);
        mLocalityViewPager.setPadding(0, 0, 60, 0);
        mLocalityViewPager.setOffscreenPageLimit(2);
    }

}
