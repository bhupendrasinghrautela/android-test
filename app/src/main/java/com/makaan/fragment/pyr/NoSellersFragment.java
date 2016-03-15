package com.makaan.fragment.pyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.event.saveSearch.SaveSearchGetEvent;
import com.makaan.fragment.listing.SetAlertsDialogFragment;
import com.makaan.network.VolleyErrorParser;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.RangeMinMaxFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;
import com.makaan.util.AppBus;
import com.makaan.util.KeyUtil;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_MIN_MAX;
import static com.makaan.adapter.listing.FiltersViewAdapter.RADIO_BUTTON_RANGE;
import static com.makaan.adapter.listing.FiltersViewAdapter.SEEKBAR;
import static com.makaan.adapter.listing.FiltersViewAdapter.UNEXPECTED_VALUE;

/**
 * Created by proptiger on 24/1/16.
 */
public class NoSellersFragment extends Fragment {

    public static final String SEPARATOR_FILTER = "/";
    private String mSearchName;

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppBus.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppBus.getInstance().register(this);
        View view = inflater.inflate(R.layout.no_sellers_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.set_alert)
    public void setAlert(){
        callAlert();
    }

    public void callAlert(){
        SerpRequest serpRequest=PyrPagePresenter.getPyrPagePresenter().getserpRequestObject();
        ArrayList<FilterGroup> filterGroups= PyrPagePresenter.getPyrPagePresenter().getSerpFilterGroups();

        if(PyrPagePresenter.getPyrPagePresenter().getPyrRequestObject().getSalesType().equalsIgnoreCase("buy")) {
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class
            )).saveNewSearch(createSelector(filterGroups, true, serpRequest), getSearchName(),
                    PyrPagePresenter.getPyrPagePresenter().getPyrRequestObject().getEmail());
        }
        else if(PyrPagePresenter.getPyrPagePresenter().getPyrRequestObject().getSalesType().equalsIgnoreCase("rent")){
            ((SaveSearchService) MakaanServiceFactory.getInstance().getService(SaveSearchService.class
            )).saveNewSearch(createSelector(filterGroups, false, serpRequest), getSearchName(),
                    PyrPagePresenter.getPyrPagePresenter().getPyrRequestObject().getEmail());
        }

    }

    private Selector createSelector(ArrayList<FilterGroup> mGroups, boolean mIsBuyContext, SerpRequest mSerpRequest) {
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
                    }

                    selector.range(grp.rangeFilterValues.get(0).fieldName, grp.rangeFilterValues.get(0).selectedMinValue, grp.rangeFilterValues.get(0).selectedMaxValue);
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
        }
        mSearchName = searchNameBuilder.toString();
        return selector;
    }

    private String getSearchName(){
        String searchName=null;
        Bundle bundle=getArguments();
        if(bundle!=null){
            if(bundle.get(PyrPageActivity.KEY_LOCALITY_NAME)!=null||buildLocalitySearchName()!=null){
                if(buildLocalitySearchName()!=null){
                    searchName=buildLocalitySearchName();
                }
                else {
                    searchName = (String) bundle.get(PyrPageActivity.KEY_LOCALITY_NAME);
                }
            }
            else if(bundle.get(PyrPageActivity.KEY_CITY_NAME)!=null){
                searchName=(String)bundle.get(PyrPageActivity.KEY_CITY_NAME);
            }
            else {
                searchName="save search 1";
            }
        }
        return searchName;
    }

    private String buildLocalitySearchName(){
        ArrayList<SearchResponseItem> localityIds=PyrPagePresenter.getPyrPagePresenter().getAlreadySelectedProjects();
        String searchName=null;
        if(localityIds.size()==1){
            searchName=localityIds.get(0).entityName;
        }
        else if(localityIds.size()>1){
            searchName=localityIds.get(0).entityName+String.valueOf(localityIds.size()-1);
        }

        return searchName;
    }

    @Subscribe
    public void setAlertResponse( SaveSearchGetEvent saveSearchGetEvent){
        if(!isVisible()) {
            return;
        }
        if(saveSearchGetEvent.error==null){
            PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
            pyrPagePresenter.showThankYouScreenFragment(true, false, true);
        }
        else{
            Toast.makeText(getContext(), VolleyErrorParser.getMessage(saveSearchGetEvent.error.error), Toast.LENGTH_SHORT).show();
        }
    }
}
