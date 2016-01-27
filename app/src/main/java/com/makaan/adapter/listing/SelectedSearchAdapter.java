package com.makaan.adapter.listing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.ui.listing.CustomFlowLayout;

/**
 * Created by rohitgarg on 1/23/16.
 */
public class SelectedSearchAdapter extends CustomFlowLayout.CustomFlowLayoutAdapter<SearchResponseItem> implements View.OnClickListener {
    Context mContext;
    public SelectedSearchAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent, SearchResponseItem data) {
        View view = inflater.inflate(R.layout.selected_search_item_view, parent, false);
        if(data != null) {
            TextView textView = (TextView) view.findViewById(R.id.selected_search_item_view_text_view);
            textView.setText(data.entityName);
            ImageButton button = (ImageButton) view.findViewById(R.id.selected_search_item_view_delete_image_button);
            button.setOnClickListener(this);
            button.setTag(data);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        removeItem((SearchResponseItem)v.getTag());
    }
}
