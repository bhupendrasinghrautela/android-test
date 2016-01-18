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
import com.makaan.adapter.locality.TopAgentsPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by proptiger on 17/1/16.
 */
public class TopAgentsPagerFragment extends Fragment{

    private TopAgentsPagerAdapter mAgentsPagerAdapter;
    @Bind(R.id.top_agents_header)TextView mAgentsHeader;
    @Bind(R.id.top_agents_view_pager)ViewPager mTopAgentsViewPager;

    public TopAgentsPagerFragment(){

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
        mAgentsPagerAdapter=new TopAgentsPagerAdapter(getActivity());
        mTopAgentsViewPager.setAdapter(mAgentsPagerAdapter);
        mTopAgentsViewPager.setClipToPadding(false);
        mTopAgentsViewPager.setPageMargin(30);
        mTopAgentsViewPager.setPadding(0, 0, 60, 0);
        mTopAgentsViewPager.setOffscreenPageLimit(2);
    }

}
