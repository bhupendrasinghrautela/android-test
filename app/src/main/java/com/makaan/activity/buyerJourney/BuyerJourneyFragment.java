package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.buyerjourney.ClientEventsByGetEvent;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.buyerjourney.NewMatchesGetEvent;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.service.WishListService;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;


public class BuyerJourneyFragment extends MakaanBaseFragment {


    private Intent mIntent;

    private boolean mIsUserLoggedIn = false;

    @Bind(R.id.tv_search_subtitle)
    TextView mSearchSubTitle;

    @Bind(R.id.tv_shortlist_subtitle)
    TextView mShortListSubTitle;

    @Bind(R.id.tv_site_visit_subtitle)
    TextView mSiteVisitSubTitle;

    @Bind(R.id.iv_find_joy)
    ImageView mJoyImageView;
    @Bind(R.id.iv_booking)
    ImageView mBookingImageView;

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
    private ArrayList<SaveSearch> savedSearches;

    @OnClick(R.id.ll_search)
    public void onSearchCLick() {

        if (mIsUserLoggedIn && (mSavedSearchesCount + mNewMatchesCount > 0)) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SAVE_SEARCH);
        } else {
            mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SEARCH);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.ll_shortlist)
    public void onShortlistClick() {
        if (mIsUserLoggedIn && (mClientLeadsCount + mWishlistCount > 0)) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SHORTLIST);
        } else {
            mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SHORTLIST);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.ll_site_visit)
    public void onSiteVisitClick() {
        if (mIsUserLoggedIn && mClientEventsReceived && mClientEventsCount > 0) {
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_SITE_VISIT);
        } else {
            mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.SITE_VISIT);
            onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        }
        AppBus.getInstance().unregister(this);
    }


    @OnClick(R.id.button_get_rewards)
    public void onGetRewardsClick() {
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_REWARDS);
        AppBus.getInstance().unregister(this);
    }


    /**
     * below are all webview layout onClicks
     */
    @OnClick(R.id.ll_home_loan)
    public void onHomeLoanClick() {
        mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.HOME_LOAN);
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.ll_unit_booking)
    public void onUnitBookingClick() {
        mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.UNIT_BOOK);
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.ll_possession)
    public void onPossessionClick() {
        mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.POSSESSION);

        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.ll_registration)
    public void onRegistrationClick() {
        mIntent.putExtra(BuyerDashboardActivity.DATA, BlogContentFragment.REGISTRATION);
        onViewClick(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT);
        AppBus.getInstance().unregister(this);
    }


    private void onViewClick(int fragmentType) {
        mIntent.putExtra(BuyerDashboardActivity.TYPE, fragmentType);
        startActivity(mIntent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_buyer_journey;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mIntent = new Intent(getActivity(), BuyerDashboardActivity.class);

        mIsUserLoggedIn = CookiePreferences.isUserLoggedIn(getActivity());
        setupData();

        if(view != null) {
            mViews[0] = view.findViewById(R.id.ll_search);
            mViews[1] = view.findViewById(R.id.ll_shortlist);
            mViews[2] = view.findViewById(R.id.ll_site_visit);
        }
        return view;
    }


    private void setupData() {
        if(mIsUserLoggedIn) {
            savedSearches = MasterDataCache.getInstance().getSavedSearch();
            if(savedSearches != null && savedSearches.size() > 0) {
                mSavedSearchesCount = savedSearches.size();
                mSavedSearchesReceived = true;
            } else {
                ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearches();
                mSavedSearchesReceived = false;
            }

            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestpropertyRequirements();
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearchesNewMatches();
            ((ClientEventsService) MakaanServiceFactory.getInstance().getService(ClientEventsService.class)).getClientEvents(1);
            ((WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class)).get();

            mNewSearchesReceived = false;
            mClientEventsReceived = false;
            mClientLeadsReceived = false;
            mWishListsReceived = false;
            showProgress();
        } else {
            mSavedSearchesCount = 0;
            mNewMatchesCount = 0;
            mClientLeadsCount = 0;
            mPhaseId = 0;
            mClientEventsCount = 0;

            mSearchSubTitle.setText(getResources().getString(R.string.search_sub_title));
            mShortListSubTitle.setText(getResources().getString(R.string.shortlist_sub_title));
            mSiteVisitSubTitle.setText(getResources().getString(R.string.site_visit_sub_title));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AppBus.getInstance().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isUserLoggedIn = CookiePreferences.isUserLoggedIn(getActivity());
        if(isUserLoggedIn != mIsUserLoggedIn) {
            mIsUserLoggedIn = isUserLoggedIn;
            setupData();
        }
        savedSearches = MasterDataCache.getInstance().getSavedSearch();
        if(mIsUserLoggedIn && savedSearches != null && savedSearches.size() == 0) {
            mSavedSearchesCount = savedSearches.size();
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).getSavedSearches();
            mSavedSearchesReceived = false;
        }
    }

    @Subscribe
    public void onResults(SaveSearchGetEvent saveSearchGetEvent){
        if(!isVisible()) {
            return;
        }
        if(null == saveSearchGetEvent || null != saveSearchGetEvent.error){
            if(saveSearchGetEvent != null && !TextUtils.isEmpty(saveSearchGetEvent.error.msg)) {
                showNoResults(saveSearchGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        mSavedSearchesCount = saveSearchGetEvent.saveSearchArrayList.size();
        mSavedSearchesReceived = true;
        updateUi();
    }


    @Subscribe
    public void onResults(NewMatchesGetEvent newMatchesGetEvent){
        if(!isVisible()) {
            return;
        }
        if(null == newMatchesGetEvent || null != newMatchesGetEvent.error){
            /*if(newMatchesGetEvent != null && !TextUtils.isEmpty(newMatchesGetEvent.error.msg)) {
                showNoResults(newMatchesGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;*/

            mNewMatchesCount = 0;
            mNewSearchesReceived = true;
            updateUi();
            return;
        }
        mNewMatchesCount = newMatchesGetEvent.totalCount;
        mNewSearchesReceived = true;
        updateUi();
    }

    @Subscribe
    public void wishListResponse(WishListResultEvent wishListResultEvent){
        if(!isVisible()) {
            return;
        }
        if(wishListResultEvent == null || wishListResultEvent.error != null) {
            if(wishListResultEvent != null && !TextUtils.isEmpty(wishListResultEvent.error.msg)) {
                showNoResults(wishListResultEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        mWishlistCount = wishListResultEvent.wishListResponse.totalCount;
        mWishListsReceived = true;
        updateUi();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent){
        if(!isVisible()) {
            return;
        }
        if(null == clientLeadsByGetEvent || null != clientLeadsByGetEvent.error){
            if(clientLeadsByGetEvent != null && !TextUtils.isEmpty(clientLeadsByGetEvent.error.msg)) {
                showNoResults(clientLeadsByGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        mClientLeadsCount = clientLeadsByGetEvent.totalCount;
        if(clientLeadsByGetEvent.results != null && clientLeadsByGetEvent.results.size() > 0) {
            if(clientLeadsByGetEvent.results.get(0).clientActivity != null) {
                mPhaseId = clientLeadsByGetEvent.results.get(0).clientActivity.phaseId;
            }
        }
        if(mPhaseId >= 0) {
            updateStage(mPhaseId);
        }
        mClientLeadsReceived = true;
        updateUi();
    }

    private void updateStage(int i) {
        if(i < mViews.length) {
            mViews[i].findViewById(R.id.iv_view).setVisibility(View.VISIBLE);
            mViews[i].findViewById(R.id.iv_stage).setVisibility(View.INVISIBLE);
        }

        if(i >= LeadPhaseConstants.LEAD_PHASE_BOOKING) {
            Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("journey_makaan");
            if (bitmap == null) {
                int id = R.drawable.journey_makaan;
                bitmap = BitmapFactory.decodeResource(getResources(), id);
                MakaanBuyerApplication.bitmapCache.putBitmap("journey_makaan", bitmap);
            }

            mJoyImageView.setImageBitmap(bitmap);
            if(i == LeadPhaseConstants.LEAD_PHASE_REGISTRATION) {
                mBookingImageView.setImageBitmap(bitmap);
            }
        }

        for(int j = 0; j < i && j < mViews.length; j++) {
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
        if(!isVisible()) {
            return;
        }
        if(null == clientEventsByGetEvent || null != clientEventsByGetEvent.error){
            if(clientEventsByGetEvent != null && !TextUtils.isEmpty(clientEventsByGetEvent.error.msg)) {
                showNoResults(clientEventsByGetEvent.error.msg);
            } else {
                showNoResults();
            }
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
            showContent();
        }
    }
}
