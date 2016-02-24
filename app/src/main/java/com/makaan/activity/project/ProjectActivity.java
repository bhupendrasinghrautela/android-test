package com.makaan.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.listing.TotalImagesCount;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.project.OnSeeOnMapClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.jarvis.event.IncomingMessageEvent;
import com.makaan.response.project.Project;
import com.makaan.response.search.event.SearchResultEvent;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectActivity extends MakaanBaseSearchActivity implements TotalImagesCount {

    public static final String PROJECT_ID = "projectId";
    private Long projectId;
    private ProjectFragment projectFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;
    private NeighborhoodMapFragment.EntityInfo mEntityInfo;
    private int mTotalImagesSeen=0;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_project;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProjectId();
        addProjectFragment();
        initUi(true);
    }

    private void addProjectFragment() {
        projectFragment = new ProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(PROJECT_ID, projectId);
        projectFragment.setArguments(bundle);
        initFragment(R.id.container, projectFragment, false);
    }

    private void setProjectId() {
        Intent intent = getIntent();

        if(intent != null && intent.getExtras() != null) {
            projectId = intent.getExtras().getLong(PROJECT_ID, 0);
        }else{
            //TODO error, and finish
        }
    }

    @Subscribe
    public void onResult(OnSeeOnMapClicked seeOnMapClicked){
        mNeighborhoodMapFragment = new NeighborhoodMapFragment();
        mNeighborhoodMapFragment.setData(mEntityInfo,seeOnMapClicked.amenityClusters);
        initFragment(R.id.container, mNeighborhoodMapFragment, true);
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Override
    public String getScreenName() {
        return "Project";
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }

    @Override
    protected boolean needScrollableSearchBar() {
        return false;
    }

    @Override
    protected boolean supportsListing() {
        return false;
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent) {
        if (null == projectByIdEvent || null != projectByIdEvent.error) {
            //getActivity().finish();
        } else {
            Project project = projectByIdEvent.project;
            if(null!=project && project.latitude!=null && project.longitude!=null) {
                mEntityInfo = new NeighborhoodMapFragment.EntityInfo(project.name,
                        project.latitude, project.longitude);
            }
        }
    }

    @Subscribe
    public void onIncomingMessage(IncomingMessageEvent event){
        animateJarvisHead();
    }

    @Override
    public void ImageCount(int count) {
        mTotalImagesSeen=count;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, mTotalImagesSeen);
        MakaanEventPayload.endBatch(getApplicationContext(), MakaanTrackerConstants.Action.clickPropertyImages);
    }
}
