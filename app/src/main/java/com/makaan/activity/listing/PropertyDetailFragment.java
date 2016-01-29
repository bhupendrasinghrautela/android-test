package com.makaan.activity.listing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.city.CityActivity;
import com.makaan.activity.locality.LocalityActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.locality.LocalityPropertiesFragment;
import com.makaan.pojo.TaxonomyCard;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Project;
import com.makaan.response.property.Property;
import com.makaan.service.ImageService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.amenity.AmenityViewPager;
import com.makaan.ui.property.AboutBuilderExpandedLayout;
import com.makaan.ui.property.AmenitiesViewScroll;
import com.makaan.ui.property.ListingDataOverViewScroll;
import com.makaan.ui.property.PropertyImageViewPager;
import com.makaan.util.KeyUtil;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by sunil on 18/01/16.
 */
public class PropertyDetailFragment extends MakaanBaseFragment {

    @Bind(R.id.amenity_viewpager)
    AmenityViewPager mAmenityViewPager;

    @Bind(R.id.property_image_viewpager)
    PropertyImageViewPager mPropertyImageViewPager;

    @Bind(R.id.unit_name)
    TextView mUnitName;

    @Bind(R.id.about_builder_layout)
    AboutBuilderExpandedLayout mABoutBuilderLayout;

    @Bind(R.id.about_locality)
    TextView mAboutLocality;

    @Bind(R.id.listing_over_view_scroll_layout)
    ListingDataOverViewScroll mListingDataOverViewScroll;

    @Bind(R.id.amenities_scroll_layout)
    AmenitiesViewScroll mAmenitiesViewScroll;

    @Bind(R.id.locality_brief)
    TextView mLocalityBrief;

    @Bind(R.id.locality_score_progress)
    ProgressBar mLocalityScoreProgress;

    @Bind(R.id.locality_score_text)
    TextView mLocalityScoreText;

    @OnClick(R.id.more_about_locality)
    public void openLocality(){
        Intent intent = new Intent(getActivity(), LocalityActivity.class);
        intent.putExtra(KeyUtil.LOCALITY_ID,mListingDetail.property.project.localityId);
        startActivity(intent);
    }

    @Bind(R.id.content_text)
    TextView mListingBrief;

    private ListingDetail mListingDetail;
    private Long listingId;
    private Context mContext;


    @Override
    protected int getContentViewId() {
        return R.layout.property_detail_frgament;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        listingId = args.getLong(KeyUtil.LISTING_ID);

    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        mAmenityViewPager.bindView();
        mAmenityViewPager.setData(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        mListingDetail = listingByIdGetEvent.listingDetail;
        TestUi(mListingDetail);
        ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(listingId);
    }

    @Subscribe
    public void onResults(ImagesGetEvent imagesGetEvent){
        mPropertyImageViewPager.bindView();
        mPropertyImageViewPager.setData(imagesGetEvent.images, mListingDetail.currentListingPrice.price);
    }

    private void TestUi(ListingDetail listingDetail){
        mListingDataOverViewScroll.bindView(listingDetail);
        mAmenitiesViewScroll.bindView(listingDetail.listingCategory, listingDetail.unitType, listingDetail.listingAmenities);
        if(listingDetail.property != null) {
            Property property = listingDetail.property;

            mUnitName.setText(listingDetail.property.bedrooms + "bhk " +
                    (property.unitType != null ? property.unitType : "") + " - " +
                    (property.size != null ? property.size : "") + " " +
                    (property.measure != null ? property.measure : ""));
            if(property.project != null) {
                Project project = property.project;

                mABoutBuilderLayout.bindView(listingDetail.property.project.builder);

                if(project.locality != null) {
                    Locality locality = project.locality;
                    mAboutLocality.setText("about ".concat((locality.label != null ? locality.label : "")));
                    mLocalityBrief.setText(Html.fromHtml((locality.description != null ? locality.description : "")));
                    if(locality.livabilityScore != null) {
                        mLocalityScoreProgress.setProgress((int) (locality.livabilityScore * 10));
                        mLocalityScoreText.setText(String.valueOf(locality.livabilityScore));
                    } else {
                        mLocalityScoreProgress.setProgress(0);
                        mLocalityScoreText.setText("NA");
                    }
                }
            }
            mListingBrief.setText((listingDetail.description != null ? Html.fromHtml(listingDetail.description) : ""));
        }
        mListingBrief.setTextColor(getResources().getColor(R.color.listingBlack));
        MasterDataCache.getInstance().getDisplayOrder(listingDetail.listingCategory, listingDetail.property.unitType, "overview");
    }

    //TODO add smiliar properties instead of this
    private void addProperties(List<TaxonomyCard> taxonomyCardList) {
        LocalityPropertiesFragment newFragment = new LocalityPropertiesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", getResources().getString(R.string.properties_in));
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
