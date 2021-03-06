package com.makaan.ui.property;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.makaan.R;
import com.makaan.constants.ImageConstants;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.ui.FadeInNetworkImageView;
import com.makaan.util.ImageUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aishwarya on 02/02/16.
 */
public class FloorPlanLayout extends LinearLayout {

    @Bind(R.id.property_floor_plan_tab)
    TabLayout tabLayout;
    @Bind(R.id.property_floor_image_pager)
    ViewPager viewPager;
    private Context mContext;
    private FloorPlanPagerAdapter mFloorPlanPagerAdapter;

    public FloorPlanLayout(Context context) {
        super(context);
        mContext = context;
    }

    public FloorPlanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FloorPlanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mFloorPlanPagerAdapter = new FloorPlanPagerAdapter();
        viewPager.setAdapter(mFloorPlanPagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(20);
        viewPager.setPadding(10, 10,10, 10);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void bindFloorPlan(ImagesGetEvent imagesGetEvent) {
        setVisibility(VISIBLE);
        mFloorPlanPagerAdapter.addData(imagesGetEvent);
        if(ImageConstants.FLOOR_PLAN.equalsIgnoreCase(imagesGetEvent.imageType)){
            tabLayout.addTab(tabLayout.newTab().setText("2D"));
        }
        else if(ImageConstants.THREED_FLOOR_PLAN.equalsIgnoreCase(imagesGetEvent.imageType)){
            tabLayout.addTab(tabLayout.newTab().setText("3D"));
        }
    }


    class FloorPlanPagerAdapter extends PagerAdapter{

        ArrayList<ImagesGetEvent> imagesGetEventArrayList;

        public FloorPlanPagerAdapter() {
            imagesGetEventArrayList = new ArrayList<>();
        }

        public void addData(ImagesGetEvent imagesGetEvent){
            imagesGetEventArrayList.add(imagesGetEvent);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return imagesGetEventArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater mLayoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CardView cardView=
                    (CardView) mLayoutInflater.inflate(R.layout.floor_plan_pager_item, null);
            FadeInNetworkImageView imageView = (FadeInNetworkImageView) cardView.findViewById(R.id.floor_plan_imageview);
            if(imagesGetEventArrayList.get(position).images.size()>0) {
                int width = (int) (getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
                int height = (int)Math.ceil(getResources().getDimension(R.dimen.floor_plan_view_pager_height));
                imageView.setImageUrl(ImageUtils.getImageRequestUrl(imagesGetEventArrayList.get(position).images.get(0).absolutePath, width, height, false),
                        MakaanNetworkClient.getInstance().getImageLoader());
            }
            container.addView(cardView,0);
            return cardView;
        }
    }
}
