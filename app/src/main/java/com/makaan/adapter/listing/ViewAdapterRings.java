package com.makaan.adapter.listing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import com.makaan.R;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.response.serp.TermFilter;

import java.util.ArrayList;

/**
 * Created by root on 5/1/16.
 */
public class ViewAdapterRings extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    Context context;
    ArrayList<TermFilter> values;
    private PyrPagePresenter pyrPagePresenter= PyrPagePresenter.getPyrPagePresenter();

    public ViewAdapterRings(Context context, ArrayList<TermFilter> values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        if(values == null) {
            return 0;
        }
        return values.size();
    }

    @Override
    public TermFilter getItem(int position) {
        if(values == null) {
            return  null;
        }
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(values == null) {
            return 0;
        }
        return values.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToggleButtonViewHolder holder;
        if (convertView == null || !(convertView instanceof CheckBox)) {

            convertView = LayoutInflater.from(context).inflate(R.layout.pyr_bedroom_toggle_button, parent, false);
            holder = new ToggleButtonViewHolder();
            holder.view = convertView;
            holder.pos = position;
            convertView.setTag(holder);
            ((ToggleButton)holder.view).setOnCheckedChangeListener(null);
            ((ToggleButton) holder.view).setTextOn(String.valueOf(this.getItem(position).displayName));
            ((ToggleButton) holder.view).setTextOff(String.valueOf(this.getItem(position).displayName));
            ((ToggleButton)holder.view).setText(String.valueOf(this.getItem(position).displayName));
            ((ToggleButton)holder.view).setChecked(this.getItem(position).selected);
            ((ToggleButton)holder.view).setOnCheckedChangeListener(this);
        }

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ToggleButtonViewHolder holder = (ToggleButtonViewHolder) buttonView.getTag();
        int pos = holder.pos;
        this.getItem(pos).selected = isChecked;

    }

    class ToggleButtonViewHolder {
        View view;
        int pos;
    }
}
