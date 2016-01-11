package com.makaan.activity.pyr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.makaan.R;
import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.fragment.pyr.PyrReplaceFragment;

/**
 * Created by proptiger on 6/1/16.
 */
public class PyrPageActivity extends AppCompatActivity implements PyrReplaceFragment{

    private FragmentTransaction mFragmentTransaction;
    private PyrPagePresenter mPagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pyr_activity_layout);
        mPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        mPagePresenter.setReplaceFragment(this);
        mPagePresenter.showPyrMainPageFragment();
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean shouldAddToBackStack) {
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.pyr_fragment_holder, fragment, fragment.getClass().getName());
        if(shouldAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        mFragmentTransaction.commit();
    }

    @Override
    public void showPropertySearchFragment(FilterableMultichoiceDialogFragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragment.show(fragmentManager, "PropertyList");
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