package com.makaan.adapter.listing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.AbstractFilterValue;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.ui.pyr.RangeSeekBar;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by root on 5/1/16.
 */
public class FiltersViewAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, RangeSeekBar.OnRangeSeekBarChangeListener<Double> {
    public static final int TOGGLE_BUTTON = 1;
    public static final int SEEKBAR = 2;
    public static final int CHECKBOX = 3;
    public static final int RADIO_BUTTON = 4;
    public static final int CHECKBOX_MIN_MAX = 5;
    public static final int RADIO_BUTTON_RANGE = 6;
    public static final int SINGLE_CHECKBOX = 7;

    public static final int UNEXPECTED_VALUE = -1000000;
    public static final String RADIO_BUTTON_BOTH_SELECTED = "-1";
    public static final String TYPE_YEAR = "year";
    public static final String TYPE_DAY = "day";
    public static final String INFINITE_SELECTOR = "+";
    public static final String MIN_MAX_SEPARATOR = "-";

    private final int type;
    Context context;
    private final FilterGroup filterGroup;
    ArrayList<TermFilter> termValues;
    ArrayList<RangeFilter> rangeValues;
    ArrayList<RangeMinMaxFilter> rangeMinMaxValues;

    public FiltersViewAdapter(Context context, FilterGroup filterGroup, int type) {
        this.context = context;
        this.termValues = filterGroup.termFilterValues;
        this.rangeValues = filterGroup.rangeFilterValues;
        this.rangeMinMaxValues = filterGroup.rangeMinMaxFilterValues;
        this.filterGroup = filterGroup;

        this.type = type;
    }

    @Override
    public int getCount() {
        if(type == SEEKBAR || type == RADIO_BUTTON_RANGE) {
            return rangeValues.size();
        } else if(type == CHECKBOX_MIN_MAX) {
            return rangeMinMaxValues.size();
        } else {
            return termValues.size();
        }
    }

    @Override
    public AbstractFilterValue getItem(int position) {
        if(type == SEEKBAR || type == RADIO_BUTTON_RANGE) {
            return rangeValues.get(position);
        } else if(type == CHECKBOX_MIN_MAX) {
            return rangeMinMaxValues.get(position);
        } else {
            return termValues.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if(type == SEEKBAR || type == RADIO_BUTTON_RANGE) {
            return rangeValues.size();
        } else if(type == CHECKBOX_MIN_MAX) {
            return rangeMinMaxValues.size();
        } else {
            return termValues.size();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            if (type == CHECKBOX || type == CHECKBOX_MIN_MAX) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_checkbox_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((CheckBox) holder.view).setOnCheckedChangeListener(this);
            } else if (type == TOGGLE_BUTTON) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_toggle_button_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((ToggleButton) holder.view).setOnCheckedChangeListener(this);
            } else if(type == RADIO_BUTTON || type == RADIO_BUTTON_RANGE) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_radio_button_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((RadioButton) holder.view).setOnCheckedChangeListener(this);
            } else if(type == SINGLE_CHECKBOX) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_filters_single_checkbox_item_view, parent, false);
                holder = new ViewHolder();
                holder.view = convertView;
                ((CheckBox)holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_checkbox)).setOnCheckedChangeListener(this);
                holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_checkbox).setTag(holder);
            } else {
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

        if (type == CHECKBOX || type == CHECKBOX_MIN_MAX) {
            ((CheckBox) holder.view).setText(this.getItem(position).displayName);
            ((CheckBox) holder.view).setChecked(this.getItem(position).selected);
        } else if (type == TOGGLE_BUTTON) {
            ((ToggleButton) holder.view).setTextOn(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setTextOff(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setText(this.getItem(position).displayName);
            ((ToggleButton) holder.view).setChecked(this.getItem(position).selected);
        } else if (type == RADIO_BUTTON || type == RADIO_BUTTON_RANGE) {
            ((RadioButton) holder.view).setOnCheckedChangeListener(null);
            ((RadioButton) holder.view).setText(this.getItem(position).displayName);
            ((RadioButton) holder.view).setChecked(this.getItem(position).selected);
            ((RadioButton) holder.view).setOnCheckedChangeListener(this);
        } else if (type == SEEKBAR) {
            RangeSeekBar<Double> seekBar = (RangeSeekBar<Double>) holder.view.findViewById(R.id.fragment_dialog_filter_seekbar_item_view_seekbar);
            seekBar.setInitialValues(((RangeFilter) this.getItem(position)).minValue, ((RangeFilter) this.getItem(position)).maxValue);
            seekBar.setNotifyWhileDragging(true);

            if("currency".equalsIgnoreCase(filterGroup.type)) {
                ((TextView) holder.view.findViewById(R.id.fragment_dialog_filter_seekbar_item_view_textview)).setText(
                        String.format("%s - %s", StringUtil.getDisplayPrice(((RangeFilter) this.getItem(holder.pos)).selectedMinValue),
                                StringUtil.getDisplayPrice((((RangeFilter) this.getItem(holder.pos)).selectedMaxValue))));
            } else {
                ((TextView) holder.view.findViewById(R.id.fragment_dialog_filter_seekbar_item_view_textview)).setText(
                        String.format("%s - %s", ((RangeFilter) this.getItem(holder.pos)).selectedMinValue,
                                (((RangeFilter) this.getItem(holder.pos)).selectedMaxValue)));
                seekBar.setStepValues(new double[] {0, 0.0416, 0.0833, 0.125, 0.1666, 0.2083, 0.25, 0.2916, 0.333, 0.375, 0.416,
                        0.4583, 0.5, 0.5416, 0.5833, 0.625, 0.666, 0.7083, 0.75, 0.7916, 0.833, 0.875, 0.916, 0.9583, 1});
            }
            seekBar.setSelectedMinValue(((RangeFilter) this.getItem(holder.pos)).selectedMinValue);
            seekBar.setSelectedMaxValue(((RangeFilter) this.getItem(holder.pos)).selectedMaxValue);
            seekBar.setOnRangeSeekBarChangeListener(this);
        } else if(type == SINGLE_CHECKBOX) {
            if(filterGroup.imageName != null) {
                Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(filterGroup.imageName);
                if (bitmap != null) {
                    ((ImageView) holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_image_view)).setImageBitmap(bitmap);
                } else {
                    int id = context.getResources().getIdentifier(filterGroup.imageName, "drawable", "com.makaan");
                    Bitmap b = BitmapFactory.decodeResource(context.getResources(), id);
                    ((ImageView) holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_image_view)).setImageBitmap(b);
                    MakaanBuyerApplication.bitmapCache.putBitmap(filterGroup.imageName, b);
                }
            }

            ((CheckBox)holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_checkbox)).setText(this.getItem(position).displayName);
            ((CheckBox)holder.view.findViewById(R.id.fragment_dialog_filters_single_checkbox_item_view_checkbox)).setChecked(this.getItem(position).selected);
        }

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ViewHolder holder = (ViewHolder) buttonView.getTag();
        int pos = holder.pos;
        if(buttonView instanceof RadioButton && isChecked) {
            // set selected property of all radio buttons to false
            if(type == RADIO_BUTTON) {
                for (TermFilter filter : termValues) {
                    filter.selected = false;
                }
                termValues.get(pos).selected = true;
            } else if(type == RADIO_BUTTON_RANGE) {
                for (RangeFilter filter : rangeValues) {
                    filter.selected = false;
                }
                rangeValues.get(pos).selected = true;
            }
            notifyDataSetChanged();
        } else {
            if(type == CHECKBOX_MIN_MAX) {
                rangeMinMaxValues.get(pos).selected = isChecked;
            } else {
                termValues.get(pos).selected = isChecked;
            }
        }
    }

    // Same logic should be updated in SetAlertsDialogFragment::createSelector() as well
    public void applyFilters(Selector selector, ArrayList<FilterGroup> filterGroups) {
        FilterGroup currentGroup = clearFilterGroup(filterGroups);
        Properties properties= MakaanEventPayload.beginBatch();
        //properties.putCategory(MakaanTrackerConstants.Category.propertyFilter.getValue());
        if(type == SEEKBAR) {
            selector.removeRange(rangeValues.get(0).fieldName);

            RangeFilter filter = rangeValues.get(0);
            if(filter.selectedMinValue > filter.minValue || filter.selectedMaxValue < filter.maxValue) {
                if(currentGroup != null) {
                    currentGroup.isSelected = true;
                }

                if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_budget")) {
                    properties.put(MakaanEventPayload.MIN_BUDGET, filter.selectedMinValue);
                    properties.put(MakaanEventPayload.MAX_BUDGET, filter.selectedMaxValue);
                } else if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_sq_ft")) {
                    properties.put(MakaanEventPayload.MIN_AREA, filter.selectedMinValue);
                    properties.put(MakaanEventPayload.MAX_AREA, filter.selectedMaxValue);
                } else {
                    properties.put(filterGroup.displayName, filter.selectedMinValue + "-" + filter.selectedMaxValue);
                }
                selector.range(rangeValues.get(0).fieldName, rangeValues.get(0).selectedMinValue, rangeValues.get(0).selectedMaxValue);
            }
        } else if(type == RADIO_BUTTON) {

            for(TermFilter filter : termValues) {
                if(filter.selected) {
                    selector.removeTerm(termValues.get(0).fieldName);
                    if(currentGroup != null) {
                        currentGroup.isSelected = true;
                    }
                    if(RADIO_BUTTON_BOTH_SELECTED.equals(filter.value)) {
                        for(TermFilter filter2 : termValues) {
                            if(!filter2.selected && !RADIO_BUTTON_BOTH_SELECTED.equals(filter2.value)) {
                                selector.term(filter2.fieldName, filter2.value);
                            }
                        }
                        currentGroup.isSelected = false;
        // both
                        if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_new_resale")){
                            properties.put(MakaanEventPayload.NEW_RESALE, filter.displayName);
                        } else {
                            properties.put(filterGroup.displayName, filter.displayName);
                        }
                    } else {
                        selector.term(filter.fieldName, filter.value);
                        // rest


                        if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_new_resale")){
                            properties.put(MakaanEventPayload.NEW_RESALE, filter.displayName);
                        } else {
                            properties.put(filterGroup.displayName, filter.displayName);
                        }
                    }
                }
            }
        } else if(type == RADIO_BUTTON_RANGE) {
            selector.removeRange(rangeValues.get(0).fieldName);

            for(RangeFilter filter : rangeValues) {
                if(filter.selected) {
                    if(currentGroup != null) {
                        currentGroup.isSelected = true;
                    }
                    if(filter.minValue != UNEXPECTED_VALUE || filter.maxValue != UNEXPECTED_VALUE) {
                        Long minValue = (long)UNEXPECTED_VALUE;
                        Long maxValue = (long)UNEXPECTED_VALUE;
                        if (filterGroup.type.equalsIgnoreCase(TYPE_YEAR)) {
                            Calendar cal = Calendar.getInstance();
                            if(filter.minValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.YEAR, (int) filter.minValue);
                                minValue = cal.getTimeInMillis();
                                cal.add(Calendar.YEAR, -(int)filter.minValue);
                            }

                            if(filter.maxValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.YEAR, (int) filter.maxValue);
                                maxValue = cal.getTimeInMillis();
                            }
                            if(minValue == UNEXPECTED_VALUE) {
                                minValue = null;
                            }
                            if(maxValue == UNEXPECTED_VALUE) {
                                maxValue = null;
                            }

                            selector.range(filter.fieldName, minValue, maxValue);
                        } else if(filterGroup.type.equalsIgnoreCase(TYPE_DAY)) {
                            Calendar cal = Calendar.getInstance();
                            if(filter.minValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.DAY_OF_MONTH, (int) filter.minValue);
                                minValue = cal.getTimeInMillis();
                                cal.add(Calendar.DAY_OF_MONTH, -(int)filter.minValue);
                            }

                            if(filter.maxValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.DAY_OF_MONTH, (int) filter.maxValue);
                                maxValue = cal.getTimeInMillis();
                            }
                            if(minValue == UNEXPECTED_VALUE) {
                                minValue = null;
                            }
                            if(maxValue == UNEXPECTED_VALUE) {
                                maxValue = null;
                            }

                            selector.range(filter.fieldName, minValue, maxValue);
                        } else {
                            selector.range(filter.fieldName, filter.minValue, filter.maxValue);
                        }

                        if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_possession in")){
                            properties.put(MakaanEventPayload.UNDER_CONSTRUCTION, filter.displayName);
                        } else {
                            properties.put(filterGroup.displayName, filter.displayName);
                        }


                    }
                }
            }
        } else if(type == CHECKBOX_MIN_MAX) {
            selector.removeRange(rangeMinMaxValues.get(0).minFieldName);
            selector.removeRange(rangeMinMaxValues.get(0).maxFieldName);

            long minValue = Long.MAX_VALUE;
            long maxValue = Long.MIN_VALUE;
            String minFieldName = "";
            String maxFieldName = "";
            boolean filterApplied = false;
            for(RangeMinMaxFilter filter : rangeMinMaxValues) {
                if(filter.selected) {
                    if(currentGroup != null) {
                        currentGroup.isSelected = true;
                    }
                    filterApplied = true;
                    if (filterGroup.type.equalsIgnoreCase(TYPE_YEAR)) {
                        Calendar cal = Calendar.getInstance();
                        if(filter.minValue != UNEXPECTED_VALUE) {
                            cal.add(Calendar.YEAR, (int) filter.minValue);
                            if(cal.getTimeInMillis() < minValue) {
                                minValue = cal.getTimeInMillis();
                            }
                            cal.add(Calendar.YEAR, -(int) filter.minValue);
                        }
                        if(filter.maxValue != UNEXPECTED_VALUE) {
                            cal.add(Calendar.YEAR, (int) filter.maxValue);
                            if(cal.getTimeInMillis() > maxValue) {
                                maxValue = cal.getTimeInMillis();
                            }
                        }
                    }
                    if(currentGroup!=null && currentGroup.internalName.equalsIgnoreCase("i_ready_to_move")){
                        properties.put(MakaanEventPayload.READY_TO_MOVE, filter.displayName);
                    } else {
                        properties.put(filterGroup.displayName, filter.displayName);
                    }
                    minFieldName = filter.minFieldName;
                    maxFieldName = filter.maxFieldName;
                }
            }

            if(filterApplied) {
                if (minValue != Long.MAX_VALUE) {
                    selector.range(minFieldName, minValue, null);
                }
                if (maxValue != Long.MIN_VALUE) {
                    selector.range(maxFieldName, null, maxValue);
                }
            }
        } else {
            selector.removeTerm(termValues.get(0).fieldName);
            StringBuilder builder = new StringBuilder();
            String separator = "";

            for(TermFilter filter : termValues) {
                if(filter.selected) {
                    if(currentGroup != null) {
                        currentGroup.isSelected = true;
                    }
                    if(filter.displayName.contains(INFINITE_SELECTOR)) {
                        String[] val = filter.value.split(MIN_MAX_SEPARATOR);
                        int min = Integer.valueOf(val[0]);
                        int max = Integer.valueOf(val[1]);
                        for(int i = min; i <= max; i++) {
                            selector.term(filter.fieldName, String.valueOf(i));
                        }
                    } else {
                        selector.term(filter.fieldName, filter.value);
                    }
                    builder.append(separator + filter.displayName);
                    separator = ",";
                }
            }
            if(!TextUtils.isEmpty(builder.toString())) {
                if(currentGroup.internalName.equalsIgnoreCase("i_beds")) {
                    properties.put(MakaanEventPayload.BEDROOM, builder.toString());
                }
                else if(currentGroup.internalName.equalsIgnoreCase("i_property_type")){
                    properties.put(MakaanEventPayload.PROPERTY_TYPE, builder.toString());
                }
                else if(currentGroup.internalName.equalsIgnoreCase("i_bathroom")){
                    properties.put(MakaanEventPayload.BATHROOM, builder.toString());
                }
                else if(currentGroup.internalName.equalsIgnoreCase("i_listed_by")){
                    properties.put(MakaanEventPayload.LISTED_BY, builder.toString());
                }
                else if(currentGroup.internalName.equalsIgnoreCase("i_m_plus")){
                    properties.put(MakaanEventPayload.MPLUS, builder.toString());
                }
                else{
                    properties.put(filterGroup.displayName, builder.toString());
                }
            }
        }
        if (currentGroup != null) {
            applyFilterGroup(currentGroup);
        } else {
            applyFilterGroup(filterGroups);
        }
    }

    private FilterGroup clearFilterGroup(ArrayList<FilterGroup> filterGroups) {
        for(FilterGroup group : filterGroups) {
            if(group.internalName.equals(filterGroup.internalName)) {
                group.isSelected = false;
                return group;
            }
        }
        return null;
    }

    private void applyFilterGroup(FilterGroup group) {
        group.applyFilters(filterGroup);
    }

    private void applyFilterGroup(ArrayList<FilterGroup> filterGroups) {
        for(FilterGroup group : filterGroups) {
            if(group.internalName.equals(filterGroup.internalName)) {
                group.applyFilters(filterGroup);
            }
        }
    }

    public void reset() {
        if(type == SEEKBAR || type == RADIO_BUTTON_RANGE) {
            for(RangeFilter filter : rangeValues) {
                filter.selectedMinValue = filter.minValue;
                filter.selectedMaxValue = filter.maxValue;
                filter.selected = false;
            }
        } else if(type == CHECKBOX_MIN_MAX) {
            for(RangeMinMaxFilter filter : rangeMinMaxValues) {
                filter.selected = false;
            }
        }  else {
            for(TermFilter filter : termValues) {
                filter.selected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
        ViewHolder holder = (ViewHolder) ((View)bar.getParent()).getTag();
        ((RangeFilter) this.getItem(holder.pos)).selectedMinValue = normalize(minValue);
        ((RangeFilter) this.getItem(holder.pos)).selectedMaxValue = normalize(maxValue);

        if("currency".equalsIgnoreCase(filterGroup.type)) {
            ((TextView) holder.view.findViewById(R.id.fragment_dialog_filter_seekbar_item_view_textview)).setText(
                    String.format("%s - %s", StringUtil.getDisplayPrice(((RangeFilter) this.getItem(holder.pos)).selectedMinValue),
                            StringUtil.getDisplayPrice((((RangeFilter) this.getItem(holder.pos)).selectedMaxValue))));
        } else {
            ((TextView) holder.view.findViewById(R.id.fragment_dialog_filter_seekbar_item_view_textview)).setText(
                    String.format("%s - %s", ((RangeFilter) this.getItem(holder.pos)).selectedMinValue,
                            (((RangeFilter) this.getItem(holder.pos)).selectedMaxValue)));
        }
    }

    private int normalize(Double value) {
        return ((int)((value + 50) / 100)) * 100;
    }

    class ViewHolder {
        View view;
        int pos;
    }
}
