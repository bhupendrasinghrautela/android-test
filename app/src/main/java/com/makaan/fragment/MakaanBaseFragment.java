package com.makaan.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makaan.R;
import com.makaan.activity.HomeActivity;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/9/16.
 */
public abstract class MakaanBaseFragment extends Fragment {
    @Bind(R.id.fragment_makaan_base_no_result_image_view)
    ImageView mNoResultsImageView;
    @Bind(R.id.fragment_makaan_base_no_result_layout)
    View mNoResultsLayout;
    @Bind(R.id.fragment_makaan_base_no_result_text_view)
    TextView mNoResultsTextView;
    @Bind(R.id.fragment_makaan_base_loading_progress_bar)
    ImageView mLoadingProgressBar;
    @Bind(R.id.fragment_makaan_base_content_frame_layout)
    FrameLayout mContentFrameLayout;

    protected abstract int getContentViewId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_makaan_base, container, false);

        View childView = inflater.inflate(getContentViewId(), container, false);

        ((FrameLayout)view.findViewById(R.id.fragment_makaan_base_content_frame_layout)).addView(childView);

        // bind view to ButterKnife
        ButterKnife.bind(this, view);
        // register for event bus callbacks
//        try {
            AppBus.getInstance().register(this);
        /*} catch(IllegalArgumentException ex) {
            AppBus.getInstance().unregister(this);
            AppBus.getInstance().register(this);
        }*/

        if (!AppUtils.haveNetworkConnection(getActivity())) {
            showNoNetworkFound();
        }
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        AppBus.getInstance().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);

    }

    private void showNoNetworkFound() {
        //TODO: implement
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*RefWatcher refWatcher = MakaanBuyerApplication.getRefWatcher(getActivity());
        if(refWatcher != null) {
            refWatcher.watch(this);
        }*/
    }

    protected void showProgress() {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        Glide.with(this).load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgressBar);
    }

    protected void showProgressWithContent() {
        mContentFrameLayout.setVisibility(View.VISIBLE);
        mNoResultsLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        Glide.with(this).load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mLoadingProgressBar);
    }

    protected void showNoResults() {
        showNoResults(null);
    }
    protected void showNoResults(String message) {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);

        if(message == null) {
            mNoResultsTextView.setText(R.string.generic_error);
        } else {
            mNoResultsTextView.setText(message);
        }
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mNoResultsImageView);
        Glide.with(this).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);
    }

    protected void showNoResults(int stringId) {
        mContentFrameLayout.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);

        if(stringId <= 0) {
            mNoResultsTextView.setText(R.string.generic_error);
        } else {
            mNoResultsTextView.setText(stringId);
        }
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mNoResultsImageView);
        Glide.with(this).load(R.raw.no_result).crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mNoResultsImageView);
    }

    protected void showContent() {
        mContentFrameLayout.setVisibility(View.VISIBLE);
        mNoResultsLayout.setVisibility(View.GONE);
        mLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppBus.getInstance().register(this);
    }

    @OnClick(R.id.fragment_makaan_base_no_result_action_button)
    public void onGoHomePressed(View view) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
