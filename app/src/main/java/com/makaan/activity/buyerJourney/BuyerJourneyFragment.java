package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.buyerjourney.ClientEventsByGetEvent;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BuyerJourneyFragment extends Fragment {


    private Intent intent;

    private boolean isUserLoggedIn = false;

    @Bind(R.id.tv_search_subtitle)
    TextView mSearchSubTitle;

    @Bind(R.id.tv_shortlist_subtitle)
    TextView mShortListSubTitle;

    @Bind(R.id.tv_site_visit_subtitle)
    TextView mSiteVisitSubTitle;

    @Bind(R.id.tv_search_subtitle)
    TextView mSearchSubtitle;
    @Bind(R.id.tv_shortlist_subtitle)
    TextView mShortlistSubtitle;
    @Bind(R.id.tv_site_visit_subtitle)
    TextView mSiteVisitSubtitle;

    private int mSavedSearchesCount;
    private int mNewMatchesCount;
    private int mClientLeadsCount;
    private int mPhaseId;
    private int mClientEventsCount;

    boolean mSavedSearchesReceived = false;
    boolean mNewSearchesReceived = false;
    boolean mClientEventsReceived = false;
    boolean mClientLeadsReceived = false;

    @OnClick(R.id.ll_search)
    public void onSearchCLick() {

        if (isUserLoggedIn) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SAVE_SEARCH);
        } else {
            intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SEARCH);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
    }

    @OnClick(R.id.ll_shortlist)
    public void onShortlistClick() {
        if (isUserLoggedIn) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SHORTLIST);
        } else {
            intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SHORTLIST);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
    }

    @OnClick(R.id.ll_site_visit)
    public void onSiteVisitClick() {
        //TODO

        if (isUserLoggedIn) {
        /*intent.putExtra(BuyerDashboardActivity.DATA, "homeloan");
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);*/
        } else {
            intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SITE_VISIT);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
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
        intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.HOME_LOAN);
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_unit_booking)
    public void onUnitBookingClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.UNIT_BOOK);
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_possession)
    public void onPossessionClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.POSSESSION);

        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
    }

    @OnClick(R.id.ll_registration)
    public void onRegistrationClick() {
        intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.REGISTRATION);
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

        isUserLoggedIn = CookiePreferences.isUserLoggedIn(getActivity());
        if(isUserLoggedIn) {
            setSubTitle();
        }

        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearches();
        ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadsActivity();
        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearchesNewMatches();
        ((ClientEventsService) MakaanServiceFactory.getInstance().getService(ClientEventsService.class)).getClientEvents(1);
        mSavedSearchesReceived = false;
        mNewSearchesReceived = true; // TODO need to implement this
        mClientEventsReceived = false;
        mClientLeadsReceived = false;

        return view;
    }

    /**
     *set subtitle if user is logged in
     */
    private void setSubTitle() {

        mSearchSubTitle.setText("4 saved searches | 50 new matches");
        mShortListSubTitle.setText("visit 12 shortlisted properties");
        mSiteVisitSubTitle.setText("2 upcoming site visit");

    }


    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        if(null == saveSearchGetEvent || null != saveSearchGetEvent.error){
            //TODO handle error
            return;
        }
        mSavedSearchesCount = saveSearchGetEvent.saveSearchArrayList.size();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent){
        if(null == clientLeadsByGetEvent || null != clientLeadsByGetEvent.error){
            //TODO handle error
            return;
        }
        mClientLeadsCount = clientLeadsByGetEvent.totalCount;
        if(clientLeadsByGetEvent.results != null && clientLeadsByGetEvent.results.size() >= 0) {
            if(clientLeadsByGetEvent.results.get(0).clientActivity != null) {
                mPhaseId = clientLeadsByGetEvent.results.get(0).clientActivity.phaseId;
            }
        }
    }

    @Subscribe
    public void onResults(ClientEventsByGetEvent clientEventsByGetEvent){
        if(null == clientEventsByGetEvent || null != clientEventsByGetEvent.error){
            //TODO handle error
            return;
        }
        mClientEventsCount = clientEventsByGetEvent.totalCount;
    }

    void updateUi() {
        if(mClientEventsReceived && mClientLeadsReceived && mNewSearchesReceived && mSavedSearchesReceived) {
            if(mSavedSearchesCount + mNewMatchesCount > 0) {
                mSearchSubtitle.setText(String.format("%d saved searches | %d new matches", mSavedSearchesCount, mNewMatchesCount));
            } else {
                mSearchSubtitle.setText(getResources().getString(R.string.search_sub_title));
            }
            mSearchSubtitle.setText(String.format("%d shortlisted properties", mClientLeadsCount));
            mSearchSubtitle.setText(String.format("%d upcoming site visits", mClientEventsCount));
        }
    }
}
