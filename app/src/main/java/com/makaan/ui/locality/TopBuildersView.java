package com.makaan.ui.locality;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.ui.listing.BaseCardView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 17/1/16.
 */
public class TopBuildersView extends BaseCardView {
    @Bind(R.id.builder_name)TextView mBuilderName;
    @Bind(R.id.experience_time)TextView mBuilderExperience;
    @Bind(R.id.total_project_count)TextView mTotalProjects;
    @Bind(R.id.ongoing_project_count)TextView mOngoingProjects;
    @Bind(R.id.view_projects)TextView mViewProjects;
    @Bind(R.id.builder_group_image)FadeInNetworkImageView mBuilderGroupImage;

    public TopBuildersView(Context context) {
        super(context);
    }

    public TopBuildersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBuildersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public void bindView(Context context, Object item) {

    }

    @OnClick()
    public void viewProjects(){

    }

}
