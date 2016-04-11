package com.makaan.adapter.property;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.overview.OverviewActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.fragment.overview.OverviewFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.response.image.Image;
import com.makaan.ui.property.PropertyImageCardView;
import com.makaan.util.CommonUtil;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aishwarya on 19/01/16.
 */
public class PropertyImagesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<Image> mItems = new ArrayList<>();
    private ArrayList<Image> mGalleryItems = new ArrayList<>();
    private int mCount = 0;
    private ViewPager pager;
    private Double price;
    private boolean hasIncreased;
    private Double size;
    private PropertyImageCardView mCurrentPropertyImageCardView;
    private String category;
    private int previousPosition=-1;
    private int mTotalCount=0;

    public PropertyImagesPagerAdapter(final ViewPager pager,Context context) {
        this.mContext = context;
        this.pager = pager;
        pager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               // Log.e("position ",""+position);
            }

            @Override
            public void onPageSelected(int position) {
                mTotalCount = mTotalCount + 1;
                OverviewFragment.OverviewActivityCallbacks totalImagesCount = (OverviewFragment.OverviewActivityCallbacks) mContext;
                totalImagesCount.imagesSeenCount(mTotalCount);
                Properties properties = MakaanEventPayload.beginBatch();
                if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                }
                else if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                }

                if (position == 0) {
                    //left
                    if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.left));
                        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyImages);
                    }
                    pager.setCurrentItem(mCount - 2, false);
                } else if (position == mCount - 1) {
                    //right
                    if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                        properties.put(MakaanEventPayload.LABEL, String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.right));
                        MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectImages);
                    }
                    pager.setCurrentItem(1, false);
                }
                else{
                    if(previousPosition!=-1 && previousPosition<position){
                        //right
                        if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                            properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                    mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.left));
                            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectImages);
                        }
                        else if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                            properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                    mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.left));
                            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyImages);
                        }
                    }
                    else if(previousPosition!=-1 && previousPosition>position){
                        //left
                        if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                            properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                    mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.right));
                            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickProjectImages);
                        }
                        else if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)mContext).getScreenName())) {
                            properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s",mItems.get(position).imageType.displayName!=null?
                                    mItems.get(position).imageType.displayName:"" , MakaanTrackerConstants.Label.right));
                            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyImages);
                        }
                    }
                }
                previousPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setData(List<Image> list, Double price, Double size,boolean hasIncreased,String category){
        if(list == null || list.isEmpty()){
            return;
        }
        mItems.clear();
        mGalleryItems.clear();
        mGalleryItems.addAll(list);
        this.price = price;
        this.size = size;
        this.hasIncreased = hasIncreased;
        this.category = category;
        if(list.size()>1) {
            mItems.add(list.get(list.size() - 1));
            mItems.addAll(list);
            mItems.add(list.get(0));
        }
        else{
            mItems.addAll(list);
        }
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
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCurrentPropertyImageCardView =
                (PropertyImageCardView) mLayoutInflater.inflate(R.layout.property_images_viewpager_item, null);

        if(mCount == 1) {
            mCurrentPropertyImageCardView.bindView(mContext, mItems.get(position),1, price,size,hasIncreased,category);
        }
        else {
            mCurrentPropertyImageCardView.bindView(mContext, mItems.get(position), position, price,size,hasIncreased,category);
        }
        mCurrentPropertyImageCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KeyUtil.KEY_FULL_SCREEN_SRC_NAME,"Test");
                bundle.putParcelableArrayList(KeyUtil.IMAGE_DATA_LIST, mGalleryItems);
                CommonUtil.launchGallery((Activity) mContext, bundle);
            }
        });
        container.addView(mCurrentPropertyImageCardView,0);
        return mCurrentPropertyImageCardView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
