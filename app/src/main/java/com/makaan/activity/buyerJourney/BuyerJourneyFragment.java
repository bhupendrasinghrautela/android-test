package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;

import butterknife.ButterKnife;


public class BuyerJourneyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_journey, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
