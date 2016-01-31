package com.makaan.ui.project;

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
import com.makaan.fragment.project.ProjectSpecificationPagerFragment;
import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.ui.view.WrapContentViewPager;
import com.makaan.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectSpecificationView extends LinearLayout{
    @Bind(R.id.project_specification_tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.project_specification_view_pager)
    WrapContentViewPager viewPager;
    private Context mContext;
    private HashMap<String, ArrayList<SpecificaitonsUI>> map;

    public ProjectSpecificationView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProjectSpecificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ProjectSpecificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }


    public void bindView(HashMap<String, ArrayList<SpecificaitonsUI>> map, FragmentActivity compatActivity) {
        this.map = map;
        initView(compatActivity);
    }

    private void initView(FragmentActivity compatActivity){
        setupViewPager(viewPager,compatActivity);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(final WrapContentViewPager viewPager, FragmentActivity compatActivity) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(compatActivity.getSupportFragmentManager());
        int max = 0;
        for(String tab:map.keySet()) {
            ArrayList<SpecificaitonsUI> specificaitonsUIs = map.get(tab);
            if(specificaitonsUIs.size()>max)
                max = specificaitonsUIs.size();

            ProjectSpecificationPagerFragment fragment = new ProjectSpecificationPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("specs", specificaitonsUIs);
            fragment.setArguments(bundle);
            adapter.addFrag(fragment, tab);
        }
        viewPager.setHeight(max > 4 && max > 0 ? CommonUtil.dpToPixel(mContext,4 * 105): CommonUtil.dpToPixel(mContext,max * 105));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.reMeasureCurrentPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
