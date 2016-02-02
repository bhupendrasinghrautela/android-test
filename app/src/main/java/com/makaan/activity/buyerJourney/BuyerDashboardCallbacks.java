package com.makaan.activity.buyerJourney;

import android.os.Bundle;

/**
 * Created by rohitgarg on 1/29/16.
 */
public interface BuyerDashboardCallbacks {
    void loadFragment(int type, boolean shouldAddToBackStack, Bundle data);
}
