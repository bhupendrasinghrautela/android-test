package com.makaan.activity.project;

import android.content.Intent;
import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.fragment.locality.LocalityFragment;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectActivity extends MakaanFragmentActivity {

    public static final String LOCALITY_ID = "localityId";
    private Long localityId;
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
    }

    private void addProjectFragment() {
        projectFragment = new ProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("localityId",localityId);
        projectFragment.setArguments(bundle);
        initFragment(R.id.container, projectFragment, false);
    }

    private void setProjectId() {
        Intent intent = getIntent();
        if(intent != null) {
            localityId = intent.getLongExtra(LOCALITY_ID, 50001); //use  50157 for electronic city
        }else{
            localityId = Long.valueOf(50001);
        }
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }
}
