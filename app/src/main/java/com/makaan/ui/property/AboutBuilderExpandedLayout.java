package com.makaan.ui.property;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.project.Builder;
import com.makaan.response.project.Project;
import com.makaan.response.project.ProjectStatusCount;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.ExpandableLinearLayout;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.util.DateUtil;
import com.makaan.util.ImageUtils;
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
    @Bind(R.id.project_name)
    TextView projectNameTv;
    @Bind(R.id.project_address)
    TextView projectAddressTv;

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
    private boolean mProjectNameVisible;
    private boolean mProjectAddressVisible;
    private Builder mItem;
    private Boolean textIsCollapsed = true;

    @OnClick(R.id.about_builder)
    public void onChange(){
        isExpanded = !isExpanded;
        if(isExpanded){
            mProjectNameVisible = projectNameTv.getVisibility() == VISIBLE;
            mProjectAddressVisible = projectAddressTv.getVisibility() == VISIBLE;

            if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            }
            else if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderMore);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            }
            mExpandableLayout.expand();
            mAboutBuilderTv.setText(getResources().getString(R.string.less_about_builder));
            projectNameTv.setVisibility(View.GONE);
            projectAddressTv.setVisibility(View.GONE);
        }
        else{
            if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            }
            else if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.builderLess);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectOverView);
            }
            mExpandableLayout.collapse();
            mAboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
            if(mProjectNameVisible) {
                projectNameTv.setVisibility(View.VISIBLE);
            } else {
                projectNameTv.setVisibility(View.GONE);
            }
            if(mProjectAddressVisible) {
                projectAddressTv.setVisibility(VISIBLE);
            } else {
                projectAddressTv.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.read_more)
    public void openBuilderSerp(TextView v){
        if(textIsCollapsed){
            mBuilderDescription.setMaxLines(Integer.MAX_VALUE);
            v.setText("less");
        }
        else{
            mBuilderDescription.setMaxLines(5);
            v.setText("more");
        }
        textIsCollapsed =!textIsCollapsed;
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
        mItem = item;
        if(item != null) {
            boolean isDataPresent = false;
            mAboutBuilderTv.setText(getResources().getString(R.string.more_about_builder));
            if(item.imageURL!=null) {
                int width = getResources().getDimensionPixelSize(R.dimen.property_page_builder_logo_width);
                int height = getResources().getDimensionPixelSize(R.dimen.property_page_builder_logo_height);
                mBuilderLogo.setVisibility(VISIBLE);
                mBuilderLogo.setImageUrl(ImageUtils.getImageRequestUrl(item.imageURL, width, height, false),
                        MakaanNetworkClient.getInstance().getImageLoader());
                isDataPresent = true;
            }
            if(item.establishedDate!=null){
                try {
                    mBuilderExperience.setText(DateUtil.getDiffUsingTimeStamp(mContext,Long.parseLong(item.establishedDate)));
                    isDataPresent = true;
                }
                catch (NumberFormatException e){
                    Crashlytics.logException(e);
                    mBuilderExperienceLayout.setVisibility(GONE);
                }
            }
            else{
                mBuilderExperienceLayout.setVisibility(GONE);
            }
            if(item.projectStatusCount !=null){
                ProjectStatusCount projectStatusCount = item.projectStatusCount;
                if(projectStatusCount.completed != 0) {
                    setProject(mBuilderCompleted, projectStatusCount.completed);
                    isDataPresent = true;
                }
                else{
                    mBuilderCompletedLayout.setVisibility(GONE);
                }
                if(projectStatusCount.underConstruction != 0) {
                    setProject(mBuilderOngoing, projectStatusCount.underConstruction);
                    isDataPresent = true;
                }
                else{
                    mBuilderOngoingLayout.setVisibility(GONE);
                }
            }
            else{
                mBuilderCompletedLayout.setVisibility(GONE);
                mBuilderOngoingLayout.setVisibility(GONE);
            }
            if(item.description!=null) {
                mBuilderDescription.setText(Html.fromHtml(item.description).toString().toLowerCase());
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

    public void setProjectData(Project project){
        if(project != null) {
            if(!TextUtils.isEmpty(project.name)) {
                if(project.builder != null && project.builder.name != null) {
                    projectNameTv.setText(String.format("%s %s", project.builder.name, project.name).toLowerCase());
                } else {
                    projectNameTv.setText(project.name.toLowerCase());
                }
                projectNameTv.setVisibility(View.VISIBLE);
            }
            if(project.locality != null) {
                if(project.locality.suburb != null && project.locality.suburb.city != null) {
                    if(!TextUtils.isEmpty(project.locality.suburb.city.label)) {
                        projectAddressTv.setVisibility(View.VISIBLE);
                        projectAddressTv.setText(String.format("%s, %s", project.locality.label, project.locality.suburb.city.label).toLowerCase());
                    } else {
                        projectAddressTv.setVisibility(View.VISIBLE);
                        projectAddressTv.setText(project.locality.label.toLowerCase());
                    }
                } else if(!TextUtils.isEmpty(project.locality.label)) {
                    projectAddressTv.setVisibility(View.VISIBLE);
                    projectAddressTv.setText(project.locality.label.toLowerCase());
                }
            } else {
                projectAddressTv.setVisibility(View.GONE);
            }
        } else {
            projectNameTv.setVisibility(View.GONE);
            projectAddressTv.setVisibility(View.GONE);
        }
    }
}
