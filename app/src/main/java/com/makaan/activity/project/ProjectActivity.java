package com.makaan.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.event.project.OnSeeOnMapClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.squareup.otto.Subscribe;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectActivity extends MakaanBaseSearchActivity {

    public static final String PROJECT_ID = "projectId";
    private Long projectId;
    private ProjectFragment projectFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_locality;
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
            projectId = intent.getExtras().getLong(PROJECT_ID, 506147);//the kove 506147 : rangoli 643539
        }else{
            projectId = Long.valueOf(506147);
        }
    }

    @Subscribe
    public void onResult(OnSeeOnMapClicked seeOnMapClicked){
        mNeighborhoodMapFragment = new NeighborhoodMapFragment();
        initFragment(R.id.container, mNeighborhoodMapFragment, true);
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }
}
