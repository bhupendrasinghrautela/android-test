package com.makaan.jarvis;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 13/01/16.
 */
public abstract class BaseJarvisActivity extends AppCompatActivity{

    private FrameLayout mActivityContent;

    public abstract boolean isJarvisSupported();

    @Bind(R.id.jarvis_head)
    ImageView mJarvisHead;

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
        if(!isJarvisSupported()){
            super.startActivity(intent);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, mJarvisHead, getString(R.string.jarvis_button_transition));
            super.startActivity(intent, options.toBundle());
        }
        else {
            super.startActivity(intent);
        }
    }

    @OnClick(R.id.jarvis_head)
    public void onJarvisClicked() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
