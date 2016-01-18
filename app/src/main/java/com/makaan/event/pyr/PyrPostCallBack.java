package com.makaan.event.pyr;

import android.util.Log;

import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.StringRequestCallback;

import org.json.JSONObject;

/**
 * Created by makaanuser on 9/1/16.
 */
public class PyrPostCallBack extends StringRequestCallback {

    @Override
    public void onSuccess(String response) {

        Log.e("Success pyr",response);

        PyrPagePresenter mPyrPagePresenter = new PyrPagePresenter();
        mPyrPagePresenter.showPyrOtpFragment();

    }
}
