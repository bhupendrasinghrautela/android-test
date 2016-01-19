package com.makaan.adapter.listing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aishwarya on 02/12/15.
 * An abstract class to be extended if someone wants to set an adapter for the
 * CustomHorizontalScrollView Class
 */
public abstract class CustomAbstractHorizontalScrollViewAdapter<D> {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<D> mDataList = new ArrayList<>();

    public CustomAbstractHorizontalScrollViewAdapter(Context context, List<D> dataList){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDataList.addAll(dataList);
    }

    public abstract List<View> getAllViews();

    public abstract View inflateAndBindDataToView(D dataItem,int positon);
}
