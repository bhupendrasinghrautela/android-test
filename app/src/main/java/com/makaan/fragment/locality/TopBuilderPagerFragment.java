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
import com.makaan.adapter.locality.TopBuilderPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 17/1/16.
 */
public class TopBuilderPagerFragment extends Fragment {

    private TopBuilderPagerAdapter mBuilderPagerAdapter;
    @Bind(R.id.top_builder_header)TextView mBuilderHeader;
    @Bind(R.id.top_builder_view_pager)ViewPager mBuilderViewPager;

    public  TopBuilderPagerFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.nearby_localities_pager_fragment_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBuilderPagerAdapter=new TopBuilderPagerAdapter(getActivity());
        mBuilderViewPager.setAdapter(mBuilderPagerAdapter);
        mBuilderViewPager.setClipToPadding(false);
        mBuilderViewPager.setPageMargin(30);
        mBuilderViewPager.setPadding(0, 0, 60, 0);
        mBuilderViewPager.setOffscreenPageLimit(2);
    }

}
