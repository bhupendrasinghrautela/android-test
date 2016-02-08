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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.event.project.OnViewAllPropertiesClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.ProjectConfigItemClickListener;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.service.AmenityService;
import com.makaan.service.ImageService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.makaan.ui.locality.ProjectConfigView;
import com.makaan.ui.project.ProjectSpecificationView;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.AmenitiesViewScroll;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.util.KeyUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

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
    @Bind(R.id.content_text) TextView descriptionTv;
    @Bind(R.id.amenities_scroll_layout) AmenitiesViewScroll mAmenitiesViewScroll;
    @Bind(R.id.ll_project_container) LinearLayout projectContainer;
    @Bind(R.id.frame_locality_score) FrameLayout scoreFrameLayout;
    private Context mContext;
    private Project project;
    private long projectId;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        if(args!=null)
            this.projectId = args.getLong(ProjectActivity.PROJECT_ID);
        fetchData();
        return view;
    }

    private void fetchData() {
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectById(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectConfiguration(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getSimilarProjects(projectId, 10);
    }

    @Subscribe
    public void onResults(OnViewAllPropertiesClicked onViewAllPropertiesClicked) {
        SerpRequest request = new SerpRequest();
        request.setCityId(project.locality.cityId);
        request.setLocalityId(project.localityId);
        request.setProjectId(project.projectId);
        if(onViewAllPropertiesClicked.isRent) {
            request.setSerpContext(SerpRequest.CONTEXT_RENT);
        } else {
            request.setSerpContext(SerpRequest.CONTEXT_BUY);
        }

        request.launchSerp(getActivity(), SerpActivity.TYPE_PROJECT);
    }

    @Subscribe
    public void onResults(ProjectConfigItemClickListener configItemClickListener){
        switch (configItemClickListener.configItemType){
            case SELLER:
                //TODO: open seller screen
                break;
            case PROPERTIES:
                startSerpActivity(configItemClickListener);
                break;
        }
    }

    private void startSerpActivity(ProjectConfigItemClickListener configItemClickListener) {
        SerpRequest request = new SerpRequest();
        request.setCityId(project.locality.cityId);
        request.setLocalityId(project.localityId);
        request.setProjectId(project.projectId);
        request.setMinBudget(((Double) configItemClickListener.projectConfigItem.minPrice).longValue());
        request.setMaxBudget(((Double) configItemClickListener.projectConfigItem.maxPrice).longValue());
        if(configItemClickListener.isRent) {
            request.setSerpContext(SerpRequest.CONTEXT_RENT);
        }else {
            request.setSerpContext(SerpRequest.CONTEXT_BUY);
        }

        request.launchSerp(getActivity(), SerpActivity.TYPE_PROJECT);
    }

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        try {
            if (imagesGetEvent.images.size() > 0) {
                mPropertyImageViewPager.setVisibility(View.VISIBLE);
                mPropertyImageViewPager.bindView();
                mPropertyImageViewPager.setData(imagesGetEvent.images, project.minPrice, null);
            } else {
                mPropertyImageViewPager.setVisibility(View.GONE);
            }
        }catch (Exception e){

        }
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent) {
        if (projectByIdEvent.error != null) {
            getActivity().finish();
            Toast.makeText(getActivity(), "project details could not be loaded at this time. please try later.", Toast.LENGTH_LONG).show();
        } else {
            project = projectByIdEvent.project;
            mAmenitiesViewScroll.bindView(project.projectAmenities);
            projectSpecificationView.bindView(project.getFormattedSpecifications(), getActivity());
            initUi();
            ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getNearByLocalities(project.locality.latitude, project.locality.longitude, 10);
            addConstructionTimelineFragment();
            if (project.locality.latitude != null && project.locality.longitude != null) {
                ((AmenityService) MakaanServiceFactory.getInstance().getService(AmenityService.class)).getAmenitiesByLocation(project.locality.latitude, project.locality.longitude, 10);
            }
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getProjectTimelineImages(project.projectId);
        }
    }
    @Subscribe
    public void onResults(NearByLocalitiesEvent localitiesEvent){
        addPriceTrendsFragment(localitiesEvent.nearbyLocalities);
    }


    private void initUi() {
        projectContainer.setVisibility(View.VISIBLE);
        descriptionTv.setText(Html.fromHtml(project.description));
        aboutBuilderExpandedLayout.bindView(project.builder);
        builderDescriptionTv.setText(Html.fromHtml(project.builder.description));

        if(project.livabilityScore !=null) {
            projectScoreProgreessBar.setProgress(project.livabilityScore.intValue() * 10);
            projectScoreTv.setText("" + project.livabilityScore);
        } else {
            scoreFrameLayout.setVisibility(View.GONE);
        }
        projectNameTv.setText(project.name);
        projectLocationTv.setText(project.address);
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
    public void onResult(SimilarProjectGetEvent similarProjectGetEvent){
        if(similarProjectGetEvent.similarProjects !=null && similarProjectGetEvent.similarProjects.size()>0)
            addSimilarProjectsFragment(similarProjectGetEvent.similarProjects);
    }

    @Subscribe
    public void onResult(OnSimilarProjectClickedEvent onSimilarProjectClickedEvent){
        Intent i = new Intent(getActivity(),ProjectActivity.class);
        i.putExtra(ProjectActivity.PROJECT_ID, onSimilarProjectClickedEvent.id);
        startActivity(i);
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
    }

    private void addPriceTrendsFragment(ArrayList<Locality> nearbyLocalities) {
        ArrayList<Long> localities = new ArrayList<>();
        for(int i = 0;i< nearbyLocalities.size() && i<6;i++){
            localities.add(nearbyLocalities.get(i).localityId);
        }
        ProjectPriceTrendsFragment fragment = new ProjectPriceTrendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.project_price_trends_title));
        bundle.putLong("localityId", project.localityId);
        bundle.putLong("projectId", project.projectId);
        bundle.putSerializable("localities", localities);
        if(project.locality.avgPricePerUnitArea != null)
        bundle.putInt("price", project.locality.avgPricePerUnitArea.intValue());
        fragment.setArguments(bundle);
        initFragment(R.id.container_price_trends, fragment, false);
    }

    private void addConstructionTimelineFragment() {
        ConstructionTimelineFragment fragment = new ConstructionTimelineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.project_construction_title));
        bundle.putLong("projectId", project.projectId);
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

        List<AmenityCluster> mAmenityClusters = new ArrayList<>();
        for(AmenityCluster cluster : amenityClusterList){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }

        if(mAmenityClusters.isEmpty()){
            return;
        }

        ProjectKynFragment fragment = new ProjectKynFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "about " + project.locality.label);
        bundle.putLong("localityId", project.localityId);
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
