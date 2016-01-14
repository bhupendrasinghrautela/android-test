package com.makaan.adapter.listing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import com.makaan.R;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.ui.pyr.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;


/**
 * Created by root on 5/1/16.
 */
public class FiltersViewAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    public static final int TOGGLE_BUTTON = 1;
    public static final int SEEKBAR = 2;
    public static final int CHECKBOX = 3;
    public static final int RADIO_BUTTON = 4;

    private final int type;
    Context context;
    ArrayList<TermFilter> termValues;
    ArrayList<RangeFilter> rangeValues;

    public FiltersViewAdapter(Context context, List<TermFilter> term, List<RangeFilter> range, int type) {
        this.context = context;
        this.termValues = new ArrayList<TermFilter>();
        this.termValues.addAll(term);

        this.rangeValues = new ArrayList<RangeFilter>();
        this.rangeValues.addAll(range);

        this.type = type;
    }

    @Override
    public int getCount() {
        return termValues.size();
    }

    @Override
    public TermFilter getItem(int position) {
        return termValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return termValues.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            if (type == CHECKBOX) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_checkbox_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((CheckBox) holder.view).setOnCheckedChangeListener(this);
            } else if (type == TOGGLE_BUTTON) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_toggle_button_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((ToggleButton) holder.view).setOnCheckedChangeListener(this);
            } else if(type == RADIO_BUTTON) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_radio_button_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((RadioButton) holder.view).setOnCheckedChangeListener(this);
            } else if(type == SEEKBAR) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_seekbar_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
            }
            holder.pos = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.pos = position;
        }
        if (type == CHECKBOX) {
            ((CheckBox) holder.view).setText(this.getItem(position).displayName);
            ((CheckBox) holder.view).setChecked(this.getItem(position).selected);
        } else if (type == TOGGLE_BUTTON) {
            ((ToggleButton) holder.view).setTextOn(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setTextOff(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setText(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setChecked(this.getItem(position).selected);
        } else if (type == RADIO_BUTTON) {
            ((RadioButton) holder.view).setOnCheckedChangeListener(null);
            ((RadioButton) holder.view).setText(this.getItem(position).displayName);
            ((RadioButton) holder.view).setChecked(this.getItem(position).selected);
            ((RadioButton) holder.view).setOnCheckedChangeListener(this);
        } else if (type == SEEKBAR) {
            ((RangeSeekBar<Double>) holder.view).setInitialValues(rangeValues.get(position).minValue, rangeValues.get(position).maxValue);
        }

        return convertView;
    }

    public List<String> getSelectedOptions() {
        List<String> list = null;
        if (termValues != null && termValues.size() > 0) {
            list = new ArrayList<>();
            list.add(termValues.get(0).fieldName);
            for (TermFilter filter : termValues) {
                if (filter.selected) {
                    list.add(String.valueOf(filter.value));
                }
            }
        }
        return list;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewHolder holder = (ViewHolder) buttonView.getTag();
        int pos = holder.pos;
        if(buttonView instanceof RadioButton) {
            // set selected property of all radio buttons to false
            for(TermFilter filter : termValues) {
                filter.selected = false;
            }
            termValues.get(pos).selected = isChecked;
            notifyDataSetChanged();
        } else {
            termValues.get(pos).selected = isChecked;
        }
    }

    class ViewHolder {
        View view;
        int pos;
    }
}
