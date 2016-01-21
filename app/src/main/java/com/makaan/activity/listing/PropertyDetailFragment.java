package com.makaan.activity.listing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.listing.ListingImagesGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.ui.amenity.AmenityViewPager;
import com.makaan.ui.property.PropertyImageViewPager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

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

    @Bind(R.id.project_name)
    TextView mProjectName;

    @Bind(R.id.locality)
    TextView mLocalityName;

    @Bind(R.id.about_locality)
    TextView mAboutLocality;

    @Bind(R.id.locality_brief)
    TextView mLocalityBrief;

    @Bind(R.id.listing_brief)
    TextView mListingBrief;


    @Override
    protected int getContentViewId() {
        return R.layout.property_deatil_frgament;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Subscribe
    public void onResults(AmenityGetEvent amenityGetEvent) {
        mAmenityViewPager.bindView();
        mAmenityViewPager.setData(amenityGetEvent.amenityClusters);
    }

    @Subscribe
    public void onResults(ListingByIdGetEvent listingByIdGetEvent) {
        ListingDetail detail = listingByIdGetEvent.listingDetail;
        TestUi(detail);
    }

    @Subscribe
    public void onResults(ListingImagesGetEvent listingImagesGetEvent){
        mPropertyImageViewPager.bindView();
        mPropertyImageViewPager.setData(listingImagesGetEvent.listingDetailImages);
    }

    private void TestUi(ListingDetail listingDetail){
        mUnitName.setText(listingDetail.property.bedrooms+"bhk "+listingDetail.property.unitType + " - "+listingDetail.property.size+ " "+listingDetail.property.measure);
        mProjectName.setText(listingDetail.property.project.getFullName());
        mLocalityName.setText(listingDetail.property.project.locality.label);
        mAboutLocality.setText("about ".concat(listingDetail.property.project.locality.label));
        mLocalityBrief.setText(listingDetail.property.project.locality.description);
        mListingBrief.setText(listingDetail.description);
        MasterDataCache.getInstance().getDisplayOrder(listingDetail.listingCategory,listingDetail.property.unitType,"overview");
    }
}
