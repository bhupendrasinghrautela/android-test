package com.makaan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.MakaanBuyerApplication;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;
import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/9/16.
 */
public abstract class MakaanBaseFragment extends Fragment {

    protected abstract int getContentViewId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), container, false);
        // bind view to ButterKnife
        ButterKnife.bind(this, view);
        // register for event bus callbacks
//        try {
            AppBus.getInstance().register(this);
        /*} catch(IllegalArgumentException ex) {
            AppBus.getInstance().unregister(this);
            AppBus.getInstance().register(this);
        }*/

        if (!AppUtils.haveNetworkConnection(getActivity())) {
            showNoNetworkFound();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppBus.getInstance().unregister(this);

    }

    private void showNoNetworkFound() {
        //TODO: implement
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*RefWatcher refWatcher = MakaanBuyerApplication.getRefWatcher(getActivity());
        if(refWatcher != null) {
            refWatcher.watch(this);
        }*/
    }
}
