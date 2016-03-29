package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.makaan.adapter.listing.ListingPagerAdapter;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 2/10/16.
 */
public class LoadMoreListingCardView extends BaseCardView<ListingPagerAdapter.PaginationListener> {
//    @Bind(R.id.listing_load_more_view_layout_button)
    Button mLoadMoreButton;
//    @Bind(R.id.listing_load_more_view_layout_progress_bar)
    ProgressBar mProgressBar;

    private ListingPagerAdapter.PaginationListener mListener;

    public LoadMoreListingCardView(Context context) {
        super(context);
    }

    public LoadMoreListingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreListingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, ListingPagerAdapter.PaginationListener listener) {
        mListener = listener;
        mProgressBar.setVisibility(View.GONE);
        mLoadMoreButton.setVisibility(View.VISIBLE);
    }

//    @OnClick(R.id.listing_load_more_view_layout_button)
    public void onLoadMoreClicked(View view) {
        if(mListener != null) {
            mListener.onLoadMoreItems();
            mProgressBar.setVisibility(View.VISIBLE);
            mLoadMoreButton.setVisibility(View.GONE);
        }
    }
}
