package com.makaan.ui.listing;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 1/23/16.
 */
public class CustomFlowLayout extends FlowLayout {

    private static final int MSG_UPDATE_LAYOUT = 0x01;
    private static final int DELAY = 200;

    private CustomFlowLayoutAdapter mAdapter;
    CustomFlowHandler handler = new CustomFlowHandler();

    class CustomFlowHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LAYOUT:
                    invalidateAdapterViews();
                    break;
            }
        }
    }

    private void invalidateAdapterViews() {
        removeAllViews();
        if(mAdapter != null) {
            for(int i = 0; i < mAdapter.getItemCount(); i++) {
                addView(mAdapter.getView(LayoutInflater.from(getContext()), this, mAdapter.getItem(i)));
            }
        }
    }

    public CustomFlowLayout(Context context) {
        super(context);
    }

    public CustomFlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomFlowLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    public void setAdapter(CustomFlowLayoutAdapter adapter) {
        adapter.setObserver(this);
        mAdapter = adapter;
        handler.sendEmptyMessageDelayed(MSG_UPDATE_LAYOUT, DELAY);
    }

    public void onChanged() {
        handler.sendEmptyMessageDelayed(MSG_UPDATE_LAYOUT, DELAY);
    }

    @Override
    public void setVisibility(int visibility) {
        int currentVisibility = getVisibility();
        super.setVisibility(visibility);
        if(currentVisibility != visibility && visibility == VISIBLE) {
            handler.sendEmptyMessageDelayed(MSG_UPDATE_LAYOUT, DELAY);
        }
    }

    public abstract static class CustomFlowLayoutAdapter<T> {
        private CustomFlowLayout observer;

        private ArrayList<T> items = new ArrayList<>();
        private ItemRemoveListener itemRemoveListener;

        public void notifyDataSetChanged() {
            observer.onChanged();
        }

        public void invalidate() {
            observer.removeAllViews();
            observer.onChanged();
        }

        public void setObserver(CustomFlowLayout observer) {
            this.observer = observer;
        }

        public int getItemCount() {
            return items.size();
        }

        abstract public View getView(LayoutInflater inflater, ViewGroup parent, T data);

        public void setData(ArrayList<T> data) {
            items.clear();
            if(!(data == null || data.size() == 0)) {
                items.addAll(data);
            }
            notifyDataSetChanged();
        }

        public void addData(ArrayList<T> data) {
            if(!(data == null || data.size() == 0)) {
                items.addAll(data);
            }
            notifyDataSetChanged();
        }

        public T getItem(int position) {
            if(items.size() > position) {
                return items.get(position);
            }
            return null;
        }

        protected void removeItem(T item) {
            if(items.size() > 0) {
                items.remove(item);
                if(this.itemRemoveListener != null) {
                    itemRemoveListener.itemRemoved(item);
                }
                notifyDataSetChanged();
            }
        }

        public void setItemRemoveListener(ItemRemoveListener listener) {
            this.itemRemoveListener = listener;
        }
    }

    public interface ItemRemoveListener {
        void itemRemoved(Object item);
    }
}
