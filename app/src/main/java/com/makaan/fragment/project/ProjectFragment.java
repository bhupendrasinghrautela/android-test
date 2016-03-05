package com.makaan.fragment.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.project.ProjectActivity;
import com.makaan.adapter.project.KeyDetailsAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.project.OnRentBuyClicked;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.event.project.OnViewAllPropertiesClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.ProjectConfigItemClickListener;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.property.ViewSellersDialogFragment;
import com.makaan.pojo.KeyDetail;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SellerCard;
import com.makaan.pojo.SerpRequest;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.service.AmenityService;
import com.makaan.service.ImageService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.makaan.ui.CompressedTextView;
import com.makaan.ui.FixedGridView;
import com.makaan.ui.locality.ProjectConfigView;
import com.makaan.ui.project.ProjectSpecificationView;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.AmenitiesViewScroll;
import com.makaan.ui.property.ListingDataOverViewScroll;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.ui.view.FontTextView;
import com.makaan.util.AppUtils;
import com.makaan.util.RecentPropertyProjectManager;
import com.segment.analytics.Properties;
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
    @Bind(R.id.key_detail_container) LinearLayout keyDetailContainer;
    @Bind(R.id.key_details_grid)
    FixedGridView mKeyDetailGrid;
    @Bind(R.id.project_score_progress) ProgressBar projectScoreProgreessBar;
    @Bind(R.id.locality_score_label)
    FontTextView mScoreLabel;
    @Bind(R.id.listing_over_view_scroll_layout)
    ListingDataOverViewScroll mProjectDataOverViewScroll;
    @Bind(R.id.project_score_text) TextView projectScoreTv;
    @Bind(R.id.tv_project_name) TextView projectNameTv;
    @Bind(R.id.tv_project_location) TextView projectLocationTv;
    @Bind(R.id.content_text) TextView descriptionTv;
    @Bind(R.id.compressed_text_layout)
    CompressedTextView mCompressedDescriptionLayout;
    @Bind(R.id.project_description)
    LinearLayout mProjectDescriptionLayout;
    @Bind(R.id.amenities_scroll_layout) AmenitiesViewScroll mAmenitiesViewScroll;
    @Bind(R.id.ll_project_container) FrameLayout projectContainer;
    @Bind(R.id.frame_locality_score) FrameLayout scoreFrameLayout;
    private Context mContext;
    private Project project;
    private long projectId;
    private String sellerImgUrl;
    private boolean isRent = false;
    private ProjectConfigEvent mProjectConfigEvent;
    private RecentPropertyProjectManager.DataObject mDataObject;
    private boolean mProjectReceived;
    private boolean mConfigReceived;

    @OnClick(R.id.contact_top_seller)
    public void openTopSeller(){
        if(mProjectConfigEvent == null){
            return;
        }
        SellerCard sellerCard = null;
        if(isRent && mProjectConfigEvent.rentProjectConfigItems!=null){
            for(ProjectConfigItem projectConfigItem:mProjectConfigEvent.rentProjectConfigItems){
                if(sellerCard == null){
                    sellerCard = projectConfigItem.topSellerCard;
                }
                else if(sellerCard.noOfProperties<projectConfigItem.topSellerCard.noOfProperties){
                    sellerCard = projectConfigItem.topSellerCard;
                }
                else if(sellerCard.noOfProperties == projectConfigItem.topSellerCard.noOfProperties
                        && sellerCard.rating<projectConfigItem.topSellerCard.rating){
                    sellerCard = projectConfigItem.topSellerCard;
                }
            }
        }
        else if(!isRent && mProjectConfigEvent.buyProjectConfigItems!=null){
            for(ProjectConfigItem projectConfigItem:mProjectConfigEvent.buyProjectConfigItems){
                if(sellerCard == null){
                    sellerCard = projectConfigItem.topSellerCard;
                }
                else if(sellerCard.noOfProperties<projectConfigItem.topSellerCard.noOfProperties){
                    sellerCard = projectConfigItem.topSellerCard;
                }
                else if(sellerCard.noOfProperties==projectConfigItem.topSellerCard.noOfProperties
                        && sellerCard.rating!=null && projectConfigItem.topSellerCard.rating!=null &&  sellerCard.rating<projectConfigItem.topSellerCard.rating){
                    sellerCard = projectConfigItem.topSellerCard;
                }
            }
        }
        if(sellerCard!=null) {
            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
            try {
                intent.putExtra("name", sellerCard.name);
                if (sellerCard.rating != null) {
                    intent.putExtra("score", sellerCard.rating.toString());
                } else {
                    intent.putExtra("score", "0");
                }
                intent.putExtra("phone",sellerCard.contactNo);//todo: not available in pojo
                intent.putExtra("id", sellerCard.sellerId.toString());
                intent.putExtra("source",ProjectFragment.class.getName());
                intent.putExtra("listingId",project.projectId);
                if(project!=null && project.locality!=null && project.locality.cityId!=null) {
                    intent.putExtra("cityId", project.locality.cityId);
                }
                if(project!=null && project.locality!=null && project.locality.localityId!=null) {
                    intent.putExtra("localityId", project.locality.localityId);
                }
                if(sellerCard.imageUrl!=null){
                    intent.putExtra("sellerImageUrl", sellerCard.imageUrl);
                }
                getActivity().startActivity(intent);
            } catch (NullPointerException e) {
            }
        }
    }
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
        showProgress();
        fetchData();
        return view;
    }

    private void fetchData() {
        mProjectReceived = false;
        mConfigReceived = false;
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectById(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectConfiguration(projectId);
        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getSimilarProjects(projectId, 10);
    }

    @Subscribe
    public void onResults(OnViewAllPropertiesClicked onViewAllPropertiesClicked) {

        Properties properties= MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.viewAllPropertiesToBuyIn+String.valueOf(project.projectId));
        MakaanEventPayload.endBatch(getActivity(),MakaanTrackerConstants.Action.clickProjectConfiguration);

        SerpRequest request = new SerpRequest(SerpActivity.TYPE_PROJECT);
        request.setCityId(project.locality.cityId);
        request.setLocalityId(project.localityId);
        request.setProjectId(project.projectId);
        request.setTitle(project.name);
        if(onViewAllPropertiesClicked.isRent) {
            request.setSerpContext(SerpRequest.CONTEXT_RENT);
        } else {
            request.setSerpContext(SerpRequest.CONTEXT_BUY);
        }

        request.launchSerp(getActivity());
    }

    @Subscribe
    public void onResults(ProjectConfigItemClickListener configItemClickListener){
        switch (configItemClickListener.configItemType){
            case SELLER:
                ProjectConfigItem projectConfigItem = configItemClickListener.projectConfigItem;
                ArrayList<SellerCard> sellerCards = new ArrayList<>();
                for(SellerCard sellerCard:projectConfigItem.companies.values()){
                    sellerCards.add(sellerCard);
                }
                if(sellerCards.size()>0) {
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ViewSellersDialogFragment viewSellersDialogFragment = new ViewSellersDialogFragment();

                    Bundle bundle=new Bundle();
                    bundle.putString(MakaanEventPayload.PROJECT_ID, String.valueOf(project.projectId));
                    bundle.putString("source", ProjectFragment.class.getName());
                    if(project!=null && project.locality!=null && project.locality.cityId!=null) {
                        bundle.putLong("cityId", project.locality.cityId);
                    }
                    if(project!=null && project.projectId!=null) {
                        bundle.putLong("listingId", project.projectId);
                    }
                    if(project!=null && project.locality!=null && project.locality.label!=null) {
                        bundle.putString("locality", String.valueOf(project.locality.label));
                    }
                    if(project!=null && project.locality!=null && project.locality.localityId != null) {
                        bundle.putLong("localityId", project.locality.localityId);
                    }
                    viewSellersDialogFragment.setArguments(bundle);
                    viewSellersDialogFragment.bindView(sellerCards);
                    viewSellersDialogFragment.show(ft, "allSellers");
                }
                break;
            case PROPERTIES:
                startSerpActivity(configItemClickListener);
                break;
        }
    }

    @Subscribe
    public void onRentBuySelected(OnRentBuyClicked rentBuyClicked){
        isRent = rentBuyClicked.isRent;
    }

    private void startSerpActivity(ProjectConfigItemClickListener configItemClickListener) {
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_PROJECT);
        request.setCityId(project.locality.cityId);
        request.setLocalityId(project.localityId);
        request.setProjectId(project.projectId);
        if(configItemClickListener.projectConfigItem.minPrice > 0) {
            request.setMinBudget(((Double) configItemClickListener.projectConfigItem.minPrice).longValue());
        }
        if(configItemClickListener.projectConfigItem.maxPrice > 0) {
            request.setMaxBudget(((Double) configItemClickListener.projectConfigItem.maxPrice).longValue());
        }
        if(configItemClickListener.isRent) {
            request.setSerpContext(SerpRequest.CONTEXT_RENT);
        }else {
            request.setSerpContext(SerpRequest.CONTEXT_BUY);
        }

        request.launchSerp(getActivity());
    }

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        if(null== imagesGetEvent || null!=imagesGetEvent.error){
            //TODO handle error
            return;
        }
        try {
            if (imagesGetEvent.images.size() > 0) {
                mPropertyImageViewPager.setVisibility(View.VISIBLE);
                mPropertyImageViewPager.bindView();
                if(project.locality.avgPricePerUnitArea!=null) {
                    if(project.locality.avgPricePerUnitArea>project.minPricePerUnitArea) {
                        mPropertyImageViewPager.setData(imagesGetEvent.images, project.minPrice, project.minPricePerUnitArea, false,null);
                    }
                    else{
                        mPropertyImageViewPager.setData(imagesGetEvent.images, project.minPrice, project.minPricePerUnitArea, true,null);
                    }
                }
                else{
                    mPropertyImageViewPager.setData(imagesGetEvent.images, project.minPrice, project.minPricePerUnitArea, false,null);
                }
            } else {
                mPropertyImageViewPager.setVisibility(View.GONE);
            }
        }catch (Exception e){
            mPropertyImageViewPager.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent) {
        if (null == projectByIdEvent || null != projectByIdEvent.error) {
            //getActivity().finish();
            showNoResults();
            Toast.makeText(getActivity(), "project details could not be loaded at this time. please try later.", Toast.LENGTH_LONG).show();
        } else {
            project = projectByIdEvent.project;
            showContent();
            /*if(mConfigReceived) {
                // add project to recent
                RecentPropertyProjectManager manager = RecentPropertyProjectManager.getInstance(getContext().getApplicationContext());
                if (mDataObject == null) {
                    mDataObject = manager.new DataObject(project);
                } else {
                    mDataObject.updateProjectData(project);
                }
                manager.addEntryToRecent(mDataObject, getContext().getApplicationContext());
            }*/

            if(project.projectAmenities !=null && !project.projectAmenities.isEmpty()) {
                mAmenitiesViewScroll.setVisibility(View.VISIBLE);
                mAmenitiesViewScroll.bindView(project.projectAmenities);
            }
            else{
                mAmenitiesViewScroll.setVisibility(View.GONE);
            }
            mProjectDataOverViewScroll.bindView(project);
            populateKeyDetails();
            if(project.getFormattedSpecifications() !=null && project.getFormattedSpecifications().size()>0) {
                projectSpecificationView.setVisibility(View.VISIBLE);
                projectSpecificationView.bindView(project.getFormattedSpecifications(), getActivity());
            }
            else {
                projectSpecificationView.setVisibility(View.GONE);
            }
            initUi();
            if (project.locality.latitude != null && project.locality.longitude != null) {
                ((AmenityService) MakaanServiceFactory.getInstance().getService(AmenityService.class)).getAmenitiesByLocation(project.locality.latitude, project.locality.longitude,3);
                ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getNearByLocalities(project.locality.latitude, project.locality.longitude, 10);
            }
            addConstructionTimelineFragment();
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getProjectTimelineImages(project.projectId);
        }
    }

    private void populateKeyDetails() {
        List<KeyDetail> mKeyDetailList = new ArrayList<>();
        if(project.isPrimary){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.new_resale);
            keyDetail.value = mContext.getString(R.string.new_);
            mKeyDetailList.add(keyDetail);
        }
        else{
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.new_resale);
            keyDetail.value = mContext.getString(R.string.resale);
            mKeyDetailList.add(keyDetail);
        }
        if(project.minSize!=null && project.maxSize!=null){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = "size";
            keyDetail.value = project.minSize+" - "+project.maxSize+" sq ft";
            mKeyDetailList.add(keyDetail);
        }
        if(project.sizeInAcres!=null && project.sizeInAcres!=0){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = "total area";
            keyDetail.value = project.sizeInAcres.toString();
            mKeyDetailList.add(keyDetail);
        }
        if(project.projectStatus!=null && project.projectStatus.toLowerCase().equals("pre launch")) {
            if (project.possessionDate != null) {
                KeyDetail keyDetail = new KeyDetail();
                keyDetail.label = mContext.getString(R.string.pre_launch_date);
                keyDetail.value = AppUtils.getMMMYYYYDateStringFromEpoch(project.possessionDate);
                mKeyDetailList.add(keyDetail);
            }
        }
        else{
            if(project.launchDate != null){
                KeyDetail keyDetail = new KeyDetail();
                keyDetail.label = mContext.getString(R.string.launch_date);
                keyDetail.value = AppUtils.getMMMYYYYDateStringFromEpoch(project.launchDate);
                mKeyDetailList.add(keyDetail);
            }
        }
        if(project.hasTownship){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.township);
            keyDetail.value = "present";
            mKeyDetailList.add(keyDetail);
        }
        else{
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.township);
            keyDetail.value = "not present";
            mKeyDetailList.add(keyDetail);
        }
        if(mKeyDetailList.size()==0){
            keyDetailContainer.setVisibility(View.GONE);
        }
        else{
            KeyDetailsAdapter keyDetailsAdapter = new KeyDetailsAdapter(mContext,mKeyDetailList);
            mKeyDetailGrid.setAdapter(keyDetailsAdapter);
        }
    }

    @Subscribe
    public void onResults(NearByLocalitiesEvent localitiesEvent){
        addPriceTrendsFragment(localitiesEvent.nearbyLocalities);
    }


    private void initUi() {
        projectContainer.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(project.description)) {
            mProjectDescriptionLayout.setVisibility(View.VISIBLE);
            descriptionTv.setTextColor(getResources().getColor(R.color.listingBlack));
            descriptionTv.setText((project.description != null ? Html.fromHtml(project.description.toLowerCase()) : ""));
            ViewTreeObserver vto = descriptionTv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(descriptionTv!=null) {
                        Layout l = descriptionTv.getLayout();
                        if (l != null) {
                            int lines = l.getLineCount();
                            if (lines > 0) {
                                if (l.getEllipsisCount(lines - 1) > 0) {
                                    Log.d("test", "Text is ellipsized");
                                } else {
                                    mCompressedDescriptionLayout.removeMore(true);
                                }
                            }
                        }
                    }
                }
            });
        }
        else{
            mProjectDescriptionLayout.setVisibility(View.GONE);
        }
        aboutBuilderExpandedLayout.bindView(project.builder);
        if(!TextUtils.isEmpty(project.builder.description)) {
            builderDescriptionTv.setText(Html.fromHtml(project.builder.description.toLowerCase()));
        }

        if(project.livabilityScore !=null && project.livabilityScore!=0) {
            projectScoreProgreessBar.setProgress(project.livabilityScore.intValue() * 10);
            projectScoreTv.setText(String.valueOf(project.livabilityScore).toLowerCase());
        } else {
            scoreFrameLayout.setVisibility(View.GONE);
            mScoreLabel.setVisibility(View.GONE);
        }
        projectNameTv.setText(project.name.toLowerCase());
        if(getActivity() instanceof MakaanBaseSearchActivity) {
            if(project.builder != null && !TextUtils.isEmpty(project.builder.name)) {
                getActivity().setTitle(project.builder.name.toLowerCase() + " " + project.name.toLowerCase());
            } else if(!TextUtils.isEmpty(project.builderName)) {
                getActivity().setTitle(project.builderName.toLowerCase() + " " + project.name.toLowerCase());
            } else {
                getActivity().setTitle(project.name.toLowerCase());
            }
        }
        projectLocationTv.setText(project.address.toLowerCase());
    }


    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(null== amenityGetEvent || null!=amenityGetEvent.error){
            //TODO handle error
            return;
        }
        addProjectAboutLocalityFragment(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResult(ProjectConfigEvent projectConfigEvent){
        this.mProjectConfigEvent = projectConfigEvent;
        if(null== projectConfigEvent || null!=projectConfigEvent.error){
            //TODO handle error
            return;
        }

        if(mProjectReceived) {

        }

        /*RecentPropertyProjectManager manager = RecentPropertyProjectManager.getInstance(getContext().getApplicationContext());
        if(mDataObject == null) {
            mDataObject = manager.new DataObject(mProjectConfigEvent);
        } else {
            mDataObject.updateProjectData(mProjectConfigEvent);
        }
        manager.addEntryToRecent(mDataObject, getContext().getApplicationContext());*/

        projectConfigView.bindView(projectConfigEvent, getActivity());
    }

    @Subscribe
    public void onResult(SimilarProjectGetEvent similarProjectGetEvent){
        if(null== similarProjectGetEvent || null!=similarProjectGetEvent.error){
            //TODO handle error
            return;
        }
        if(similarProjectGetEvent.similarProjects !=null && similarProjectGetEvent.similarProjects.size()>0)
            addSimilarProjectsFragment(similarProjectGetEvent.similarProjects);
    }

    @Subscribe
    public void onResult(OnSimilarProjectClickedEvent onSimilarProjectClickedEvent){

        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, onSimilarProjectClickedEvent.id+"_"+onSimilarProjectClickedEvent.clickedPosition);
        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectSimilarProjects);

        Intent i = new Intent(getActivity(),ProjectActivity.class);
        i.putExtra(ProjectActivity.PROJECT_ID, onSimilarProjectClickedEvent.id);
        startActivity(i);
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
    }

    private void addPriceTrendsFragment(ArrayList<Locality> nearbyLocalities) {
        ArrayList<Long> localities = new ArrayList<>();
        if(localities.size()>0) {
            for (int i = 0; i < nearbyLocalities.size() && i < 3; i++) {
                localities.add(nearbyLocalities.get(i).localityId);
            }
        }
        ProjectPriceTrendsFragment fragment = new ProjectPriceTrendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getString(R.string.project_price_trends_title));
        bundle.putLong("localityId", project.localityId);
        bundle.putLong("projectId", project.projectId);
        bundle.putSerializable("localities", localities);
        if(project.minPricePerUnitArea != null) {
            bundle.putInt("price", project.minPricePerUnitArea.intValue());
            if (project.locality != null && project.locality.avgPricePerUnitArea < project.minPricePerUnitArea) {
                bundle.putBoolean("increased",true);
            } else {
                bundle.putBoolean("increased",false);
            }
        }
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

        if(amenityClusterList == null || amenityClusterList.size() == 0){
            return;
        }
        List<AmenityCluster> mAmenityClusters = new ArrayList<>();
        if(amenityClusterList.size()>0) {
            for (AmenityCluster cluster : amenityClusterList) {
                if (null != cluster && null != cluster.cluster && cluster.cluster.size() > 0) {
                    mAmenityClusters.add(cluster);
                }
            }
        }

        if(mAmenityClusters.isEmpty()){
            return;
        }

        ProjectKynFragment fragment = new ProjectKynFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "about " + project.locality.label);
        bundle.putLong("localityId", project.localityId);
        bundle.putDouble("score", project.locality.livabilityScore==null?0:project.locality.livabilityScore);
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
