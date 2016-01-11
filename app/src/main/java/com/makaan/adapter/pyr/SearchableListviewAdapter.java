package com.makaan.adapter.pyr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makaan.response.search.Search;

import java.util.ArrayList;
import java.util.List;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchableListviewAdapter extends BaseAdapter {
    
    private List<Search> mData = new ArrayList<Search>();
    private LayoutInflater mInflater;
    private int layoutId;
    
    public SearchableListviewAdapter(Context context, List<Search> data, int layoutId) {
        this.mData = data ;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
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
 
        holder.text.setText(mData.get(position).getLocality());
 
        return convertView;
    }
    
    static class ViewHolder {
        TextView text;
    }
 
    public void updateDataItems(List<Search> data){
        mData =data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Search getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getLocalityId();
    }

}