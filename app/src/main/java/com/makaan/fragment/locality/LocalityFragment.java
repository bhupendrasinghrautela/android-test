package com.makaan.fragment.locality;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.constants.RequestConstants;
import com.makaan.event.agents.callback.TopAgentsCallback;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.locality.OnNearByLocalityClickEvent;
import com.makaan.event.locality.OnTopAgentClickEvent;
import com.makaan.event.locality.OnTopBuilderClickEvent;
import com.makaan.event.locality.TopBuilderInLocalityEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.TaxonomyCard;
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
import com.makaan.util.Blur;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/21/16.
 */
public class LocalityFragment extends MakaanBaseFragment{
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
    ProgressBar livingScoreProgress;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price)
    TextView salesMedianPrice;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_label)
    TextView salesMedianPriceLabel;
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_rent)
    TextView rentMedianPrice;
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
    @Bind(R.id.view_locality_seperator_2)
    View secondSectionSeperator;
    @Bind(R.id.tv_locality_interested_in)
    TextView interestedInTv;

    private static final int BLUR_EFFECT_HEIGHT = 300;
    private float alpha;
    private Long localityId ;
    private Locality locality;
    private Context mContext ;
    private Integer meadianRental, meadianSale;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_locality;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        setLocalityId();
        fetchData();
        initListeners();
    }

    private void setLocalityId() {
        localityId = getArguments().getLong("localityId");
    }

    private void fetchData() {
        ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getLocalityById(localityId);
    }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent){
        locality = localityByIdEvent.locality;
        Log.i(this.getClass().getSimpleName(), locality.toString());
        populateLocalityData();
        frame.setVisibility(View.VISIBLE);
        fetchHero();
        mMainCityImage.setDefaultImageResId(R.drawable.locality_hero);
        addLocalitiesLifestyleFragment(locality.entityDescriptions);
        addProperties(new TaxonomyService().getTaxonomyCardForLocality(locality.localityId, locality.minAffordablePrice, locality.maxAffordablePrice, locality.maxAffordablePrice, locality.maxBudgetPrice));
        ((LocalityService)MakaanServiceFactory.getInstance().getService(LocalityService.class)).getNearByLocalities(locality.latitude, locality.longitude, 10);
        ((AmenityService)MakaanServiceFactory.getInstance().getService(AmenityService.class)).getAmenitiesByLocation(locality.latitude, locality.longitude, 10);
        ((AgentService)MakaanServiceFactory.getInstance().getService(AgentService.class)).getTopAgentsForLocality(locality.cityId, locality.localityId, 10, false, new TopAgentsCallback() {
            @Override
            public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {
                addTopAgentsFragment(topAgents);
            }
        });
        ((LocalityService)MakaanServiceFactory.getInstance().getService(LocalityService.class)).getTopBuildersInLocality(locality.localityId, 10);
        addPriceTrendFragment();
        addLocalitiesApartmentsFragment(locality.listingAggregations);
    }

    @Subscribe
    public void onResults(TopBuilderInLocalityEvent topBuilderInLocalityEvent){
        addTopBuilders(topBuilderInLocalityEvent.builders);
    }

    @Subscribe
    public void onResults(NearByLocalitiesEvent localitiesEvent){
        addNearByLocalitiesFragment(localitiesEvent.nearbyLocalities);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        addKyn(amenityGetEvent.amenityClusters);
    }

    private void populateLocalityData() {
        final Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comforta.ttf");
        mCityCollapseToolbar.setCollapsedTitleTypeface(tf);
        mCityCollapseToolbar.setExpandedTitleTypeface(tf);
        if(locality.description !=null && !locality.description.isEmpty())
            overviewContentTV.setText(Html.fromHtml(locality.description));
        else {
            compressedTv.setVisibility(View.GONE);
        }
        mCityCollapseToolbar.setTitle(locality.label);
        livinScoreTv.setText("" + locality.livabilityScore);
        livingScoreProgress.setProgress(locality.livabilityScore == null ? 0 : (int) (locality.livabilityScore * 10));
        livingScoreProgress.setVisibility(locality.livabilityScore == null ? View.GONE : View.VISIBLE);
        livinScoreTv.setVisibility(locality.livabilityScore == null ? View.GONE : View.VISIBLE);
        calculateMedian(locality.listingAggregations);
        interestedInTv.setText("interested in "+locality.label+"?");
        boolean showFirstSectionDivider = false;
        boolean showSecondSectionDivider = false;
        if(meadianSale != null && meadianSale.intValue() != 0) {
            showFirstSectionDivider = true;
            salesMedianPrice.setVisibility(View.VISIBLE);
            salesMedianPriceLabel.setVisibility(View.VISIBLE);
            salesMedianPrice.setText("\u20B9 " + meadianSale + " / sq ft");
        }
        if(meadianRental != null && meadianRental.intValue() != 0) {
            showSecondSectionDivider = true;
            rentMedianPrice.setVisibility(View.VISIBLE);
            rentMedianPriceLabel.setVisibility(View.VISIBLE);
            rentMedianPrice.setText("\u20B9 " + meadianRental + " / month");
        }
        if(locality.avgPriceRisePercentage!=null){
            showFirstSectionDivider = true;
            annualGrowthLabelTv.setVisibility(View.VISIBLE);
            annualGrowthTv.setVisibility(View.VISIBLE);
            annualGrowthTv.setText(locality.avgPriceRisePercentage + " %");
        }
       if(locality.avgRentalDemandRisePercentage != null) {
            showSecondSectionDivider = true;
            annualRentDemandGrowthTv.setVisibility(View.VISIBLE);
            annualRentDemandGrowthLabelTv.setVisibility(View.VISIBLE);
            annualRentDemandGrowthTv.setText(locality.avgPriceRisePercentage + " %");
        }
        if(!showSecondSectionDivider) {
            rentMedianPrice.setVisibility(View.GONE);
            rentMedianPriceLabel.setVisibility(View.GONE);
            annualRentDemandGrowthLabelTv.setVisibility(View.GONE);
            annualRentDemandGrowthTv.setVisibility(View.GONE);
            secondSectionSeperator.setVisibility(View.GONE);
        }
        if(!showFirstSectionDivider) {
            annualGrowthTv.setVisibility(View.GONE);
            annualGrowthLabelTv.setVisibility(View.GONE);
            firstSectionSeperator.setVisibility(View.GONE);
            salesMedianPrice.setVisibility(View.GONE);
            salesMedianPriceLabel.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        mCityScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                alpha = (float) scrollY / (float) BLUR_EFFECT_HEIGHT;
                Log.e("value", scrollY + " " + oldScrollY + " " + alpha);
                if (alpha > 1) {
                    alpha = 1;
                    mCityCollapseToolbar.setTitle(locality.label+" - "+locality.suburb.city.label);
                }else{
                    mCityCollapseToolbar.setTitle(locality.label);
                }
                mBlurredCityImage.setAlpha(alpha);
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
            newFragment.setData(amenityClusters);
        }
    }

    private void addNearByLocalitiesFragment(ArrayList<Locality> nearbyLocalities) {
        if(nearbyLocalities != null && nearbyLocalities.size()>0) {
            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_nearby_localities_title));
            bundle.putInt("placeholder", R.drawable.placeholder_localities_nearby);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities, newFragment, false);
            newFragment.setNearByLocalityData(nearbyLocalities);
        }
    }

    private void addLocalitiesApartmentsFragment(ArrayList<ListingAggregation> listingAggregations) {
        if(listingAggregations != null && listingAggregations.size()>0) {
            LocalitiesApartmentsFragment newFragment = new LocalitiesApartmentsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_available_locality_status));
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_apartments, newFragment, false);
            newFragment.setData(listingAggregations);
        }
    }

    private void addPriceTrendFragment() {
        LocalityPriceTrendFragment newFragment = new LocalityPriceTrendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_price_trends_label));
        bundle.putLong("localityId", locality.localityId);
        bundle.putInt("primaryMedian", meadianSale == null ? 0 : meadianSale);
        bundle.putDouble("primaryRise", locality.avgPriceRisePercentage == null ? 0 : locality.avgPriceRisePercentage);
        bundle.putDouble("secondaryAverage", locality.averageRentPerMonth == null ? 0 : locality.averageRentPerMonth);
        bundle.putInt("secondaryMedian", meadianRental == null ? 0 : meadianRental);
        bundle.putString("localityName", locality.label);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_price_trends, newFragment, false);
    }


    private void addLocalitiesLifestyleFragment(ArrayList<EntityDesc> entityDescriptions) {
        if(entityDescriptions != null && entityDescriptions.size()>0) {
            LocalityLifestyleFragment newFragment = new LocalityLifestyleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.localities_lifestyle_title)+" "+locality.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_lifestyle, newFragment, false);
            newFragment.setData(entityDescriptions);
        }
    }

    private void addTopAgentsFragment(ArrayList<TopAgent> topAgents) {
        if(topAgents != null && topAgents.size()>0) {
            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_top_agents_label));
            bundle.putInt("placeholder", R.drawable.placeholder_agent);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_top_agents, newFragment, false);
            newFragment.setDataForTopAgents(locality.cityId, locality.localityId, topAgents);
        }
    }


    private void addTopBuilders(ArrayList<Builder> builders) {
        if(builders != null && builders.size()>0) {
            NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_top_builders_label));
            bundle.putInt("placeholder", R.drawable.placeholder_localities_builders);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_top_builders, newFragment, false);
            newFragment.setDataForTopBuilders(builders);
        }
    }
    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        if (taxonomyCardList != null && taxonomyCardList.size() > 0) {
            LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.locality_properties_label)+" "+locality.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_props, newFragment, false);
            newFragment.setData(taxonomyCardList);
        }
    }

    private void fetchHero()
    {
        if(locality.localityHeroshotImageUrl!=null){
            MakaanNetworkClient.getInstance().getImageLoader().get(locality.localityHeroshotImageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {

                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
                    final Bitmap newImg = Blur.fastblur(mContext, image, 25);
                    mBlurredCityImage.setImageBitmap(newImg);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            mMainCityImage.setImageUrl(locality.localityHeroshotImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        }
    }


    private void calculateMedian(ArrayList<ListingAggregation> listingAggregations) {
        double saleMedian = 0;
        int countsSales = 0;
        for (ListingAggregation ListingAggregation:listingAggregations) {
            if (ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.PRIMARY) ||
                    ListingAggregation.listingCategory.equalsIgnoreCase(RequestConstants.RESALE)) {
                saleMedian = saleMedian + ListingAggregation.avgPricePerUnitArea;
                countsSales++;
            }
        }
            if(countsSales!=0)
                saleMedian = saleMedian / countsSales;

            meadianSale = (int) saleMedian;
            meadianRental = locality.averageRentPerMonth == null? null : locality.averageRentPerMonth.intValue();

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
        Toast.makeText(getActivity(), "Nearby clicked, locality id :" + nearByLocalityClickEvent.localityId, Toast.LENGTH_SHORT);
        Intent intent = new Intent(getActivity(),LocalityActivity.class);
        intent.putExtra(LocalityActivity.LOCALITY_ID, nearByLocalityClickEvent.localityId);
        startActivity(intent);
    }

    @Subscribe
    public void onResult(OnTopAgentClickEvent onTopAgentClickEvent){
        Toast.makeText(getActivity(), "Agent clicked, agent id :" + onTopAgentClickEvent.agentId, Toast.LENGTH_SHORT);
    }

    @Subscribe
    public void onResult(OnTopBuilderClickEvent onTopBuilderClickEvent){
        Toast.makeText(getActivity(), "Builder clicked, builder id :" + onTopBuilderClickEvent.builderId, Toast.LENGTH_SHORT);
    }

}
