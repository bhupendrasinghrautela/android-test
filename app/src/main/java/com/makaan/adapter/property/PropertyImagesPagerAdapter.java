package com.makaan.adapter.property;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
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

    public PropertyImagesPagerAdapter(final ViewPager pager,Context context) {
        this.mContext = context;
        this.pager = pager;
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int pageCount = getCount();
                Log.d("position current", position + " " + pageCount);
                if (position == 0){
                    pager.setCurrentItem(5, false);
                    Log.d("position changed", 3 + " ");
                } else if (position == pageCount-1){
                    pager.setCurrentItem(1, false);
                    Log.d("position changed", 1 + " ");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setData(List<Image> list){
        mItems.clear();
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

        PropertyImageCardView propertyImageCardView =
                (PropertyImageCardView) mLayoutInflater.inflate(R.layout.property_images_viewpager_item, null);

        propertyImageCardView.bindView(mContext, mItems.get(position),position);
        container.addView(propertyImageCardView,0);
        return propertyImageCardView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

}
