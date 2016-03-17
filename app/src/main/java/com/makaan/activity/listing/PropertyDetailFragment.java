package com.makaan.activity.listing;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ImageConstants;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.listing.OtherSellersGetEvent;
import com.makaan.event.listing.SimilarListingGetEvent;
import com.makaan.event.listing.SimilarListingGetEvent.ListingItems;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.property.SimilarPropertyFragment;
import com.makaan.fragment.property.ViewSellersDialogFragment;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SellerCard;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.image.Image;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.response.user.Company;
import com.makaan.response.user.User;
import com.makaan.service.AmenityService;
import com.makaan.service.ImageService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.CompressedTextView;
import com.makaan.ui.amenity.AmenityViewPager;
import com.makaan.ui.project.ProjectSpecificationView;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.AmenitiesViewScroll;
import com.makaan.ui.property.FloorPlanLayout;
import com.makaan.ui.property.ListingDataOverViewScroll;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.ui.view.WishListButton;
import com.makaan.ui.view.WishListButton.WishListDto;
import com.makaan.ui.view.WishListButton.WishListType;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sunil on 18/01/16.
 */
public class PropertyDetailFragment extends MakaanBaseFragment implements OpenListingListener {

    @Bind(R.id.amenity_viewpager)
    AmenityViewPager mAmenityViewPager;

    @Bind(R.id.project_specification_view)
    ProjectSpecificationView projectSpecificationView;


    @Bind(R.id.property_image_viewpager)
    PropertyImageViewPager mPropertyImageViewPager;

    @Bind(R.id.floor_plan_layout)
    FloorPlanLayout mFloorPlanLayout;

    @Bind(R.id.serp_default_listing_property_shortlist_checkbox)
    public WishListButton mPropertyWishListCheckbox;

    @Bind(R.id.unit_name)
    TextView mUnitName;

    @Bind(R.id.unit_area)
    TextView mUnitArea;

    @Bind(R.id.seller_assist_button)
    Button mSellerAssistButton;

    @Bind(R.id.seller_rating)
    CustomRatingBar mSellerRating;

    @Bind(R.id.seller_image_view)
    CircleImageView mSellerImageView;

    @Bind(R.id.container_similar_properties)
    FrameLayout mSimilarPropertyContainer;

    @Bind(R.id.seller_logo_text_view)
    TextView mSellerLogoTextView;

    @Bind(R.id.seller_name_text_view)
    TextView mSellerName;

    @Bind(R.id.about_builder_layout)
    AboutBuilderExpandedLayout mABoutBuilderLayout;

    @Bind(R.id.about_locality)
    TextView mAboutLocality;

    @Bind(R.id.property_container)
    LinearLayout mPropertyContainer;

    @Bind(R.id.compressed_text_layout)
    CompressedTextView mCompressedDescriptionLayout;

    @Bind(R.id.listing_over_view_scroll_layout)
    ListingDataOverViewScroll mListingDataOverViewScroll;

    @Bind(R.id.amenities_scroll_layout)
    AmenitiesViewScroll mAmenitiesViewScroll;

    @Bind(R.id.locality_brief)
    TextView mLocalityBrief;

    @Bind(R.id.locality_score_progress)
    ProgressBar mLocalityScoreProgress;

    @Bind(R.id.all_seller_layout)
    LinearLayout mAllSellerLayout;

    @Bind(R.id.locality_score_text)
    TextView mLocalityScoreText;

    @Bind(R.id.content_text)
    TextView mListingBrief;

    @Bind(R.id.view_on_map)
    View mViewOnMap;
    private String bhkAndUnitType,Area,Locality;
    private Long mListingId;


    @OnClick(R.id.more_about_locality)
    public void openLocality(){
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.localityDetails);
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);

        Intent intent = new Intent(getActivity(), LocalityActivity.class);
        intent.putExtra(KeyUtil.LOCALITY_ID,mListingDetail.property.project.localityId);
        startActivity(intent);
    }

    @OnClick(R.id.all_seller_text)
    public void openAllSellerDialog(){
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.sellerDetails);
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);

        if(mSellerCards!=null) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ViewSellersDialogFragment viewSellersDialogFragment = new ViewSellersDialogFragment();
            Bundle args = getArguments();
            Bundle bundle=new Bundle();
            bundle.putString(MakaanEventPayload.PROJECT_ID, String.valueOf(args.getLong(KeyUtil.LISTING_ID)));
            bundle.putString(KeyUtil.SOURCE_LEAD_FORM, PropertyDetailFragment.class.getName());

            if(mListingDetail!=null && mListingDetail.listingCategory!=null && !TextUtils.isEmpty(mListingDetail.listingCategory)){
                if(mListingDetail.listingCategory.equalsIgnoreCase("primary")||mListingDetail.listingCategory.equalsIgnoreCase("resale")){
                    bundle.putString(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
                }
                else if(mListingDetail.listingCategory.equalsIgnoreCase("rental")){
                    bundle.putString(KeyUtil.SALE_TYPE_LEAD_FORM, "rent");
                }
            }

            if(mListingDetail!=null && mListingDetail.property!=null && mListingDetail.property.project!=null &&
                    mListingDetail.property.project.locality!=null ) {

                if(mListingDetail.property.project.locality.cityId!=null) {
                    bundle.putLong(KeyUtil.CITY_ID_LEAD_FORM, mListingDetail.property.project.locality.cityId);
                }

                if(mListingDetail.property.project.locality.suburb!=null
                        && mListingDetail.property.project.locality.suburb.city!=null && mListingDetail.property.project.locality.suburb.city.label!=null){
                    bundle.putString(KeyUtil.CITY_NAME_LEAD_FORM, mListingDetail.property.project.locality.suburb.city.label);
                }

                if(mListingDetail.property.project.locality.label!=null){
                    bundle.putString("locality", String.valueOf(mListingDetail.property.project.locality.label));
                }

                if(mListingDetail.property.project.locality.localityId != null){
                    bundle.putLong(KeyUtil.LOCALITY_ID_LEAD_FORM, mListingDetail.property.project.locality.localityId);
                }
            }

            if(mListingDetail!=null && mListingDetail.property != null) {

                if(mListingDetail.property.propertyId!=null ){
                    bundle.putLong(KeyUtil.PROPERTY_Id_LEAD_FORM, mListingDetail.property.propertyId);
                }

                if(mListingDetail.property.project != null) {
                    if (mListingDetail.property.project.builder != null) {
                        bundle.putString("builder", mListingDetail.property.project.builder.name);
                    }
                    if (mListingDetail.property.project.name != null) {
                        bundle.putString(KeyUtil.PROJECT_NAME_LEAD_FORM, mListingDetail.property.project.name);
                    }
                }
            }

            if(mListingDetail!=null && mListingDetail.projectId!=null) {
                bundle.putLong(KeyUtil.PROJECT_ID_LEAD_FORM, mListingDetail.projectId);
            }

            if(mListingDetail!=null && mListingDetail.id!=null) {
                bundle.putLong(KeyUtil.LISTING_ID_LEAD_FORM, mListingDetail.id);
            }

            if(Area!=null){
                bundle.putString(KeyUtil.AREA_LEAD_FORM, Area);
            }
            if(bhkAndUnitType!=null){
                bundle.putString(KeyUtil.BHK_UNIT_TYPE, bhkAndUnitType);
            }
            if(mListingDetail!=null && mListingDetail.companySeller!=null && mListingDetail.companySeller.company!=null
                    && mListingDetail.companySeller.company.logo != null) {
                bundle.putString(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM, mListingDetail.companySeller.company.logo);
            }
            viewSellersDialogFragment.setArguments(bundle);
            viewSellersDialogFragment.bindView(mSellerCards);
            viewSellersDialogFragment.show(ft, "allSellers");
        }
    }

    @OnClick(R.id.contact_seller)
    public void openContactSeller(){
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.contactSeller);
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);

        if(mListingDetail != null &&
                mListingDetail.companySeller != null &&
                mListingDetail.companySeller.user !=null) {
            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
            User user = mListingDetail.companySeller.user;
            Company company = mListingDetail.companySeller.company;
            try {
                if(company.name!=null) {
                    intent.putExtra(KeyUtil.NAME_LEAD_FORM, company.name);
                }
                else if(user.fullName!=null){
                    intent.putExtra(KeyUtil.NAME_LEAD_FORM, user.fullName);
                }
                else{
                    intent.putExtra(KeyUtil.NAME_LEAD_FORM,"");
                }
                if (company!=null && company.score != null) {
                    intent.putExtra(KeyUtil.SCORE_LEAD_FORM, company.score.toString());
                } else {
                    intent.putExtra(KeyUtil.SCORE_LEAD_FORM, "0");
                }
                if(user.contactNumbers!=null && !user.contactNumbers.isEmpty()) {
                    intent.putExtra(KeyUtil.PHONE_LEAD_FORM, user.contactNumbers.get(0).contactNumber);
                }
                intent.putExtra(KeyUtil.SOURCE_LEAD_FORM, PropertyDetailFragment.class.getName());
                intent.putExtra(KeyUtil.SINGLE_SELLER_ID, String.valueOf(company.id));
                if(mListingDetail!=null && mListingDetail.id!=null) {
                    intent.putExtra(KeyUtil.LISTING_ID_LEAD_FORM, mListingDetail.id);
                }
                if(mListingDetail!=null && mListingDetail.listingCategory!=null && !TextUtils.isEmpty(mListingDetail.listingCategory)){
                    if(mListingDetail.listingCategory.equalsIgnoreCase("primary")||mListingDetail.listingCategory.equalsIgnoreCase("resale")){
                        intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
                    }
                    else if(mListingDetail.listingCategory.equalsIgnoreCase("rental")){
                        intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "rent");
                    }
                }

                if(mListingDetail!=null && mListingDetail.property!=null && mListingDetail.property.project!=null &&
                        mListingDetail.property.project.locality!=null ) {

                    if(mListingDetail.property.project.locality.cityId!=null) {
                        intent.putExtra(KeyUtil.CITY_ID_LEAD_FORM, mListingDetail.property.project.locality.cityId);
                    }
                    if(mListingDetail.property.project.locality.suburb!=null &&
                            mListingDetail.property.project.locality.suburb.city!=null && mListingDetail.property.project.locality.suburb.city.label!=null){
                        intent.putExtra(KeyUtil.CITY_NAME_LEAD_FORM, mListingDetail.property.project.locality.suburb.city.label);
                    }

                    if(mListingDetail.property.project.locality.localityId!=null){
                        intent.putExtra(KeyUtil.LOCALITY_ID_LEAD_FORM, mListingDetail.property.project.locality.localityId);
                    }
                }

                if(mListingDetail!=null && mListingDetail.propertyId!=null){
                    intent.putExtra(KeyUtil.PROPERTY_Id_LEAD_FORM, mListingDetail.propertyId);
                }
                else if(mListingDetail!=null && mListingDetail.property!=null && mListingDetail.property.propertyId!=null){
                    intent.putExtra(KeyUtil.PROPERTY_Id_LEAD_FORM, mListingDetail.property.propertyId);
                }

                if(mListingDetail!=null && mListingDetail.projectId!=null){
                    intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM, mListingDetail.projectId);
                }
                else if(mListingDetail!=null && mListingDetail.property!=null && mListingDetail.property.project!=null &&
                        mListingDetail.property.project.projectId!=null) {

                    intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM, mListingDetail.property.project.projectId);
                    if(mListingDetail.property.project.name!=null){
                        intent.putExtra(KeyUtil.PROJECT_NAME_LEAD_FORM, mListingDetail.property.project.name);
                    }
                }

                if(Area!=null){
                    intent.putExtra(KeyUtil.AREA_LEAD_FORM, Area);
                }
                if(bhkAndUnitType!=null){
                    intent.putExtra(KeyUtil.BHK_UNIT_TYPE, bhkAndUnitType);
                }
                if(!TextUtils.isEmpty(company.logo)){
                    intent.putExtra(KeyUtil.SELLER_IMAGE_URL_LEAD_FORM, company.logo);
                }

                getActivity().startActivity(intent);
            } catch (NullPointerException e) {
            }
        }
    }

    @OnClick(R.id.amenity_see_on_map)
    public void showMap(){
        mShowMapCallback.showMapFragment();
    }

    private ListingDetail mListingDetail;
    private List<AmenityCluster> mAmenityClusters = new ArrayList<>();
    private ShowMapCallBack mShowMapCallback;
    private Long listingId;
    private String listingMainUrl;
    private Context mContext;
    private ArrayList<ImagesGetEvent> mImagesGetEventArrayList;
    private ArrayList<SellerCard> mSellerCards;


    @Override
    protected int getContentViewId() {
        return R.layout.property_detail_frgament;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListingId = getArguments().getLong(KeyUtil.LISTING_ID);
        fetchPropertytDetail();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void fetchPropertytDetail(){
        //Intent intent = getIntent();
        //long listingId = intent.getExtras().getLong("listingId");
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(mListingId);
        //TODO correct similar listing
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getSimilarListingDetail(mListingId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        showProgress();
        listingId = args.getLong(KeyUtil.LISTING_ID);
        listingMainUrl = args.getString(KeyUtil.LISTING_Image);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(amenityGetEvent.amenityClusters == null){
            return;
        }
        mAmenityViewPager.bindView();
        mAmenityClusters.clear();
        for(AmenityCluster cluster : amenityGetEvent.amenityClusters){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }

        if(!mAmenityClusters.isEmpty()){
            mAmenityViewPager.setVisibility(View.VISIBLE);
            mViewOnMap.setVisibility(View.VISIBLE);
            mAmenityViewPager.setData(mAmenityClusters);
        }
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(listingByIdGetEvent.listingDetail ==null){
            showNoResults();
            return;
        }
        else if(null != listingByIdGetEvent.error){
           showNoResults();
        }
        else {
            mListingDetail = listingByIdGetEvent.listingDetail;
            showContent();
            // adding property to recent manager
            RecentPropertyProjectManager manager = RecentPropertyProjectManager.getInstance(getActivity().getApplicationContext());
            manager.addEntryToRecent(manager.new DataObject(mListingDetail), getActivity().getApplicationContext());

            if(mListingDetail.latitude!=0 && mListingDetail.longitude!=0) {
                ((AmenityService) (MakaanServiceFactory.getInstance().getService(
                        AmenityService.class))).getAmenitiesByLocation(mListingDetail.latitude, mListingDetail.longitude, 3, AmenityService.EntityType.PROJECT);
            }
            TestUi(mListingDetail);
            ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getOtherSellersOnListingDetail(
                    mListingDetail.property.projectId, mListingDetail.property.bedrooms, mListingDetail.property.bathrooms, mListingDetail.property.studyRoom
                    , mListingDetail.property.poojaRoom, mListingDetail.property.servantRoom, 5
            );
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId);
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId, ImageConstants.THREED_FLOOR_PLAN);
            ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId, ImageConstants.FLOOR_PLAN);
        }
    }

    public Image getDummyImage(){
        Image image = new Image();
        image.absolutePath = mListingDetail.mainImageURL;
        return image;
    }

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        if(!isVisible()) {
            return;
        }
        if(imagesGetEvent == null){
            imagesGetEvent = new ImagesGetEvent();
            imagesGetEvent.images = new ArrayList<>();
            imagesGetEvent.images.add(getDummyImage());
        }
        else if(imagesGetEvent.error!=null){
            imagesGetEvent = new ImagesGetEvent();
            imagesGetEvent.images = new ArrayList<>();
            imagesGetEvent.images.add(getDummyImage());
            mFloorPlanLayout.setVisibility(View.GONE);
        }

        if(imagesGetEvent.imageType == null) {
            if(imagesGetEvent.images.size()==0){
                imagesGetEvent.images.add(getDummyImage());
            }
            mPropertyImageViewPager.setVisibility(View.VISIBLE);
            if(mListingDetail != null && mListingDetail.currentListingPrice != null) {
                if (mListingDetail.currentListingPrice.price != 0 && mListingDetail.currentListingPrice.pricePerUnitArea != 0) {
                    mPropertyImageViewPager.bindView();
                    if (mListingDetail.property != null && mListingDetail.property.project != null && mListingDetail.property.project.locality != null) {
                        if (mListingDetail.property.project.locality.avgPricePerUnitArea != null &&
                                mListingDetail.property.project.locality.avgPricePerUnitArea < mListingDetail.currentListingPrice.pricePerUnitArea) {
                            mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price, mListingDetail.currentListingPrice.pricePerUnitArea, true, mListingDetail.listingCategory);
                        } else {
                            mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price, mListingDetail.currentListingPrice.pricePerUnitArea, false, mListingDetail.listingCategory);
                        }
                    } else {
                        mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price, mListingDetail.currentListingPrice.pricePerUnitArea, false, mListingDetail.listingCategory);
                    }
                } else {
                    mPropertyImageViewPager.bindView();
                    mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price, mListingDetail.currentListingPrice.pricePerUnitArea, false, mListingDetail.listingCategory);
                }
            }
        }
        else if(imagesGetEvent.images!= null && imagesGetEvent.images.size()>0){
            mFloorPlanLayout.bindFloorPlan(imagesGetEvent);
        }
    }

    @Subscribe
    public void onOtherSellers(OtherSellersGetEvent otherSellersGetEvent){
        if(!isVisible()) {
            return;
        }
        mSellerCards = otherSellersGetEvent.sellerCards;
    }

    @Subscribe
    public void onSimilarListings(SimilarListingGetEvent similarListingGetEvent){
        if(!isVisible()) {
            return;
        }
        if(similarListingGetEvent == null || similarListingGetEvent.error!=null){
            return;
        }

        if(similarListingGetEvent.data!=null && similarListingGetEvent.data.items!=null
                && similarListingGetEvent.data.items.size() > 0){
            if(listingId != null && listingId > 0) {
                for (Iterator<ListingItems> iterator = similarListingGetEvent.data.items.iterator(); iterator.hasNext(); ) {
                    ListingItems item = iterator.next();
                    if(item != null && item.listing != null && item.listing.id != null && item.listing.id.equals(listingId)) {
                        iterator.remove();
                    }
                }
            }
            if(similarListingGetEvent.data.items.size() > 0) {
                addSimilarProperties(similarListingGetEvent.data.items);
            }
        }
    }

    private void TestUi(ListingDetail listingDetail){
        mPropertyContainer.setVisibility(View.VISIBLE);
        mListingDataOverViewScroll.bindView(listingDetail);
        // todo need to show seller logo image if available
        Company company = listingDetail.companySeller!=null?listingDetail.companySeller.company:null;
        if(company!=null) {
            showSellerDetail(company);
        }
        if(listingDetail.property != null) {
            Property property = listingDetail.property;

            mPropertyWishListCheckbox.bindView(new WishListDto(mListingDetail.id.longValue(),property.projectId.longValue(), WishListType.listing));
            if(listingDetail.listingAmenities !=null && !listingDetail.listingAmenities.isEmpty()) {
                mAmenitiesViewScroll.setVisibility(View.VISIBLE);
                mAmenitiesViewScroll.bindView(listingDetail.listingCategory,property.unitType, listingDetail.listingAmenities);
            }
            else{
                mAmenitiesViewScroll.setVisibility(View.GONE);
            }
            StringBuilder bhkInfo = new StringBuilder();
            if(property.unitType != null && "plot".equalsIgnoreCase(property.unitType)) {
                bhkInfo.append("residential plot");
            } else {
                bhkInfo.append(property.bedrooms > 0 ? property.bedrooms.toString()+" bhk ": "");
                bhkInfo.append(property.unitType != null ? property.unitType.toLowerCase():"");

            }
            mUnitName.setText(bhkInfo.toString()+ " - ");
            bhkAndUnitType=(listingDetail.property.bedrooms + "bhk " +
                    (property.unitType != null ? property.unitType : "")).toLowerCase();

            if (getActivity() instanceof MakaanBaseSearchActivity) {
                if (listingDetail.property.bedrooms == 0)
                    getActivity().setTitle(getActivity().getString(R.string.search_residential_plot_string));
                else
                    getActivity().setTitle((listingDetail.property.bedrooms + " bhk " +
                            (property.unitType != null ? property.unitType : "")).toLowerCase());
            }
            Area=(property.size != null ? StringUtil.getFormattedNumber(property.size) : "") + " " +
                    (property.measure != null ? property.measure : "");

            mUnitArea.setText(
                    (property.size != null ? StringUtil.getFormattedNumber(property.size) : "") + " " +
                    (property.measure != null ? property.measure : ""));
            if(property.project != null) {
                Project project = property.project;
                if(project.getFormattedSpecifications() !=null && project.getFormattedSpecifications().size()>0) {
                    projectSpecificationView.setVisibility(View.VISIBLE);
                    projectSpecificationView.bindView(project.getFormattedSpecifications(), getActivity());
                }
                else {
                    projectSpecificationView.setVisibility(View.GONE);
                }

                mABoutBuilderLayout.bindView(listingDetail.property.project.builder);
                mABoutBuilderLayout.setProjectData(listingDetail.property.project);

                if(project.locality != null) {
                    Locality locality = project.locality;
                    mAboutLocality.setText("about ".concat((locality.label != null ? locality.label.toLowerCase() : "")));
                    mLocalityBrief.setText(Html.fromHtml((locality.description != null ? locality.description.toLowerCase() : "")));
                    if(locality.livabilityScore != null) {
                        mLocalityScoreProgress.setProgress((int) (locality.livabilityScore * 10));
                        mLocalityScoreText.setText(String.valueOf(locality.livabilityScore));
                    } else {
                        mLocalityScoreProgress.setVisibility(View.GONE);
                        mLocalityScoreText.setVisibility(View.GONE);
                    }
                }
                else{
                    mAboutLocality.setVisibility(View.GONE);
                    mLocalityScoreText.setVisibility(View.GONE);
                    mLocalityScoreProgress.setVisibility(View.GONE);
                }
            }
            if(listingDetail.description!=null) {
                mCompressedDescriptionLayout.setVisibility(View.VISIBLE);
                mListingBrief.setTextColor(getResources().getColor(R.color.listingBlack));
                mListingBrief.setText((listingDetail.description != null ? Html.fromHtml(listingDetail.description).toString().toLowerCase() : ""));
                ViewTreeObserver vto = mListingBrief.getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if(mListingBrief!=null) {
                            Layout l = mListingBrief.getLayout();
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
                mCompressedDescriptionLayout.setVisibility(View.GONE);
            }
        }
    }

    private void showSellerDetail(Company company) {
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);
        mAllSellerLayout.setVisibility(View.VISIBLE);
        String name = null;
        if(company.name!=null){
            name = company.name;
        }
        else if(mListingDetail.companySeller.user!=null && mListingDetail.companySeller.user.fullName!=null){
            name = mListingDetail.companySeller.user.fullName;
        }
        if("broker".equalsIgnoreCase(company.type)) {
            if(name != null){
                mSellerName.setText(String.format("%s (%s)",name, "agent").toLowerCase());
            }
        } else {
            if(name != null){
                if(company.type!=null) {
                    mSellerName.setText(String.format("%s (%s)",name, company.type).toLowerCase());
                }
                else{
                    mSellerName.setText(String.format("%s",name).toLowerCase());
                }
            }
        }
        if(company.score!=null) {
            mSellerRating.setRating(company.score.floatValue() / 2);
        }
        User user = mListingDetail.companySeller!=null?mListingDetail.companySeller.user:null;
        if(!company.assist){
            mSellerAssistButton.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(name)) {
            mSellerLogoTextView.setText(String.valueOf(name.charAt(0)));
        }
        if(!TextUtils.isEmpty(company.logo)) {
            mSellerLogoTextView.setVisibility(View.GONE);
            mSellerImageView.setVisibility(View.VISIBLE);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(company.logo, width, height, false), new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerLogoTextView.setVisibility(View.GONE);
                    mSellerImageView.setVisibility(View.VISIBLE);
                    mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                    showTextAsImage();
                }
            });
        } else if(user!=null && !TextUtils.isEmpty(user.profilePictureURL)) {
            mSellerLogoTextView.setVisibility(View.GONE);
            mSellerImageView.setVisibility(View.VISIBLE);
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(user.profilePictureURL, width, height, false), new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    mSellerLogoTextView.setVisibility(View.GONE);
                    mSellerImageView.setVisibility(View.VISIBLE);
                    mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showTextAsImage();
                }
            });
        } else {
            showTextAsImage();
        }
    }

    private void showTextAsImage() {
        mSellerImageView.setVisibility(View.GONE);
        // show seller first character as logo

        int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

        Random random = new Random();
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//        int color = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSellerLogoTextView.setBackground(drawable);
        } else {
            mSellerLogoTextView.setBackgroundDrawable(drawable);
        }
    }

    //TODO add smiliar properties instead of this
    private void addSimilarProperties(List<ListingItems> listingDetailList) {
        SimilarPropertyFragment newFragment = new SimilarPropertyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.similar_properties));
        newFragment.setArguments(bundle);
        newFragment.setListener(this);
        mSimilarPropertyContainer.setVisibility(View.VISIBLE);
        initFragment(R.id.container_similar_properties, newFragment, false);
        newFragment.setData(listingDetailList);
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction =((PropertyActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void bindView(ShowMapCallBack showMapCallBack) {
        mShowMapCallback = showMapCallBack;
    }

    @Override
    public void openPropertyPage(Long listingId, Double longitude, Double latitude) {
        Intent intent = new Intent(getActivity(), PropertyActivity.class);
        intent.putExtra(KeyUtil.LISTING_ID, listingId);
        startActivity(intent);
    }
}
