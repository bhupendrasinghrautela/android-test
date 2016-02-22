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
import com.makaan.jarvis.analytics.AnalyticsConstants;
import com.makaan.jarvis.analytics.AnalyticsService;
import com.makaan.jarvis.message.CtaType;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.ui.cards.BaseCtaView;
import com.makaan.jarvis.ui.cards.CtaCardFactory;
import com.makaan.jarvis.ui.cards.PyrPopupCard;
import com.makaan.jarvis.ui.cards.SerpFilterCard;
import com.makaan.pojo.SerpObjects;
import com.makaan.response.project.Project;
import com.makaan.service.MakaanServiceFactory;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static Runnable mUserActivityTrackerRunnable;
    private static Handler mUserActivityTrackerRenewHandler =new Handler();

    private String currentPageUrl;
    private static boolean isUserActivenessTrackEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SerpObjects.putSerpObject(this, getSerpObjects());

        currentPageUrl = "";

        setupActivityTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserActivenessTrackEnabled = shouldTrackUserActiveness();
        if(!TextUtils.isEmpty(currentPageUrl)) {
            renewUserActivityTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(shouldTrackUserActiveness()){
            mUserActivityTrackerRenewHandler.removeCallbacks(mUserActivityTrackerRunnable);
        }
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
                int listingId = data.getIntExtra("listingId",-1);
                if(listingId>0){
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(AnalyticsConstants.KEY_PAGE_TYPE, getScreenName());
                        jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.ENQUIRY_DROPPED);
                        jsonObject.put(AnalyticsConstants.KEY_LISTING_ID, listingId);
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

    public static void renewUserActivityTimer(){
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
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

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
                mJarvisPopupCard.removeAllViews();
                BaseCtaView card = CtaCardFactory.createCard(BaseJarvisActivity.this, message);
                mJarvisPopupCard.addView(card);
                showPopupWithAnim();


                if(CtaType.serpScroll == message.properties.ctaType) {
                    card.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                        @Override
                        public void onApplyClick() {
                            dismissPopupWithAnim();
                        }
                    });
                }else if (CtaType.contentPyr == message.properties.ctaType) {
                    card.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                        @Override
                        public void onApplyClick() {
                            dismissPopupWithAnim();
                        }
                    });

                }

                mPopupDismissHandler.postDelayed(mPopupDismissRunnable,
                        JarvisConstants.JARVIS_ACTION_DISMISS_TIMEOUT);
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

    private void showPopupWithAnim(){
        mJarvisPopupCard.setVisibility(View.VISIBLE);
        //mJarvisContainer.invalidate();
        //Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        //mJarvisPopupCard.setAnimation(zoomin);
    }

    private void dismissPopupWithAnim(){
        if(this==null || isFinishing() || mJarvisPopupCard ==null){
            return;
        }

        mJarvisPopupCard.removeAllViews();
        mJarvisPopupCard.setVisibility(View.GONE);
        //mJarvisContainer.invalidate();
        //Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        //mJarvisPopupCard.setAnimation(zoomout);
    }

    protected void setIsJarvisVisible(boolean visible) {
        if(mJarvisHead != null) {
            mJarvisHead.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setCurrentPageUrl(String currentPageUrl){
        this.currentPageUrl = currentPageUrl;
        renewUserActivityTimer();
    }

    private boolean shouldTrackUserActiveness(){
        if(this instanceof SerpActivity || this instanceof ProjectActivity ||
                this instanceof LocalityActivity || this instanceof CityActivity){
            return true;
        }

        return false;
    }

    private void trackUserActiveness(){
        try {
            if(!shouldTrackUserActiveness()){
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AnalyticsConstants.KEY_PAGE_TYPE, getScreenName());
            jsonObject.put(AnalyticsConstants.KEY_EVENT_NAME, AnalyticsConstants.CONTENT_PYR);
            jsonObject.put(AnalyticsConstants.KEY_CURRENT_PAGEURL, currentPageUrl);
            AnalyticsService analyticsService =
                    (AnalyticsService) MakaanServiceFactory.getInstance().getService(AnalyticsService.class);
            analyticsService.track(AnalyticsService.Type.track, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
