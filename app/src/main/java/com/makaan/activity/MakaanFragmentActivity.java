package com.makaan.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makaan.R;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.ui.MakaanProgressDialog;
import com.makaan.util.AnimationUtils;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by vaibhav on 23/12/15.
 */
public abstract class MakaanFragmentActivity extends BaseJarvisActivity {
    ImageView mNoResultsImageView;
    ProgressBar mLoadingProgressBar;
    FrameLayout mContentFrameLayout;

    private MakaanProgressDialog progressDialog;
    private TextView mNoResultsTextView;
    private View mNoResultsLayout;


    protected abstract int getContentViewId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);

        if (!AppUtils.haveNetworkConnection(this)) {
            showNoNetworkFound();
        }

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.activity_makaan_base);

        mNoResultsImageView = (ImageView) findViewById(R.id.activity_makaan_base_no_result_image_view);
        mNoResultsTextView = (TextView) findViewById(R.id.activity_makaan_base_no_result_text_view);
        mNoResultsLayout = findViewById(R.id.activity_makaan_base_no_result_layout);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.activity_makaan_base_loading_progress_bar);
        mContentFrameLayout = (FrameLayout) findViewById(R.id.activity_makaan_base_content_frame_layout);

        getLayoutInflater().inflate(layoutResID, mContentFrameLayout, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationUtils.backActivityAnimation(this);
    }



    private void showLoadingDialog() {
        progressDialog = new MakaanProgressDialog(this);
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void initFragments(int[] fragmentHolderId, Fragment[] fragment, boolean shouldAddToBackStack) {
        if (fragmentHolderId.length != fragment.length) {
            return;
        }
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragmentHolderId.length; i++) {
            fragmentTransaction.replace(fragmentHolderId[i], fragment[i], fragment.getClass().getName());
        }
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    private void cancelLoadingDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Not able to launch activity and dismiss progressbar", e);
        }
    }

    private void showNoNetworkFound() {
        //TODO: implement
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

    }

    protected void showProgress() {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }
    protected void showNoResults() {
        showNoResults(null);
    }

    protected void showNoResults(String message) {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);

        if(message == null) {
            mNoResultsTextView.setText(R.string.default_error_message);
        } else {
            mNoResultsTextView.setText(message);
        }
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mNoResultsImageView);
        Glide.with(this).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);
    }

    protected void showContent() {
        mContentFrameLayout.setVisibility(View.VISIBLE);
        mNoResultsLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.GONE);
    }


}
