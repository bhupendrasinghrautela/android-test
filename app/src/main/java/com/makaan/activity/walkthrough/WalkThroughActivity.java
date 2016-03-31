package com.makaan.activity.walkthrough;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.HomeActivity;
import com.makaan.activity.MakaanFragmentActivity;
import com.makaan.util.CommonPreference;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aishwarya on 22/03/16.
 */
public class WalkThroughActivity extends MakaanFragmentActivity {
    @Bind(R.id.walkthrough_pager)
    ViewPager mWalkThroughPager;
    Context mContext;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_walkthrough;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if(CommonPreference.isWalkThroughSeen(this)){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        mWalkThroughPager.setAdapter(new WalkThroughAdapter());
        mWalkThroughPager.setOffscreenPageLimit(2);
        mWalkThroughPager.setPageTransformer(true,new WalkThroughTransformer());
    }

    @Override
    public boolean isJarvisSupported() {
        return false;
    }

    @Override
    public String getScreenName() {
        return null;
    }

    @OnClick(R.id.skip_tv)
    void onClick(){
        CommonPreference.setWalkThroughSeen(this);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private class WalkThroughAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View)object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater mLayoutInflater =
                    (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view;
            if(position == 0) {
                view =  mLayoutInflater.inflate(R.layout.view_1_walkthrough,null);
            }
            else if(position == 1) {
                view =  mLayoutInflater.inflate(R.layout.view_2_walkthrough,null);
            }
            else if(position == 2){
                view =  mLayoutInflater.inflate(R.layout.view_3_walkthrough,null);
            }
            else{
                view =  mLayoutInflater.inflate(R.layout.view_4_walkthrough,null);
            }
            view.setTag(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
