package com.makaan.ui;

/**
 * Created by sunil on 21/09/15.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.makaan.adapter.PaginatedBaseAdapter;

import java.util.List;

/**
 * An extension of {@link RecyclerView} to support pagination
 * */
public class PaginatedListView extends RecyclerView {

    public interface PaginationListener {
        void onLoadMoreItems();
    }

    private boolean isLoading;
    private boolean hasMoreItems;
    private PaginationListener pagingableListener;
    private LayoutManager mLayoutManager;
    private int firstVisibleItem, visibleItemCount, totalItemCount;


    public PaginatedListView(Context context) {
        super(context);
        init();
    }

    public PaginatedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaginatedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try{
            super.dispatchDraw(canvas);
        }catch(Exception e){
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try{
            return super.dispatchTouchEvent(ev);
        }catch(Exception e){
            return true;
        }
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        removeFooterView();
    }

    public void setPaginableListener(PaginationListener pagingableListener) {
        this.pagingableListener = pagingableListener;
    }

    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
    }

    public boolean hasMoreItems() {
        return this.hasMoreItems;
    }

    public void resetLoadingView(){
        isLoading = true;
        addFooterView();
    }

    public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems) {
        setHasMoreItems(hasMoreItems);
        setIsLoading(false);
        if(newItems != null && newItems.size() > 0) {
            RecyclerView.Adapter adapter = (getAdapter());
            if(adapter instanceof PaginatedBaseAdapter) {
                ((PaginatedBaseAdapter)adapter).addMoreItems(newItems);
            }
        }
    }

    private void init() {
        mLayoutManager = getLayoutManager();
        isLoading = false;
        addFooterView();
        super.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {

                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_SETTLING ) {
                    notifyAdapterOfScrollingState(false);
                } else {
                    notifyAdapterOfScrollingState(true);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(mLayoutManager==null){
                    mLayoutManager = getLayoutManager();
                }

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();

                LinearLayoutManager layoutManager = (LinearLayoutManager)mLayoutManager;
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
                        if (pagingableListener != null) {
                            isLoading = true;
                            pagingableListener.onLoadMoreItems();
                            addFooterView();
                        }
                    }
                }
            }
        });
    }

    private void addFooterView(){
        RecyclerView.Adapter adapter = (getAdapter());
        if(adapter instanceof PaginatedBaseAdapter) {
            ((PaginatedBaseAdapter)adapter).showFooterLoading();
        }
    }

    private void removeFooterView(){
        RecyclerView.Adapter adapter = (getAdapter());
        if(adapter instanceof PaginatedBaseAdapter) {
            ((PaginatedBaseAdapter)adapter).dismissFooter();
        }
    }

    private void notifyAdapterOfScrollingState(boolean isScrolling){
        RecyclerView.Adapter adapter = (getAdapter());
        if(adapter instanceof PaginatedBaseAdapter) {
            ((PaginatedBaseAdapter)adapter).setListViewScrollState(isScrolling);
        }
    }


}