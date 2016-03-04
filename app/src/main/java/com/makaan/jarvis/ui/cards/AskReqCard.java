package com.makaan.jarvis.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.jarvis.JarvisClient;
import com.makaan.jarvis.event.SendRequirementEvent;
import com.makaan.jarvis.message.ChatObject;
import com.makaan.jarvis.message.Message;
import com.makaan.jarvis.message.MessageType;
import com.makaan.pojo.SerpObjects;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.TermFilter;
import com.makaan.ui.view.BaseView;
import com.makaan.ui.view.ExpandableHeightGridView;
import com.makaan.util.AppBus;
import com.makaan.util.CommonUtil;
import com.makaan.util.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 19/01/16.
 */
public class AskReqCard extends BaseView<Message> {

    @Bind(R.id.type_spinner)
    Spinner mPropertyType;

    @Bind(R.id.et_price)
    EditText mPrice;

    @Bind(R.id.et_locality)
    EditText mLocality;

    @Bind(R.id.btn_apply)
    View applyButton;

    private GridView mGridView;
    private FiltersViewAdapter mFiltersViewAdapter;

    private Context mContext;
    private ArrayList<FilterGroup> filterGroups;
    private static final String BED_INTERNAL_NAME = "i_beds";
    private static String[] PROPERTY_TYPE = {"apartment", "villa", "plot"};
    private String selectedPropertyType = "";

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
                filterGroups = MasterDataCache.getInstance().getAllBuyFilterGroups();
            } else {
                filterGroups = MasterDataCache.getInstance().getAllRentFilterGroups();
            }
            filterGroups = getClonedFilterGroups(filterGroups);
            populateFilters(filterGroups);
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }

        setupPropertyTypeSpinner();

    }

    private void populateFilters(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if(filterGroup.displayOrder < 0) {
                    continue;
                }

                if(BED_INTERNAL_NAME.equalsIgnoreCase(filterGroup.internalName)){
                    mGridView = (GridView) findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                    if (mGridView != null) {
                        ((ExpandableHeightGridView) mGridView).setExpanded(true);
                        mFiltersViewAdapter = new FiltersViewAdapter(mContext, filterGroup, filterGroup.layoutType);
                        mGridView.setAdapter(mFiltersViewAdapter);
                    }
                }

            }
        }
    }


    private String getBedroomText(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if(filterGroup.displayOrder < 0) {
                    continue;
                }

                if(BED_INTERNAL_NAME.equalsIgnoreCase(filterGroup.internalName)){
                    if(null!=filterGroup.termFilterValues || !filterGroup.termFilterValues.isEmpty()){
                        StringBuilder bhkBuilder = new StringBuilder();
                        int i=0;
                        for(TermFilter termFilter : filterGroup.termFilterValues){
                            if(!termFilter.selected){
                                continue;
                            }
                            if(i>0){
                                bhkBuilder.append(",");
                            }
                            bhkBuilder.append(termFilter.displayName);
                            i++;
                        }

                        if(!bhkBuilder.toString().isEmpty()){
                            return bhkBuilder.append("BHK").toString();
                        }
                    }

                    break;
                }

            }
        }

        return  "";
    }


    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for(FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    private void setupPropertyTypeSpinner(){
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, PROPERTY_TYPE);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mPropertyType.setAdapter(dataAdapter);

        selectedPropertyType = PROPERTY_TYPE[0];

        mPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPropertyType = PROPERTY_TYPE[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRequirementSentUi(){
        mGridView.setEnabled(false);
        mGridView.setClickable(false);
        mGridView.setFocusable(false);
        int childCount = mGridView.getChildCount();
        for(int i = 0;i<childCount;i++) {
            mGridView.getChildAt(i).setEnabled(false);
        }

        mFiltersViewAdapter.notifyDataSetChanged();
        mPropertyType.setEnabled(false);
        mLocality.setEnabled(false);
        mPrice.setEnabled(false);
        applyButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_apply)
    public void onFilterApply(){

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(getBedroomText(filterGroups));

        if(!mPrice.getText().toString().isEmpty()) {
            try {
                messageBuilder.append((messageBuilder.toString().isEmpty() ? "" : ", ") +
                        StringUtil.getDisplayPrice(Double.valueOf(mPrice.getText().toString())));
            }catch (Exception e){}
        }

        if(!TextUtils.isEmpty(selectedPropertyType)) {
            messageBuilder.append((messageBuilder.toString().isEmpty() ? "" : ", ") + selectedPropertyType);
        }

        if(!mLocality.getText().toString().isEmpty()) {
            messageBuilder.append((messageBuilder.toString().isEmpty() ? "" : ", ") + mLocality.getText().toString());
        }



        if(messageBuilder.toString().isEmpty()){
            return;
        }



        AppBus.getInstance().register(this);
        Message message = new Message();
        message.messageType = MessageType.outText;
        message.message = "you suggested " + messageBuilder.toString();
        SendRequirementEvent sendRequirementEvent = new SendRequirementEvent();
        sendRequirementEvent.message = message;
        AppBus.getInstance().post(sendRequirementEvent);

        setRequirementSentUi();
    }

}
