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
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.adapter.project.KeyDetailsAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.project.OnRentBuyClicked;
import com.makaan.event.project.OnSimilarProjectClickedEvent;
import com.makaan.event.project.OnViewAllPropertiesClicked;
import com.makaan.event.project.OpenPyrClicked;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.event.project.ProjectConfigItemClickListener;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.fragment.property.ViewSellersDialogFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.pojo.KeyDetail;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SellerCard;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.image.Image;
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
import com.makaan.ui.view.MakaanProgressBar;
import com.makaan.util.AppUtils;
import com.makaan.util.CommonUtil;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/26/16.
 */
public class ProjectFragment extends OverviewFragment{
    @Bind(R.id.project_specification_view) ProjectSpecificationView projectSpecificationView;
    @Bind(R.id.project_config_view) ProjectConfigView projectConfigView;
    @Bind(R.id.property_image_viewpager) PropertyImageViewPager mPropertyImageViewPager;
    @Bind(R.id.about_builder_layout) AboutBuilderExpandedLayout aboutBuilderExpandedLayout;
    @Bind(R.id.builder_description) TextView builderDescriptionTv;
    @Bind(R.id.about_builder) TextView aboutBuilderTv;
    @Bind(R.id.key_detail_container) LinearLayout keyDetailContainer;
    @Bind(R.id.key_details_grid)
    FixedGridView mKeyDetailGrid;
    @Bind(R.id.project_score_progress)
    MakaanProgressBar projectScoreProgreessBar;
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
    private OverviewActivityCallbacks mActivityCallbacks;

    @OnClick(R.id.contact_top_seller)
    public void openTopSeller(){
        if(mProjectConfigEvent == null){
            return;
        }
        SellerCard sellerCard = null;
        if(isRent && mProjectConfigEvent.rentProjectConfigItems!=null){
            for(ProjectConfigItem projectConfigItem:mProjectConfigEvent.rentProjectConfigItems){
                if(projectConfigItem.topSellerCard == null){
                    continue;
                }
                if (sellerCard == null) {
                    sellerCard = projectConfigItem.topSellerCard;
                } else if (sellerCard.noOfProperties != null && projectConfigItem.topSellerCard.noOfProperties != null) {
                    if(sellerCard.noOfProperties < projectConfigItem.topSellerCard.noOfProperties) {
                        sellerCard = projectConfigItem.topSellerCard;
                    } else if (sellerCard.noOfProperties.equals(projectConfigItem.topSellerCard.noOfProperties)
                            && sellerCard.rating != null && projectConfigItem.topSellerCard.rating != null
                            && sellerCard.rating < projectConfigItem.topSellerCard.rating) {
                        sellerCard = projectConfigItem.topSellerCard;
                    }
                }
            }
        }
        else if(!isRent && mProjectConfigEvent.buyProjectConfigItems!=null){
            for(ProjectConfigItem projectConfigItem:mProjectConfigEvent.buyProjectConfigItems){
                if(projectConfigItem.topSellerCard == null){
                    continue;
                }
                if (sellerCard == null) {
                    sellerCard = projectConfigItem.topSellerCard;
                } else if (sellerCard.noOfProperties != null && projectConfigItem.topSellerCard.noOfProperties != null) {
                    if (sellerCard.noOfProperties < projectConfigItem.topSellerCard.noOfProperties) {
                        sellerCard = projectConfigItem.topSellerCard;
                    } else if (sellerCard.noOfProperties.equals(projectConfigItem.topSellerCard.noOfProperties)
                            && sellerCard.rating != null && projectConfigItem.topSellerCard.rating != null
                            && sellerCard.rating < projectConfigItem.topSellerCard.rating) {
                        sellerCard = projectConfigItem.topSellerCard;
                    }
                }
            }
        }
        if(sellerCard!=null) {
            /*-------------------track------------------event-------------------*/
            Properties properties = MakaanEventPayload.beginBatch();
            if(project!=null && project.projectId!=null) {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, project.projectId));
            }else {
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
            }
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.leadFormOpen);
            /*-------------------------------------------------------------------*/

            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
            try {
                intent.putExtra(KeyUtil.NAME_LEAD_FORM, sellerCard.name);
                if (sellerCard.rating != null) {
                    intent.putExtra(KeyUtil.SCORE_LEAD_FORM, sellerCard.rating.toString());
                } else {
                    intent.putExtra(KeyUtil.SCORE_LEAD_FORM, "0");
                }
                intent.putExtra(KeyUtil.PHONE_LEAD_FORM, sellerCard.contactNo);//todo: not available in pojo
                intent.putExtra(KeyUtil.SINGLE_SELLER_ID, sellerCard.sellerId.toString());
                intent.putExtra(KeyUtil.SOURCE_LEAD_FORM, ProjectFragment.class.getName());
                intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM ,project.projectId);
                intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
                if(sellerCard.userId!=null) {
                    intent.putExtra(KeyUtil.USER_ID, sellerCard.userId);
                }

                if(project!=null && project.name!=null) {
                    intent.putExtra(KeyUtil.PROJECT_NAME_LEAD_FORM, project.name);
                    if (project.projectId != null) {
                        intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM, project.projectId);
                    }
                    if(project.locality!=null){
                        if(project.locality.suburb!=null &&
                                project.locality.suburb.city!=null && project.locality.suburb.city.label!=null){
                            intent.putExtra(KeyUtil.CITY_NAME_LEAD_FORM,project.locality.suburb.city.label);
                        }

                        if(project!=null && project.locality!=null && project.locality.cityId!=null) {
                            intent.putExtra(KeyUtil.CITY_ID_LEAD_FORM, project.locality.cityId);
                        }

                        if(project!=null && project.locality!=null && project.locality.localityId!=null) {
                            intent.putExtra(KeyUtil.LOCALITY_ID_LEAD_FORM, project.locality.localityId);
                        }
                    }
                }

                if(sellerCard.imageUrl!=null){
                    intent.putExtra(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM, sellerCard.imageUrl);
                }
                getActivity().startActivity(intent);
            } catch (NullPointerException e) {
                Crashlytics.logException(e);
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
            this.projectId = args.getLong(OverviewActivity.ID);
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
        if(!isVisible()) {
            return;
        }

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
        if(!isVisible()) {
            return;
        }
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
                    bundle.putString(KeyUtil.SOURCE_LEAD_FORM, ProjectFragment.class.getName());
                    bundle.putString(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");

                    if(project!=null) {
                        if(project.projectId!=null) {
                            bundle.putLong(KeyUtil.PROJECT_ID_LEAD_FORM, project.projectId);
                        }
                        if(project.name!=null) {
                            bundle.putString(KeyUtil.PROJECT_NAME_LEAD_FORM, project.name);
                        }

                        if(project.locality!=null){

                            if(project.locality.localityId != null){
                                bundle.putLong(KeyUtil.LOCALITY_ID_LEAD_FORM, project.locality.localityId);

                            }
                            if(project.locality.label!=null){
                                bundle.putString("locality", String.valueOf(project.locality.label));
                            }
                            if(project.locality.cityId != null){
                                bundle.putLong(KeyUtil.CITY_ID_LEAD_FORM, project.locality.cityId);
                            }
                            if(project.locality.suburb != null
                                    && project.locality.suburb.city != null && project.locality.suburb.city.label != null){
                                bundle.putString(KeyUtil.CITY_NAME_LEAD_FORM, project.locality.suburb.city.label);
                            }
                        }

                        if(project.builder!=null && project.builder.name!=null) {
                            bundle.putString("builder", String.valueOf(project.builder.name));
                        }
                    }

                    if(project!=null && project.name!=null) {
                        bundle.putString(KeyUtil.PROJECT_LEAD_FORM, String.valueOf(project.name));
                    }

                    viewSellersDialogFragment.setArguments(bundle);
                    viewSellersDialogFragment.bindView(sellerCards);
                    viewSellersDialogFragment.show(ft, "allSellers");
                }
                break;
            case PROPERTIES:
                startSerpActivity(configItemClickListener);
                break;
            case PYR:
                startPyrActivity(configItemClickListener);
                break;
        }
    }

    private void startPyrActivity(ProjectConfigItemClickListener configItemClickListener) {
        /*-------------------track------------------event-------------------*/
        Properties properties = MakaanEventPayload.beginBatch();
        if(project!=null && project.projectId!=null) {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, project.projectId));
        }else {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
        }
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.pyrFormOpen);
        /*-------------------------------------------------------------------*/

        Intent pyrIntent = new Intent(getActivity(), PyrPageActivity.class);
        if(project.locality.cityId != null && project.locality.cityId > 0) {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, project.locality.cityId);
        } else {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, project.locality.suburb.city.id);
        }
        pyrIntent.putExtra(PyrPageActivity.KEY_CITY_NAME, project.locality.suburb.city.label);
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_ID, project.locality.localityId);
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_NAME, project.locality.label);
        pyrIntent.putExtra(PyrPageActivity.BEDROOM_AND_BUDGET, configItemClickListener.projectConfigItem);
        pyrIntent.putExtra(PyrPageActivity.BUY_SELECTED, !configItemClickListener.isRent);
        pyrIntent.putExtra(PyrPageActivity.SOURCE_SCREEN_NAME, ((BaseJarvisActivity) getActivity()).getScreenName());
        getActivity().startActivity(pyrIntent);
    }

    @Subscribe
    public  void openPyr(OpenPyrClicked openPyrClicked){
        if(!isVisible()) {
            return;

        }
        /*-------------------track------------------event-------------------*/
        Properties properties = MakaanEventPayload.beginBatch();
        if(project!=null && project.projectId!=null) {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, project.projectId));
        }else {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
        }
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.pyrFormOpen);
        /*-------------------------------------------------------------------*/

        Intent pyrIntent = new Intent(getActivity(), PyrPageActivity.class);
        if(project.locality.cityId != null && project.locality.cityId > 0) {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, project.locality.cityId);
        } else {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, project.locality.suburb.city.id);
        }
        pyrIntent.putExtra(PyrPageActivity.KEY_CITY_NAME, project.locality.suburb.city.label);
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_ID, project.locality.localityId);
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_NAME, project.locality.label);

        getActivity().startActivity(pyrIntent);
    }

    @Subscribe
    public void onRentBuySelected(OnRentBuyClicked rentBuyClicked){
        if(!isVisible()) {
            return;
        }
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
        if(!isVisible()) {
            return;
        }
        if(null== imagesGetEvent || null!=imagesGetEvent.error){
            imagesGetEvent = new ImagesGetEvent();
            imagesGetEvent.images = new ArrayList<>();
            imagesGetEvent.images.add(getDummyImage());
            imagesGetEvent.imageType = ImageService.COMBINED_IMAGES;
        }
        if(imagesGetEvent.imageType==null ||!imagesGetEvent.imageType.equals(ImageService.COMBINED_IMAGES)){
            return;
        }
        try {
            Double price = project.minResaleOrPrimaryPrice != null ? project.minResaleOrPrimaryPrice : project.minPrice;
            if(imagesGetEvent.images.size() == 0){
                imagesGetEvent.images.add(getDummyImage());
            }
            if (imagesGetEvent.images.size() > 0) {
                mPropertyImageViewPager.setVisibility(View.VISIBLE);
                mPropertyImageViewPager.bindView();
                if(project.locality.avgPricePerUnitArea!=null && project.minPricePerUnitArea != null) {
                    if(project.locality.avgPricePerUnitArea>project.minPricePerUnitArea) {
                        mPropertyImageViewPager.setData(imagesGetEvent.images, price, project.minPricePerUnitArea, false,null);
                    }
                    else{
                        mPropertyImageViewPager.setData(imagesGetEvent.images, price, project.minPricePerUnitArea, true,null);
                    }
                }
                else{
                    mPropertyImageViewPager.setData(imagesGetEvent.images, price, project.minPricePerUnitArea, false,null);
                }
            } else {
                mPropertyImageViewPager.setVisibility(View.GONE);
            }
        } catch (Exception e){
            Crashlytics.logException(e);
            if(mPropertyImageViewPager!=null) {
                mPropertyImageViewPager.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void onResult(ProjectByIdEvent projectByIdEvent) {
        if(!isVisible()) {
            return;
        }
        if (null == projectByIdEvent || null != projectByIdEvent.error) {
            //getActivity().finish();
            showNoResults();
            Toast.makeText(getActivity(), "project details could not be loaded at this time. please try later.", Toast.LENGTH_LONG).show();
        } else {
            project = projectByIdEvent.project;
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

                ((AmenityService) MakaanServiceFactory.getInstance().getService(
                        AmenityService.class)).getAmenitiesByLocation(project.locality.latitude, project.locality.longitude,3, AmenityService.EntityType.PROJECT);

                ((LocalityService) MakaanServiceFactory.getInstance().getService(
                        LocalityService.class)).getNearByLocalities(project.locality.latitude, project.locality.longitude, 1);
            }
            addConstructionTimelineFragment();
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getProjectTimelineImages(project.projectId,project.locality.localityId);
            //handler.sendEmptyMessageDelayed(0, 500);
            showContent();
        }
    }
/*    class TempHandler extends android.os.Handler {
        boolean sent = false;
        @Override
        public void handleMessage(Message msg) {
            if(sent) {
            } else {
                showProgress();
            }
            if(!sent) {
                handler.sendEmptyMessageDelayed(0, 500);
                sent = true;
            }
        }
    }
    TempHandler handler = new TempHandler();*/

    private void populateKeyDetails() {
        List<KeyDetail> mKeyDetailList = new ArrayList<>();
        if(project.isPrimary && project.isResale) {
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.availability);
            keyDetail.value = mContext.getString(R.string.new_resale);
            mKeyDetailList.add(keyDetail);
        } else if(project.isPrimary) {
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.availability);
            keyDetail.value = mContext.getString(R.string.new_);
            mKeyDetailList.add(keyDetail);
        } else {
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = mContext.getString(R.string.availability);
            keyDetail.value = mContext.getString(R.string.resale);
            mKeyDetailList.add(keyDetail);
        }
        if(project.minSize!=null && project.maxSize!=null){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = "size";
            keyDetail.value = StringUtil.getFormattedNumber(project.minSize) + " - " + StringUtil.getFormattedNumber(project.maxSize) + " sq ft";
            mKeyDetailList.add(keyDetail);
        }
        if(project.sizeInAcres!=null && project.sizeInAcres!=0){
            KeyDetail keyDetail = new KeyDetail();
            keyDetail.label = "total area";
            keyDetail.value = StringUtil.getFormattedNumber(project.sizeInAcres) + " acres";
            mKeyDetailList.add(keyDetail);
        }
        // todo discuss following scenario with PMs
        if(project.projectStatus!=null && project.projectStatus.toLowerCase().equals("pre launch")) {
            if (project.preLaunchDate != null) {
                KeyDetail keyDetail = new KeyDetail();
                keyDetail.label = mContext.getString(R.string.pre_launch_date);
                keyDetail.value = AppUtils.getMMMYYYYDateStringFromEpoch(String.valueOf(project.possessionDate));
                mKeyDetailList.add(keyDetail);
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
        }
        else{
            if(project.launchDate != null){
                KeyDetail keyDetail = new KeyDetail();
                keyDetail.label = mContext.getString(R.string.launch_date);
                keyDetail.value = AppUtils.getMMMYYYYDateStringFromEpoch(project.launchDate);
                mKeyDetailList.add(keyDetail);
            }

            if(project.preLaunchDate != null) {
                KeyDetail keyDetail = new KeyDetail();
                keyDetail.label = mContext.getString(R.string.pre_launch_date);
                keyDetail.value = AppUtils.getMMMYYYYDateStringFromEpoch(String.valueOf(project.preLaunchDate));
                mKeyDetailList.add(keyDetail);
            }
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
        if(!isVisible()) {
            return;
        }
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
                                    CommonUtil.TLog(Log.DEBUG, "test", "Text is ellipsized");
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
        if(!TextUtils.isEmpty(project.getFullName())) {
            projectNameTv.setText(project.getFullName().toLowerCase());
        }
        if(getActivity() instanceof MakaanBaseSearchActivity) {
            if(!TextUtils.isEmpty(project.name)) {
                if (project.builder != null && !TextUtils.isEmpty(project.builder.name)) {
                    getActivity().setTitle(project.builder.name.toLowerCase() + " " + project.name.toLowerCase());
                } else if (!TextUtils.isEmpty(project.builderName)) {
                    getActivity().setTitle(project.builderName.toLowerCase() + " " + project.name.toLowerCase());
                } else {
                    getActivity().setTitle(project.name.toLowerCase());
                }
            }
        }
        if(!TextUtils.isEmpty(project.address)) {
            projectLocationTv.setText(project.address.toLowerCase());
        }
    }


    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(null== amenityGetEvent || null!=amenityGetEvent.error){
            //TODO handle error
            return;
        }
        addProjectAboutLocalityFragment(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResult(ProjectConfigEvent projectConfigEvent){
        if(!isVisible()) {
            return;
        }
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

        if(projectConfigView != null) {
            projectConfigView.bindView(projectConfigEvent, getActivity());
            projectConfigView.invalidate();
        }
    }

    @Subscribe
    public void onResult(SimilarProjectGetEvent similarProjectGetEvent){
        if(!isVisible()) {
            return;
        }
        if(null== similarProjectGetEvent || null!=similarProjectGetEvent.error){
            //TODO handle error
            return;
        }
        if(similarProjectGetEvent.similarProjects !=null && similarProjectGetEvent.similarProjects.size()>0)
            addSimilarProjectsFragment(similarProjectGetEvent.similarProjects);
    }

    @Subscribe
    public void onResult(OnSimilarProjectClickedEvent onSimilarProjectClickedEvent){
        if(!isVisible()) {
            return;
        }

        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, onSimilarProjectClickedEvent.id + "_" + onSimilarProjectClickedEvent.clickedPosition);
        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectSimilarProjects);

        Intent i = new Intent(getActivity(),OverviewActivity.class);
        Bundle bundle = new Bundle();

        bundle.putLong(OverviewActivity.ID, onSimilarProjectClickedEvent.id);
        bundle.putInt(OverviewActivity.TYPE, OverviewItemType.PROJECT.ordinal());

        i.putExtras(bundle);
        startActivity(i);
    }

    private void addPriceTrendsFragment(ArrayList<Locality> nearbyLocalities) {
        if(!isVisible() || project == null){
            return;
        }
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
        bundle.putString("projectName",project.name);
        if(project.minPricePerUnitArea != null) {
            bundle.putInt("price", project.minPricePerUnitArea.intValue());
            if (project.locality != null && project.locality.avgPricePerUnitArea!=null
                    && project.locality.avgPricePerUnitArea < project.minPricePerUnitArea) {
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
        if(isVisible()) {
            SimilarProjectFragment fragment = new SimilarProjectFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.project_similar_project_title));
            fragment.setArguments(bundle);
            initFragment(R.id.container_similar_projects, fragment, false);
            fragment.setData(similarProjects);
        }
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

        if(project.locality != null || project.localityId != null) {
            ProjectKynFragment fragment = new ProjectKynFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", "about " + project.locality.label);
            bundle.putLong("localityId", project.localityId);
            bundle.putDouble("score", project.locality.livabilityScore == null ? 0 : project.locality.livabilityScore);
            bundle.putString("description", project.locality.description);
            fragment.setArguments(bundle);
            initFragment(R.id.container_about_locality, fragment, false);
            fragment.setData(mAmenityClusters, mActivityCallbacks);
        }
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction =((MakaanFragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public Image getDummyImage() {
        Image image = new Image();
        image.absolutePath = project.imageURL;
        return image;
    }

    @Override
    public void bindView(OverviewActivityCallbacks activityCallbacks) {

        mActivityCallbacks = activityCallbacks;
    }

}
