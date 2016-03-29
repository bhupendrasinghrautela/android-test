package com.makaan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 22/09/15.
 *
 * An extension of recyclerview adapter for supporting Pagination
 *
 */
public abstract class PaginatedBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mItems;
    protected RecycleViewMode recycleViewMode;
    protected boolean isListScrolling;
    protected int errorMessage = 0;


    private static final int FOOTER_SIZE = 1;

    public PaginatedBaseAdapter() {
        this.mItems = new ArrayList<T>();
    }


    public void setData(List<T> listings, int requestType){
        if(listings ==null){
            removeAllItems();
            return;
        }
        this.mItems = listings;
        this.notifyDataSetChanged();
    }

    public void addMoreItems(List<T> newItems) {
        this.mItems.addAll(newItems);
        this.notifyDataSetChanged();
    }

    public void removeAllItems() {
        this.mItems.clear();
        notifyDataSetChanged();
    }

    public List<T> getAllItems(){
        return this.mItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        if(viewType == RecycleViewMode.FOOTER_LOADING.getValue()){

            viewHolder = onCreateFooterLoadingViewHolder(parent);

        } else if(viewType == RecycleViewMode.FOOTER_ERROR.getValue()) {

            viewHolder = onCreateFooterErrorViewHolder(parent);
        }
        else {
            viewHolder = onCreateDataViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position==getItemCount()-1 && recycleViewMode == RecycleViewMode.FOOTER_LOADING) {
            onBindFooterLoadingViewHolder(holder, position);
        } else if (position==getItemCount()-1 && recycleViewMode == RecycleViewMode.FOOTER_ERROR) {
            onBindFooterErrorViewHolder(holder, position);
        } else {
            onBindDataViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(RecycleViewMode.DATA.getValue() != recycleViewMode.getValue()){
            if(position==getItemCount()-1){
                return recycleViewMode.getValue();
            }
        }
        return RecycleViewMode.DATA.getValue();
    }

    @Override
    public int getItemCount() {
        if(RecycleViewMode.DATA.getValue() != recycleViewMode.getValue()){
            return mItems.size() + FOOTER_SIZE;
        }else{
            return mItems.size();
        }
    }

    //Abstract methods for creating viewholer for different viewType
    public abstract RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType);
    public abstract RecyclerView.ViewHolder onCreateFooterLoadingViewHolder(ViewGroup parent);
    public abstract RecyclerView.ViewHolder onCreateFooterErrorViewHolder(ViewGroup parent);

    //Abstract methods for binding data to viewholders
    public abstract void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position);
    public abstract void onBindFooterLoadingViewHolder(RecyclerView.ViewHolder holder, int position);
    public abstract void onBindFooterErrorViewHolder(RecyclerView.ViewHolder holder, int position);

    public void setListViewScrollState(boolean isScrolling){
        isListScrolling = isScrolling;
    }

    /**
     * Adds a loading footer
     * */
    public void showFooterLoading(){
        dismissFooter();
        recycleViewMode = RecycleViewMode.FOOTER_LOADING;
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Adds a error/retry footer
     * */
    public void showFooterError(int errorId){
        errorMessage = errorId;
        dismissFooter();
        recycleViewMode = RecycleViewMode.FOOTER_ERROR;
        notifyItemInserted(getItemCount() - 1);
    }

    public void dismissFooter(){
        recycleViewMode = RecycleViewMode.DATA;
        notifyItemRemoved(getItemCount()-1);
    }

    protected class FooterHeaderLoaderViewAdapter extends RecyclerView.ViewHolder {
        public FooterHeaderLoaderViewAdapter(View itemView) {
            super(itemView);
        }
    }
}
