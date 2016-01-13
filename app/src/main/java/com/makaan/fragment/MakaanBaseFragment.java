package com.makaan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;

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
        AppBus.getInstance().register(this);

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
}
