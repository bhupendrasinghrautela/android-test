package com.makaan.ui.listing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.makaan.R;
import com.makaan.adapter.listing.ListingPagerAdapter;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/10/16.
 */
public class LoadMoreListingCardView extends BaseCardView<ListingPagerAdapter.PaginationListener> {
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
    }

    @OnClick(R.id.listing_load_more_view_layout_button)
    public void onLoadMoreClicked(View view) {
        if(mListener != null) {
            mListener.onLoadMoreItems();
        }
    }
}
