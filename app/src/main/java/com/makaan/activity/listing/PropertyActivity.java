package com.makaan.activity.listing;

import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.event.amenity.AmenityGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.service.AmenityService;
import com.makaan.service.ImageService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.amenity.AmenityViewPager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by sunil on 17/01/16.
 */
public class PropertyActivity extends MakaanBaseSearchActivity {



    private PropertyDetailFragment mPropertyDeatilFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.property_activity_layout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPropertyDeatilFragment = new PropertyDetailFragment();
        initFragment(R.id.container, mPropertyDeatilFragment, true);

        fetchProjectDetail();

    }

    private void fetchProjectDetail(){
        //Intent intent = getIntent();
        //long listingId = intent.getExtras().getLong("listingId");
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(436057L);
        ((ImageService) (MakaanServiceFactory.getInstance().getService(ImageService.class))).getListingImages(436057L);
        ((AmenityService) (MakaanServiceFactory.getInstance().getService(AmenityService.class))).getAmenitiesByLocation(13.03244019, 77.6019516, 3);
    }

    @Override
    public boolean isJarvisSupported() {
        return true;
    }
}
