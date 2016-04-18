package com.makaan.fragment.overview;

import com.makaan.fragment.MakaanBaseFragment;

/**
 * Created by rohitgarg on 3/21/16.
 */
public abstract class OverviewFragment extends MakaanBaseFragment {
    public abstract void bindView(OverviewActivityCallbacks activity);

    public interface OverviewActivityCallbacks {
        void showMapFragment();
        void showMapFragmentWithSpecificAmenity(int displayId,String placeName);
        void imagesSeenCount(int count);
    }
}
