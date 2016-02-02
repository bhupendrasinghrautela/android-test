package com.makaan.ui.property;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Builder;
import com.makaan.response.project.ProjectStatusCount;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.ExpandableLinearLayout;
import com.makaan.util.DateUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aishwarya on 24/01/16.
 */
public class AboutBuilderExpandedLayout extends BaseLinearLayout<Builder> {

    @Bind(R.id.builder_logo)
    FadeInNetworkImageView mBuilderLogo;

    @Bind(R.id.builder_expandable)
    ExpandableLinearLayout mExpandableLayout;

    @Bind(R.id.about_builder)
    TextView mAboutBuilderTv;

    @Bind(R.id.builder_description)
    TextView mBuilderDescription;

    @Bind(R.id.experience)
    TextView mBuilderExperience;
    @Bind(R.id.ongoing)
    TextView mBuilderOngoing;
    @Bind(R.id.completed)
    TextView mBuilderCompleted;

    private boolean isExpanded = false;
    private Context mContext;

    @OnClick(R.id.about_builder)
    public void onChange(){
        isExpanded = !isExpanded;
        if(isExpanded){
            mExpandableLayout.expand();
            mAboutBuilderTv.setText(getResources().getString(R.string.less_about_builder));
        }
        else{
            mExpandableLayout.collapse();
            mAboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
        }
    }

    public AboutBuilderExpandedLayout(Context context) {
        super(context);
        mContext = context;
    }

    public AboutBuilderExpandedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AboutBuilderExpandedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(Builder item) {
        if(item != null) {
            mBuilderLogo.setVisibility(VISIBLE);
            mAboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
            if(item.imageURL!=null) {
                mBuilderLogo.setImageUrl(item.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
                this.setVisibility(VISIBLE);
            }
            if(item.establishedDate!=null){
                try {
                    mBuilderExperience.setText(DateUtil.getDiffUsingTimeStamp(mContext,Long.parseLong(item.establishedDate)));
                }
                catch (NumberFormatException e){}
            }
            if(item.projectStatusCount !=null){
                ProjectStatusCount projectStatusCount = item.projectStatusCount;
                setProject(mBuilderCompleted,projectStatusCount.completed);
                setProject(mBuilderOngoing,projectStatusCount.underConstruction);

            }
            mBuilderDescription.setText(Html.fromHtml(item.description));
        }
    }

    public void setProject(TextView v,int count){
        if(count<1){
            v.setText(count+" "+mContext.getString(R.string.project));
        }
        else{
            v.setText(count+ " "+mContext.getString(R.string.projects));
        }
    }
}
