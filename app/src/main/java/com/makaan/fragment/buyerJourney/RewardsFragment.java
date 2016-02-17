package com.makaan.fragment.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.OnClick;

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

    @OnClick(R.id.fragment_get_rewards_button)
    void onGetRewardsClicked(View view) {
        if(getActivity() instanceof BuyerDashboardCallbacks) {
            ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CLIENT_LEADS, true, null, "cashback request", null);
        }
    }
}
