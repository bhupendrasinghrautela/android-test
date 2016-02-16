package com.makaan.activity.lead;

import android.os.Bundle;

import com.makaan.fragment.pyr.ThankYouScreenFragment;

/**
 * Created by proptiger on 7/1/16.
 */
public class LeadFormPresenter {
    private static LeadFormPresenter mLeadFormPresenter;
    private LeadFormReplaceFragment mLeadFormReplaceFragment;
    private LeadCallNowFragment mLeadCallNowFragment;
    private LeadInstantCallBackFragment mLeadInstantCallBackFragment;
    private LeadLaterCallBackFragment mLeadLaterCallBackFragment;
    private ThankYouScreenFragment mThankYouFragment;
    public static String MAKAAN_ASSIST_VALUE = "makaan_assist";
    public static String NO_SELLERS_FRAGMENT = "no_seller";

    String name;
    String id;
    String phone;
    String score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public LeadFormPresenter() {

    }

    public static LeadFormPresenter getLeadFormPresenter() {
        if (mLeadFormPresenter == null) {
            mLeadFormPresenter = new LeadFormPresenter();
        }
        return mLeadFormPresenter;
    }

    public void setReplaceFragment(LeadFormReplaceFragment replaceFragment) {
        mLeadFormReplaceFragment = replaceFragment;
    }


    public void showLeadCallNowFragment() {
        mLeadCallNowFragment = new LeadCallNowFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadCallNowFragment, true);
    }

    public void showLeadInstantCallBackFragment() {
        mLeadInstantCallBackFragment = new LeadInstantCallBackFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadInstantCallBackFragment, true);
    }

    public void showLeadLaterCallBAckFragment() {
        mLeadLaterCallBackFragment = new LeadLaterCallBackFragment();
        mLeadFormReplaceFragment.replaceFragment(mLeadLaterCallBackFragment, true);
    }

    public void showThankYouScreenFragment(boolean makaanAssist, boolean throughNoSellers) {
        mThankYouFragment = new ThankYouScreenFragment();
        Bundle bundle=new Bundle();
        bundle.putBoolean(MAKAAN_ASSIST_VALUE, makaanAssist);
        bundle.putBoolean(NO_SELLERS_FRAGMENT, throughNoSellers);
        mThankYouFragment.setArguments(bundle);
        mLeadFormReplaceFragment.popFromBackstack(2);
        mLeadFormReplaceFragment.replaceFragment(mThankYouFragment, false);
    }

}
