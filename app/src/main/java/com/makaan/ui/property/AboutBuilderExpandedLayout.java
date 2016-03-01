package com.makaan.ui.property;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Builder;
import com.makaan.response.project.ProjectStatusCount;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.ExpandableLinearLayout;
import com.makaan.util.DateUtil;
import com.segment.analytics.Properties;

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
    @Bind(R.id.builder_experience)
    LinearLayout mBuilderExperienceLayout;
    @Bind(R.id.ongoing)
    TextView mBuilderOngoing;
    @Bind(R.id.builder_ongoing)
    LinearLayout mBuilderOngoingLayout;
    @Bind(R.id.completed)
    TextView mBuilderCompleted;
    @Bind(R.id.builder_completed)
    LinearLayout mBuilderCompletedLayout;

    private boolean isExpanded = false;
    private Context mContext;

    @OnClick(R.id.about_builder)
    public void onChange(){
        isExpanded = !isExpanded;
        if(isExpanded){

            if(mContext instanceof PropertyActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            }
            else if(mContext instanceof ProjectActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            }
            mExpandableLayout.expand();
            mAboutBuilderTv.setText(getResources().getString(R.string.less_about_builder));
        }
        else{
            if(mContext instanceof PropertyActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            }
            else if(mContext instanceof ProjectActivity) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            }
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
            boolean isDataPresent = false;
            mAboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
            if(item.imageURL!=null) {
                mBuilderLogo.setVisibility(VISIBLE);
                mBuilderLogo.setImageUrl(item.imageURL, MakaanNetworkClient.getInstance().getImageLoader());
                isDataPresent = true;
            }
            if(item.establishedDate!=null){
                try {
                    mBuilderExperience.setText(DateUtil.getDiffUsingTimeStamp(mContext,Long.parseLong(item.establishedDate)));
                    isDataPresent = true;
                }
                catch (NumberFormatException e){
                    mBuilderExperienceLayout.setVisibility(GONE);
                }
            }
            else{
                mBuilderExperienceLayout.setVisibility(GONE);
            }
            if(item.projectStatusCount !=null){
                ProjectStatusCount projectStatusCount = item.projectStatusCount;
                    setProject(mBuilderCompleted, projectStatusCount.completed);
                    setProject(mBuilderOngoing, projectStatusCount.underConstruction);
                isDataPresent = true;
            }
            else{
                mBuilderCompletedLayout.setVisibility(GONE);
                mBuilderOngoingLayout.setVisibility(GONE);
            }
            if(item.description!=null) {
                mBuilderDescription.setText(Html.fromHtml(item.description));
                isDataPresent = true;
            }
            if(!isDataPresent){
                this.setVisibility(GONE);
            }
        }
        else{
            this.setVisibility(GONE);
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
