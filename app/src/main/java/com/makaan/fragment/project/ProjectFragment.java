package com.makaan.fragment.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.event.project.OnViewAllPropertiesClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.project.Project;
import com.makaan.service.AmenityService;
import com.makaan.service.ImageService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.locality.ProjectConfigView;
import com.makaan.ui.project.ProjectSpecificationView;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.util.DateUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectFragment extends MakaanBaseFragment{
    @Bind(R.id.project_specification_view) ProjectSpecificationView projectSpecificationView;
    @Bind(R.id.project_config_view) ProjectConfigView projectConfigView;
    @Bind(R.id.property_image_viewpager) PropertyImageViewPager mPropertyImageViewPager;
    @Bind(R.id.about_builder_layout) AboutBuilderExpandedLayout aboutBuilderExpandedLayout;
    @Bind(R.id.builder_description) TextView builderDescriptionTv;
    @Bind(R.id.about_builder) TextView aboutBuilderTv;
    @Bind(R.id.project_score_progress) ProgressBar projectScoreProgreessBar;
    @Bind(R.id.project_score_text) TextView projectScoreTv;
    @Bind(R.id.tv_project_name) TextView projectNameTv;
    @Bind(R.id.tv_project_location) TextView projectLocationTv;
    @Bind(R.id.tv_project_experience) TextView projectExperienceTv;
    @Bind(R.id.tv_project_average_delay) TextView projectAverageDelayTv;
    @Bind(R.id.tv_project_ongoing_delay) TextView projectOngoingTv;
    @Bind(R.id.tv_project_past) TextView projectPastTv;
    @Bind(R.id.content_text) TextView descriptionTv;
    private Context mContext;
    private Project project;

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

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        if(imagesGetEvent.images.size()>0) {
            mPropertyImageViewPager.setVisibility(View.VISIBLE);
            mPropertyImageViewPager.bindView();
            mPropertyImageViewPager.setData(imagesGetEvent.images, project.minPrice);
        }
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent){
        project = projectByIdEvent.project;
        projectSpecificationView.bindView(project.getFormattedSpecifications(), getActivity());
        initUi();
        addPriceTrendsFragment();
        addConstructionTimelineFragment();
        ((AmenityService) MakaanServiceFactory.getInstance().getService(AmenityService.class)).getAmenitiesByLocation(project.locality.latitude, project.locality.longitude, 10);
        ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(323996L);
    }

    private void initUi() {
        descriptionTv.setText(Html.fromHtml(project.description));
        aboutBuilderExpandedLayout.bindView(project.builder);
        builderDescriptionTv.setText(Html.fromHtml(project.builder.description));
        projectScoreProgreessBar.setProgress(project.projectSocietyScore.intValue() * 10);
        projectScoreTv.setText(""+project.projectSocietyScore);
        projectNameTv.setText(project.name);
        projectLocationTv.setText(project.address);
        Date date = new Date(Long.parseLong(project.builder.establishedDate));
        int experience = DateUtil.getDiffYears(date, new Date());
        projectExperienceTv.setText("" + experience);
        projectAverageDelayTv.setText("" + project.delayInMonths.intValue());
        projectOngoingTv.setText("" + project.builder.projectStatusCount.underConstruction);
        projectPastTv.setText("" + project.builder.projectStatusCount.launch);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        addProjectAboutLocalityFragment(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResult(ProjectConfigEvent projectConfigEvent){
        projectConfigView.bindView(projectConfigEvent, getActivity());
    }

    @Subscribe
    public void onResult(OnViewAllPropertiesClicked viewAllPropertiesClicked){
        //TODO :  Integrate with i don't know who.
    }

    @Subscribe
    public void onResult(SimilarProjectGetEvent similarProjectGetEvent){
        if(similarProjectGetEvent.similarProjects !=null && similarProjectGetEvent.similarProjects.size()>0)
            addSimilarProjectsFragment(similarProjectGetEvent.similarProjects);
    }

    @Subscribe
    public void onResult(OnSimilarProjectClickedEvent onSimilarProjectClickedEvent){
        Intent i = new Intent(getActivity(),ProjectActivity.class);
        i.putExtra(ProjectActivity.PROJECT_ID, onSimilarProjectClickedEvent.id);
        startActivity(i);
        getActivity().finish();
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
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
