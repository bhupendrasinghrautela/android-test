package com.makaan.service.user;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;


/**
 * Created by sunil on 29/12/15.
 */
public class FacebookTokenInteractor {

    private Context mContext;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    public FacebookTokenInteractor(Context context){
        mContext = context;
    }

    private void initFacbookSdk(Bundle savedInstanceState){
        try {
            if (mContext != null) {
                //TODO use a valid fb id
                AppEventsLogger.activateApp(mContext, "");
            }
        } catch (Exception e) {}

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

                            GraphRequestAsyncTask request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            if (object != null) {
                                                try {
                                                    //TODO
                                                    AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                                                } catch (Exception e) {
                                                    //TODO
                                                }
                                            } else {
                                                //TODO
                                            }
                                        }
                                    }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        //TODO

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //TODO
                    }
                });
    }
}
