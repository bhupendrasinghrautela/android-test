package com.makaan.activity.shortlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortlistEnquiredFragment extends MakaanBaseFragment {
    @Bind(R.id.enquired_recycler_view)
    RecyclerView enquiredRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_enquired;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        enquiredRecyclerView.setLayoutManager(mLayoutManager);
       // enquiredRecyclerView.setAdapter(new ShortListEnquiredAdapter(getActivity()));
    }
}
