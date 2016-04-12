package com.makaan.activity.city;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.city.CityTrendEvent;
import com.makaan.event.trend.CityPriceTrendEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.locality.LocalityLifestyleFragment;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.TaxonomyCard;
import com.makaan.response.city.City;
import com.makaan.response.city.CityTrendData;
import com.makaan.response.city.EntityDesc;
import com.makaan.response.locality.Locality;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.response.trend.PriceTrendKey;
import com.makaan.service.CityService;
import com.makaan.service.PriceTrendService;
import com.makaan.service.TaxonomyService;
import com.makaan.ui.MakaanBarChartView;
import com.makaan.ui.MakaanBarChartView.OnBarTouchListener;
import com.makaan.ui.MultiSelectionSpinner;
import com.makaan.ui.MultiSelectionSpinner.OnSelectionChangeListener;
import com.makaan.ui.PriceTrendView;
import com.makaan.ui.city.TopLocalityView;
import com.makaan.util.DateUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.LocalityUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by aishwarya on 01/01/16.
 */
public class CityOverViewFragment extends OverviewFragment {

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
    @Bind(R.id.rental_growth_text_refer)
    TextView mRentalGrowthRefer;
    @Bind(R.id.annual_growth_text)
    TextView mAnnualGrowth;
    @Bind(R.id.annual_growth_text_refer)
    TextView mAnnualGrowthRefer;
    @Bind(R.id.content_text)
    TextView mCityDescription;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.compressed_text_view)
    LinearLayout mCompressedTextViewLayout;
    @Bind(R.id.top_locality_layout)
    TopLocalityView mTopLocalityLayout;
    @Bind(R.id.property_range_layout)
    RelativeLayout mPropertyRangeLayout;
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

    @Bind(R.id.pyr_button_bottom)
    Button mPyrButtonBottom;

    ArrayList<Integer> mSelectedPropertyTypes;
    ArrayList<Integer> mSelectedBedroomTypes;
    @Bind(R.id.tv_locality_interested_in)
    TextView interestedInTv;
    @Bind(R.id.city_property_buy_rent_switch)
    Switch mBuyRentSwitch;

    @Bind(R.id.iv_rental_growth)
    ImageView mIvRental;

    @Bind(R.id.iv_annual_growth)
    ImageView mIvAnnual;
    private long mCityId;

    @OnCheckedChanged(R.id.city_property_buy_rent_switch)
    public void onBuyRentSwitched(){
        if(mBuyRentSwitch.isChecked()){
            isRent = true;
            Properties properties= MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
            properties.put(MakaanEventPayload.LABEL, "rent");
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.selectCityPropertyRange);
            List<Integer> previousSelectedIds = mPropertyTypeSpinner.getSelectedIds();
            mPropertyTypeSpinner.setItems(MasterDataCache.getInstance().getRentPropertyTypes());
            mPropertyTypeSpinner.setSelectedIds(previousSelectedIds);
        }
        else{
            Properties properties= MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
            properties.put(MakaanEventPayload.LABEL, "buy");
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.selectCityPropertyRange);
            List<Integer> previousSelectedIds = mPropertyTypeSpinner.getSelectedIds();
            mPropertyTypeSpinner.setItems(MasterDataCache.getInstance().getBuyPropertyTypes());
            mPropertyTypeSpinner.setSelectedIds(previousSelectedIds);
            isRent = false;
        }
        mSelectedPropertyTypes = (ArrayList<Integer>) mPropertyTypeSpinner.getSelectedIds();
        mSelectedBedroomTypes = (ArrayList<Integer>) mBhkSpinner.getSelectedIds();
        makeBarGraphRequest();
    }

    private static final int BLUR_EFFECT_HEIGHT = 200;
    private float alpha;
    private Context mContext;
    private City mCity;
    private Boolean isRent = false;
    private String PREFIX_PROPERTY_SPINNER = "property type :";
    private String POSTFIX_BHK_SPINNER = "bhk :";
    private ArrayList<Locality> mCityTopLocalities;
    private LocalityPriceTrendDto mLocalityPriceTrendDto;

    @Override
    protected int getContentViewId() {
        return R.layout.city_overview_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        setCityId();
        fetchData();
        initToolbar();
        initView();
        initListeners();
        showProgress();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
//        mMainCityImage.setDefaultImageResId(R.drawable.city_background_placeholder);
        return view;
    }

    private void fetchData() {
        new CityService().getCityById(mCityId);
        new CityService().getTopLocalitiesInCity(mCityId, 4);
    }

    private void setCityId() {
        mCityId = getArguments().getLong("id");
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

    private void initUiUsingCityDetails() {
        int width = getResources().getConfiguration().screenWidthDp;
        int height = getResources().getConfiguration().screenHeightDp;
        if (mCity.cityBuyMinPrice != null && mCity.cityBuyMaxPrice != null) {
            new CityService().getPropertyRangeInCity(mCity.id, null, null, false, mCity.cityBuyMinPrice.intValue(), mCity.cityBuyMaxPrice.intValue(),
                    (mCity.cityBuyMaxPrice.intValue() - mCity.cityBuyMinPrice.intValue()) / 20);
        }
        if(mCity.label!=null) {
            mCityCollapseToolbar.setTitle(mCity.label.toLowerCase());
            mCityCollapseToolbar.setCollapsedTitleTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/comforta.ttf"));
            mCityCollapseToolbar.setExpandedTitleTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/comforta.ttf"));
            interestedInTv.setText("interested in " + mCity.label.toLowerCase() + "?");
        }
        addLocalitiesLifestyleFragment(mCity.entityDescriptions);
        addProperties(new TaxonomyService().getTaxonomyCardForCity(mCity.id, mCity.minAffordablePrice, mCity.maxAffordablePrice,
                mCity.minLuxuryPrice, mCity.maxBudgetPrice));
        //mCityConnectHeader.setText(getString(R.string.city_connect_header_text) + " " + mCity.label);
        if(mCity.cityHeroshotImageUrl != null) {
            MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(mCity.cityHeroshotImageUrl, width / 2, height / 2, true).concat("&blur=true"),
                    new CustomImageLoaderListener() {
                @Override
                public void onResponse(final ImageContainer imageContainer, boolean b) {
                    if(!isVisible()){
                        return;
                    }
                    if (b && imageContainer.getBitmap() == null) {
                        return;
                    }
                    final Bitmap image = imageContainer.getBitmap();
//                    final Bitmap newImg = Blur.fastblur(mContext, image, 25);
                    if(mBlurredCityImage != null) {
                        mBlurredCityImage.setImageBitmap(image);
                    }
                }
            });
            mMainCityImage.setImageUrl(ImageUtils.getImageRequestUrl(mCity.cityHeroshotImageUrl, width / 2, height / 2, true),
                    MakaanNetworkClient.getInstance().getImageLoader());
        }/*
        else{
            mBlurredCityImage.setImageResource(R.drawable.city_background_blur_placeholder);
        }*/
        if(mCity.cityTagLine!=null) {
            mCityTagLine.setText(mCity.cityTagLine.toLowerCase());
        }
        if(mCity.annualGrowth!=null) {
            mAnnualGrowthRefer.setVisibility(View.VISIBLE);
            mAnnualGrowth.setText(StringUtil.getTwoDecimalPlaces(mCity.annualGrowth) + "%");
            if(mCity.annualGrowth<0)
                mIvAnnual.setImageResource(R.drawable.bottom_arrow_circle_down);
            else
                mIvAnnual.setImageResource(R.drawable.bottom_arrow_circle_green_up);
        }
        else{
            mAnnualGrowth.setVisibility(View.GONE);
            mAnnualGrowthRefer.setVisibility(View.GONE);
        }
        if(mCity.rentalYield!=null) {
            mRentalGrowthRefer.setVisibility(View.VISIBLE);
            mRentalGrowth.setText(StringUtil.getTwoDecimalPlaces(mCity.rentalYield) + "%");
            if(mCity.rentalYield<0)
                mIvRental.setImageResource(R.drawable.bottom_arrow_circle_down);
            else
                mIvRental.setImageResource(R.drawable.bottom_arrow_circle_green_up);
        }
        else{
            mRentalGrowthRefer.setVisibility(View.GONE);
            mRentalGrowth.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mCity.description)) {
            mCompressedTextViewLayout.setVisibility(View.VISIBLE);
            mCityDescription.setText(Html.fromHtml(mCity.description).toString().toLowerCase());
        }
        else{
            mCompressedTextViewLayout.setVisibility(View.GONE);
        }

    //lineChartLayout.bindView(getResources().getString(R.string.response));
    }

    private void initListeners() {
        mCityScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                alpha = (float) scrollY / (float) BLUR_EFFECT_HEIGHT;
                if (alpha > 1) {
                    alpha = 1;
                }

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
        mPropertyTypeSpinner.setOnSelectionChangeListener(new OnSelectionChangeListener() {
            @Override
            public void onSelectionChanged() {
                mSelectedPropertyTypes = (ArrayList<Integer>) mPropertyTypeSpinner.getSelectedIds();
                StringBuilder builder =new StringBuilder();
                String append="";
                if(isRent){
                    builder =new StringBuilder();
                    for(int id:mSelectedPropertyTypes){
                        builder.append(append);
                        builder.append(MasterDataCache.getInstance().getRentPropertyType(id).name);
                        append="_";
                    }
                }
                else{
                    builder =new StringBuilder();
                    for(int id:mSelectedPropertyTypes){
                        builder.append(append);
                        builder.append(MasterDataCache.getInstance().getBuyPropertyType(id).name);
                        append="_";
                    }
                }
                Properties properties= MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                properties.put(MakaanEventPayload.LABEL, builder);
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.selectCityPropertyRange);

                makeBarGraphRequest();
            }
        });
        mBhkSpinner.setOnSelectionChangeListener(new OnSelectionChangeListener() {
            @Override
            public void onSelectionChanged() {
                mSelectedBedroomTypes = (ArrayList<Integer>) mBhkSpinner.getSelectedIds();
                Properties properties= MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerCity);
                properties.put(MakaanEventPayload.LABEL, mSelectedBedroomTypes.toString()+"Bhk");
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.selectCityPropertyRange);
                makeBarGraphRequest();
            }
        });
    }

    private void makeBarGraphRequest() {
        if(mCity!=null) {
            if (isRent) {
                if(mCity.cityRentMinPrice != null && mCity.cityRentMaxPrice != null) {
                    new CityService().getPropertyRangeInCity(mCity.id, mSelectedBedroomTypes, mSelectedPropertyTypes,
                            isRent, mCity.cityRentMinPrice.intValue(), mCity.cityRentMaxPrice.intValue(),
                            (mCity.cityRentMaxPrice.intValue() - mCity.cityRentMinPrice.intValue()) / 20);
                }
            } else {
                if(mCity.cityBuyMinPrice != null && mCity.cityBuyMaxPrice != null) {
                    new CityService().getPropertyRangeInCity(mCity.id, mSelectedBedroomTypes, mSelectedPropertyTypes,
                            isRent, mCity.cityBuyMinPrice.intValue(), mCity.cityBuyMaxPrice.intValue(),
                            (mCity.cityBuyMaxPrice.intValue() - mCity.cityBuyMinPrice.intValue()) / 20);
                }
            }
        }
    }

    private void initView() {
        isRent = false;
        mCompressedTextViewLayout.setVisibility(View.INVISIBLE);
        mPropertyTypeSpinner.setMessage(PREFIX_PROPERTY_SPINNER, null);
        mPropertyTypeSpinner.setItems(MasterDataCache.getInstance().getBuyPropertyTypes());
        mBhkSpinner.setMessage(null, POSTFIX_BHK_SPINNER);
        mBhkSpinner.setItems(MasterDataCache.getInstance().getBhkList());
    }

    @Subscribe
    public void onResults(CityByIdEvent cityByIdEvent){
        if(!isVisible()) {
            return;
        }
        if (null == cityByIdEvent || null != cityByIdEvent.error) {
            Toast.makeText(getActivity(), "city details could not be loaded at this time. please try later.", Toast.LENGTH_LONG).show();
            showNoResults();
        }
        else {
            mCity = cityByIdEvent.city;
            if(mCity!=null) {
                showContent();
                initUiUsingCityDetails();
            }
        }
    }

    @Subscribe
    public void onTopLocalityResults(CityTopLocalityEvent cityTopLocalityEvent){
        if(!isVisible()) {
            return;
        }
        mCityTopLocalities = cityTopLocalityEvent.topLocalitiesInCity;
        if(mCityTopLocalities != null && mCityTopLocalities.size()>0) {
            mTopLocalityLayout.setVisibility(View.VISIBLE);
            mTopLocalityLayout.bindView(mCityTopLocalities);
            mTopLocalityLayout.setShowAllPropertiesClickListener(mCityId, isRent);
            fetchPriceTrendData(mCityTopLocalities);
        }
    }

    @Subscribe
    public void onPriceTrendResults(CityTrendEvent cityTrendEvent){
        if(!isVisible()) {
            return;
        }
        mPropertyRangeLayout.setVisibility(View.VISIBLE);
        mBarChartView.bindView(cityTrendEvent.cityTrendData);
        mBarChartView.setListener(new OnBarTouchListener() {
            @Override
            public void onBarTouched(CityTrendData cityTrendData) {
                SerpRequest serpRequest = new SerpRequest(SerpActivity.TYPE_CITY);
                if (cityTrendData.minPrice != null) {
                    serpRequest.setMinBudget(cityTrendData.minPrice.longValue());
                }
                if (cityTrendData.maxPrice != null) {
                    serpRequest.setMaxBudget(cityTrendData.maxPrice.longValue());
                }
                for (int propertyType : mPropertyTypeSpinner.getSelectedIds()) {
                    serpRequest.setPropertyType(propertyType);
                }
                for (int bedroom : mBhkSpinner.getSelectedIds()) {
                    serpRequest.setBedrooms(bedroom);
                    if (bedroom == 4) {
                        for (int i = 5; i <= 10; i++) {
                            serpRequest.setBedrooms(i);
                        }
                    }
                }
                if (isRent) {
                    serpRequest.setSerpContext(SerpRequest.CONTEXT_RENT);
                } else {
                    serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);
                }
                serpRequest.setCityId(mCity.id);
                serpRequest.launchSerp(mContext);
            }
        });
    }

    private void fetchPriceTrendData(ArrayList<Locality> cityTopLocalities) {
        ArrayList<Long> localityIds = new ArrayList<>();
        for(Locality locality:cityTopLocalities){
            localityIds.add(locality.localityId);
        }
        final String minTime = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getDateMonthsBack(0));
        final String maxTime = new SimpleDateFormat("yyyy-MM-dd").format(DateUtil.getDateMonthsBack(37));
        new PriceTrendService().getPriceTrendForLocalities(localityIds, minTime, maxTime, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                if (localityPriceTrendDto.data != null && localityPriceTrendDto.data.size() != 0) {
                    mPriceTrendView.setVisibility(View.VISIBLE);
                    if (mCity!= null && mCity.id != 0) {
                        mPriceTrendView.setCityId(mCity.id);
                    }
                    mPriceTrendView.bindView(localityPriceTrendDto);
                    mLocalityPriceTrendDto = localityPriceTrendDto;
                    new PriceTrendService().getCityPriceTrendForCity(mCity.id, minTime, maxTime);
                } else
                    mPriceTrendView.setVisibility(View.GONE);
            }
        });
    }

    @Subscribe
    public void onCityPrice(CityPriceTrendEvent cityPriceTrendEvent){
        if(!isVisible()) {
            return;
        }
        if(cityPriceTrendEvent!=null && cityPriceTrendEvent.cityPriceTrendDto!=null && mLocalityPriceTrendDto.data!=null){
            Set<PriceTrendKey> priceTrendKeySet = cityPriceTrendEvent.cityPriceTrendDto.data.keySet();
            for (PriceTrendKey key : priceTrendKeySet) {
                key.label = mCity.label;
                mLocalityPriceTrendDto.data.put(key, cityPriceTrendEvent.cityPriceTrendDto.data.get(key));
            }
            if (mCity!= null && mCity.id != 0) {
                mPriceTrendView.setCityId(mCity.id);
            }
            mPriceTrendView.bindView(mLocalityPriceTrendDto);
        }
    }

    private void addLocalitiesLifestyleFragment(ArrayList<EntityDesc> entityDescriptions) {
        if(entityDescriptions != null && entityDescriptions.size()>0) {
            LocalityLifestyleFragment newFragment = new LocalityLifestyleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.know_more) + " " + mCity.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_lifestyle, newFragment, false);
            insertImagesinEntityDescriptions();
            newFragment.setData(entityDescriptions);
        }
    }

    private void insertImagesinEntityDescriptions() {
        HashMap<String,String> imagesHashMap = LocalityUtil.getImageHashMap(mCity.images);
        if(imagesHashMap!=null && mCity.entityDescriptions!=null) {
            for (EntityDesc entityDesc : mCity.entityDescriptions) {
                if(entityDesc.entityDescriptionCategories!=null && entityDesc.entityDescriptionCategories.masterDescriptionCategory!=null
                        && entityDesc.entityDescriptionCategories.masterDescriptionCategory.name!=null) {
                    entityDesc.imageUrl = imagesHashMap.get(entityDesc.entityDescriptionCategories.masterDescriptionCategory.name.toLowerCase());
                }
            }
        }
    }

    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        if(taxonomyCardList != null && taxonomyCardList.size()>0) {
            LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", getResources().getString(R.string.properties_in) + " " + mCity.label);
            newFragment.setArguments(bundle);
            initFragment(R.id.container_nearby_localities_props, newFragment, false);
            newFragment.setData(taxonomyCardList);
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

    @OnClick(R.id.pyr_button_bottom)
    public void onBottomPyrClick(){
        /*-------------------track------------------event-------------------*/
        Properties properties = MakaanEventPayload.beginBatch();
        if(mCity!=null && mCity.id!=null) {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, mCity.id));
        }else {
            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY, ""));
        }
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.city);
        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.pyrFormOpen);
        /*-------------------------------------------------------------------*/

        Intent pyrIntent = new Intent(getActivity(), PyrPageActivity.class);
        if(mCity!=null && mCity.label!=null) {
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_NAME, mCity.label);
            pyrIntent.putExtra(PyrPageActivity.KEY_CITY_Id, mCity.id);
            pyrIntent.putExtra(PyrPageActivity.SOURCE_SCREEN_NAME, ((BaseJarvisActivity) getActivity()).getScreenName());

        }
        getActivity().startActivity(pyrIntent);
    }

    @Override
    public void bindView(OverviewActivityCallbacks activityCallbacks) {

    }
}
