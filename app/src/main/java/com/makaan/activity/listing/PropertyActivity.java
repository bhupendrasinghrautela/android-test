package com.makaan.activity.listing;

import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.fragment.neighborhood.NeighborhoodMapFragment;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.AmenityService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.squareup.otto.Subscribe;

/**
 * Created by sunil on 17/01/16.
 */
public class PropertyActivity extends MakaanBaseSearchActivity {



    private PropertyDetailFragment mPropertyDeatilFragment;
    private NeighborhoodMapFragment mNeighborhoodMapFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.property_activity_layout;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mPropertyDeatilFragment = new PropertyDetailFragment();
        //initFragment(R.id.container, mPropertyDeatilFragment, true);

        mNeighborhoodMapFragment = new NeighborhoodMapFragment();
        initFragment(R.id.container, mNeighborhoodMapFragment, false);

        fetchProjectDetail();
        initUi(true);

    }

    private void fetchProjectDetail(){
        //Intent intent = getIntent();
        //long listingId = intent.getExtras().getLong("listingId");
        ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(323996L);
        ((AmenityService) (MakaanServiceFactory.getInstance().getService(AmenityService.class))).getAmenitiesByLocation(13.03244019, 77.6019516, 3);
    }



    @Override
    public boolean isJarvisSupported() {
        return true;
    }

    @Subscribe
    public void onResults(SearchResultEvent searchResultEvent) {
        super.onResults(searchResultEvent);
    }
}
