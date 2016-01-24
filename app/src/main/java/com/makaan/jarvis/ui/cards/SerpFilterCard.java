package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.GridView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.Message;
import com.makaan.response.serp.FilterGroup;
import com.makaan.ui.view.BaseView;
import com.makaan.ui.view.ExpandableHeightGridView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 23/01/16.
 */
public class SerpFilterCard extends BaseView<ExposeMessage> {

    @Bind(R.id.serp_jarvis_filters_item_layout_grid_view)
    ExpandableHeightGridView mFilterGridView;

    private Context mContext;
    private OnApplyClickListener mOnApplyClickListener;

    public SerpFilterCard(Context context) {
        super(context);
        mContext = context;
    }

    public SerpFilterCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SerpFilterCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public interface OnApplyClickListener{
        void onApplyClick();
    }

    @Override
    public void bindView(Context context, ExposeMessage item) {
        try {

            String filter = getFilterType(item);
            if(TextUtils.isEmpty(filter)){
                return;
            }

            if (MakaanBuyerApplication.serpSelector.isBuyContext()) {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllBuyFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups), filter );
            } else {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllRentFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups), filter);
            }
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

    }

    public void setOnApplyClickListener(OnApplyClickListener onApplyClickListener){
        mOnApplyClickListener = onApplyClickListener;
    }

    private void populateFilters(ArrayList<FilterGroup> filterGroups, String filter) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if(filterGroup.displayOrder < 0) {
                    continue;
                }

                if(filter.equalsIgnoreCase(filterGroup.displayName)){
                    if (mFilterGridView != null) {
                        mFilterGridView.setExpanded(true);
                        FiltersViewAdapter adapter = new FiltersViewAdapter(mContext, filterGroup, filterGroup.layoutType);
                        mFilterGridView.setAdapter(adapter);
                        if(filterGroup.layoutType != FiltersViewAdapter.TOGGLE_BUTTON) {
                            mFilterGridView.setNumColumns(1);
                        }
                    }
                }
            }
        }
    }

    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for(FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    @OnClick(R.id.btn_apply)
    public void onFilterApply(){
        ArrayList<FilterGroup> filterGroups;
        if (MakaanBuyerApplication.serpSelector.isBuyContext()) {
            filterGroups = MasterDataCache.getInstance().getAllBuyFilterGroups();
        } else {
            filterGroups = MasterDataCache.getInstance().getAllRentFilterGroups();
        }

        FiltersViewAdapter adapter = (FiltersViewAdapter) (mFilterGridView.getAdapter());
        adapter.applyFilters(MakaanBuyerApplication.serpSelector, filterGroups);

        if(MasterDataCache.getAppliedFilterCount() > 0) {
            MakaanBuyerApplication.serpSelector.sort("price", "desc");
        }
        if (mContext instanceof SerpRequestCallback) {
            ((SerpRequestCallback) mContext).serpRequest(SerpActivity.TYPE_FILTER, MakaanBuyerApplication.serpSelector);
        }


        Intent intent = new Intent(mContext, SerpActivity.class);
        intent.putExtra(SerpActivity.REQUEST_TYPE, SerpActivity.TYPE_FILTER);
        mContext.startActivity(intent);

        if(null!=mOnApplyClickListener){
            mOnApplyClickListener.onApplyClick();
        }


    }

    private String getFilterType(ExposeMessage item){
        //This will be picked from config file

        if(item.properties.suggest_filter.equalsIgnoreCase("bhk")){
            return "Beds";
        } else if(item.properties.suggest_filter.equalsIgnoreCase("budget")){
            return "Budget";
        } else if(item.properties.suggest_filter.equalsIgnoreCase("property_type")){
            return "Property Type";
        }
        return "";
    }
}
