package com.makaan.ui.project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.pojo.ProjectConfigItem;

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

    public ProjectConfigItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectConfigItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void bindView(List<ProjectConfigItem> items, ProjectConfigItemView projectConfigItemView) {
        this.items = items;
        this.projectConfigItemView = projectConfigItemView;
        mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < items.size() && i<4; i++) {
            addViews(items.get(i), i);
        }
    }

    private void addViews(ProjectConfigItem item, int i) {
        final View configView =
                mLayoutInflater.inflate(R.layout.row_project_config_item, null);
        if(item.minPrice!= 0d && item.maxPrice != 0d)
        ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_one)).setText("\u20B9 "+(item.minPrice/100000)+" L - "+"\u20B9 "+(item.minPrice/100000)+" L");
        ((TextView) configView.findViewById(R.id.tv_project_config_item_labe_two)).setText(getBedroomString(item));
        configView.setTag(i);
        configView.setOnClickListener(this);
        projectConfigItemView.addView(configView, i);
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
                break;
            case R.id.tv_project_config_item_properties:
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

    private void shrinkView(int tag) {
        LinearLayout containerView = (LinearLayout) projectConfigItemView.getChildAt(tag).findViewById(R.id.project_config_row_container);
        containerView.removeViewAt(containerView.getChildCount()-1);
        currentExpandedItemPos = -1;
    }

    private void expandView(int tag) {
        ProjectConfigItem item =  items.get(tag);
        int propertyCount = item.propertyCount;
        int sellerCount = item.sellerCount;
        LinearLayout containerView = (LinearLayout) projectConfigItemView.getChildAt(tag).findViewById(R.id.project_config_row_container);
        View expandedView = mLayoutInflater.inflate(R.layout.row_project_config_item_expanded, null);
        TextView propTv = (TextView) expandedView.findViewById(R.id.tv_project_config_item_properties);
        TextView sellerTv = (TextView) expandedView.findViewById(R.id.tv_project_config_item_seller);
        if(propertyCount>0) {
            propTv.setText("view " + propertyCount + " properties");
            propTv.setOnClickListener(this);
        }else
            propTv.setText("no property");

        if(sellerCount>0) {
            sellerTv.setText("call " + sellerCount + " sellers");
            sellerTv.setOnClickListener(this);
        }else
            sellerTv.setText("no seller");
        containerView.addView(expandedView,containerView.getChildCount());
        currentExpandedItemPos = tag;
    }

}
