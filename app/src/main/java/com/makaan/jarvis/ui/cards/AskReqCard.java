package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.event.SendRequirementEvent;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.Message;
import com.makaan.pojo.SerpObjects;
import com.makaan.response.serp.FilterGroup;
import com.makaan.ui.view.BaseView;
import com.makaan.ui.view.ExpandableHeightGridView;
import com.makaan.util.AppBus;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by sunil on 19/01/16.
 */
public class AskReqCard extends BaseView<Message> {

    private Context mContext;

    public AskReqCard(Context context) {
        super(context);
        mContext = context;
    }

    public AskReqCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AskReqCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(Context context, Message item) {

        try {
            if (SerpObjects.isBuyContext(getContext())) {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllBuyFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups));
            } else {
                ArrayList<FilterGroup> filterGroups = MasterDataCache.getInstance().getAllRentFilterGroups();
                populateFilters(getClonedFilterGroups(filterGroups));
            }
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

    }

    private void populateFilters(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if(filterGroup.displayOrder < 0) {
                    continue;
                }

                if("bedroom".equalsIgnoreCase(filterGroup.displayName)){
                    GridView gridView = (GridView) findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                    if (gridView != null) {
                        ((ExpandableHeightGridView) gridView).setExpanded(true);
                        FiltersViewAdapter adapter = new FiltersViewAdapter(mContext, filterGroup, filterGroup.layoutType);
                        gridView.setAdapter(adapter);
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
        AppBus.getInstance().register(this);
        Message sendReqMessage = new Message();
        sendReqMessage.appliedFilter = true;
        sendReqMessage.filtered = "plainLink";
        sendReqMessage.chatObj = new ChatObject();
        sendReqMessage.chatObj.beds = "3";
        sendReqMessage.chatObj.propertyType = "Apartment";
        sendReqMessage.chatObj.budget = 100000;
        sendReqMessage.chatObj.locality = "Whitefield";
        SendRequirementEvent sendRequirementEvent = new SendRequirementEvent();
        sendRequirementEvent.message = sendReqMessage;
        AppBus.getInstance().post(sendRequirementEvent);
    }

}
