package com.makaan.fragment.listing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.event.serp.SerpGetEvent;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.fragment.MakaanBaseDialogFragment;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.saveSearch.SaveNewSearch;
import com.makaan.request.selector.Selector;
import com.makaan.response.saveSearch.SaveSearch;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchSuggestionType;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.util.JsonBuilder;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.OnClick;

import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON;
import static com.makaan.adapter.listing.FiltersViewAdapter.CHECKBOX_MIN_MAX;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_RANGE;
import static com.makaan.adapter.listing.FiltersViewAdapter.SEEKBAR;
import static com.makaan.adapter.listing.FiltersViewAdapter.UNEXPECTED_VALUE;

/**
 * Created by rohitgarg on 2/11/16.
 */
public class SetAlertsDialogFragment extends MakaanBaseDialogFragment {
    public static final String SEPARATOR_FILTER = "/";
    @Bind(R.id.fragment_set_alerts_content_linear_layout)
    LinearLayout mContentLinearLayout;
    @Bind(R.id.fragment_set_alerts_name_edit_text)
    EditText mSetAlertsNameEditText;

    private ArrayList<FilterGroup> mGroups;
    private SerpGetEvent mListingGetEvent;
    private String mSearchName;
    private boolean mIsBuyContext;
    private SerpRequest mSerpRequest;
    private Context mContext;
    private boolean isLoginInitiated;
    private boolean isAfterLoginInitiated;
    private boolean isSubmitInitiated;
    private String serpSubCategory;
    private String bedroom,propertyType,budget,idForSetAlertEvent;

    public void setData(ArrayList<FilterGroup> grps, SerpGetEvent listingGetEvent, boolean serpContext, Context context, String serpSubCategory) {
        mGroups = grps;
        mListingGetEvent = listingGetEvent;
        mIsBuyContext = serpContext;
        mContext = context;
        this.serpSubCategory=serpSubCategory;
    }

    public void setData(ArrayList<FilterGroup> grps, SerpRequest serpRequest, boolean serpContext, Context context, String serpSubCategory) {
        mGroups = grps;
        mSerpRequest = serpRequest;
        mIsBuyContext = serpContext;
        mContext = context;
        this.serpSubCategory=serpSubCategory;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if(mGroups == null){
            dismiss();
            return view;
        }
        if (MasterDataCache.getInstance().getSavedSearch() != null && MasterDataCache.getInstance().getSavedSearch().size() > 0) {
            /*SaveSearchService saveSearchService =
                    (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
            saveSearchService.getSavedSearches();*/

            showContent();

            populateData();
            mSetAlertsNameEditText.setText(handleSearchName(new SaveSearch.JSONDump()));
            mSetAlertsNameEditText.setSelection(mSetAlertsNameEditText.getText().length());
            mSetAlertsNameEditText.requestFocus();
            sendSetAlertEvent();
        } else {
            // get saved searches
            SaveSearchService saveSearchService =
                    (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
            saveSearchService.getSavedSearches();

            showProgress();
        }


        return view;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_set_alert;
    }

    private String handleSearchName(SaveSearch.JSONDump jsonDump) {
        String name = "";
        if (mSerpRequest != null) {
            ArrayList<SearchResponseItem> searches = mSerpRequest.getSearches();
            if (searches.size() == 1) {
                if (jsonDump != null) {
                    if (SearchSuggestionType.LOCALITY.getValue().equalsIgnoreCase(searches.get(0).type)) {
                        jsonDump.localityName = searches.get(0).entityName;
                        if (searches.get(0).city != null) {
                            jsonDump.cityName = searches.get(0).city;
                        }
                        setIdForSetAlertEvent(SearchSuggestionType.LOCALITY, searches.get(0).entityId);
                    } else if (SearchSuggestionType.CITY.getValue().equalsIgnoreCase(searches.get(0).type)) {
                        jsonDump.cityName = searches.get(0).entityName;
                        setIdForSetAlertEvent(SearchSuggestionType.CITY, searches.get(0).entityId);
                    } else if (SearchSuggestionType.BUILDER.getValue().equalsIgnoreCase(searches.get(0).type)) {
                        jsonDump.builderName = searches.get(0).entityName;
                        setIdForSetAlertEvent(SearchSuggestionType.BUILDER, searches.get(0).entityId);

                    } else if (SearchSuggestionType.SELLER.getValue().equalsIgnoreCase(searches.get(0).type)) {
                        jsonDump.sellerName = searches.get(0).entityName;
                        setIdForSetAlertEvent(SearchSuggestionType.SELLER, searches.get(0).entityId);

                    } else if (SearchSuggestionType.PROJECT.getValue().equalsIgnoreCase(searches.get(0).type)) {
                        jsonDump.projectName = searches.get(0).displayText;
                        setIdForSetAlertEvent(SearchSuggestionType.PROJECT, searches.get(0).entityId);
                    }
                }
                name = searches.get(0).displayText;
            } else if (searches.size() > 1) {
                if (jsonDump != null) {
                    jsonDump.localityName = String.format("%s + %d", searches.get(0).entityName, (searches.size() - 1));
                }
                name = searches.get(0).displayText + " + " + (searches.size() - 1);
                ArrayList<String>ids=new ArrayList<String>();
                for (SearchResponseItem searchItem : searches) {
                    ids.add(searchItem.entityId);
                }
                setIdForSetAlertEvent(SearchSuggestionType.LOCALITY, ids.toString());
            }
        } else if (mListingGetEvent != null) {
            name = mListingGetEvent.listingData.facets != null ? mListingGetEvent.listingData.facets.getSearchName() : null;
        }
        if (TextUtils.isEmpty(name)) {
            StringBuilder bhk = new StringBuilder();
            StringBuilder budget = new StringBuilder();
            String separator = "";
            for (FilterGroup grp : mGroups) {
                if (!grp.isSelected) {
                    continue;
                }
                if ("i_beds".equalsIgnoreCase(grp.internalName)) {
                    for (TermFilter filter : grp.termFilterValues) {
                        if (filter.selected) {
                            bhk.append(separator);
                            bhk.append(filter.displayName);
                            separator = ", ";
                        }
                    }
                }
                if ("i_budget".equalsIgnoreCase(grp.internalName)) {
                    for (RangeFilter filter : grp.rangeFilterValues) {
                        if (filter.selectedMinValue != filter.minValue || filter.selectedMaxValue != filter.maxValue) {
                            budget.append(separator);
                            budget.append(StringUtil.getDisplayPrice(filter.selectedMinValue));
                            budget.append("-");
                            budget.append(StringUtil.getDisplayPrice(filter.selectedMaxValue));
                            separator = ", ";
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(bhk.toString())) {
                bhk.append(" bhk");
            }
            if (!(TextUtils.isEmpty(bhk.toString()) && TextUtils.isEmpty(budget.toString()))) {
                return bhk.toString() + budget.toString();
            } else {
                return "save search 1";
            }
        } else {
            return name;
        }
    }

    private void populateData() {
        mContentLinearLayout.removeAllViews();
        StringBuilder builder = new StringBuilder();
        for (FilterGroup grp : mGroups) {
            if (!("i_beds".equalsIgnoreCase(grp.internalName)
                    || "i_budget".equalsIgnoreCase(grp.internalName)
                    || "i_property_type".equalsIgnoreCase(grp.internalName))) {
                continue;
            }
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
            mContentLinearLayout.addView(view);
            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText(grp.displayName);

            if (grp.imageName != null) {
                Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap(grp.imageName);
                if (bitmap != null) {
                    ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageBitmap(bitmap);
                } else {
                    int id = this.getResources().getIdentifier(grp.imageName, "drawable", "com.makaan");
                    Bitmap b = BitmapFactory.decodeResource(getResources(), id);
                    ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageBitmap(b);
                    MakaanBuyerApplication.bitmapCache.putBitmap(grp.imageName, b);
                }
            }

            if (!grp.isSelected) {
                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText("not selected");
                continue;
            }

            if (builder.length() > 0) {
                builder.delete(0, builder.length());
            }

            String separator = "";
            for (TermFilter filter : grp.termFilterValues) {
                if (filter.selected) {
                    builder.append(separator);
                    builder.append(filter.displayName);
                    separator = ",";
                }
                if("i_beds".equalsIgnoreCase(grp.internalName)) {
                    bedroom=builder.toString();
                } else if("i_property_type".equalsIgnoreCase(grp.internalName)){
                    propertyType=builder.toString();
                }
            }

            for (RangeFilter filter : grp.rangeFilterValues) {
                if (filter.selectedMinValue != filter.minValue || filter.selectedMaxValue != filter.maxValue) {
                    builder.append(separator);
                    if ("i_budget".equalsIgnoreCase(grp.internalName)) {
                        builder.append(StringUtil.getDisplayPrice(filter.selectedMinValue));
                    } else {
                        builder.append(filter.selectedMinValue);
                    }
                    builder.append(" - ");
                    if ("i_budget".equalsIgnoreCase(grp.internalName)) {
                        builder.append(StringUtil.getDisplayPrice(filter.selectedMaxValue));
                    } else {
                        builder.append(filter.selectedMaxValue);
                    }
                    separator = ",";
                    budget=String.format("%s-%s",filter.selectedMinValue ,filter.selectedMaxValue);
                }
            }

            for (RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                if (filter.selected) {
                    builder.append(separator);
                    builder.append(filter.displayName);
                    separator = ",";
                }
            }

            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(builder.toString());
        }
        if (mSerpRequest != null) {
            ArrayList<SearchResponseItem> searches = mSerpRequest.getSearches();
            String location = "not available";
            if (searches.size() == 1) {
                location = searches.get(0).displayText;
            } else if (searches.size() > 1) {
                builder = new StringBuilder();
                String separator = "";
                for (SearchResponseItem item : searches) {
                    builder.append(separator);
                    builder.append(item.displayText);
                    separator = ", ";
                }
                location = builder.toString();
            }
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
            mContentLinearLayout.addView(view);
            ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText("location");

            ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(R.drawable.location);

            if (location != null) {
                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(location.toLowerCase());
            }

        } else if (mListingGetEvent != null && mListingGetEvent.listingData.facets != null) {
            String location = mListingGetEvent.listingData.facets.buildDisplayName();
            if (!TextUtils.isEmpty(location)) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_set_alerts_list_item, mContentLinearLayout, false);
                mContentLinearLayout.addView(view);
                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_display_text_view)).setText("location");

                ((ImageView) view.findViewById(R.id.fragment_set_alerts_list_item_image_view)).setImageResource(R.drawable.location);

                ((TextView) view.findViewById(R.id.fragment_set_alerts_list_item_content_text_view)).setText(location);
            }
        }
    }

    private void createSelector(SaveSearch saveSearch) {
        Selector selector = new Selector();
        SaveSearch.JSONDump jsonDump = new SaveSearch.JSONDump();

        if (!mIsBuyContext) {
            selector.term(KeyUtil.LISTING_CATEGORY, "Rental");
        }
        for (FilterGroup grp : mGroups) {
            if (!grp.isSelected) {
                if ("i_new_resale".equalsIgnoreCase(grp.internalName)) {
                    if (mIsBuyContext) {
                        selector.term(KeyUtil.LISTING_CATEGORY, "Primary");
                        selector.term(KeyUtil.LISTING_CATEGORY, "Resale");
                    }
                }
                continue;
            }

            int type = grp.layoutType;

            if (type == SEEKBAR) {
                RangeFilter filter = grp.rangeFilterValues.get(0);
                if (filter.selectedMinValue > filter.minValue || filter.selectedMaxValue < filter.maxValue) {
                    if ("i_budget".equalsIgnoreCase(grp.internalName)) {
                        jsonDump.priceRange = StringUtil.getDisplayPrice(grp.rangeFilterValues.get(0).selectedMinValue).replace("\u20B9", "")
                                + "-" + StringUtil.getDisplayPrice(grp.rangeFilterValues.get(0).selectedMaxValue).replace("\u20B9", "");
                    }

                    selector.range(grp.rangeFilterValues.get(0).fieldName, grp.rangeFilterValues.get(0).selectedMinValue, grp.rangeFilterValues.get(0).selectedMaxValue);
                }
            } else if (type == RADIO_BUTTON) {
                for (TermFilter filter : grp.termFilterValues) {
                    if (filter.selected) {
                        selector.term(filter.fieldName, filter.value);
//                        searchNameBuilder.append(filter.displayName);
                    }
                }
            } else if (type == RADIO_BUTTON_RANGE) {
                for (RangeFilter filter : grp.rangeFilterValues) {
                    if (filter.selected) {
                        if (filter.minValue != UNEXPECTED_VALUE || filter.maxValue != UNEXPECTED_VALUE) {
                            Long minValue = (long) UNEXPECTED_VALUE;
                            Long maxValue = (long) UNEXPECTED_VALUE;
                            // TODO check if there can be other case possible than YEAR case
                            //if (grp.type.equalsIgnoreCase(TYPE_YEAR)) {
                            if (filter.minValue != UNEXPECTED_VALUE) {
                                minValue = (long) filter.minValue;
                            }

                            if (filter.maxValue != UNEXPECTED_VALUE) {
                                maxValue = (long) filter.maxValue;
                            }
                            if (minValue == UNEXPECTED_VALUE) {
                                minValue = null;
                            }
                            if (maxValue == UNEXPECTED_VALUE) {
                                maxValue = null;
                            }

                            selector.range(filter.fieldName, minValue, maxValue);
//                                searchNameBuilder.append(minValue + "-" + maxValue);
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
            } else if (type == CHECKBOX_MIN_MAX) {

                long minValue = Long.MAX_VALUE;
                long maxValue = Long.MIN_VALUE;
                String minFieldName = "";
                String maxFieldName = "";
                boolean filterApplied = false;
                for (RangeMinMaxFilter filter : grp.rangeMinMaxFilterValues) {
                    if (filter.selected) {
                        Calendar cal = Calendar.getInstance();
                        if (filter.minValue != UNEXPECTED_VALUE) {
                            cal.add(Calendar.YEAR, (int) filter.minValue);
                            if(cal.getTimeInMillis() < minValue) {
                                minValue = cal.getTimeInMillis();
                            }
                            cal.add(Calendar.YEAR, -(int) filter.minValue);
                        }
                        if (filter.maxValue != UNEXPECTED_VALUE) {
                            cal.add(Calendar.YEAR, (int) filter.maxValue);
                            if(cal.getTimeInMillis() > maxValue) {
                                maxValue = cal.getTimeInMillis();
                            }
                        }

                        minFieldName = filter.minFieldName;
                        maxFieldName = filter.maxFieldName;
                        filterApplied = true;
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
                String s = "";
                for (TermFilter filter : grp.termFilterValues) {
                    if (filter.selected) {
                        if (filter.displayName.contains(FiltersViewAdapter.INFINITE_SELECTOR)) {
                            String[] val = filter.value.split(FiltersViewAdapter.MIN_MAX_SEPARATOR);
                            int min = Integer.valueOf(val[0]);
                            int max = Integer.valueOf(val[1]);
                            for (int i = min; i <= max; i++) {
                                selector.term(filter.fieldName, String.valueOf(i));
                            }
                        } else {
                            selector.term(filter.fieldName, filter.value);
                        }
                        if ("i_beds".equalsIgnoreCase(grp.internalName)) {
                            if (jsonDump.bhk == null) {
                                jsonDump.bhk = "";
                            }
                            jsonDump.bhk = jsonDump.bhk.concat(s);
                            jsonDump.bhk = jsonDump.bhk.concat(String.valueOf(filter.displayName));
                            s = ",";
                        }
                    }
                }
            }
            if ("i_beds".equalsIgnoreCase(grp.internalName)) {
                jsonDump.bhk = jsonDump.bhk.concat(" bhk");
            }
        }

        if (mSerpRequest != null) {
            mSerpRequest.applySelector(selector, null, false, true);
            handleSearchName(jsonDump);
        } else if (mListingGetEvent != null && mListingGetEvent.listingData.facets != null) {
            jsonDump.localityName = mListingGetEvent.listingData.facets.buildDisplayName();
            mListingGetEvent.listingData.facets.applySelector(selector);
        }
        saveSearch.searchQuery = selector.build();
        try {
            saveSearch.jsonDump = JsonBuilder.toJson(jsonDump).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onSubmitClicked(View view) {
        if (null == MasterDataCache.getInstance().getUserData()) {
            isLoginInitiated = true;
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
                            /*Toast.makeText(mContext, "name already exists", Toast.LENGTH_SHORT).show();*/
                            MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                                    "name already exists", "ok");
                            return;
                        }
                    }
                }
            }

            SaveSearch saveSearch = new SaveSearch();
            saveSearch.name = mSetAlertsNameEditText.getText().toString();
            createSelector(saveSearch);
            SaveNewSearch saveNewSearch = new SaveNewSearch();
            saveNewSearch.name = saveSearch.name;
            saveNewSearch.searchQuery = saveSearch.searchQuery;
            saveNewSearch.jsonDump = saveSearch.jsonDump;
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class)).saveNewSearch(saveNewSearch);
            isAfterLoginInitiated = false;
            isLoginInitiated = false;

            isSubmitInitiated = true;
        }
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent) {
        if(!isVisible()) {
            return;
        }
        if (null == userLoginEvent || null != userLoginEvent.error) {
            showNoResults(R.string.generic_error);

        }

        SaveSearchService saveSearchService =
                (SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class);
        saveSearchService.getSavedSearches();

        if (!isLoginInitiated) {
            return;
        }

        isAfterLoginInitiated = true;
    }

    @Subscribe
    public void onResult(SaveSearchGetEvent event) {
        if(!isVisible()) {
            return;
        }
        if (event == null || event.error != null) {
            if (isSubmitInitiated) {
                MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                        (event != null && event.error != null && !TextUtils.isEmpty(event.error.msg)) ? event.error.msg : getString(R.string.generic_error), "ok");
            } else {
                if (event != null && event.error != null && event.error.error != null
                        && event.error.error.networkResponse != null && event.error.error.networkResponse.statusCode == 401) {
                    showContent();

                    populateData();
                    mSetAlertsNameEditText.setText(handleSearchName(new SaveSearch.JSONDump()));
                    mSetAlertsNameEditText.setSelection(mSetAlertsNameEditText.getText().length());
                    mSetAlertsNameEditText.requestFocus();
                    sendSetAlertEvent();
                } else if (event != null && event.error != null && event.error.msg != null) {
                    showNoResults(event.error.msg);
                } else {
                    showNoResults(getString(R.string.generic_error));
                }
            }
        } else {
            if (isSubmitInitiated) {
                if (event.saveSearchArrayList != null && event.saveSearchArrayList.size() > 0) {
                    if (!TextUtils.isEmpty(event.saveSearchArrayList.get(0).name)
                            && mSetAlertsNameEditText.getText().toString().equalsIgnoreCase(event.saveSearchArrayList.get(0).name)) {
                        /*--------------------track-----------------events------------*/
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, serpSubCategory);
                        if (mIsBuyContext) {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY,
                                    getSetAlertSubmitString()));
                        } else {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.RENT,
                                    getSetAlertSubmitString()));
                        }
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.setAlertSubmit);
                        /*---------------------------------------------------------------------*/
                        MasterDataCache.getInstance().clearSavedSearches();

                        MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                                "successfully saved", "ok", new MakaanMessageDialogFragment.MessageDialogCallbacks() {
                                    @Override
                                    public void onPositiveClicked() {
                                        dismissAllowingStateLoss();
                                    }

                                    @Override
                                    public void onNegativeClicked() {

                                    }
                                });
                    } else if (!TextUtils.isEmpty(event.saveSearchArrayList.get(0).name)) {
                        MakaanMessageDialogFragment.showMessage(getFragmentManager(),
                                "search query already exists", "ok");
                    }
                }
            } else {
                if (isAfterLoginInitiated && isLoginInitiated) {
                    onSubmitClicked(null);
                }
                showContent();

                populateData();
                mSetAlertsNameEditText.setText(handleSearchName(new SaveSearch.JSONDump()));
                mSetAlertsNameEditText.setSelection(mSetAlertsNameEditText.getText().length());
                mSetAlertsNameEditText.requestFocus();
                sendSetAlertEvent();
            }
        }

    }

    public String getSetAlertSubmitString(){
        StringBuilder builder = new StringBuilder();
        String semiColon = ";";
        if (!TextUtils.isEmpty(idForSetAlertEvent)) {
            builder.append(idForSetAlertEvent);
            builder.append(semiColon);
        }
        if (!TextUtils.isEmpty(bedroom)) {
            builder.append("Bedroom ");
            builder.append(bedroom);
            builder.append(semiColon);
        }
        if (!TextUtils.isEmpty(budget)) {
            builder.append("Budget ");
            builder.append(budget);
            builder.append(semiColon);
        }
        if (!TextUtils.isEmpty(propertyType)) {
            builder.append("Propertytype ");
            builder.append(propertyType);
        }
        return builder.toString();
    }


    private void setIdForSetAlertEvent(SearchSuggestionType value, String entityId) {

        switch (value) {
            case LOCALITY: {
                idForSetAlertEvent = String.format("%s %s", "LocalityId", entityId);
                break;
            }
            case CITY: {
                idForSetAlertEvent = String.format("%s %s", "CityId", entityId);
                break;
            }
            case BUILDER: {
                idForSetAlertEvent = String.format("%s %s", "BuilderId", entityId);
                break;
            }
            case SELLER: {
                idForSetAlertEvent = String.format("%s %s", "SellerId", entityId);
                break;
            }
            case PROJECT: {
                idForSetAlertEvent = String.format("%s %s", "ProjectId", entityId);
                break;
            }
        }
    }

    private void sendSetAlertEvent() {
         /*--------------------track-----------------events------------*/
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, serpSubCategory);
        if(mIsBuyContext) {
            if(!TextUtils.isEmpty(idForSetAlertEvent)) {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, idForSetAlertEvent));
            }else {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
            }
        }else {
            if(!TextUtils.isEmpty(idForSetAlertEvent)) {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.RENT, idForSetAlertEvent));
            }else {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.RENT, ""));
            }        }
        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.setAlertOpen);
        /*---------------------------------------------------------------------*/
    }

}
