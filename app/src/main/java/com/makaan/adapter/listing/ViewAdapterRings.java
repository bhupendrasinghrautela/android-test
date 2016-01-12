package com.makaan.adapter.listing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ToggleButton;

import com.makaan.R;

/**
 * Created by root on 5/1/16.
 */
public class ViewAdapterRings extends BaseAdapter {

    Context context;
    String values[];

    public ViewAdapterRings(Context context, String values[]) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return values.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || !(convertView instanceof CheckBox)) {

            //using toggle button
            ToggleButton toggle = new ToggleButton(context);
            toggle.setTextColor(context.getResources().getColorStateList(R.color.bedroom_toggle_button_text_color_selector));
            //uncomment below line
            // toggle.setBackground(context.getResources().getDrawable(R.drawable.bedroom_toggle_button_drawable));
            toggle.setTextSize(16);
            convertView = toggle;
            toggle.setTextOff(String.valueOf(position+1));
            toggle.setTextOn(String.valueOf(position+1));
            ((ToggleButton) convertView).setText(String.valueOf(position+1));

        }

        return convertView;
    }
}
