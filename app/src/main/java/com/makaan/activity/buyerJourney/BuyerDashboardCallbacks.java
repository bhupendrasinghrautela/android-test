package com.makaan.activity.buyerJourney;

import android.os.Bundle;

import com.makaan.fragment.buyerJourney.ClientLeadsFragment;

/**
 * Created by rohitgarg on 1/29/16.
 */
public interface BuyerDashboardCallbacks {
    void loadFragment(int type, boolean shouldAddToBackStack, Bundle data, String title, Object object);
}
