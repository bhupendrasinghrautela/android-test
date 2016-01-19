package com.makaan.activity.pyr;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;

import butterknife.ButterKnife;

/**
 * Created by makaanuser on 7/1/16.
 */
public class PyrOtpVerification extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
