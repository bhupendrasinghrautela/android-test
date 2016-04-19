package com.makaan.event.user;

import com.crashlytics.android.Crashlytics;
import com.makaan.cache.MasterDataCache;
import com.makaan.network.StringRequestCallback;
import com.makaan.response.ResponseError;
import com.makaan.util.AppBus;

/**
 * Created by Sandeep on 19/02/16.
 */
public class UserLogoutCallback extends StringRequestCallback {

    @Override
    public void onSuccess(String response) {
        UserLogoutEvent mLogoutEvent =new UserLogoutEvent();
        try {
            MasterDataCache.getInstance().setUserData(null);
            mLogoutEvent.setIsLogoutSuccessfull(true);
        } catch (Exception e) {
            Crashlytics.logException(e);
            //TODO Show error message
        }
        AppBus.getInstance().post(mLogoutEvent);
    }

    @Override
    public void onError(ResponseError error) {
        UserLogoutEvent mLogoutEvent =new UserLogoutEvent();

        mLogoutEvent.setIsLogoutSuccessfull(false);
        AppBus.getInstance().post(mLogoutEvent);
    }

}
