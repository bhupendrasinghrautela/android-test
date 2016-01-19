package com.makaan.activity.city;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.makaan.R;
import com.makaan.event.city.CityByIdEvent;
import com.makaan.event.city.CityTopLocalityEvent;
import com.makaan.event.trend.callback.LocalityTrendCallback;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.city.City;
import com.makaan.response.locality.Locality;
import com.makaan.response.trend.LocalityPriceTrendDto;
import com.makaan.service.PriceTrendService;
import com.makaan.ui.MakaanLineChartView;
import com.makaan.ui.PriceTrendView;
import com.makaan.ui.city.TopLocalityView;
import com.makaan.util.Blur;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by aishwarya on 01/01/16.
 */
public class CityOverViewFragment extends MakaanBaseFragment{

    @Bind(R.id.line_chart_layout)MakaanLineChartView lineChartLayout;
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
    @Bind(R.id.city_connect_header)
    TextView mCityConnectHeader;
    @Bind(R.id.price_trend_view)
    PriceTrendView mPriceTrendView;

    private static final int BLUR_EFFECT_HEIGHT = 300;
    private float alpha;
    private Context mContext;
    private City mCity;
    private ArrayList<Locality> mCityTopLocalities;

    @Override
    protected int getContentViewId() {
        return R.layout.city_overview_layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initListeners();
    }

    private void initUiUsingCityDetails() {
        mMainCityImage.setImageUrl(mCity.cityHeroshotImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
        mCityCollapseToolbar.setTitle(mCity.label);
        mCityConnectHeader.setText(getString(R.string.city_connect_header_text) + " " + mCity.label);
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
        mCityTagLine.setText(mCity.cityTagLine);
        mAnnualGrowth.setText(StringUtil.getTwoDecimalPlaces(mCity.annualGrowth)+"%");
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

    private void fetchPriceTrendData(ArrayList<Locality> cityTopLocalities) {
        ArrayList<Long> localityIds = new ArrayList<>();
        for(Locality locality:cityTopLocalities){
            localityIds.add(locality.localityId);
        }
        new PriceTrendService().getPriceTrendForLocalities(localityIds,60, new LocalityTrendCallback() {
            @Override
            public void onTrendReceived(LocalityPriceTrendDto localityPriceTrendDto) {
                mPriceTrendView.bindView(localityPriceTrendDto);
            }
        });
    }
}
