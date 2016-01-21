package com.makaan.activity.locality;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.event.agents.callback.TopAgentsCallback;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.locality.NearByLocalitiesEvent;
import com.makaan.event.locality.TopBuilderInLocalityEvent;
import com.makaan.fragment.locality.KynFragment;
import com.makaan.fragment.locality.LocalitiesApartmentsFragment;
import com.makaan.fragment.locality.LocalityLifestyleFragment;
import com.makaan.fragment.locality.LocalityPriceTrendFragment;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.fragment.locality.NearByLocalitiesFragment;
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
import com.makaan.util.Blur;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/18/16.
 */
public class LocalityActivity extends MakaanFragmentActivity {
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
    @Bind(R.id.tv_locality_per_sqr_ft_median_price_rent)
    TextView rentMedianPrice;
    @Bind(R.id.tv_locality_annual_growth)
    TextView annualGrowthTv;
    @Bind(R.id.tv_locality_annual_growth_rent)
    TextView annualRentDemandGrowthTv;


    private static final int BLUR_EFFECT_HEIGHT = 300;
    private float alpha;
    private Long localityId ;
    private Locality locality;
    private Context mContext = this;
    private Integer meadianRental, meadianSale;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_locality;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocalityId();
        fetchData();
        initListeners();
    }

    private void setLocalityId() {
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        localityId = bundle.getLong("localityId");
        if(localityId==null)
            this.localityId = Long.valueOf(50157); //use 50001 for belapur
    }

    private void fetchData() {
        ((LocalityService)MakaanServiceFactory.getInstance().getService(LocalityService.class)).getLocalityById(localityId);
       }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent){
        locality = localityByIdEvent.locality;
        fetchHero();
        populateLocalityData();
        addLocalitiesLifestyleFragment(locality.entityDescriptions);
        addProperties(new TaxonomyService().getTaxonomyCardForLocality(locality.localityId));
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
        mCityCollapseToolbar.setTitle(locality.label);
        overviewContentTV.setText(locality.description);
        livinScoreTv.setText("" + locality.livabilityScore);
        livingScoreProgress.setProgress((int) (locality.livabilityScore * 10));
        calculateMedian(locality.listingAggregations);
        salesMedianPrice.setText("Rs " + meadianSale + " / sq ft");
        rentMedianPrice.setText("Rs " + meadianRental + " / month");
        annualGrowthTv.setText(locality.avgPriceRisePercentage == null ? "N/A" : "" + locality.avgPriceRisePercentage + " %");
        annualRentDemandGrowthTv.setText(locality.avgRentalDemandRisePercentage == null ? "N/A" : "" + locality.avgPriceRisePercentage + " %");
    }

    private void initListeners() {
        mCityScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                alpha = (float) scrollY / (float) BLUR_EFFECT_HEIGHT;
                Log.e("value", scrollY + " " + oldScrollY + " " + alpha);
                if (alpha > 1) {
                    alpha = 1;
                }
                mBlurredCityImage.setAlpha(alpha);
            }
        });
    }

    private void addKyn(List<AmenityCluster> amenityClusters) {
        Fragment newFragment = new KynFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_kyn_title));
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_kyn, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((KynFragment)newFragment).setData(amenityClusters);
    }

    private void addNearByLocalitiesFragment(ArrayList<Locality> nearbyLocalities) {
        NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_nearby_localities_title));
        bundle.putInt("placeholder", R.drawable.placeholder_localities_nearby);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities, newFragment, false);
        newFragment.setNearByLocalityData(nearbyLocalities);
    }

    private void addLocalitiesApartmentsFragment(ArrayList<ListingAggregation> listingAggregations) {
        Fragment newFragment = new LocalitiesApartmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_available_locality_status));
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_apartments, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalitiesApartmentsFragment)newFragment).setData(listingAggregations);
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
        newFragment.setArguments(bundle);

        initFragment(R.id.container_nearby_localities_price_trends, newFragment, false);
    }


    private void addLocalitiesLifestyleFragment(ArrayList<EntityDesc> entityDescriptions) {
        Fragment newFragment = new LocalityLifestyleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.localities_lifestyle_title));
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_lifestyle, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalityLifestyleFragment)newFragment).setData(entityDescriptions);
    }

    private void addTopAgentsFragment(ArrayList<TopAgent> topAgents) {
        NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_top_agents_label));
        bundle.putInt("placeholder", R.drawable.placeholder_agent);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_top_agents, newFragment, false);
        newFragment.setDataForTopAgents(locality.cityId, locality.localityId, topAgents);
    }


    private void addTopBuilders(ArrayList<Builder> builders) {
        NearByLocalitiesFragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_top_builders_label));
        bundle.putInt("placeholder", R.drawable.placeholder_localities_builders);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_top_builders, newFragment, false);
        newFragment.setDataForTopBuilders(builders);

    }
    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.locality_properties_label));
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_props, newFragment, false);
        newFragment.setData(taxonomyCardList);
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

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    private void calculateMedian(ArrayList<ListingAggregation> listingAggregations) {
        double rentalMedian = 0, saleMedian = 0;
        int countsRental = 0, countsSales = 0;
        for (ListingAggregation ListingAggregation:listingAggregations){
            if(ListingAggregation.listingCategory.equalsIgnoreCase("primary") ||
                    ListingAggregation.listingCategory.equalsIgnoreCase("resale")){
                saleMedian = saleMedian + ListingAggregation.avgPricePerUnitArea;
                countsSales++;
            }else{
                rentalMedian = rentalMedian + ListingAggregation.avgPricePerUnitArea;
                countsRental++;
            }
            if(countsSales!=0)
                saleMedian = saleMedian / countsSales;
            if(countsRental!=0)
                rentalMedian = rentalMedian / countsRental;

            meadianSale = (int) saleMedian;
            meadianRental = (int) rentalMedian;
        }
    }
}
