package com.makaan.activity.buyerJourney;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.shortlist.ShortListFragment;
import com.makaan.activity.sitevisit.SiteVisitFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.fragment.WebViewFragment;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.fragment.buyerJourney.ClientCompanyLeadFragment;
import com.makaan.fragment.buyerJourney.ClientCompanyLeadsAddProperty;
import com.makaan.fragment.buyerJourney.ClientLeadsFragment;
import com.makaan.fragment.buyerJourney.ReviewAgentFragment;
import com.makaan.fragment.buyerJourney.RewardsFragment;
import com.makaan.fragment.buyerJourney.SaveSearchFragment;
import com.makaan.fragment.buyerJourney.UploadDocumentsFragment;
import com.makaan.fragment.pyr.ThankYouScreenFragment;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.request.buyerjourney.PhaseChange;
import com.makaan.service.ClientEventsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.segment.analytics.Properties;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/29/16.
 */
public class BuyerDashboardActivity extends MakaanFragmentActivity implements BuyerDashboardCallbacks {
    @Bind(R.id.activity_base_buyer_journey_layout_type_text_view)
    TextView mTitleTextView;

    public static final String KEY_PHASE_ID = "phaseId";

    public static final String TYPE = "type";
    public static final String DATA = "data";

    public static final int LOAD_FRAGMENT_WEB_VIEW = 1;
    public static final int LOAD_FRAGMENT_CONTENT = 2;
    public static final int LOAD_FRAGMENT_SHORTLIST = 3;
    public static final int LOAD_FRAGMENT_REWARDS = 4;
    public static final int LOAD_FRAGMENT_SAVE_SEARCH = 5;
    public static final int LOAD_FRAGMENT_CLIENT_LEADS = 6;
    public static final int LOAD_FRAGMENT_CLIENT_COMPANY_LEAD = 7;
    public static final int LOAD_FRAGMENT_CLIENT_COMPANY_LEAD_ADD_PROPERTY = 8;
    public static final int LOAD_FRAGMENT_REVIEW_AGENT = 9;
    public static final int LOAD_FRAGMENT_UPLOAD_DOCUMENTS = 10;
    public static final int LOAD_FRAGMENT_SITE_VISIT = 11;
    public static final int LOAD_THANK_YOU_FRAGMENT = 12;
    private boolean mOnlySellerRating;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base_buyer_journey;
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent.hasExtra(TYPE)) {
            int type = intent.getIntExtra(TYPE, LOAD_FRAGMENT_CONTENT);
            sendScreenNameEvent(type);
            switch (type) {
                case LOAD_FRAGMENT_CONTENT:
                    BlogContentFragment fragment = new BlogContentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(BlogContentFragment.TYPE, intent.getStringExtra(DATA));
                    fragment.setArguments(bundle);
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, false);
                    break;
                case LOAD_FRAGMENT_SHORTLIST:
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new ShortListFragment(), false);
                    setTitle("shortlist");
                    break;
                case LOAD_FRAGMENT_SAVE_SEARCH:
                    if(MasterDataCache.getInstance().getSavedSearch() == null) {
                        ((SaveSearchService) (MakaanServiceFactory.getInstance().getService(SaveSearchService.class))).getSavedSearches();
                    }
                    SaveSearchFragment fragment1 = new SaveSearchFragment();
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment1, false);
                    fragment1.setData(this);
                    setTitle("save searches");
                    break;
                case LOAD_FRAGMENT_REWARDS:
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new RewardsFragment(), false);
                    setTitle("cashback request");
                    break;
                case LOAD_FRAGMENT_SITE_VISIT:
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new SiteVisitFragment(), false);
                    setTitle("site visits");
                    break;
                default:
                    break;
            }
        } else if(intent.hasExtra(KEY_PHASE_ID)) {
            int phaseId = intent.getIntExtra(KEY_PHASE_ID, 0);
            switch (phaseId) {
                case 1:
                    mOnlySellerRating = true;
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new ClientLeadsFragment(), false);
                    setTitle("seller feedback");
                    break;
                case 2:
                    mOnlySellerRating = false;
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, new RewardsFragment(), false);
                    setTitle("cashback request");
                    break;
            }
        }
    }

    @Override
    public void loadFragment(int type, boolean shouldAddToBackStack, Bundle data, String title, Object obj) {
        switch (type) {
            case LOAD_FRAGMENT_WEB_VIEW: {
                WebViewFragment fragment = new WebViewFragment();
                if (data != null) {
                    fragment.setArguments(data);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_CLIENT_LEADS: {
                ClientLeadsFragment fragment = new ClientLeadsFragment();
                if (data != null) {
                    fragment.setArguments(data);
                }
                if(obj != null) {
                    fragment.setData(obj);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_CLIENT_COMPANY_LEAD: {
                ClientCompanyLeadFragment fragment = new ClientCompanyLeadFragment();
                if (data != null) {
                    fragment.setArguments(data);
                }
                if(obj != null) {
                    fragment.setData(obj);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_CLIENT_COMPANY_LEAD_ADD_PROPERTY: {
                ClientCompanyLeadsAddProperty fragment = new ClientCompanyLeadsAddProperty();
                if (data != null) {
                    fragment.setArguments(data);
                }
                if(obj != null) {
                    fragment.setData(obj);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_REVIEW_AGENT: {
                ReviewAgentFragment fragment = new ReviewAgentFragment();
                if (data != null) {
                    fragment.setArguments(data);
                }
                if(obj != null) {
                    fragment.setData(obj);
                }
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_UPLOAD_DOCUMENTS: {
                if(!mOnlySellerRating) {
                    UploadDocumentsFragment fragment = new UploadDocumentsFragment();
                    if (data != null) {
                        fragment.setArguments(data);
                    }
                    if (obj != null) {
                        fragment.setData(obj);
                    }
                    initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                } else {
                    if(obj != null) {
                        ClientCompanyLeadFragment.ClientCompanyLeadsObject leadsObject = (ClientCompanyLeadFragment.ClientCompanyLeadsObject)obj;
                        if(leadsObject.clientLeadObject != null && leadsObject.clientLeadObject.clientLead != null
                                && leadsObject.clientLeadObject.clientLead.id != null) {
                            PhaseChange change = new PhaseChange();
                            change.agentId = leadsObject.clientLeadObject.clientLead.companyId;
                            change.eventTypeId = LeadPhaseConstants.LEAD_EVENT_SITE_VIST_DONE;
                            change.performTime = new Date().getTime();
                            ((ClientEventsService) (MakaanServiceFactory.getInstance().getService(ClientEventsService.class))).changePhase(leadsObject.clientLeadObject.clientLead.id, change);
                        }
                    }
                    finish();
                }
                break;
            }
            case LOAD_THANK_YOU_FRAGMENT: {
                ThankYouScreenFragment fragment = new ThankYouScreenFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ThankYouScreenFragment.IS_DASHBOARD, true);
                fragment.setArguments(bundle);
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
            case LOAD_FRAGMENT_CONTENT: {
                BlogContentFragment fragment = new BlogContentFragment();
                fragment.setArguments(data);
                initFragment(R.id.activity_base_buyer_journey_content_frame_layout, fragment, shouldAddToBackStack);
                break;
            }
        }
        if(!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }


    @OnClick(R.id.activity_base_buyer_journey_layout_back_button)
    public void onBackPressed(View view) {

        ClientLeadsFragment clientLeadsFragment = (ClientLeadsFragment)getSupportFragmentManager(
        ).findFragmentByTag(ClientLeadsFragment.class.getName());

        if(clientLeadsFragment!=null && clientLeadsFragment.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCashbackSeller);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.back);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.click);
            clientLeadsFragment=null;
        }

        ClientCompanyLeadFragment clientCompanyLeadFragment = (ClientCompanyLeadFragment)getSupportFragmentManager(
        ).findFragmentByTag(ClientCompanyLeadFragment.class.getName());

        if(clientCompanyLeadFragment!=null && clientCompanyLeadFragment.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCashbackListing);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.back);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.click);
            clientCompanyLeadFragment=null;
        }

        ClientCompanyLeadsAddProperty clientCompanyLeadsAddProperty = (ClientCompanyLeadsAddProperty)getSupportFragmentManager(
        ).findFragmentByTag(ClientCompanyLeadsAddProperty.class.getName());

        if(clientCompanyLeadsAddProperty!=null && clientCompanyLeadsAddProperty.isVisible()){
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboardCashbackListing);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.back);
            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.click);
            clientCompanyLeadsAddProperty=null;
        }

        onBackPressed();
    }

    /**
     * set title of the activity
     * @param title title to display in the toolbar*
     */
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title.toString());
    }

    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(getResources().getString(titleId));
    }

    private void sendScreenNameEvent(int type) {
        if (type != 0) {
            switch (type) {
                case LOAD_FRAGMENT_SHORTLIST: {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD);
                    properties.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD_SHORTLIST);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.screenName);
                    break;
                }
                case LOAD_FRAGMENT_SAVE_SEARCH:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD);
                    properties.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD_SAVED_SEARCH);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.screenName);
                    break;
                }
                case LOAD_FRAGMENT_REWARDS:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD);
                    properties.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD_REWARDS);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.screenName);
                    break;
                }
                case LOAD_FRAGMENT_SITE_VISIT:{
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD);
                    properties.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_BUYER_DASHBOARD_SITE_VISIT);
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.screenName);
                    break;
                }
            }
        }


    }
}
