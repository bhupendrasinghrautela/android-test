package com.makaan.activity.Lead;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.makaan.R;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.fragment.pyr.PyrReplaceFragment;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadFormActivity extends MakaanFragmentActivity implements LeadFormReplaceFragment {
    private FragmentTransaction mFragmentTransaction;
    private LeadFormPresenter mLeadFormPresenter;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_lead_form;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeadFormPresenter = LeadFormPresenter.getLeadFormPresenter();
        mLeadFormPresenter.setReplaceFragment(this);
        mLeadFormPresenter.showLeadCallNowFragment();

    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.leadform_fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            super.onBackPressed();
        }
    }

}