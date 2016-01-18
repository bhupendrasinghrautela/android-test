package com.makaan.activity.pyr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by makaanuser on 7/1/16.
 */
public class PropertyTypeViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_property_type)
    TextView mTextViewPropertyType;
    @Bind(R.id.cb_tick)
    CheckBox mCheckboxSelect;

    public PropertyTypeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
