package com.makaan.fragment.locality;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.agents.callback.TopAgentsCallback;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.locality.OnNearByLocalityClickEvent;
import com.makaan.event.locality.OnTopAgentClickEvent;
import com.makaan.event.locality.OnTopBuilderClickEvent;
import com.makaan.event.locality.TopBuilderInLocalityEvent;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.TaxonomyCard;
import com.makaan.pojo.overview.OverviewItemType;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.city.EntityDesc;
import com.makaan.response.locality.ListingAggregation;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Builder;
import com.makaan.service.AgentService;
import com.makaan.service.AmenityService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.TaxonomyService;
import com.makaan.ui.CompressedTextView;
import com.makaan.ui.CompressedTextView.CompressTextViewCollapseCallback;
import com.makaan.ui.view.MakaanProgressBar;
import com.makaan.util.CommonUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.LocalityUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/21/16.
 */
public class LocalityFragment extends OverviewFragment implements CompressTextViewCollapseCallback {
    @Bind(R.id.main_city_image)
    FadeInNetworkImageView mMainCityImage;
    @Bind(R.id.blurred_city_image)
    ImageView mBlurredCityImage;
    @Bind(R.id.city_scrollview)
    NestedScrollView mCityScrollView;
    @Bind(R.id.city_collapse_toolbar)
    CollapsingToolbarLayout mCityCollapseToolbar;
    @Bind(R.id.content_text)
    TextView overviewContentTV;
    @Bind(R.id.locality_score_text)
    TextView livinScoreTv;
    @Bind(R.id.locality_score_progress)
    MakaanProgressBar livingScoreProgress;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price)
    TextView salesMedianPrice;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_label)
    TextView salesMedianPriceLabel;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_rent)
    TextView rentMedianPrice;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_rent_label)
    TextView rentMedianPriceLabel;
    @Bind(R.id.tv_locality_annual_growth)
    TextView annualGrowthTv;
    @Bind(R.id.tv_locality_annual_growth_label)
    TextView annualGrowthLabelTv;
    @Bind(R.id.tv_locality_annual_growth_rent)
    TextView annualRentDemandGrowthTv;
    @Bind(R.id.tv_locality_annual_growth_label_rent)
    TextView annualRentDemandGrowthLabelTv;
    @Bind(R.id.ll_locality_fragment)
    LinearLayout frame;
    @Bind(R.id.compressed_text_view)
    CompressedTextView compressedTv;
    @Bind(R.id.view_locality_seperator)
    View firstSectionSeperator;
    @Bind(R.id.tv_locality_interested_in)
    TextView interestedInTv;

    @Bind(R.id.iv_avg_price)
    ImageView mAvgPrice;

    private static final int BLUR_EFFECT_HEIGHT = 200;
    private float alpha;
    private Long localityId ;
    private Locality locality;
    private Context mContext ;
    private Integer meadianRental, meadianSale;
    private List<AmenityCluster> mAmenityClusters = new ArrayList<>();
    private OverviewActivityCallbacks mActivityCallbacks;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_locality;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initToolbar();
        setLocalityId();
        showProgress();
        fetchData();
        initListeners();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
//        mMainCityImage.setDefaultImageResId(R.drawable.locality_background_placeholder);
        return view;
    }

    private void initToolbar() {
        if(getActivity().isFinishing()){
            return;
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        if(((AppCompatActivity)getActivity()).getSupportActionBar()!=null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        }
    }

    private void setLocalityId() {
        localityId = getArguments().getLong("id");
    }

    private void fetchData() {
        ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getLocalityById(localityId);
    }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent) {
        if(!isVisible()) {
            return;
        }
        if (null == localityByIdEvent || null != localityByIdEvent.error || localityByIdEvent.locality == null) {
            showNoResults("locality details could not be loaded at this time. please try later.");
//            Toast.makeText(getActivity(), "locality details could not be loaded at this time. please try later.", Toast.LENGTH_LONG).show();
        } else {
            locality = localityByIdEvent.locality;
            if(isVisible()) {
                showContent();
                populateLocalityData();
                frame.setVisibility(View.VISIBLE);
                fetchHero();
                addLocalitiesLifestyleFragment(locality.entityDescriptions);
                addProperties(new TaxonomyService().getTaxonomyCardForLocality(locality.localityId, locality.minAffordablePrice, locality.maxAffordablePrice, locality.maxAffordablePrice, locality.maxBudgetPrice));
                if (locality.latitude != null && locality.longitude != null) {
                    ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getNearByLocalities(locality.latitude, locality.longitude, 16);
                    ((AmenityService) MakaanServiceFactory.getInstance().getService(
                            AmenityService.class)).getAmenitiesByLocation(locality.latitude, locality.longitude, 3, AmenityService.EntityType.LOCALITY);

                }
                ((AgentService) MakaanServiceFactory.getInstance().getService(AgentService.class)).getTopAgentsForLocality(locality.cityId, locality.localityId, 15, false, new TopAgentsCallback() {
                    @Override
                    public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {
                        if(isVisible()) {
                            addTopAgentsFragment(topAgents);
                        }
                    }
                });
                ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getTopBuildersInLocality(locality.localityId, 15);
                addLocalitiesApartmentsFragment(locality.listingAggregations);
            }
        }
    }


    @Subscribe
    public void onResults(TopBuilderInLocalityEvent topBuilderInLocalityEvent){
        if(!isVisible()) {
            return;
        }
        if(null== topBuilderInLocalityEvent || null!=topBuilderInLocalityEvent.error){
            //TODO handle error
            return;
        }
        addTopBuilders(topBuilderInLocalityEvent.builders);
    }

    @Subscribe
    public void onResults(NearByLocalitiesEvent localitiesEvent){
        if(!isVisible()) {
            return;
        }
        if(null== localitiesEvent || null!=localitiesEvent.error){
            //TODO handle error
            return;
        }
        addNearByLocalitiesFragment(localitiesEvent.nearbyLocalities);
        addPriceTrendFragment(localitiesEvent.nearbyLocalities);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(null==amenityGetEvent|| null!=amenityGetEvent.error){
            //TODO handle error
            return;
        }

        if(null==amenityGetEvent.amenityClusters){
            return;
        }
        mAmenityClusters.clear();
        for(AmenityCluster cluster : amenityGetEvent.amenityClusters){
            if(null!=cluster && null!=cluster.cluster && cluster.cluster.size()>0){
                mAmenityClusters.add(cluster);
            }
        }

        if(!mAmenityClusters.isEmpty()) {
            addKyn(mAmenityClusters);
        }
    }

    private void populateLocalityData() {
        final Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comforta.ttf");
        mCityCollapseToolbar.setCollapsedTitleTypeface(tf);
        mCityCollapseToolbar.setExpandedTitleTypeface(tf);
        if(locality.description !=null && !locality.description.isEmpty()) {
            compressedTv.setCallback(this);
            overviewContentTV.setText(Html.fromHtml(locality.description).toString().toLowerCase());
        }
        else {
            compressedTv.setVisibility(View.GONE);
        }
        if(locality.label != null) {
            mCityCollapseToolbar.setTitle(locality.label.toLowerCase());
            interestedInTv.setText("interested in "+locality.label.toLowerCase()+"?");
        }
        if(locality.livabilityScore != null) {
            livinScoreTv.setVisibility(View.VISIBLE);
            livinScoreTv.setText("" + locality.livabilityScore);
        } else {
            livinScoreTv.setVisibility(View.INVISIBLE);
        }
        livingScoreProgress.setProgress(locality.livabilityScore == null ? 0 : (int) (locality.livabilityScore * 10));
        livingScoreProgress.setVisibility(locality.livabilityScore == null ? View.GONE : View.VISIBLE);
        livinScoreTv.setVisibility(locality.livabilityScore == null ? View.GONE : View.VISIBLE);
        calculateMedian(locality.listingAggregations);
        boolean showFirstSection = false;
        boolean showSecondSection = false;
        if(meadianSale != null && meadianSale.intValue() != 0) {
            showFirstSection = true;
            salesMedianPrice.setVisibility(View.VISIBLE);
            salesMedianPriceLabel.setVisibility(View.VISIBLE);
            salesMedianPrice.setText("\u20B9 " + StringUtil.getFormattedNumber(meadianSale) + " / sq ft");
            
        }
        else{
            salesMedianPrice.setVisibility(View.INVISIBLE);
            salesMedianPriceLabel.setVisibility(View.INVISIBLE);
        }
        if(meadianRental != null && meadianRental.intValue() != 0) {
            showSecondSection = true;
            rentMedianPrice.setVisibility(View.VISIBLE);
            rentMedianPriceLabel.setVisibility(View.VISIBLE);
            rentMedianPrice.setText("\u20B9 " + StringUtil.getFormattedNumber(meadianRental) + " / month");
        }
        else{

            rentMedianPrice.setVisibility(View.GONE);
            rentMedianPriceLabel.setVisibility(View.GONE);
        }
        if(locality.avgPriceRisePercentage!=null){
            showFirstSection = true;
            annualGrowthLabelTv.setVisibility(View.VISIBLE);
            annualGrowthTv.setVisibility(View.VISIBLE);
            annualGrowthTv.setText(locality.avgPriceRisePercentage + " %");
        }
        else{

            annualGrowthLabelTv.setVisibility(View.GONE);
            annualGrowthTv.setVisibility(View.GONE);
        }
       if(locality.avgRentalDemandRisePercentage != null) {
            showSecondSection = true;
            annualRentDemandGrowthTv.setVisibility(View.VISIBLE);
            annualRentDemandGrowthLabelTv.setVisibility(View.VISIBLE);
            annualRentDemandGrowthTv.setText(locality.avgPriceRisePercentage + " %");
        }
        else{

           annualRentDemandGrowthTv.setVisibility(View.GONE);
           annualRentDemandGrowthLabelTv.setVisibility(View.GONE);
       }
        if(showSecondSection && showFirstSection) {
            firstSectionSeperator.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
        mCityScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                alpha = (float) scrollY / (float) BLUR_EFFECT_HEIGHT;
                CommonUtil.TLog("value", scrollY + " " + oldScrollY + " " + alpha);
                if (alpha > 1) {
                    alpha = 1;
                    if(locality!=null && locality.label != null) {
                        if(locality.suburb != null && locality.suburb.city != null) {
                            if(!TextUtils.isEmpty(locality.suburb.city.label)) {
                                mCityCollapseToolbar.setTitle(locality.label.toLowerCase() + " - " + locality.suburb.city.label.toLowerCase());
                            } else {
                                mCityCollapseToolbar.setTitle(locality.label.toLowerCase());
                            }
                        }
                    }
                }else{
                    if(locality!=null && locality.label != null) {
                        mCityCollapseToolbar.setTitle(locality.label.toLowerCase());
                    }
                }
                mBlurredCityImage.setAlpha(alpha);
                mBlurredCityImage.setAlpha(alpha);
                if(alpha == 0) {
                    mBlurredCityImage.setVisibility(View.GONE);
                } else {
                    mBlurredCityImage.setVisibility(View.VISIBLE);
                }
                if(alpha == 1) {
                    mMainCityImage.setVisibility(View.GONE);
                } else {
                    mMainCityImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addKyn(List<AmenityCluster> amenityClusters) {
        if(amenityClusters != null && amenityClusters.size()>0) {
            KynFragment newFragment = new KynFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_kyn_title));
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_kyn, newFragment, false);
            newFragment.setData(amenityClusters, mActivityCallbacks);
        }
    }

    private void addNearByLocalitiesFragment(ArrayList<Locality> nearbyLocalities) {
        if(nearbyLocalities != null && nearbyLocalities.size()>0) {
            if(locality != null && locality.localityId != null && locality.localityId > 0) {
                for (Iterator<Locality> iterator = nearbyLocalities.iterator(); iterator.hasNext(); ) {
                    Locality local = iterator.next();
                    if(local.localityId != null && local.localityId.equals(locality.localityId)) {
                        iterator.remove();
                    }
                }
            }

            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_nearby_localities_title));
            bundle.putInt("placeholder", R.drawable.locality_placeholder);
            bundle.putString("action", "view details");
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities, newFragment, false);
            newFragment.setNearByLocalityData(nearbyLocalities);
        }
    }

    private void addLocalitiesApartmentsFragment(ArrayList<ListingAggregation> listingAggregations) {
/*        if(listingAggregations != null && listingAggregations.size()>0) {
            LocalitiesApartmentsFragment newFragment = new LocalitiesApartmentsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_available_locality_status));
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_apartments, newFragment, false);
            newFragment.setData(listingAggregations);
        }*/
    }

    private void addPriceTrendFragment(ArrayList<Locality> nearbyLocalities) {

        if(locality == null){
            return;
        }
        ArrayList<Long> localities = new ArrayList<>();
        for(int i = 0;i< nearbyLocalities.size() && i<3;i++){
            localities.add(nearbyLocalities.get(i).localityId);
        }
        LocalityPriceTrendFragment newFragment = new LocalityPriceTrendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_price_trends_label));
        bundle.putLong("localityId", localityId);
        bundle.putSerializable("locality", localities);
        bundle.putInt("primaryAverage", meadianSale == null ? 0 : meadianSale);
        Integer cityMedian = LocalityUtil.calculateAveragePrice(locality.suburb.city.listingAggregations).intValue();
        bundle.putInt("cityAverage", cityMedian == null ? 0 : cityMedian);
        Integer cityRental = LocalityUtil.calculateRentalPrice(locality.suburb.city.listingAggregations).intValue();
        bundle.putInt("cityRental", cityRental == null ? 0 : cityRental);
        bundle.putInt("secondaryRental", meadianRental == null ? 0 : meadianRental);
        bundle.putString("localityName", locality.label);
        bundle.putString("cityName", locality.suburb.city.label);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_price_trends, newFragment, false);
    }


    private void addLocalitiesLifestyleFragment(ArrayList<EntityDesc> entityDescriptions) {
        if(entityDescriptions != null && entityDescriptions.size()>0) {
            LocalityLifestyleFragment newFragment = new LocalityLifestyleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.localities_lifestyle_title) + " " + locality.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_lifestyle, newFragment, false);
            insertImagesinEntityDescriptions();
            newFragment.setData(entityDescriptions);
        }
    }

    private void insertImagesinEntityDescriptions() {
        HashMap<String,String> imagesHashMap = LocalityUtil.getImageHashMap(locality.images);
        if(imagesHashMap!=null && locality.entityDescriptions!=null) {
            for (EntityDesc entityDesc : locality.entityDescriptions) {
                if(entityDesc.entityDescriptionCategories!=null && entityDesc.entityDescriptionCategories.masterDescriptionCategory!=null
                        && entityDesc.entityDescriptionCategories.masterDescriptionCategory.name!=null) {
                    entityDesc.imageUrl = imagesHashMap.get(entityDesc.entityDescriptionCategories.masterDescriptionCategory.name.toLowerCase());
                }
            }
        }
    }

    private void addTopAgentsFragment(ArrayList<TopAgent> topAgents) {
        if(topAgents != null && topAgents.size()>0) {
            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_top_agents_label));
            bundle.putInt("placeholder", R.drawable.seller_placeholder);
            bundle.putString("action", "view seller details");
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_top_agents, newFragment, false);
            newFragment.setDataForTopAgents(locality.cityId, locality.localityId, topAgents);
        }
    }


    private void addTopBuilders(ArrayList<Builder> builders) {
        if(builders != null && builders.size()>0) {
            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            if(null==locality){
                bundle.putString("title", getResources().getString(R.string.locality_top_builders_no_locality_label));
            }else {
                bundle.putString("title", String.format(getResources().getString(R.string.locality_top_builders_label), locality.label));
            }
            bundle.putInt("placeholder", R.drawable.builder_placeholder);
            bundle.putString("action", "view projects");
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_top_builders, newFragment, false);
            newFragment.setDataForTopBuilders(builders);
        }
    }
    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        if (taxonomyCardList != null && taxonomyCardList.size() > 0) {
            LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_properties_label) + " " + locality.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_props, newFragment, false);
            newFragment.setData(taxonomyCardList);
        }
    }

    private void fetchHero()
    {
        if(locality.localityHeroshotImageUrl!=null){
            Configuration config = getResources().getConfiguration();
            int width = config.screenWidthDp;
            int height = config.screenHeightDp;
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(locality.localityHeroshotImageUrl, width / 2, height / 2, true).concat("&blur=true"),
                    new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                    if(!isVisible()){
                        return;
                    }
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
//                    final Bitmap newImg = Blur.fastblur(mContext, image, 25);
                    if(mBlurredCityImage!=null)
                        mBlurredCityImage.setImageBitmap(image);
                }
            });
            mMainCityImage.setImageUrl(ImageUtils.getImageRequestUrl(locality.localityHeroshotImageUrl, width / 2, height / 2, true), MakaanNetworkClient.getInstance().getImageLoader());
        }/*else{
            mBlurredCityImage.setImageResource(R.drawable.locality_background_blur_placeholder);
        }*/
    }


    private void calculateMedian(ArrayList<ListingAggregation> listingAggregations) {
            meadianSale = LocalityUtil.calculateAveragePrice(listingAggregations).intValue();
            meadianRental = LocalityUtil.calculateRentalPrice(listingAggregations).intValue();

    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Subscribe
    public void onResult(OnNearByLocalityClickEvent nearByLocalityClickEvent){
        if(!isVisible()) {
            return;
        }
        Toast.makeText(getActivity(), "Nearby clicked, locality id :" + nearByLocalityClickEvent.localityId, Toast.LENGTH_SHORT);
        Intent intent = new Intent(getActivity(),OverviewActivity.class);
        Bundle bundle = new Bundle();

        bundle.putLong(OverviewActivity.ID, nearByLocalityClickEvent.localityId);
        bundle.putInt(OverviewActivity.TYPE, OverviewItemType.LOCALITY.ordinal());

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Subscribe
    public void onResult(OnTopAgentClickEvent onTopAgentClickEvent){
        if(!isVisible()) {
            return;
        }
        SerpRequest serpRequest = new SerpRequest(SerpActivity.TYPE_SELLER);
        serpRequest.setSellerId(onTopAgentClickEvent.agentId);
        serpRequest.launchSerp(getActivity());
    }

    @Subscribe
    public void onResult(OnTopBuilderClickEvent onTopBuilderClickEvent){
        if(!isVisible()) {
            return;
        }
        SerpRequest serpRequest = new SerpRequest(SerpActivity.TYPE_BUILDER);
        serpRequest.setBuilderId(onTopBuilderClickEvent.builderId);
        serpRequest.launchSerp(getActivity());
    }

    @OnClick(R.id.pyr_button_bottom)
    public void onBottomPyrClick(){
        /*-------------------track------------------event-------------------*/
        Properties properties = MakaanEventPayload.beginBatch();
        if(locality!=null && locality.localityId!=null) {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, locality.localityId));
        }else {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
        }
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.locality);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.pyrFormOpen);
        /*-------------------------------------------------------------------*/

        Intent pyrIntent = new Intent(getActivity(), PyrPageActivity.class);
        pyrIntent.putExtra(PyrPageActivity.KEY_CITY_NAME, locality.suburb.city.label);
        if(locality.cityId != null && locality.cityId > 0) {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, locality.cityId);
        } else {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, locality.suburb.city.id);
        }
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_ID, locality.localityId);
        pyrIntent.putExtra(PyrPageActivity.KEY_LOCALITY_NAME, locality.label);
        pyrIntent.putExtra(PyrPageActivity.SOURCE_SCREEN_NAME, ((BaseJarvisActivity) getActivity()).getScreenName());
        getActivity().startActivity(pyrIntent);
    }

    @Override
    public void bindView(OverviewActivityCallbacks activityCallbacks) {
        mActivityCallbacks = activityCallbacks;
    }

    @Override
    public void onCollapsed() {
        mCityScrollView.scrollTo(0,compressedTv.getTop());
    }
}
