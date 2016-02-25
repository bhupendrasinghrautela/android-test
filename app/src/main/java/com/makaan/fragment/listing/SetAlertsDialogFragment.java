package com.makaan.fragment.listing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.response.search.SearchResponse;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.SaveSearchService;
import com.makaan.util.AppBus;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

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
    public static final String SEPARATOR_FILTER = "/";
    @Bind(R.id.fragment_set_alerts_content_linear_layout)
    LinearLayout mContentLinearLayout;
    @Bind(R.id.fragment_set_alerts_name_edit_text)
    TextView mSetAlertsNameEditText;

    private ArrayList<FilterGroup> mGroups;
    private SerpGetEvent mListingGetEvent;
    private String mSearchName;
    private boolean mIsBuyContext;
    private SerpRequest mSerpRequest;
    private Context mContext;
    private boolean isSubmitInitiatedFromWishList;
    private boolean isAfterLoginInitiated;

    public void setData(ArrayList<FilterGroup> grps, SerpGetEvent listingGetEvent, boolean serpContext, Context context) {
        mGroups = grps;
        mListingGetEvent = listingGetEvent;
        mIsBuyContext = serpContext;
        mContext = context;
    }

    public void setData(ArrayList<FilterGroup> grps, SerpRequest serpRequest, boolean serpContext, Context context) {
        mGroups = grps;
        mSerpRequest = serpRequest;
        mIsBuyContext = serpContext;
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_alert, container, false);

        if(MasterDataCache.getInstance().getSavedSearch() == null) {
            SaveSearchService saveSearchService =
                    (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
            saveSearchService.getSavedSearches();
        }

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);

        populateData();
        handleSearchName();

        return view;
    }

    private void handleSearchName() {
        String name = null;
        StringBuilder builder = new StringBuilder();

        if(mSerpRequest != null) {
            ArrayList<SearchResponseItem> searches = mSerpRequest.getSearches();
            if(searches.size() == 1) {
                name = searches.get(0).displayText;
            } else if(searches.size() > 1) {
                name = searches.get(0).displayText + " + " + (searches.size() - 1);
            }
        } else if(mListingGetEvent != null) {
            name = mListingGetEvent.listingData.facets != null ? mListingGetEvent.listingData.facets.getSearchName() : null;
        }
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
            if(!("i_beds".equalsIgnoreCase(grp.internalName)
                    || "i_budget".equalsIgnoreCase(grp.internalName)
                    || "i_property_type".equalsIgnoreCase(grp.internalName))) {
                continue;
            }
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
            mContentLinearLayout.addView(view);
            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText(grp.displayName);
            int id = this.getResources().getIdentifier(grp.imageName, "drawable", "com.makaan");
            if (id != 0) {
                ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(id);
            }

            if(!grp.isSelected) {
                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText("not selected");
                continue;
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

            for(RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                if(filter.selected) {
                    builder.append(separator);
                    builder.append(filter.displayName);
                    separator = ",";
                }
            }

            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(builder.toString());
        }
        if(mSerpRequest != null) {
            ArrayList<SearchResponseItem> searches = mSerpRequest.getSearches();
            String location = "not available";
            if(searches.size() == 1) {
                location = searches.get(0).displayText;
            } else if(searches.size() > 1) {
                builder = new StringBuilder();
                String separator = "";
                for(SearchResponseItem item : searches) {
                    builder.append(separator);
                    builder.append(item.displayText);
                    separator = ", ";
                }
                location = builder.toString();
            }
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
            mContentLinearLayout.addView(view);
            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText("location");

            ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(R.drawable.location_icon);

            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(location);

        } else if(mListingGetEvent != null && mListingGetEvent.listingData.facets != null) {
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
        if(!mIsBuyContext) {
            selector.term(KeyUtil.LISTING_CATEGORY, "Rental");
        }
        for (FilterGroup grp : mGroups) {
            if(!grp.isSelected) {
                if("i_new_resale".equalsIgnoreCase(grp.internalName)) {
                    if(mIsBuyContext) {
                        selector.term(KeyUtil.LISTING_CATEGORY, "Primary");
                        selector.term(KeyUtil.LISTING_CATEGORY, "Resale");
                    }
                }
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
                        selector.term(filter.fieldName, filter.value);
                        searchNameBuilder.append(filter.displayName);
                    }
                }
            } else if(type == RADIO_BUTTON_RANGE) {
                for(RangeFilter filter : grp.rangeFilterValues) {
                    if(filter.selected) {
                        if(filter.minValue != UNEXPECTED_VALUE || filter.maxValue != UNEXPECTED_VALUE) {
                            Long minValue = (long)UNEXPECTED_VALUE;
                            Long maxValue = (long)UNEXPECTED_VALUE;
                            // TODO check if there can be other case possible than YEAR case
                            //if (grp.type.equalsIgnoreCase(TYPE_YEAR)) {
                                if(filter.minValue != UNEXPECTED_VALUE) {
                                    minValue = (long)filter.minValue;
                                }

                                if(filter.maxValue != UNEXPECTED_VALUE) {
                                    maxValue = (long)filter.maxValue;
                                }
                                if(minValue == UNEXPECTED_VALUE) {
                                    minValue = null;
                                }
                                if(maxValue == UNEXPECTED_VALUE) {
                                    maxValue = null;
                                }

                                selector.range(filter.fieldName, minValue, maxValue);
                                searchNameBuilder.append(minValue + "-" + maxValue);
                            /*} else if(grp.type.equalsIgnoreCase(TYPE_DAY)) {
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
                            }*/
                        }
                    }
                }
            } else if(type == RADIO_BUTTON_MIN_MAX) {

                for(RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                    if(filter.selected) {
                        // TODO check if there can be other case possible than YEAR case
                        //if (grp.type.equalsIgnoreCase(TYPE_YEAR)) {
                            Calendar cal = Calendar.getInstance();
                            long minValue = UNEXPECTED_VALUE;
                            long maxValue = UNEXPECTED_VALUE;
                            if(filter.minValue != UNEXPECTED_VALUE) {
                                minValue = (long)filter.minValue;
                            }
                            if(filter.maxValue != UNEXPECTED_VALUE) {
                                maxValue = (long)filter.maxValue;
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
                        /*} else {
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
                        }*/
                    }
                }
            } else {
                String s = "";
                for(TermFilter filter : grp.termFilterValues) {
                    if(filter.selected) {
                        selector.term(filter.fieldName, filter.value);
                        searchNameBuilder.append(s);
                        searchNameBuilder.append(String.valueOf(filter.displayName));
                        s = ",";
                    }
                }
            }
            if("i_beds".equalsIgnoreCase(grp.internalName)) {
                searchNameBuilder.append(" bhk");
            }
            separator = SEPARATOR_FILTER;
        }

        if(mSerpRequest != null) {
            mSerpRequest.applySelector(selector, null, false, true);
        } else if(mListingGetEvent != null && mListingGetEvent.listingData.facets != null) {
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
        if(null == MasterDataCache.getInstance().getUserData()) {
            isSubmitInitiatedFromWishList = true;
            Intent intent = new Intent(mContext, UserLoginActivity.class);
            mContext.startActivity(intent);
        } else {
            ArrayList<SaveSearch> savedSearches = MasterDataCache.getInstance().getSavedSearch();
            if (savedSearches != null && savedSearches.size() > 0) {
                for (SaveSearch search : savedSearches) {

                    String name = search.name;
                    if (!TextUtils.isEmpty(name)) {
                        int semiIndex = name.indexOf(";");
                        if (semiIndex >= 0) {
                            name = name.substring(0, semiIndex);
                        }
                        if (name.equalsIgnoreCase(mSetAlertsNameEditText.getText().toString())) {
                            Toast.makeText(mContext, "name already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).saveNewSearch(createSelector(), getSearchName());
            isAfterLoginInitiated = false;
            isSubmitInitiatedFromWishList = false;

            SaveSearchService saveSearchService =
                    (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
            saveSearchService.getSavedSearches();

            dismissAllowingStateLoss();
        }
    }

    private String getSearchName() {
        if(TextUtils.isEmpty(mSearchName)) {
            return mSetAlertsNameEditText.getText().toString();
        }
        return mSetAlertsNameEditText.getText().toString() + ";" + mSearchName;
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent){
        if(null==userLoginEvent || null!=userLoginEvent.error){
            Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        }

        SaveSearchService saveSearchService =
                (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
        saveSearchService.getSavedSearches();

        if(!isSubmitInitiatedFromWishList){
            return;
        }

        isAfterLoginInitiated = true;

        if(userLoginEvent.error != null){
            Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onResult(SaveSearchGetEvent event) {
        if(event == null || event.error != null) {
            // TODO
            return;
        } else {
            if(isAfterLoginInitiated && isSubmitInitiatedFromWishList) {
                onSubmitClicked(null);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }
}
