package com.makaan.fragment.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.project.Project;
import com.makaan.response.project.ProjectSpecification;
import com.makaan.service.AmenityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.project.ProjectSpecificationView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectFragment extends MakaanBaseFragment{
    private Context mContext;
    private Project project;
    @Bind(R.id.project_specification_view)
    ProjectSpecificationView projectSpecificationView;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();
        return view;
    }

    private void addPriceTrendsFragment() {
        ProjectPriceTrendsFragment fragment = new ProjectPriceTrendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.project_price_trends_title));
        bundle.putLong("localityId", project.localityId);
        if(project.locality.avgPricePerUnitArea != null)
        bundle.putInt("price", project.locality.avgPricePerUnitArea.intValue());
        fragment.setArguments(bundle);
        initFragment(R.id.container_price_trends, fragment, false);
    }

    private void addConstructionTimelineFragment() {
        ConstructionTimelineFragment fragment = new ConstructionTimelineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.project_construction_title));
        fragment.setArguments(bundle);
        initFragment(R.id.container_construction_photos, fragment, false);
    }

    private void addSimilarProjectsFragment(ArrayList<Project> similarProjects) {
        SimilarProjectFragment fragment = new SimilarProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",getString(R.string.project_similar_project_title));
        fragment.setArguments(bundle);
        initFragment(R.id.container_similar_projects, fragment, false);
        fragment.setData(similarProjects);
    }

    private void addProjectAboutLocalityFragment(List<AmenityCluster> amenityClusterList) {
        ProjectKynFragment fragment = new ProjectKynFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "about " + project.locality.label);
        bundle.putDouble("score", project.locality.livabilityScore);
        bundle.putString("description", project.locality.description);
        fragment.setArguments(bundle);
        initFragment(R.id.container_about_locality, fragment, false);
        fragment.setData(amenityClusterList);
    }


    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent){
        project = projectByIdEvent.project;
        projectSpecificationView.bindView(project.getFormattedSpecifications(),getActivity());
        addPriceTrendsFragment();
        addConstructionTimelineFragment();
        ((AmenityService) MakaanServiceFactory.getInstance().getService(AmenityService.class)).getAmenitiesByLocation(project.locality.latitude, project.locality.longitude, 10);

    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        addProjectAboutLocalityFragment(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResult(ProjectConfigEvent projectConfigEvent){
        Log.e(this.getClass().getSimpleName(),"");
    }

    @Subscribe
    public void onResult(SimilarProjectGetEvent similarProjectGetEvent){
        if(similarProjectGetEvent.similarProjects !=null && similarProjectGetEvent.similarProjects.size()>0)
        addSimilarProjectsFragment(similarProjectGetEvent.similarProjects);
    }

    @Subscribe
    public void onResult(OnSimilarProjectClickedEvent onSimilarProjectClickedEvent){
        Intent i = new Intent(getActivity(),ProjectActivity.class);
        i.putExtra(ProjectActivity.PROJECT_ID,onSimilarProjectClickedEvent.id);
        startActivity(i);
        getActivity().finish();
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction =((ProjectActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}
