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

    public ProjectConfigItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectConfigItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public void bindView(List<ProjectConfigItem> items, ProjectConfigItemView projectConfigItemView) {
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
    }

}
