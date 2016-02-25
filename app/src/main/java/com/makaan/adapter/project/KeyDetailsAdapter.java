package com.makaan.adapter.project;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.AbstractBaseAdapter;
import com.makaan.pojo.KeyDetail;

import java.util.List;

/**
 * Created by aishwarya on 25/02/16.
 */
public class KeyDetailsAdapter extends AbstractBaseAdapter<KeyDetail> {

    public KeyDetailsAdapter(Context context, List<KeyDetail> list) {
        super(context, list);
    }

    @Override
    protected View newView(int position) {
        ViewHolder h = new ViewHolder();
        View newView = mInflater.inflate(R.layout.key_detail_layout_item, null);
        h.mLabel = (TextView) newView.findViewById(R.id.label);
        h.mValue = (TextView) newView.findViewById(R.id.value);
        newView.setTag(h);
        return newView;
    }

    @Override
    protected void bindView(View view, int position, KeyDetail item) {
        final ViewHolder h = (ViewHolder) view.getTag();
        h.mLabel.setText(item.label);
        h.mValue.setText(item.value);
    }

    private class ViewHolder {
        TextView mLabel;
        TextView mValue;
    }
}
