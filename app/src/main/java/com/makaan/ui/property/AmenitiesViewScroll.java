package com.makaan.ui.property;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.HorizontalScrollItem;
import com.makaan.response.listing.detail.ListingAmenity;
import com.makaan.response.project.ProjectAmenity;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.listing.CustomHorizontalScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aishwarya on 25/01/16.
 */
public class AmenitiesViewScroll extends BaseLinearLayout {

    @Bind(R.id.amenity_data_scroll)
    CustomHorizontalScrollView mAmenityScroll;
    List<HorizontalScrollItem> mAmenityItems;
    List<String> mAmenitiesPriority;
    HashMap<Long,Boolean> mAmenitiesToDisplay;
    private Context mContext;
    private List<ListingAmenity> mAmenityIdList;

    public AmenitiesViewScroll(Context context) {
        super(context);
        mContext = context;
    }

    public AmenitiesViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public AmenitiesViewScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

    @Override
    public void bindView(Object item) {

    }

    public void bindView(String listingCategory,String unitType,List<ListingAmenity> amenityIdList) {
        mAmenityIdList = amenityIdList;
        if(!TextUtils.isEmpty(listingCategory) && !TextUtils.isEmpty(unitType)) {
            mAmenitiesPriority = MasterDataCache.getInstance().getDisplayOrder(listingCategory, unitType, "amenity");
            createAmenitiesToDisplay();
            createAdapterDataForProperty();
            if(mAmenityItems.size()>0){
                this.setVisibility(VISIBLE);
            }
            ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext,mAmenityItems);
            mAmenityScroll.setAdapter(listingOverViewAdapter);
        }
    }

    public void bindView(List<ProjectAmenity> projectAmenities){
        createAdapterDataForProject(projectAmenities);
    }

    private void createAdapterDataForProject(List<ProjectAmenity> projectAmenities) {
        mAmenityItems = new ArrayList<>();
        for(ProjectAmenity projectAmenity : projectAmenities){
            try {
                HorizontalScrollItem amenityItem = new HorizontalScrollItem();
                amenityItem.resourceId = R.drawable.possession;
                amenityItem.value = MasterDataCache.getInstance().getPropertyAmenityById(projectAmenity.amenityMaster.amenityId).amenityName;
                amenityItem.name = String.valueOf(projectAmenity.amenityMaster.amenityId);
                mAmenityItems.add(amenityItem);
            }catch (Exception e){}
        }
        if(mAmenityItems.size()>0){
            this.setVisibility(VISIBLE);
        }
        ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext,mAmenityItems);
        mAmenityScroll.setAdapter(listingOverViewAdapter);
    }

    private void createAmenitiesToDisplay() {
        mAmenitiesToDisplay = MasterDataCache.getInstance().getDefaultAmenityList();
        for(ListingAmenity id:mAmenityIdList){
            if(mAmenitiesToDisplay.get(id.amenity.amenityMaster.amenityId)!=null){
                mAmenitiesToDisplay.put(id.amenity.amenityMaster.amenityId,true);
            }
            else{
                mAmenitiesToDisplay.put(id.amenity.amenityMaster.amenityId,true);
            }
        }
    }

    private void createAdapterDataForProperty() {
        mAmenityItems = new ArrayList<>();
        for(String amenityId:mAmenitiesPriority){
            try {
                if((mAmenitiesToDisplay.get(Long.parseLong(amenityId)))!=null){
                    HorizontalScrollItem amenityItem = new HorizontalScrollItem();
                    Long id = Long.parseLong(amenityId);
                    amenityItem.resourceId = R.drawable.possession;
                    if(mAmenitiesToDisplay.get(id)){
                        amenityItem.activated = true;
                    }
                    else{
                        amenityItem.activated = false;
                    }
                    amenityItem.value = MasterDataCache.getInstance().getPropertyAmenityById(id).amenityName;
                    amenityItem.name = amenityId;
                    mAmenityItems.add(amenityItem);
                }
            }catch (NumberFormatException e){}
        }
    }

    public class ListingOverViewAdapter extends CustomAbstractHorizontalScrollViewAdapter<HorizontalScrollItem>{

        @Bind(R.id.image)
        FadeInNetworkImageView imageView;
        @Bind(R.id.name)
        TextView nameText;
        @Bind(R.id.value)
        TextView valueText;
        private final static String URL = "http://content.makaan.com.s3.amazonaws.com/app/icons/amenities";

        public ListingOverViewAdapter(Context context, List<HorizontalScrollItem> dataList) {
            super(context, dataList);
        }

        @Override
        public List<View> getAllViews() {
            List<View> viewList = new ArrayList<>();
            int position = 0;
            for(HorizontalScrollItem item : mDataList){
                viewList.add(inflateAndBindDataToView(item,position));
                position++;
            }
            return viewList;
        }

        @Override
        public View inflateAndBindDataToView(HorizontalScrollItem dataItem, int positon) {
            final View view = mInflater.inflate(R.layout.horizontal_scroll_item,null);
            ButterKnife.bind(this,view);
            imageView.setDefaultImageResId(dataItem.resourceId);
            StringBuilder finalImageUrl = new StringBuilder();
            finalImageUrl.append(URL.toString());
            finalImageUrl.append("/");
            finalImageUrl.append(dataItem.name);
            finalImageUrl.append(".png");
            imageView.setImageUrl(finalImageUrl.toString(), MakaanNetworkClient.getInstance().getImageLoader());
            if(!dataItem.activated){
                imageView.setAlpha(0.5f);
                valueText.setTextColor(mContext.getResources().getColor(R.color.pyr_light_grey));
            }
            nameText.setVisibility(GONE);
            valueText.setText(dataItem.value);
            return view;
        }
    }
}
