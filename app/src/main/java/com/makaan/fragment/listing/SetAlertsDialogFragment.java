package com.makaan.fragment.listing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.util.AppBus;
import com.makaan.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.makaan.adapter.listing.FiltersViewAdapter.INFINITE_SELECTOR;
import static com.makaan.adapter.listing.FiltersViewAdapter.MIN_MAX_SEPARATOR;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_BOTH_SELECTED;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_MIN_MAX;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_RANGE;
import static com.makaan.adapter.listing.FiltersViewAdapter.SEEKBAR;
import static com.makaan.adapter.listing.FiltersViewAdapter.TYPE_DAY;
import static com.makaan.adapter.listing.FiltersViewAdapter.TYPE_YEAR;
import static com.makaan.adapter.listing.FiltersViewAdapter.UNEXPECTED_VALUE;

/**
 * Created by rohitgarg on 2/11/16.
 */
public class SetAlertsDialogFragment extends DialogFragment {
    @Bind(R.id.fragment_set_alerts_content_linear_layout)
    LinearLayout mContentLinearLayout;
    @Bind(R.id.fragment_set_alerts_name_edit_text)
    TextView mSetAlertsNameEditText;

    private ArrayList<FilterGroup> mGroups;
    private SerpGetEvent mListingGetEvent;
    private String mSearchName;

    public void setData(ArrayList<FilterGroup> grps, SerpGetEvent listingGetEvent) {
        mGroups = grps;
        mListingGetEvent = listingGetEvent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_alert, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);

        populateData();
        handleSearchName();

        return view;
    }

    private void handleSearchName() {
        String name = mListingGetEvent.listingData.facets != null ? mListingGetEvent.listingData.facets.getSearchName() : null;
        if(TextUtils.isEmpty(name)) {
            StringBuilder bhk = new StringBuilder();
            StringBuilder budget = new StringBuilder();
            String separator = "";
            for (FilterGroup grp : mGroups) {
                if (!grp.isSelected) {
                    continue;
                }
                if("i_beds".equalsIgnoreCase(grp.internalName)) {
                    for(TermFilter filter : grp.termFilterValues) {
                        if(filter.selected) {
                            bhk.append(separator);
                            bhk.append(filter.displayName);
                            separator = ", ";
                        }
                    }
                }
                if("i_budget".equalsIgnoreCase(grp.internalName)) {
                    for(RangeFilter filter : grp.rangeFilterValues) {
                        if(filter.selectedMinValue != filter.minValue || filter.selectedMaxValue != filter.maxValue) {
                            budget.append(separator);
                            budget.append(StringUtil.getDisplayPrice(filter.selectedMinValue));
                            budget.append("-");
                            budget.append(StringUtil.getDisplayPrice(filter.selectedMaxValue));
                            separator = ", ";
                        }
                    }
                }
            }
            if(!TextUtils.isEmpty(bhk.toString())) {
                bhk.append(" bhk");
            }
            if(!(TextUtils.isEmpty(bhk.toString()) && TextUtils.isEmpty(budget.toString()))) {
                mSetAlertsNameEditText.setText(bhk.toString() + budget.toString());
            } else {
                mSetAlertsNameEditText.setText("save search 1");
            }
        } else {
            mSetAlertsNameEditText.setText(name);
        }
    }

    private void populateData() {
        StringBuilder builder = new StringBuilder();
        for (FilterGroup grp : mGroups) {
            if(!grp.isSelected) {
                continue;
            }

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
            mContentLinearLayout.addView(view);
            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText(grp.displayName);

            int id = this.getResources().getIdentifier(grp.imageName, "drawable", "com.makaan");
            if (id != 0) {
                ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(id);
            }

            if(builder.length() > 0) {
                builder.delete(0, builder.length());
            }

            String separator = "";
            for(TermFilter filter : grp.termFilterValues) {
                if(filter.selected) {
                    builder.append(separator);
                    builder.append(filter.displayName);
                    separator = ",";
                }
            }

            for(RangeFilter filter : grp.rangeFilterValues) {
                if(filter.selectedMinValue != filter.minValue || filter.selectedMaxValue != filter.maxValue) {
                    builder.append(separator);
                    if("i_budget".equalsIgnoreCase(grp.internalName)) {
                        builder.append(StringUtil.getDisplayPrice(filter.selectedMinValue));
                    } else {
                        builder.append(filter.selectedMinValue);
                    }
                    builder.append(" - ");
                    if("i_budget".equalsIgnoreCase(grp.internalName)) {
                        builder.append(StringUtil.getDisplayPrice(filter.selectedMaxValue));
                    } else {
                        builder.append(filter.selectedMaxValue);
                    }
                    separator = ",";
                }
            }

            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(builder.toString());
        }
        if(mListingGetEvent.listingData.facets != null) {
            String location = mListingGetEvent.listingData.facets.buildDisplayName();
            if (!TextUtils.isEmpty(location)) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
                mContentLinearLayout.addView(view);
                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText("location");

                ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(R.drawable.location_icon);

                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(location);
            }
        }
    }

    private Selector createSelector() {
        Selector selector = new Selector();
        StringBuilder searchNameBuilder = new StringBuilder();
        String separator = "";
        for (FilterGroup grp : mGroups) {
            if(!grp.isSelected) {
                continue;
            }
            searchNameBuilder.append(separator);

            int type = grp.layoutType;

            if(type == SEEKBAR) {
                RangeFilter filter = grp.rangeFilterValues.get(0);
                if(filter.selectedMinValue > filter.minValue || filter.selectedMaxValue < filter.maxValue) {
                    if("i_budget".equalsIgnoreCase(grp.internalName)) {
                        searchNameBuilder.append(StringUtil.getDisplayPrice(grp.rangeFilterValues.get(0).selectedMinValue)
                                + "-" + StringUtil.getDisplayPrice(grp.rangeFilterValues.get(0).selectedMaxValue));
                    } else {
                        searchNameBuilder.append(grp.rangeFilterValues.get(0).selectedMinValue + "-" + grp.rangeFilterValues.get(0).selectedMaxValue);
                    }

                    selector.range(grp.rangeFilterValues.get(0).fieldName, grp.rangeFilterValues.get(0).selectedMinValue, grp.rangeFilterValues.get(0).selectedMaxValue);
                }
            } else if(type == RADIO_BUTTON) {
                for(TermFilter filter : grp.termFilterValues) {
                    if(filter.selected) {
                        if(RADIO_BUTTON_BOTH_SELECTED.equals(filter.value)) {
                            for(TermFilter filter2 : grp.termFilterValues) {
                                if(!filter2.selected && !RADIO_BUTTON_BOTH_SELECTED.equals(filter2.value)) {
                                    selector.term(filter2.fieldName, filter2.value);
                                }
                            }
                        } else {
                            selector.term(filter.fieldName, filter.value);
                            searchNameBuilder.append(filter.displayName);
                        }
                    }
                }
            } else if(type == RADIO_BUTTON_RANGE) {
                for(RangeFilter filter : grp.rangeFilterValues) {
                    if(filter.selected) {
                        if(filter.minValue != UNEXPECTED_VALUE || filter.maxValue != UNEXPECTED_VALUE) {
                            Long minValue = (long)UNEXPECTED_VALUE;
                            Long maxValue = (long)UNEXPECTED_VALUE;
                            if (grp.type.equalsIgnoreCase(TYPE_YEAR)) {
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
                                searchNameBuilder.append(minValue + "-" + maxValue);
                            } else if(grp.type.equalsIgnoreCase(TYPE_DAY)) {
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
                                searchNameBuilder.append(minValue + "-" + maxValue);
                            } else {
                                selector.range(filter.fieldName, filter.minValue, filter.maxValue);
                                searchNameBuilder.append(filter.minValue + "-" + filter.maxValue);
                            }
                        }
                    }
                }
            } else if(type == RADIO_BUTTON_MIN_MAX) {

                for(RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                    if(filter.selected) {
                        if (grp.type.equalsIgnoreCase(TYPE_YEAR)) {
                            Calendar cal = Calendar.getInstance();
                            long minValue = UNEXPECTED_VALUE;
                            long maxValue = UNEXPECTED_VALUE;
                            if(filter.minValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.YEAR, (int) filter.minValue);
                                minValue = cal.getTimeInMillis();
                                cal.add(Calendar.YEAR, -(int) filter.minValue);
                            }
                            if(filter.maxValue != UNEXPECTED_VALUE) {
                                cal.add(Calendar.YEAR, (int) filter.maxValue);
                                maxValue = cal.getTimeInMillis();
                            }
                            String s = "";
                            if(minValue != UNEXPECTED_VALUE) {
                                selector.range(filter.minFieldName, minValue, null);
                                searchNameBuilder.append(minValue);
                                s = "-";
                            }
                            if(maxValue != UNEXPECTED_VALUE) {
                                selector.range(filter.maxFieldName, null, maxValue);
                                searchNameBuilder.append(s);
                                searchNameBuilder.append(maxValue);
                            }
                        } else {
                            String s = "";
                            if(filter.minValue != UNEXPECTED_VALUE) {
                                selector.term(filter.minFieldName, String.valueOf(filter.minValue));
                                searchNameBuilder.append(String.valueOf(filter.minValue));
                                s = "-";
                            }
                            if(filter.maxValue != UNEXPECTED_VALUE) {
                                selector.term(filter.maxFieldName, String.valueOf(filter.maxValue));
                                searchNameBuilder.append(s);
                                searchNameBuilder.append(String.valueOf(filter.maxValue));
                            }
                        }
                    }
                }
            } else {
                String s = "";
                for(TermFilter filter : grp.termFilterValues) {
                    if(filter.selected) {
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
                        searchNameBuilder.append(s);
                        searchNameBuilder.append(String.valueOf(filter.displayName));
                        s = ",";
                    }
                }
            }
            if("i_beds".equalsIgnoreCase(grp.internalName)) {
                searchNameBuilder.append(" bhk");
            }
            separator = "|";
        }
        if(mListingGetEvent.listingData.facets != null) {
            searchNameBuilder.append(";");
            searchNameBuilder.append(mListingGetEvent.listingData.facets.buildDisplayName());
            mListingGetEvent.listingData.facets.applySelector(selector);
        }
        mSearchName = searchNameBuilder.toString();
        return selector;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog_fragment_theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @OnClick(R.id.fragment_set_alerts_back_button)
    public void onBackClicked(View view) {
        dismiss();
    }

    @OnClick(R.id.fragment_set_alerts_submit_button)
    public void onSubmitClicked (View view) {
        getSearchName();
        ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).saveNewSearch(createSelector(), getSearchName());
    }

    private String getSearchName() {
        if(TextUtils.isEmpty(mSearchName)) {
            return mSetAlertsNameEditText.getText().toString();
        }
        return mSetAlertsNameEditText.getText().toString() + ";" + mSearchName;
    }
}
