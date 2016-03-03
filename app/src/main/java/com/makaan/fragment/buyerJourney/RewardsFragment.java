package com.makaan.fragment.buyerJourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.cookie.CookiePreferences;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

public class RewardsFragment extends MakaanBaseFragment {
    private static final int LOGIN_REQUEST = 1001;
    @Bind(R.id.fragment_get_rewards_button)
    Button mGetRewardsButton;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_get_rewards;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupUserData();
        return view;
    }

    private void setupUserData() {
        if (CookiePreferences.isUserLoggedIn(getActivity())) {
            mGetRewardsButton.setText(getString(R.string.redeem_now));
        } else {
            mGetRewardsButton.setText("login to continue");
        }
    }

    @OnClick(R.id.fragment_get_rewards_button)
    void onGetRewardsClicked(View view) {
        if(CookiePreferences.isUserLoggedIn(getActivity())) {
            if (getActivity() instanceof BuyerDashboardCallbacks) {
                ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CLIENT_LEADS, true, null, null, null);
            }
        } else {
            Intent intent = new Intent(getActivity(), UserLoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOGIN_REQUEST && resultCode == Activity.RESULT_OK){
            setupUserData();
        }
    }
}
