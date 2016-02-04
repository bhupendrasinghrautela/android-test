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

}
