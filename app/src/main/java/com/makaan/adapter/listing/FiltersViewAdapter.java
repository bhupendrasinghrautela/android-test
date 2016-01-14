package com.makaan.adapter.listing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.makaan.R;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.TermFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;


/**
 * Created by root on 5/1/16.
 */
public class FiltersViewAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    public static final int CHECKBOX_NORMAL = 1;
    public static final int CHECKBOX_RECTANGLE = 2;

    private final int type;
    Context context;
    List<TermFilter> termValues;

    public FiltersViewAdapter(Context context, List<TermFilter> values, int type) {
        this.context = context;
        this.termValues = new ArrayList<TermFilter>();
        this.termValues = values;
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
            if (type == 1) {
                convertView = new CheckBox(context);
                ((CheckBox) convertView).setTextColor(context.getResources().getColorStateList(R.color.radio_selector));
                ((CheckBox) convertView).setTextSize(14);
                holder = new ViewHolder();
                holder.view = convertView;
                ((CheckBox) holder.view).setOnCheckedChangeListener(this);
            } else if (type == 2) {
                convertView = new CheckBox(context);
                ((CheckBox) convertView).setTextColor(context.getResources().getColorStateList(R.color.radio_selector));
                ((CheckBox) convertView).setTextSize(14);
                ((CheckBox) convertView).setBackgroundResource(R.drawable.rectangular_checkbox_selector);
                ((CheckBox) convertView).setButtonDrawable(null);
                ((CheckBox) convertView).setGravity(Gravity.CENTER);
                holder = new ViewHolder();
                holder.view = convertView;
                ((CheckBox) holder.view).setOnCheckedChangeListener(this);
            }
            holder.pos = position;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.pos = position;
        }
        if (type == 1) {
            ((CheckBox) holder.view).setText((String) this.getItem(position).displayName);
            ((CheckBox) holder.view).setChecked(this.getItem(position).selected);
        } else if (type == 2) {
            ((CheckBox) holder.view).setText((String) this.getItem(position).displayName);
            ((CheckBox) holder.view).setChecked(this.getItem(position).selected);
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
        termValues.get(pos).selected = isChecked;
    }

    class ViewHolder {
        View view;
        int pos;
    }
}