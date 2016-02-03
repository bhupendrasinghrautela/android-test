package com.makaan.activity.lead;

/**
 * Created by proptiger on 7/1/16.
 */
public class LeadFormPresenter {
    private static LeadFormPresenter mLeadFormPresenter;
    private LeadFormReplaceFragment mLeadFormReplaceFragment;
    private LeadCallNowFragment mLeadCallNowFragment;
    private LeadInstantCallBackFragment mLeadInstantCallBackFragment;
    private LeadLaterCallBackFragment mLeadLaterCallBackFragment;

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

}
