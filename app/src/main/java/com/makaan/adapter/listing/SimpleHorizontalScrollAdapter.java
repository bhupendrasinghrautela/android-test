package com.makaan.adapter.listing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;

import java.util.List;

/**
 * Created by sunil on 17/01/16.
 */
public class SimpleHorizontalScrollAdapter extends
        CustomAbstractHorizontalScrollViewAdapter<SimpleEntity>  {

    public SimpleHorizontalScrollAdapter(Context context, List<SimpleEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public List<View> getAllViews() {
        return null;
    }

    @Override
    public View inflateAndBindDataToView(SimpleEntity dataItem, int positon) {
        final View view = mInflater.inflate(R.layout.simple_scollview_item,null);
        final TextView itemLabel = (TextView) view.findViewById(R.id.scrollview_item_label);
        final ImageView itemImage = (ImageView) view.findViewById(R.id.scrollview_item_image);
        return view;
    }
}
