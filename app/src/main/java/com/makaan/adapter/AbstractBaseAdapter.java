package com.makaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBaseAdapter <T> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<T> mList = new ArrayList<T>();

    public AbstractBaseAdapter(Context context, List<T> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
    }
    protected abstract View newView(int position);
    protected abstract void bindView(View view, int position, T item);

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {

        View view =null;
        if(convertView == null) {
            view = newView(position);
        } else {
            view = convertView;
        }

        bindView(view, position, mList.get(position));

        return view;
    }

    public void setData(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mList;
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
