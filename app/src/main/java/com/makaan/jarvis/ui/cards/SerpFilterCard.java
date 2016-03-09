package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.listing.SerpRequestCallback;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.analytics.SerpFilterMessageMap;
import com.makaan.jarvis.message.ExposeMessage;
import com.makaan.jarvis.message.Message;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.ui.view.BaseView;
import com.makaan.ui.view.ExpandableHeightGridView;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 23/01/16.
 */
public class SerpFilterCard extends BaseCtaView<ExposeMessage>{

    @Bind(R.id.serp_jarvis_filters_item_layout_grid_view)
    ExpandableHeightGridView mFilterGridView;

    @Bind(R.id.txt_message)
    TextView mAlertTitle;

    private Context mContext;

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


    @Override
    public void bindView(Context context, ExposeMessage item) {
        try {

            String internalName = getFilterType(item);
            if(TextUtils.isEmpty(internalName)){
                return;
            }

            ArrayList<FilterGroup> filterGroups = SerpObjects.getFilterGroups(getContext());
            populateFilters(getClonedFilterGroups(filterGroups), internalName);
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

    }

    private void populateFilters(ArrayList<FilterGroup> filterGroups, String internalName) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if(filterGroup.displayOrder < 0) {
                    continue;
                }

                if(internalName.equalsIgnoreCase(filterGroup.internalName)){
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
        ArrayList<FilterGroup> filterGroups = SerpObjects.getFilterGroups(getContext());
        Selector selector = SerpObjects.getSerpSelector(getContext());

        FiltersViewAdapter adapter = (FiltersViewAdapter) (mFilterGridView.getAdapter());
        adapter.applyFilters(selector, filterGroups);

        SerpRequest request = new SerpRequest(SerpActivity.TYPE_FILTER);
        request.launchSerp(getContext());


        if(null!=mOnApplyClickListener){
            mOnApplyClickListener.onApplyClick();
        }
    }


    @OnClick(R.id.btn_cancel)
    public void onCacelClick(){
        if(null!=mOnCancelClickListener){
            mOnCancelClickListener.onCancelClick();
        }
    }

    private String getFilterType(ExposeMessage item){
        Map<String, SerpFilterMessageMap> serpFilterMessageMap = MasterDataCache.getInstance().getSerpFilterMessageMap();

        SerpFilterMessageMap serpFilterMessageMap1 = serpFilterMessageMap.get(item.properties.suggest_filter);
        if(null!=serpFilterMessageMap1){
            mAlertTitle.setText(serpFilterMessageMap1.message.toLowerCase());
            return serpFilterMessageMap1.internalName;
        }else {
            return "";
        }
    }
}
