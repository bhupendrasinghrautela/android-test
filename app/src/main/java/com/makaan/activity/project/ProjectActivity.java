package com.makaan.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.project.ProjectFragment;
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
        fetchData();
    }

    private void fetchData() {
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectById(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectConfiguration(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getSimilarProjects(projectId, 10);
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
        if(intent != null) {
            projectId = intent.getLongExtra(PROJECT_ID, 506147);
        }else{
            projectId = Long.valueOf(506147);
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }
}
