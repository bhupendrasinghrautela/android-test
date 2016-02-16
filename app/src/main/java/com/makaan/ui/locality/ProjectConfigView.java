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
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.project.OnRentBuyClicked;
import com.makaan.event.project.OnViewAllPropertiesClicked;
import com.makaan.event.project.ProjectConfigEvent;
import com.makaan.fragment.project.ProjectConfigFragment;
import com.makaan.pojo.ProjectConfigItem;
import com.makaan.util.AppBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectConfigView extends LinearLayout implements ViewPager.OnPageChangeListener {
    @Bind(R.id.project_config_tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.project_config_view_pager)
    ViewPager viewPager;
    private Context mContext;
    @Bind(R.id.project_specification_view_all_props)
    TextView viewAllPropsTv;
    private ProjectConfigEvent projectConfigEvent;
    private int currentPage;

    public ProjectConfigView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectConfigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @OnClick(R.id.project_specification_view_all_props)
    public void viewAllPropertiesClicked(){
        if(currentPage == 0){//buy
            AppBus.getInstance().post(new OnViewAllPropertiesClicked(false));
        }else{//rent
            AppBus.getInstance().post(new OnViewAllPropertiesClicked(true));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }


    public void bindView(ProjectConfigEvent projectConfigEvent, FragmentActivity compatActivity) {
        if(projectConfigEvent.buyProjectConfigItems.size()==0 && projectConfigEvent.rentProjectConfigItems.size()==0){
            return;
        }
        else {
            this.setVisibility(VISIBLE);
            this.projectConfigEvent = projectConfigEvent;
            initView(compatActivity);
        }
    }

    private void initView(FragmentActivity compatActivity){
        setupViewPager(viewPager, compatActivity);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final ViewPager viewPager, FragmentActivity compatActivity) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(compatActivity.getSupportFragmentManager());
            viewPager.addOnPageChangeListener(this);
            if(projectConfigEvent.buyProjectConfigItems !=null && projectConfigEvent.buyProjectConfigItems.size()>0) {
                //buy
                ArrayList<ProjectConfigItem> projectConfigItems = projectConfigEvent.buyProjectConfigItems;
                ProjectConfigFragment fragment = new ProjectConfigFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("configs", projectConfigItems);
                bundle.putBoolean("isRent", false);
                fragment.setArguments(bundle);
                adapter.addFrag(fragment, "buy");
            }

            if(projectConfigEvent.rentProjectConfigItems !=null && projectConfigEvent.rentProjectConfigItems.size()>0) {
                //rent
                ArrayList<ProjectConfigItem> projectConfigItemsrent = projectConfigEvent.rentProjectConfigItems;
                ProjectConfigFragment fragmentrent = new ProjectConfigFragment();
                Bundle bundlerent = new Bundle();
                bundlerent.putParcelableArrayList("configs", projectConfigItemsrent);
                bundlerent.putBoolean("isRent", true);
                fragmentrent.setArguments(bundlerent);
                adapter.addFrag(fragmentrent, "rent");
            }

            viewPager.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPage = position;
        if(currentPage == 0){//buy
            AppBus.getInstance().post(new OnRentBuyClicked(false));
        }else{//rent
            AppBus.getInstance().post(new OnRentBuyClicked(true));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
