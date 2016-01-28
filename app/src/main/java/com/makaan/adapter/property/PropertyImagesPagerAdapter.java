package com.makaan.adapter.property;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.response.image.Image;
import com.makaan.ui.property.PropertyImageCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aishwarya on 19/01/16.
 */
public class PropertyImagesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Image> mItems = new ArrayList<>();
    private int mCount = 0;
    private ViewPager pager;
    private Double price;
    private PropertyImageCardView mCurrentPropertyImageCardView;

    public PropertyImagesPagerAdapter(final ViewPager pager,Context context) {
        this.mContext = context;
        this.pager = pager;
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    pager.setCurrentItem(mCount - 2, false);
                } else if (position == mCount - 1) {
                    pager.setCurrentItem(1, false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setData(List<Image> list,Double price){
        if(list == null || list.isEmpty()){
            return;
        }
        mItems.clear();
        this.price = price;
        mItems.add(list.get(list.size() - 1));
        mItems.addAll(list);
        mItems.add(list.get(0));
        mCount = mItems.size();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCurrentPropertyImageCardView =
                (PropertyImageCardView) mLayoutInflater.inflate(R.layout.property_images_viewpager_item, null);

        mCurrentPropertyImageCardView.bindView(mContext, mItems.get(position),position,price);
        container.addView(mCurrentPropertyImageCardView,0);
        return mCurrentPropertyImageCardView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
