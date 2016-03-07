package com.makaan.ui.project;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.project.ProjectConfigItemClickListener;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.util.AppBus;
import com.makaan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tusharchaudhary on 1/28/16.
 */
public class ProjectConfigItemView extends LinearLayout implements View.OnClickListener{
    private Context mContext;
    private View moreView;
    private ProjectConfigItemView projectConfigItemView;
    private LayoutInflater mLayoutInflater;
    private int currentExpandedItemPos = -1;
    private List<ProjectConfigItem> items;
    private boolean isRent;
    private TextView moreTv;
    private ArrayList<View> mGoneHideViews = new ArrayList<>();

    public ProjectConfigItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectConfigItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void bindView(List<ProjectConfigItem> items, ProjectConfigItemView projectConfigItemView, boolean isRent) {
        this.items = items;
        this.projectConfigItemView = projectConfigItemView;
        this.isRent = isRent;
        mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < items.size(); i++) {
            if(i>3){
                addViewsWithGone(items.get(i),i);
            }
            else{
                addViews(items.get(i), i);
            }
        }
        if(items.size()>3){
            moreView =  mLayoutInflater.inflate(R.layout.row_project_specification_item_expanded, null);
            moreTv = (TextView) moreView.findViewById(R.id.project_specification_read_more);
            moreTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(moreTv.getText().equals("more")){
                        showViews();
                        moreTv.setText("less");
                    }
                    else{
                        hideViews();
                        moreTv.setText("more");
                    }
                }
            });
            projectConfigItemView.addView(moreView);
        }
        if(items.size()>0)
            expandView(0);
    }

    private void showViews() {
        for(View view:mGoneHideViews){
            view.setVisibility(VISIBLE);
        }
    }

    private void hideViews() {
        for(View view:mGoneHideViews){
            view.setVisibility(GONE);
        }
    }

    private void addViewsWithGone(ProjectConfigItem item, int i) {
        final View configView =
                mLayoutInflater.inflate(R.layout.row_project_config_item, null);
        if(item.minPrice!= 0d && item.maxPrice != 0d) {
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText(String.format("%s - %s", StringUtil.getDisplayPriceSingle(item.minPrice), StringUtil.getDisplayPrice(item.maxPrice)));
        }
        else if(item.minPrice!=0){
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText("> "+StringUtil.getDisplayPriceSingle(item.minPrice));
        }
        else if(item.maxPrice!=0){
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText("< "+StringUtil.getDisplayPriceSingle(item.maxPrice));
        }
        ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_two)).setText(getBedroomString(item));
        configView.setTag(i);
        configView.setOnClickListener(this);
        configView.setVisibility(GONE);
        mGoneHideViews.add(configView);
        projectConfigItemView.addView(configView);
    }

    private void addViews(ProjectConfigItem item, int i) {
        final View configView =
                mLayoutInflater.inflate(R.layout.row_project_config_item, null);
        if(item.minPrice!= 0d && item.maxPrice != 0d) {
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText(String.format("%s - %s", StringUtil.getDisplayPriceSingle(item.minPrice), StringUtil.getDisplayPrice(item.maxPrice)));
        }
        else if(item.minPrice!=0){
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText("> "+StringUtil.getDisplayPriceSingle(item.minPrice));
        }
        else if(item.maxPrice!=0){
            ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText("< "+StringUtil.getDisplayPriceSingle(item.maxPrice));
        }
        ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_two)).setText(getBedroomString(item));
        configView.setTag(i);
        configView.setOnClickListener(this);
        projectConfigItemView.addView(configView);
    }

    private String getBedroomString(ProjectConfigItem item) {
        StringBuilder builder = new StringBuilder("");
        for(int bedroom :item.bedrooms){
            builder.append(bedroom+" bhk  ");
        }
        return builder.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_project_config_item_seller:
                AppBus.getInstance().post(new ProjectConfigItemClickListener(items.get((int) v.getTag()),ConfigItemType.SELLER,isRent));
                break;
            case R.id.tv_project_config_item_properties:
                AppBus.getInstance().post(new ProjectConfigItemClickListener(items.get((int) v.getTag()),ConfigItemType.PROPERTIES,isRent));
                break;
            case R.id.iv_project_config_item_seller:
                AppBus.getInstance().post(new ProjectConfigItemClickListener(items.get((int) v.getTag()),ConfigItemType.SELLER,isRent));
            break;
            case R.id.iv_project_config_item_properties:
                AppBus.getInstance().post(new ProjectConfigItemClickListener(items.get((int) v.getTag()),ConfigItemType.PROPERTIES,isRent));
                break;
            default:
                expandOrShrinkLayout(v);
        }
    }

    private void expandOrShrinkLayout(View v) {
        if(currentExpandedItemPos == -1){
            expandView((int)v.getTag());
        }else{
            if(currentExpandedItemPos == (int)v.getTag())
                shrinkView(currentExpandedItemPos);
            else {
                shrinkView(currentExpandedItemPos);
                expandView((int)v.getTag());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void shrinkView(int tag) {
        LinearLayout containerView = (LinearLayout) projectConfigItemView.getChildAt(tag).findViewById(R.id.project_config_row_container);
        containerView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.project_config_item_bg));
        containerView.removeViewAt(containerView.getChildCount()-1);
        currentExpandedItemPos = -1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void expandView(int tag) {
        ProjectConfigItem item =  items.get(tag);
        int propertyCount = item.propertyCount;
        int sellerCount = item.companies.size();
        LinearLayout containerView = (LinearLayout) projectConfigItemView.getChildAt(tag).findViewById(R.id.project_config_row_container);
        containerView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.project_config_item_selected_bg));
        View expandedView = null;
        if(propertyCount>0){
        expandedView = mLayoutInflater.inflate(R.layout.row_project_config_item_expanded, null);
        TextView propTv = (TextView) expandedView.findViewById(R.id.tv_project_config_item_properties);
        TextView sellerTv = (TextView) expandedView.findViewById(R.id.tv_project_config_item_seller);
        ImageView propIv = (ImageView) expandedView.findViewById(R.id.iv_project_config_item_properties);
        ImageView sellerIv = (ImageView) expandedView.findViewById(R.id.iv_project_config_item_seller);
            propTv.setText("view " + propertyCount + " properties");
            propTv.setOnClickListener(this);
            propIv.setOnClickListener(this);
            propIv.setTag(tag);
            propTv.setTag(tag);
            if(sellerCount>0) {
                sellerTv.setText("call " + sellerCount + " sellers");
                sellerTv.setOnClickListener(this);
                sellerIv.setOnClickListener(this);
                sellerIv.setTag(tag);
                sellerTv.setTag(tag);
            }else
                sellerTv.setText("no seller");
        }else {
            expandedView = mLayoutInflater.inflate(R.layout.no_property, null);
            TextView pyrTv = (TextView) expandedView.findViewById(R.id.open_pyr_project);
            pyrTv.setOnClickListener(this);
        }
        containerView.addView(expandedView);
        currentExpandedItemPos = tag;
    }
    public enum ConfigItemType{
         SELLER, PROPERTIES,PYR
    }
}
