package com.makaan.jarvis;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.ui.cards.SerpFilterCard;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 13/01/16.
 */
public abstract class BaseJarvisActivity extends AppCompatActivity{

    private FrameLayout mActivityContent;

    public abstract boolean isJarvisSupported();

    @Nullable
    @Bind(R.id.jarvis_head)
    ImageView mJarvisHead;

    @Nullable
    @Bind(R.id.cta_card)
    LinearLayout mCtaCard;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                mCtaCard.removeAllViews();
                SerpFilterCard serpFilterCard =
                        (SerpFilterCard) getLayoutInflater().inflate(R.layout.card_serp_filter, null);
                serpFilterCard.bindView(getApplicationContext(), message);
                mCtaCard.addView(serpFilterCard);
                showWithAnim();
                serpFilterCard.setOnApplyClickListener(new SerpFilterCard.OnApplyClickListener() {
                    @Override
                    public void onApplyClick() {
                        dismissWithAnim();
                    }
                });

                mCtaCard.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissWithAnim();
                    }
                }, JarvisConstants.JARVIS_ACTION_DISMISS_TIMEOUT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mCtaCard.getVisibility()==View.VISIBLE){
            mCtaCard.removeAllViews();
            mCtaCard.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    private void showWithAnim(){
        mCtaCard.setVisibility(View.VISIBLE);
        Animation zoomin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        mCtaCard.setAnimation(zoomin);
    }

    private void dismissWithAnim(){
        mCtaCard.setVisibility(View.GONE);
        Animation zoomout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        mCtaCard.setAnimation(zoomout);
        mCtaCard.removeAllViews();
    }
}
