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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.jarvis.analytics.AnalyticsConstants;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.jarvis.analytics.BuyerJourneyMessage;
import com.makaan.jarvis.analytics.SerpFilterMessageMap;
import com.makaan.jarvis.event.JarvisTrackExtraData;
import com.makaan.jarvis.event.PageTag;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.ui.cards.BaseCtaView;
import com.makaan.jarvis.ui.cards.CtaCardFactory;
import com.makaan.jarvis.ui.cards.InterceptedLinearLayout;
import com.makaan.jarvis.ui.cards.PyrPopupCard;
import com.makaan.jarvis.ui.cards.SerpFilterCard;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.JsonBuilder;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.makaan.jarvis.message.CtaType.*;

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
    InterceptedLinearLayout mJarvisPopupCard;

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
    private String contentPyrUrl=null;
    private String buyerJourneyMessageString=null;

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
            //mUserActivityTrackerRenewHandler.removeCallbacks(mUserActivityTrackerRunnable);
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

        if(null!=mUserActivityTrackerRenewHandler && null!=mUserActivityTrackerRunnable) {
            mUserActivityTrackerRenewHandler.removeCallbacks(mUserActivityTrackerRunnable);
        }

        if(null!=mPopupDismissHandler && null!=mPopupDismissRunnable) {
            mPopupDismissHandler.removeCallbacks(mPopupDismissRunnable);
        }
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
            mJarvisPopupCard = (InterceptedLinearLayout) findViewById(R.id.card_cta);
        }

        mPopupDismissRunnable=new Runnable() {

            @Override
            public void run() {
                dismissPopupWithAnim();
            }
        };

        mJarvisPopupCard.setOnInterceptTouchListener(new InterceptedLinearLayout.OnInterceptTouchListener() {
            @Override
            public void OnInterceptTouch() {
                mPopupDismissHandler.removeCallbacks(mPopupDismissRunnable);
            }
        });


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

        if (isJarvisHeadVisible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        String launchPage=getScreenName();
        if(!TextUtils.isEmpty(launchPage)) {
            switch (launchPage.toLowerCase()) {
                case "project":
                case "listing detail":
                case "serp":
                case "city":
                case "locality":
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMManual);
                    properties.put(MakaanEventPayload.LABEL, launchPage.toLowerCase());
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                    break;
            }
        }
        Intent intent = new Intent(this, ChatActivity.class);
        if(!TextUtils.isEmpty(launchPage)){
            intent.putExtra(ChatActivity.LAUNCH_PAGE, launchPage);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, mJarvisHead, getString(R.string.jarvis_button_transition));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }*/

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

                    card.setOnCancelClickListener(new BaseCtaView.OnCancelClickListener() {
                        @Override
                        public void onCancelClick() {
                            createPopUpCardCloseTrackEvent(message.properties.ctaType, message);
                            dismissPopupWithAnim();
                        }
                    });

                    mPopupDismissHandler.postDelayed(mPopupDismissRunnable,
                            JarvisConstants.JARVIS_ACTION_DISMISS_TIMEOUT);
                    createPopUpCardViewTrackEvent(message.properties.ctaType, message);
                } catch (Exception e) {
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

        if(null!=mPopupDismissHandler && null!=mPopupDismissRunnable) {
            mPopupDismissHandler.removeCallbacks(mPopupDismissRunnable);
        }

        if(mJarvisPopupCard != null && mJarvisPopupCard.getVisibility()==View.VISIBLE){
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
        // todo discuss if we need to omit PropertyActivity
        return this instanceof SerpActivity || (this instanceof OverviewActivity
                && !OverviewActivity.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(getScreenName()));

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

        if(CookiePreferences.shouldDisplayBuyerJourney(this)){
            CookiePreferences.setBuyerJourneyPopupTimestamp(this);
        }else{
            return;
        }

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

    public boolean isActivityDead(){
        return isFinishing() || isActivityDestroyed();

    }

    private boolean isActivityDestroyed(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(isDestroyed()){
                return true;
            }
        }

        return false;
    }


    protected boolean needBackProcessing() {
        return (mJarvisPopupCard !=null && mJarvisPopupCard.getVisibility()==View.VISIBLE);
    }

    public void createPopUpCardViewTrackEvent(CtaType ctaType, ExposeMessage message) {
        switch (ctaType) {
            case serpScroll: {
                createSerpScrollEvent(message, MakaanTrackerConstants.Action.view);
                break;
            }
            case enquiryDropped:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",MakaanTrackerConstants.Label.mAutoView,
                        MakaanTrackerConstants.Label.enquiryDroppedSimilarSuggestion));
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                break;
            }
            case childSerp:{
                if(message.properties!=null && message.properties.content==null && message.city!=null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoViewPyr,
                            message.city.toLowerCase()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                }
                break;
            }
            case contentPyr:{

                if(message.properties!=null && message.properties.content==null && message.city!=null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoViewPyr,
                            message.city.toLowerCase()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                }
                else {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = JsonBuilder.toJsonArray(message.properties.content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Content> contents=new ArrayList<Content>();
                    if(jsonArray!=null) {
                        contents = MakaanBuyerApplication.gson.fromJson(jsonArray.toString(), new TypeToken<List<Content>>() {
                        }.getType());
                    }
                    Content content=null;
                    if(contents!=null && contents.size()>0){
                        content = contents.get(0);
                    }
                    if(content!=null && content.guid!=null && !TextUtils.isEmpty(content.guid)) {
                        contentPyrUrl=content.guid;
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",MakaanTrackerConstants.Label.mAutoView, content.guid));
                        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                    }
                }
                    break;
            }
            case pageVisits:{
                Map<String, BuyerJourneyMessage> jarvisBuyerJourneyMessageMap =
                        MasterDataCache.getInstance().getJarvisBuyerJourneyMessageMap();

                if(null!=jarvisBuyerJourneyMessageMap && !jarvisBuyerJourneyMessageMap.isEmpty()){
                    if(message.properties!=null && message.properties.message_type!=null &&
                            !TextUtils.isEmpty(message.properties.message_type)){
                        BuyerJourneyMessage buyerJourneyMessage = jarvisBuyerJourneyMessageMap.get(message.properties.message_type);
                        if(buyerJourneyMessage.message!=null && !TextUtils.isEmpty(buyerJourneyMessage.message)){
                            buyerJourneyMessageString=buyerJourneyMessage.message;
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",MakaanTrackerConstants.Label.mAutoView, buyerJourneyMessage.message));
                            MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.view);
                        }
                    }
                }

                break;
            }
        }
    }

    public void createSerpScrollEvent(ExposeMessage item, MakaanTrackerConstants.Action action){
        Map<String, SerpFilterMessageMap> serpFilterMessageMap = MasterDataCache.getInstance().getSerpFilterMessageMap();
        SerpFilterMessageMap serpFilterMessageMap1 = serpFilterMessageMap.get(item.properties.suggest_filter);
        String labelEnd;
        if(serpFilterMessageMap1!=null) {
            labelEnd = serpFilterMessageMap1.filter;
        }else {
            labelEnd="";
        }
        Properties properties=MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
        if(action.getValue().equalsIgnoreCase(MakaanTrackerConstants.Action.view.getValue())){
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s_%s",MakaanTrackerConstants.Label.mAutoView,
                MakaanTrackerConstants.Label.serpScroll,labelEnd));
        }
        else if(action.getValue().equalsIgnoreCase(MakaanTrackerConstants.Action.close.getValue())){
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s_%s",MakaanTrackerConstants.Label.mAutoClick,
                    MakaanTrackerConstants.Label.serpScroll,labelEnd));
        }
        MakaanEventPayload.endBatch(this, action);

    }


    private void createPopUpCardCloseTrackEvent(CtaType ctaType, ExposeMessage message) {
        switch (ctaType) {
            case serpScroll: {
                createSerpScrollEvent(message, MakaanTrackerConstants.Action.close);
                break;
            }
            case enquiryDropped:{
                Properties properties=MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",MakaanTrackerConstants.Label.mAutoClick,
                        MakaanTrackerConstants.Label.enquiryDroppedSimilarSuggestion));
                MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
                break;
            }
            case childSerp:{
                if(message.properties!=null && message.properties.content==null && message.city!=null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s", MakaanTrackerConstants.Label.mAutoViewPyr,
                            message.city.toLowerCase()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
                }

                break;
            }
            case contentPyr:{
                if(message.properties!=null && message.properties.content==null && message.city!=null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoViewPyr,
                            message.city.toLowerCase()));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
                }else {
                    if(contentPyrUrl!=null&& !TextUtils.isEmpty(contentPyrUrl)) {
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoClick, contentPyrUrl));
                        MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
                    }
                }
                break;
            }
            case pageVisits:{
                if(buyerJourneyMessageString!=null && !TextUtils.isEmpty(buyerJourneyMessageString)) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerMAuto);
                    properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", MakaanTrackerConstants.Label.mAutoClick, buyerJourneyMessageString));
                    MakaanEventPayload.endBatch(this, MakaanTrackerConstants.Action.close);
                }
                break;
            }
        }
    }
    private static class Content {
        public String guid;
        public String postTitle;
        public String primaryImageUrl;

    }

}
