package com.makaan.activity.buyerJourney;

import android.os.Bundle;

import com.makaan.R;
import com.makaan.activity.MakaanBaseSearchActivity;
import com.makaan.fragment.buyerJourney.SaveSearchFragment;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SaveSearchService;

/**
 * Created by aishwarya on 29/01/16.
 */
public class SaveSearchActivity extends MakaanBaseSearchActivity {
    private SaveSearchFragment mSaveSearchFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_save_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SaveSearchService) (MakaanServiceFactory.getInstance().getService(SaveSearchService.class))).getSavedSearches();
        mSaveSearchFragment = new SaveSearchFragment();
        initFragment(R.id.container, mSaveSearchFragment, false);
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }
}
