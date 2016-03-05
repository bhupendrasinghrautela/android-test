package com.makaan.notification;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by sunil on 29/02/16.
 */
public class MakaanBuyerInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MakaanInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        GcmRegister.checkAndSetGcmId(getApplicationContext(), null);
    }

}
