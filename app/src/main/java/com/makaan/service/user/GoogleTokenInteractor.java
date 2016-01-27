package com.makaan.service.user;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthUtil;

import java.io.IOException;

/**
 * Created by sunil on 30/12/15.
 */
public class GoogleTokenInteractor implements AccountManagerCallback<Bundle> {

    private OnGoogleTokenListener mOnGoogleTokenListener;

    private static final String GOOGLE_OAUTH_TYPE =
            "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";

    private Activity mActivity;

    public GoogleTokenInteractor(Activity activity, OnGoogleTokenListener listener){
        mActivity = activity;
        mOnGoogleTokenListener = listener;
    }

    public void requestGoogleAccessToken(String accountName){
        AccountManager am = AccountManager.get(mActivity);
        Account selectedAccount = null;
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);

        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equalsIgnoreCase(accountName)) {
                selectedAccount = accounts[i];
                break;
            }
        }

        if (accounts.length > 0) {
            am.getAuthToken(selectedAccount, GOOGLE_OAUTH_TYPE, new Bundle(), mActivity, this, null);
        } else {
            mOnGoogleTokenListener.onGoogleTokenFail();
        }
    }

    @Override
    public void run(AccountManagerFuture<Bundle> result) {

        if (result == null) {
            mOnGoogleTokenListener.onGoogleTokenFail();
            return;
        }

        // Get the result of the operation from the AccountManagerFuture.
        Bundle bundle = null;
        try {
            bundle = result.getResult();
        } catch (OperationCanceledException e) {
            mOnGoogleTokenListener.onGoogleTokenFail();
            return;
        } catch (AuthenticatorException e) {
            mOnGoogleTokenListener.onGoogleTokenFail();
            return;
        } catch (IOException e) {
            mOnGoogleTokenListener.onGoogleTokenFail();
            return;
        }

        if (bundle == null) {
            mOnGoogleTokenListener.onGoogleTokenFail();
            return;
        }

        // The token is a named value in the bundle. The name of the
        // value is stored in the constant AccountManager.KEY_AUTHTOKEN.
        final String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        mOnGoogleTokenListener.onGoogleTokenSuccess(token);
    }
}
