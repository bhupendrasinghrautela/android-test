package com.makaan.adapter.pyr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makaan.response.search.SearchResponseItem;

import java.util.List;

/**
 * Created by proptiger on 9/1/16.
 */
public class SelectedListViewAdapter extends BaseAdapter {

    private List<SearchResponseItem> mSelectedList = null;
    private LayoutInflater mInflater;
    private int layoutId;

    public SelectedListViewAdapter(Context context, List<SearchResponseItem> data,
                                   int layoutId) {
        this.mSelectedList = data;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mSelectedList.size();
    }

    public SearchResponseItem getItem(int position) {
        return mSelectedList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(mSelectedList.get(position).entityName);
        return convertView;
    }

    private class ViewHolder {
        TextView text;
    }

    public void updateDataItems(List<SearchResponseItem> data){
        mSelectedList =data;
        notifyDataSetChanged();
    }
}
