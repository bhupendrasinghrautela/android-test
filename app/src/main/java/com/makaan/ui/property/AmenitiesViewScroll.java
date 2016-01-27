package com.makaan.ui.property;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.pojo.HorizontalScrollItem;
import com.makaan.response.listing.detail.ListingDetail;
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
public class AmenitiesViewScroll extends BaseLinearLayout<ListingDetail> {

    @Bind(R.id.amenity_data_scroll)
    CustomHorizontalScrollView mAmenityScroll;
    List<HorizontalScrollItem> mAmenityItems;
    List<String> mAmenitiesPriority;
    HashMap<Long,Boolean> mAmenitiesToDisplay;
    private Context mContext;
    private ListingDetail mDetail;

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
    public void bindView(ListingDetail item) {
        mDetail = item;
        mAmenitiesPriority = MasterDataCache.getInstance().getDisplayOrder(item.listingCategory, item.property.unitType, "amenity");
        createAmenitiesToDisplay();
        createAdapterData();
        ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext,mAmenityItems);
        mAmenityScroll.setAdapter(listingOverViewAdapter);
    }

    private void createAmenitiesToDisplay() {
        mAmenitiesToDisplay = MasterDataCache.getInstance().getDefaultAmenityList();
        for(Long id:mDetail.neighbourhoodAmenitiesIds){
            if(mAmenitiesToDisplay.get(id)!=null){
                mAmenitiesToDisplay.put(id,true);
            }
            else{
                mAmenitiesToDisplay.put(id,true);
            }
        }
    }

    private void createAdapterData() {
        mAmenityItems = new ArrayList<>();
        for(String amenityId:mAmenitiesPriority){
            try {
                if((mAmenitiesToDisplay.get(Long.parseLong(amenityId)))!=null){
                    HorizontalScrollItem amenityItem = new HorizontalScrollItem();
                    Long id = Long.parseLong(amenityId);
                    if(mAmenitiesToDisplay.get(id)){
                        amenityItem.resourceId = R.drawable.possession;
                    }
                    else{
                        amenityItem.resourceId = R.drawable.oval_gray_background;
                    }
                    amenityItem.value = MasterDataCache.getInstance().getPropertyAmenityById(id).amenityName;
                    mAmenityItems.add(amenityItem);
                }
            }catch (NumberFormatException e){}
        }
    }

    public class ListingOverViewAdapter extends CustomAbstractHorizontalScrollViewAdapter<HorizontalScrollItem>{

        @Bind(R.id.image)
        ImageView imageView;
        @Bind(R.id.name)
        TextView nameText;
        @Bind(R.id.value)
        TextView valueText;
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
            imageView.setImageResource(dataItem.resourceId);
            nameText.setText(dataItem.name);
            valueText.setText(dataItem.value);
            return view;
        }
    }
}
