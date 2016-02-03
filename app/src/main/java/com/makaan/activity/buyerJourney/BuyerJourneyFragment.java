package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class BuyerJourneyFragment extends Fragment {


    private Intent intent;

    @OnClick(R.id.ll_search)
    public void onSearchCLick() {
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SAVE_SEARCH);
    }

    @OnClick(R.id.ll_shortlist)
    public void onShortlistClick() {
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SHORTLIST);
    }

    @OnClick(R.id.ll_site_visit)
    public void onSiteVisitClick() {
        //TODO
        /*intent.putExtra(BuyerDashboardActivity.DATA, "homeloan");
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);*/
    }


    @OnClick(R.id.button_get_rewards)
    public void onGetRewardsClick() {
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_REWARDS);
    }


    /**
     * below are all webview layout onClicks
     */
    @OnClick(R.id.ll_home_loan)
    public void onHomeLoanClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, "homeloan");
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_unit_booking)
    public void onUnitBookingClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, "unitbooking");
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_possession)
    public void onPossessionClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, "possession");

        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_registration)
    public void onRegistrationClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, "registration");
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }


    private void onViewClick(int fragmentType) {
        intent.putExtra(BuyerDashboardActivity.TYPE, fragmentType);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_journey, container, false);
        ButterKnife.bind(this, view);
        intent = new Intent(getActivity(), BuyerDashboardActivity.class);
        return view;
    }
}
