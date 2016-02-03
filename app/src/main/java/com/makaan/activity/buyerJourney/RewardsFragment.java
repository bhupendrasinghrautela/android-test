package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.shortlist.ShortlistPagerAdapter;
import com.makaan.fragment.MakaanBaseFragment;

public class RewardsFragment extends MakaanBaseFragment {

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_get_rewards;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }
}
