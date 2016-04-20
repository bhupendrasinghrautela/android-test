package com.makaan.service.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.makaan.R;

import org.json.JSONObject;


/**
 * Created by sunil on 29/12/15.
 */
public class FacebookTokenInteractor {

    private Context mContext;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;
    private OnFacebookTokenListener mOnFacebookTokenListener;

    public FacebookTokenInteractor(Context context, OnFacebookTokenListener onFacebookTokenListener){
        mContext = context;
        mOnFacebookTokenListener=onFacebookTokenListener;
    }

    public void initFacebookSdk(Bundle savedInstanceState){
        try {
            if (mContext != null) {
                AppEventsLogger.activateApp(mContext, mContext.getResources().getString(R.string.app_FB_id));
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        mCallbackManager = CallbackManager.Factory.create();

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                //TODO this thing should be automatically handeled
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };

        mAccessTokenTracker.startTracking();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        if(null == mOnFacebookTokenListener) {
                                            return;
                                        }

                                        if (object != null) {
                                            try {
                                                mOnFacebookTokenListener.onFacebookTokenSuccess(loginResult.getAccessToken().getToken());
                                                AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                                            } catch (Exception e) {
                                                mOnFacebookTokenListener.onFacebookTokenFail(e);
                                            }
                                            //mUserId=object.optString("email");
                                        } else {
                                            mOnFacebookTokenListener.onFacebookTokenFail(new RuntimeException());
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email,first_name,last_name,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        if(null != mOnFacebookTokenListener) {
                            mOnFacebookTokenListener.onFacebookCancel();
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if(null!=mOnFacebookTokenListener) {
                            mOnFacebookTokenListener.onFacebookTokenFail(exception);
                        }
                    }
                });
    }

    public void stopTracking(){
        if(null!=mAccessTokenTracker){
            mAccessTokenTracker.stopTracking();
        }
    }

    public void onFacebookActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
