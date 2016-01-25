package com.makaan.fragment.pyr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 24/1/16.
 */
public class NoSellersFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_sellers_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.set_alert)
    public void setAlert(){
        PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        pyrPagePresenter.showThankYouScreenFragment(true, false, true);
    }

}

