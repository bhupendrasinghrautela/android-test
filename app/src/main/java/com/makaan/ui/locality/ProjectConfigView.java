package com.makaan.ui.locality;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.fragment.project.ProjectConfigFragment;
import com.makaan.fragment.project.ProjectSpecificationPagerFragment;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.ui.view.WrapContentViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectConfigView extends LinearLayout{
    @Bind(R.id.project_config_tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.project_config_view_pager)
    ViewPager viewPager;
    private Context mContext;
    private ProjectConfigEvent projectConfigEvent;

    public ProjectConfigView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }


    public void bindView(ProjectConfigEvent projectConfigEvent, FragmentActivity compatActivity) {
        this.projectConfigEvent = projectConfigEvent;
        initView(compatActivity);
    }

    private void initView(FragmentActivity compatActivity){
        setupViewPager(viewPager, compatActivity);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager, FragmentActivity compatActivity) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(compatActivity.getSupportFragmentManager());

            //buy
            ArrayList<ProjectConfigItem> projectConfigItems = projectConfigEvent.buyProjectConfigItems;
            ProjectConfigFragment fragment = new ProjectConfigFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("configs", projectConfigItems);
            fragment.setArguments(bundle);
            adapter.addFrag(fragment, "buy");

            //rent
            ArrayList<ProjectConfigItem> projectConfigItemsrent = projectConfigEvent.rentProjectConfigItems;
            ProjectConfigFragment fragmentrent = new ProjectConfigFragment();
            Bundle bundlerent = new Bundle();
            bundlerent.putParcelableArrayList("configs", projectConfigItemsrent);
            fragmentrent.setArguments(bundlerent);
            adapter.addFrag(fragmentrent, "rent");

            viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
