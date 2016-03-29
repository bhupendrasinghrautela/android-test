package com.makaan.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makaan.R;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 2/27/16.
 */
public abstract class MakaanBaseDialogFragment extends DialogFragment {
    @Bind(R.id.dialog_fragment_makaan_base_no_result_image_view)
    ImageView mNoResultsImageView;
    @Bind(R.id.dialog_fragment_makaan_base_no_result_layout)
    View mNoResultsLayout;
    @Bind(R.id.dialog_fragment_makaan_base_no_result_text_view)
    TextView mNoResultsTextView;
    @Bind(R.id.dialog_fragment_makaan_base_loading_progress_bar)
    ImageView mLoadingProgressBar;
    @Bind(R.id.dialog_fragment_makaan_base_content_frame_layout)
    FrameLayout mContentFrameLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_makaan_base, container, false);

        View childView = inflater.inflate(getContentViewId(), container, false);

        ((FrameLayout)view.findViewById(R.id.dialog_fragment_makaan_base_content_frame_layout)).addView(childView);

        // bind view to ButterKnife
        ButterKnife.bind(this, view);
        AppBus.getInstance().register(this);

        if (!AppUtils.haveNetworkConnection(getActivity())) {
            showNoNetworkFound();
        }
        return view;
    }

    private void showNoNetworkFound() {
        //TODO: implement
    }

    protected void showProgress() {
        mContentFrameLayout.setVisibility(View.GONE);
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
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }

    protected abstract int getContentViewId();
}
