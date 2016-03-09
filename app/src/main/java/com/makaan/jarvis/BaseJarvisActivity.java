package com.makaan.jarvis;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.jarvis.analytics.AnalyticsConstants;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.event.JarvisTrackExtraData;
import com.makaan.jarvis.event.PageTag;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.ui.cards.BaseCtaView;
import com.makaan.jarvis.ui.cards.CtaCardFactory;
import com.makaan.jarvis.ui.cards.PyrPopupCard;
import com.makaan.jarvis.ui.cards.SerpFilterCard;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.buyerjourney.PhaseChange;
import com.makaan.response.project.Project;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.JsonBuilder;
import com.makaan.util.JsonParser;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 13/01/16.
 */
public abstract class BaseJarvisActivity extends AppCompatActivity{

    private FrameLayout mActivityContent;


    public abstract boolean isJarvisSupported();
    public abstract String getScreenName();

    @Nullable
    @Bind(R.id.jarvis_head)
    ImageView mJarvisHead;

    @Nullable
    @Bind(R.id.card_cta)
    LinearLayout mJarvisPopupCard;

    @Nullable
    @Bind(R.id.jarvis_container)
    View mJarvisContainer;

    private Runnable mPopupDismissRunnable;
    private Handler mPopupDismissHandler =new Handler();

    private Runnable mUserActivityTrackerRunnable;
    private Handler mUserActivityTrackerRenewHandler =new Handler();

    private PageTag currentPageTag;
    private JarvisTrackExtraData jarvisTrackExtraData;
    private boolean isUserActivenessTrackEnabled;

    private boolean isPyrContentPopupDisplayed = false;

    private boolean isJarvisHeadVisible;

    private Map<SerpRequest, Boolean> serpRequestTrackMap = new HashMap<>();
    private SerpRequest currentSerpRequest = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SerpObjects.putSerpObject(this, getSerpObjects());

        currentPageTag = new PageTag();
        initExtraData();

        setupActivityTimer();
        identifyUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserActivenessTrackEnabled = shouldTrackUserActiveness();
        if(null!=currentPageTag) {
            renewUserActivityTimer();
        }

        if(isJarvisHeadVisible && mJarvisHead != null) {
            if(mJarvisHead.getVisibility()!=View.VISIBLE) {
                mJarvisHead.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        JarvisClient.getInstance().refreshJarvisSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(shouldTrackUserActiveness()){
            mUserActivityTrackerRenewHandler.removeCallbacks(mUserActivityTrackerRunnable);
        }
    }

    @Override
    protected void onStop() {
        dismissPopupWithAnim();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerpObjects.removeSerpObject(this);
    }

    protected SerpObjects getSerpObjects() {
        return null;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if(!isJarvisSupported()){
            super.setContentView(layoutResID);
            return;
        }

        isJarvisHeadVisible = true;

        View baseView = getLayoutInflater().inflate(R.layout.base_jarvis_activity, null);
        mActivityContent = (FrameLayout) baseView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, mActivityContent, true);
        super.setContentView(baseView);

        setupPopup();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LeadFormActivity.LEAD_DROP_REQUEST && resultCode==RESULT_OK){
            if(null!=data){
                long listingId = data.getLongExtra(KeyUtil.LISTING_ID,-1);
                if(listingId>0){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(AnalyticsConstants.KEY_PAGE_TYPE, getScreenName());
                        jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.ENQUIRY_DROPPED);
                        jsonObject.put(AnalyticsConstants.KEY_LISTING_ID, listingId);
                        jarvisTrackExtraData.setPageType(getScreenName());
                        jsonObject.put(AnalyticsConstants.KEY_EXTRA, JsonBuilder.toJson(jarvisTrackExtraData));
                        AnalyticsService analyticsService =
                                (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
                        analyticsService.track(AnalyticsService.Type.track, jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        if(null!=currentPageTag) {
            renewUserActivityTimer();
        }
    }

    private void initExtraData(){
        jarvisTrackExtraData = new JarvisTrackExtraData();
        jarvisTrackExtraData.setPageTimeStamp(System.currentTimeMillis());
    }

    private void setupActivityTimer(){
        if(!shouldTrackUserActiveness()){
            return;
        }

        if(null==mUserActivityTrackerRunnable) {
            mUserActivityTrackerRunnable = new Runnable() {

                @Override
                public void run() {
                    trackUserActiveness();
                }
            };
        }
    }

    private void renewUserActivityTimer(){
        if(!isUserActivenessTrackEnabled){
            return;
        }

        mUserActivityTrackerRenewHandler.removeCallbacks(mUserActivityTrackerRunnable);

        mUserActivityTrackerRenewHandler.postDelayed(mUserActivityTrackerRunnable,
                JarvisConstants.JARVIS_USER_ACTIVITY_TIMEOUT);
    }

    private void setupPopup(){
        if(!isJarvisSupported()){
            return;
        }

        if(null== mJarvisPopupCard){
            mJarvisPopupCard = (LinearLayout) findViewById(R.id.card_cta);
        }

        mPopupDismissRunnable=new Runnable() {

            @Override
            public void run() {
                dismissPopupWithAnim();
            }
        };

        mJarvisPopupCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mPopupDismissHandler.removeCallbacks(mPopupDismissRunnable);
                return true;
            }
        });
    }

    public void startActivity(Intent intent){
        if(!isJarvisSupported()) {
            super.startActivity(intent);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, mJarvisHead,
                            getString(R.string.jarvis_button_transition));
            super.startActivity(intent, options.toBundle());
        }
        else {
            super.startActivity(intent);
        }
    }

    @Nullable
    @OnClick(R.id.jarvis_head)
    public void onJarvisClicked() {
        switch(getScreenName()){
            case "Project":{
                Properties properties= MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.chat);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProject);
                break;
            }
            case "Listing detail":{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.chat);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickProperty);
                break;
            }
            case "serp":{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.chat);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickSerp);
                break;
            }
            case "City":{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.chat);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickCity);
                break;
            }
            case "Locality":{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.chat);
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.clickLocality);
                break;
            }

        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        if(mJarvisHead != null) {
            mJarvisHead.setVisibility(View.GONE);
        }

    }

    protected void animateJarvisHead() {
        if(!isJarvisSupported()){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation shakeAnimation =
                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                mJarvisHead.startAnimation(shakeAnimation);
            }
        });
    }

    protected void displayPopupWindow(final ExposeMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (jarvisTrackExtraData.pageTimestamp != message.properties.extra.pageTimestamp || !isJarvisHeadVisible) {
                        return;
                    }


                    mJarvisPopupCard.removeAllViews();
                    BaseCtaView card = CtaCardFactory.createCard(BaseJarvisActivity.this, message);
                    mJarvisPopupCard.addView(card);
                    showPopupWithAnim();


                    if (CtaType.serpScroll == message.properties.ctaType) {
                        card.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                            @Override
                            public void onApplyClick() {
                                if(null!=currentSerpRequest) {
                                    serpRequestTrackMap.put(currentSerpRequest, false);
                                }
                                dismissPopupWithAnim();
                            }
                        });
                    } else if (CtaType.contentPyr == message.properties.ctaType ||
                            CtaType.childSerp == message.properties.ctaType) {
                        isPyrContentPopupDisplayed = true;
                        card.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                            @Override
                            public void onApplyClick() {
                                dismissPopupWithAnim();
                            }
                        });
                    } else if (CtaType.pageVisits == message.properties.ctaType ) {
                        card.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                            @Override
                            public void onApplyClick() {
                                dismissPopupWithAnim();
                            }
                        });
                    }

                    card.setOnCancelClickListener(new SerpFilterCard.OnCancelClickListener() {
                        @Override
                        public void onCancelClick() {
                            dismissPopupWithAnim();
                        }
                    });

                    mPopupDismissHandler.postDelayed(mPopupDismissRunnable,
                            JarvisConstants.JARVIS_ACTION_DISMISS_TIMEOUT);
                }catch (Exception e){
                    //Some error with jarvis payload data, don't do anything
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mJarvisPopupCard !=null && mJarvisPopupCard.getVisibility()==View.VISIBLE){
            mJarvisPopupCard.removeAllViews();
            mJarvisPopupCard.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }


    public JarvisTrackExtraData getJarvisExtraData(){
        return jarvisTrackExtraData;
    }

    private void showPopupWithAnim(){
        mJarvisPopupCard.setVisibility(View.VISIBLE);
        //mJarvisContainer.invalidate();
        //Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        //mJarvisPopupCard.setAnimation(zoomin);
    }

    public void dismissPopupWithAnim(){
        if(this==null || isFinishing() || mJarvisPopupCard ==null){
            return;
        }

        if(mJarvisPopupCard !=null && mJarvisPopupCard.getVisibility()==View.VISIBLE){
            mJarvisPopupCard.removeAllViews();
            mJarvisPopupCard.setVisibility(View.GONE);
        }

        //mJarvisContainer.invalidate();
        //Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        //mJarvisPopupCard.setAnimation(zoomout);
    }

    protected void setJarvisVisibility(boolean visible) {
        dismissPopupWithAnim();
        isJarvisHeadVisible = visible;
        if(mJarvisHead != null) {
            mJarvisHead.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setCurrentPageTag(PageTag currentPageTag){
        this.currentPageTag = currentPageTag;
        renewUserActivityTimer();
    }

    private boolean shouldTrackUserActiveness(){
        return this instanceof SerpActivity || this instanceof ProjectActivity ||
                this instanceof LocalityActivity || this instanceof CityActivity;

    }

    public void addSerpScrollTrackStatus(SerpRequest request){
        if(null==request){
            return;
        }
        serpRequestTrackMap.put(request,false);
    }

    public void trackScroll(SerpRequest request, int requestType, int position){
        if (request==null || serpRequestTrackMap.get(request)){
            return;
        }

        if(null==jarvisTrackExtraData){
            return;
        }

        currentSerpRequest = request;

        if(requestType == SerpActivity.TYPE_CLUSTER) {
            try {
                JSONObject jsonObject = new JSONObject();

                jarvisTrackExtraData.setPageType("child serp");
                jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.CHILD_SERP);
                jsonObject.put(AnalyticsConstants.KEY_EXTRA, JsonBuilder.toJson(jarvisTrackExtraData));

                AnalyticsService analyticsService =
                        (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
                analyticsService.track(AnalyticsService.Type.track, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            AnalyticsService analyticsService =
                    (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
            jarvisTrackExtraData.setPageType(SerpActivity.SCREEN_NAME);
            analyticsService.trackSerpScroll(SerpObjects.getSelectedFilterNames(this), position, jarvisTrackExtraData);
        }

        serpRequestTrackMap.put(request, true);
    }
    public void trackBuyerJourney(int phaseId){

        AnalyticsService analyticsService =
                (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
        jarvisTrackExtraData.setPageType(getScreenName());

        Map<String, BuyerJourneyMessage> map =  MasterDataCache.getInstance().getJarvisBuyerJourneyMessageMap();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            BuyerJourneyMessage message = (BuyerJourneyMessage) pair.getValue();
            if(phaseId==message.phaseId){
                String eventName = (String) pair.getKey();
                if(!TextUtils.isEmpty(eventName)) {
                    analyticsService.trackBuyerJourney(eventName, jarvisTrackExtraData);
                }
            }
        }

    }

    private void trackUserActiveness(){
        try {
            if(!shouldTrackUserActiveness() || isPyrContentPopupDisplayed){
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.CONTENT_PYR);
            //jsonObject.put(AnalyticsConstants.KEY_CURRENT_PAGE_TAG, JsonBuilder.toJson(currentPageTag));

            JSONObject pageTagObject = new JSONObject();
            if(null!=currentPageTag.city && !currentPageTag.city.isEmpty()){
                pageTagObject.accumulate("city",JsonBuilder.toJsonArray(currentPageTag.city));
            }

            if(null!=currentPageTag.locality && !currentPageTag.locality.isEmpty()){
                pageTagObject.accumulate("locality",JsonBuilder.toJsonArray(currentPageTag.locality));
            }

            if(null!=currentPageTag.project && !currentPageTag.project.isEmpty()){
                pageTagObject.accumulate("project",JsonBuilder.toJsonArray(currentPageTag.project));
            }

            if(null!=currentPageTag.suburb && !currentPageTag.suburb.isEmpty()){
                pageTagObject.accumulate("suburb",JsonBuilder.toJsonArray(currentPageTag.suburb));
            }

            jsonObject.put(AnalyticsConstants.KEY_CURRENT_PAGE_TAG, pageTagObject);

            jarvisTrackExtraData.setPageType(getScreenName());
            jsonObject.put(AnalyticsConstants.KEY_EXTRA, JsonBuilder.toJson(jarvisTrackExtraData));

            AnalyticsService analyticsService =
                    (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
            analyticsService.track(AnalyticsService.Type.track, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void identifyUser(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AnalyticsConstants.KEY_PAGE_TYPE, getScreenName());
            AnalyticsService analyticsService =
                    (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
            analyticsService.track(AnalyticsService.Type.identify, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    protected boolean needBackProcessing() {
        return (mJarvisPopupCard !=null && mJarvisPopupCard.getVisibility()==View.VISIBLE);
    }
}
