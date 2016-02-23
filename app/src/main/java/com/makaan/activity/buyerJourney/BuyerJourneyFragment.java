package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.buyerjourney.ClientEventsByGetEvent;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.service.WishListService;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;


public class BuyerJourneyFragment extends MakaanBaseFragment {


    private Intent intent;

    private boolean isUserLoggedIn = false;

    @Bind(R.id.tv_search_subtitle)
    TextView mSearchSubTitle;

    @Bind(R.id.tv_shortlist_subtitle)
    TextView mShortListSubTitle;

    @Bind(R.id.tv_site_visit_subtitle)
    TextView mSiteVisitSubTitle;

    private int mSavedSearchesCount;
    private int mNewMatchesCount;
    private int mClientLeadsCount;
    private int mPhaseId;
    private int mClientEventsCount;

    boolean mSavedSearchesReceived = false;
    boolean mNewSearchesReceived = false;
    boolean mClientEventsReceived = false;
    boolean mClientLeadsReceived = false;
    boolean mWishListsReceived = false;
    private int mWishlistCount;


    private View[] mViews = new View[3];

    @OnClick(R.id.ll_search)
    public void onSearchCLick() {

        if (isUserLoggedIn && (mSavedSearchesCount + mNewMatchesCount > 0)) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SAVE_SEARCH);
        } else {
            intent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SEARCH);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
    }

    @OnClick(R.id.ll_shortlist)
    public void onShortlistClick() {
        if (isUserLoggedIn && (mClientLeadsCount + mWishlistCount > 0)) {
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
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SITE_VISIT);
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
    protected int getContentViewId() {
        return R.layout.fragment_buyer_journey;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        intent = new Intent(getActivity(), BuyerDashboardActivity.class);

        isUserLoggedIn = CookiePreferences.isUserLoggedIn(getActivity());
        if(isUserLoggedIn) {
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearches();
            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadsActivity();
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearchesNewMatches();
            ((ClientEventsService) MakaanServiceFactory.getInstance().getService(ClientEventsService.class)).getClientEvents(1);
            ((WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class)).get();
            mSavedSearchesReceived = false;
            mNewSearchesReceived = true; // TODO need to implement this
            mClientEventsReceived = false;
            mClientLeadsReceived = false;
            mWishListsReceived = false;
        }

        mViews[0] = view.findViewById(R.id.ll_search);
        mViews[1] = view.findViewById(R.id.ll_shortlist);
        mViews[2] = view.findViewById(R.id.ll_site_visit);
        return view;
    }


    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        if(null == saveSearchGetEvent || null != saveSearchGetEvent.error){
            //TODO handle error
            return;
        }
        mSavedSearchesCount = saveSearchGetEvent.saveSearchArrayList.size();
        mSavedSearchesReceived = true;
        updateUi();
    }

    @Subscribe
    public void wishListResponse(WishListResultEvent wishListResultEvent){
        if(wishListResultEvent == null || wishListResultEvent.error != null) {
            // TODO handle error
            return;
        }
        mWishlistCount = wishListResultEvent.wishListResponse.totalCount;
        mWishListsReceived = true;
        updateUi();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent){
        if(null == clientLeadsByGetEvent || null != clientLeadsByGetEvent.error){
            //TODO handle error
            return;
        }
        mClientLeadsCount = clientLeadsByGetEvent.totalCount;
        if(clientLeadsByGetEvent.results != null && clientLeadsByGetEvent.results.size() > 0) {
            if(clientLeadsByGetEvent.results.get(0).clientActivity != null) {
                mPhaseId = clientLeadsByGetEvent.results.get(0).clientActivity.phaseId;
            }
        }
        if(mPhaseId > 0) {
            updateStage(mPhaseId - 1);
        }
        mClientLeadsReceived = true;
        updateUi();
    }

    private void updateStage(int i) {
        if(i < mViews.length) {
            mViews[i].findViewById(R.id.iv_view).setVisibility(View.VISIBLE);
            mViews[i].findViewById(R.id.iv_stage).setVisibility(View.INVISIBLE);
        }

        for(int j = 0; j < i; j++) {
            mViews[j].findViewById(R.id.iv_view).setVisibility(View.INVISIBLE);
            mViews[j].findViewById(R.id.iv_stage).setVisibility(View.VISIBLE);
            ((ImageView)mViews[j].findViewById(R.id.iv_stage)).setImageResource(R.drawable.check_tick_red);
        }
        for(int j = i + 1; j < mViews.length; j++) {
            mViews[j].findViewById(R.id.iv_view).setVisibility(View.INVISIBLE);
            mViews[j].findViewById(R.id.iv_stage).setVisibility(View.VISIBLE);
            ((ImageView)mViews[j].findViewById(R.id.iv_stage)).setImageResource(R.drawable.arrow_right_small);
        }
    }

    @Subscribe
    public void onResults(ClientEventsByGetEvent clientEventsByGetEvent){
        if(null == clientEventsByGetEvent || null != clientEventsByGetEvent.error){
            //TODO handle error
            return;
        }
        mClientEventsCount = clientEventsByGetEvent.totalCount;
        mClientEventsReceived = true;
        updateUi();
    }

    void updateUi() {
        if(mClientEventsReceived && mClientLeadsReceived && mNewSearchesReceived && mSavedSearchesReceived && mWishListsReceived) {
            if(mSavedSearchesCount + mNewMatchesCount > 0) {
                mSearchSubTitle.setText(String.format("%d saved searches | %d new matches", mSavedSearchesCount, mNewMatchesCount));
            } else {
                mSearchSubTitle.setText(getResources().getString(R.string.search_sub_title));
            }
            if((mClientLeadsCount + mWishlistCount) > 0) {
                mShortListSubTitle.setText(String.format("%d shortlisted properties", (mClientLeadsCount + mWishlistCount)));
            } else {
                mShortListSubTitle.setText(getResources().getString(R.string.shortlist_sub_title));
            }
            if(mClientEventsCount > 0) {
                mSiteVisitSubTitle.setText(String.format("%d upcoming site visits", mClientEventsCount));
            } else {
                mSiteVisitSubTitle.setText(getResources().getString(R.string.site_visit_sub_title));
            }
        }
    }
}
