package com.makaan.activity.locality;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.locality.KynFragment;
import com.makaan.fragment.locality.LocalitiesApartmentsFragment;
import com.makaan.fragment.locality.LocalityLifestyleFragment;
import com.makaan.fragment.locality.LocalityPriceTrendFragment;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.fragment.locality.NearByLocalitiesFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.TaxonomyCard;
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.response.locality.ListingAggregation;
import com.makaan.response.locality.Locality;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.AmenityService;
import com.makaan.service.LocalityService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.TaxonomyService;
import com.makaan.util.AppBus;
import com.makaan.util.Blur;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

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
            this.localityId = Long.valueOf(50157);
    }

    private void fetchData() {
        new LocalityService().getLocalityById(localityId);
        addProperties(new TaxonomyService().getTaxonomyCardForLocality(localityId));
        new AmenityService().getAmenitiesByLocation(13.03244019, 77.6019516, 50);//TODO: replace with locality's lat long


        ArrayList<Long> localityIds = new ArrayList<>();
        localityIds.add(localityId);
        localityIds.add((long) 53250);
        new PriceTrendService().getPriceTrendForLocalities(localityIds, 12, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
//                priceTrendView.bindView(localityPriceTrendDto);
            }
        });
    }

    @Subscribe
    public void onResults(LocalityByIdEvent localityByIdEvent){
        locality = localityByIdEvent.locality;
        populateLocalityData();
        fetchHero();
        addStuff();
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent){
        addKyn(amenityGetEvent.amenityClusters);
    }

    private void populateLocalityData() {
        mCityCollapseToolbar.setTitle(locality.label);
        overviewContentTV.setText(locality.description);
        livinScoreTv.setText("" + locality.livabilityScore);
        livingScoreProgress.setProgress((int) (locality.livabilityScore * 10));
        calculateMedian(locality.listingAggregations);
        salesMedianPrice.setText("Rs " + meadianSale + " / sq ft");
        rentMedianPrice.setText("Rs "+meadianRental+" / month");
        annualGrowthTv.setText(locality.avgPriceRisePercentage == null ? "N/A" : "" + locality.avgPriceRisePercentage + " %");
        annualRentDemandGrowthTv.setText(locality.avgRentalDemandRisePercentage == null ? "N/A" : "" + locality.avgPriceRisePercentage + " %");
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

    private void addStuff() {
        addNearByLocalitiesFragment();
        addTopAgentsFragment();
        addTopBuilders();
        addLocalitiesLifestyleFragment();
        addLocalitiesApartmentsFragment();
        addPriceTrendFragment();
    }

    private void addKyn(List<AmenityCluster> amenityClusters) {
        Fragment newFragment = new KynFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "know your neighbourhood");
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_kyn, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((KynFragment)newFragment).setData(amenityClusters);
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

    private void addNearByLocalitiesFragment() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "nearby localities");
        bundle.putInt("placeholder", R.drawable.placeholder_localities_nearby);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyData());
    }

    private void addLocalitiesApartmentsFragment() {
        Fragment newFragment = new LocalitiesApartmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "available property status");
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_apartments, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalitiesApartmentsFragment)newFragment).setData(getDummyDataForApartments());
    }

    private void addPriceTrendFragment() {
        LocalityPriceTrendFragment newFragment = new LocalityPriceTrendFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "price trends");
        bundle.putLong("localityId", locality.localityId);
        bundle.putInt("primaryMedian", meadianSale == null ? 0 : meadianSale);
        bundle.putDouble("primaryRise", locality.avgPriceRisePercentage == null ? 0 : locality.avgPriceRisePercentage);
        bundle.putDouble("secondaryAverage", locality.averageRentPerMonth == null ? 0 : locality.averageRentPerMonth);
        bundle.putInt("secondaryMedian", meadianRental == null ? 0 : meadianRental);
        newFragment.setArguments(bundle);

        initFragment(R.id.container_nearby_localities_price_trends,newFragment,false);
    }

    private List<LocalitiesApartmentsFragment.Properties> getDummyDataForApartments() {
        List<LocalitiesApartmentsFragment.Properties> properties = new ArrayList<>();
        LocalitiesApartmentsFragment.Properties property = new LocalitiesApartmentsFragment.Properties(
                "apartment","1 bhk","2 bhk", "3 bhk","30L - 40L","50L - 660L","70L - 90L","450 - 790 sq ft","450 - 790 sq ft","450 - 790 sq ft","3,475 - 4,580 / sq ft","3,475 - 4,580 / sq ft", "3,475 - 4,580 / sq ft");
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        properties.add(property);
        return properties;
    }

    private void addLocalitiesLifestyleFragment() {
        Fragment newFragment = new LocalityLifestyleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "lifestyle in electronic city");
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_lifestyle, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalityLifestyleFragment)newFragment).setData(getDummyDataForLifestyle());
    }

    private void addTopAgentsFragment() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "top agents");
        bundle.putInt("placeholder", R.drawable.placeholder_agent);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_top_agents, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyDataForAgents());
    }


    private void addTopBuilders() {
        Fragment newFragment = new NearByLocalitiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "top builders");
        bundle.putInt("placeholder", R.drawable.placeholder_localities_builders);
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_top_builders, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((NearByLocalitiesFragment)newFragment).setData(getDummyDataForBuilders());
    }
    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        Fragment newFragment = new LocalityPropertiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", "properties in electronic city");
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_nearby_localities_props, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        ((LocalityPropertiesFragment)newFragment).setData(taxonomyCardList);
    }

    private List<NearByLocalitiesFragment.NearByLocalities> getDummyData() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","1090 +","102 +","median price: 2,400 / sq ft","Koramangla");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);return nearByLocalities;
    }

    private List<NearByLocalitiesFragment.NearByLocalities> getDummyDataForAgents() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","109 +","552 +","investor clinic","Sachin Singh");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        return nearByLocalities;
    }


    private List<NearByLocalitiesFragment.NearByLocalities> getDummyDataForBuilders() {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        NearByLocalitiesFragment.NearByLocalities nearby = new NearByLocalitiesFragment.NearByLocalities("","42 ","18","experience: 18 years","Supertech Group");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);return nearByLocalities;
    }

    private List<LocalityLifestyleFragment.Properties> getDummyDataForLifestyle() {
        List<LocalityLifestyleFragment.Properties> nearByLocalities = new ArrayList<>();
        LocalityLifestyleFragment.Properties nearby = new LocalityLifestyleFragment.Properties("","schools","with constant monitoring and regular rating, we ensure that best brokers feature more on makaan.com. best brokers feature more on makaan. with constant monitoring and regular rating, we ensure that best brokers feature more on makaan.com. best brokers feature more on makaan with constant monitoring and regular rating brokers feature more on makaan. best brokers feature.");
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);
        nearByLocalities.add(nearby);return nearByLocalities;
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
}
