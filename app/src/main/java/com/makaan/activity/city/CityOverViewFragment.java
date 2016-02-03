package com.makaan.activity.city;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.city.CityTrendEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.locality.LocalityLifestyleFragment;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.TaxonomyCard;
import com.makaan.response.city.City;
import com.makaan.response.city.EntityDesc;
import com.makaan.response.locality.Locality;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.CityService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.TaxonomyService;
import com.makaan.ui.MakaanBarChartView;
import com.makaan.ui.MultiSelectionSpinner;
import com.makaan.ui.MultiSelectionSpinner.OnSelectionChangeListener;
import com.makaan.ui.PriceTrendView;
import com.makaan.ui.city.TopLocalityView;
import com.makaan.util.Blur;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnCheckedChanged;

/**
 * Created by aishwarya on 01/01/16.
 */
public class CityOverViewFragment extends MakaanBaseFragment{

    @Bind(R.id.main_city_image)
    FadeInNetworkImageView mMainCityImage;
    @Bind(R.id.blurred_city_image)
    ImageView mBlurredCityImage;
    @Bind(R.id.city_scrollview)
    NestedScrollView mCityScrollView;
    @Bind(R.id.city_collapse_toolbar)
    CollapsingToolbarLayout mCityCollapseToolbar;
    @Bind(R.id.city_tag_line)
    TextView mCityTagLine;
    @Bind(R.id.rental_growth_text)
    TextView mRentalGrowth;
    @Bind(R.id.annual_growth_text)
    TextView mAnnualGrowth;
    @Bind(R.id.content_text)
    TextView mCityDescription;
    @Bind(R.id.top_locality_layout)
    TopLocalityView mTopLocalityLayout;
/*    @Bind(R.id.city_connect_header)
    TextView mCityConnectHeader;*/
    @Bind(R.id.price_trend_view)
    PriceTrendView mPriceTrendView;
    @Bind(R.id.bar_chart_layout)
    MakaanBarChartView mBarChartView;
    @Bind(R.id.bhk_spinner)
    MultiSelectionSpinner mBhkSpinner;
    @Bind(R.id.property_type_spinner)
    MultiSelectionSpinner mPropertyTypeSpinner;
    ArrayList<Integer> mSelectedPropertyTypes;
    ArrayList<Integer> mSelectedBedroomTypes;
    @Bind(R.id.tv_locality_interested_in)
    TextView interestedInTv;
    @Bind(R.id.city_property_buy_rent_switch)
    Switch mBuyRentSwitch;

    @OnCheckedChanged(R.id.city_property_buy_rent_switch)
    public void onBuyRentSwitched(){
        if(mBuyRentSwitch.isChecked()){
            isRent = true;
        }
        else{
            isRent = false;
        }
    }

    private static final int BLUR_EFFECT_HEIGHT = 300;
    private float alpha;
    private Context mContext;
    private City mCity;
    private Boolean isRent;
    private String PREFIX_PROPERTY_SPINNER = "property type :";
    private String POSTFIX_BHK_SPINNER = "bhk";
    private ArrayList<Locality> mCityTopLocalities;

    @Override
    protected int getContentViewId() {
        return R.layout.city_overview_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initListeners();
    }

    private void initUiUsingCityDetails() {
        mMainCityImage.setImageUrl(mCity.cityHeroshotImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        mCityCollapseToolbar.setTitle(mCity.label);
        interestedInTv.setText("interested in " + mCity.label + "?");
        addLocalitiesLifestyleFragment(mCity.entityDescriptions);
        addProperties(new TaxonomyService().getTaxonomyCardForCity(mCity.id, mCity.minAffordablePrice, mCity.maxAffordablePrice, mCity.minLuxuryPrice, mCity.maxBudgetPrice));
        //mCityConnectHeader.setText(getString(R.string.city_connect_header_text) + " " + mCity.label);
        if(mCity.cityHeroshotImageUrl != null) {
            MakaanNetworkClient.getInstance().getImageLoader().get(mCity.cityHeroshotImageUrl, new ImageListener() {
                @Override
                public void onResponse(final ImageContainer imageContainer, boolean b) {
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
        }
        else{
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.locality_hero);
            final Bitmap newImg = Blur.fastblur(mContext, image, 25);
            mBlurredCityImage.setImageBitmap(newImg);
        }
        mCityTagLine.setText(mCity.cityTagLine);
        mAnnualGrowth.setText(StringUtil.getTwoDecimalPlaces(mCity.annualGrowth) + "%");
        mRentalGrowth.setText(StringUtil.getTwoDecimalPlaces(mCity.rentalYield) + "%");
        mCityDescription.setText(Html.fromHtml(mCity.description));

    //lineChartLayout.bindView(getResources().getString(R.string.response));
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
        mPropertyTypeSpinner.setOnSelectionChangeListener(new OnSelectionChangeListener() {
            @Override
            public void onSelectionChanged() {
                mSelectedPropertyTypes = (ArrayList<Integer>) mPropertyTypeSpinner.getSelectedIds();
                makeBarGraphRequest();
            }
        });
        mBhkSpinner.setOnSelectionChangeListener(new OnSelectionChangeListener() {
            @Override
            public void onSelectionChanged() {
                mSelectedBedroomTypes = (ArrayList<Integer>) mBhkSpinner.getSelectedIds();
                makeBarGraphRequest();
            }
        });
    }

    private void makeBarGraphRequest() {
        if(mCity!=null) {
            new CityService().getPropertyRangeInCity(mCity.id, mSelectedBedroomTypes,mSelectedPropertyTypes, isRent, 10000, 500000, 50000);
        }
    }

    private void initView() {
        mPropertyTypeSpinner.setMessage(PREFIX_PROPERTY_SPINNER, null);
        mPropertyTypeSpinner.setItems(MasterDataCache.getInstance().getBuyPropertyTypes());
        mBhkSpinner.setMessage(null, POSTFIX_BHK_SPINNER);
        mBhkSpinner.setItems(MasterDataCache.getInstance().getBhkList());
        mMainCityImage.setDefaultImageResId(R.drawable.locality_hero);
    }

    @Subscribe
    public void onResults(CityByIdEvent cityByIdEvent){
        mCity = cityByIdEvent.city;
        initUiUsingCityDetails();
    }

    @Subscribe
    public void onTopLocalityResults(CityTopLocalityEvent cityTopLocalityEvent){
        mCityTopLocalities = cityTopLocalityEvent.topLocalitiesInCity;
        mTopLocalityLayout.bindView(mCityTopLocalities);
        fetchPriceTrendData(mCityTopLocalities);
    }

    @Subscribe
    public void onPriceTrendResults(CityTrendEvent cityTrendEvent){
        mBarChartView.bindView(cityTrendEvent.cityTrendData);
    }

    private void fetchPriceTrendData(ArrayList<Locality> cityTopLocalities) {
        ArrayList<Long> localityIds = new ArrayList<>();
        for(Locality locality:cityTopLocalities){
            localityIds.add(locality.localityId);
        }
        new PriceTrendService().getPriceTrendForLocalities(localityIds, 60, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                mPriceTrendView.bindView(localityPriceTrendDto);
            }
        });
    }

    private void addLocalitiesLifestyleFragment(ArrayList<EntityDesc> entityDescriptions) {
        LocalityLifestyleFragment newFragment = new LocalityLifestyleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.know_more)+" "+mCity.label);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_lifestyle, newFragment, false);
        newFragment.setData(entityDescriptions);
    }

    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.properties_in)+" "+mCity.label);
        newFragment.setArguments(bundle);
        initFragment(R.id.container_nearby_localities_props, newFragment, false);
        newFragment.setData(taxonomyCardList);
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction =((CityActivity) mContext).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}
