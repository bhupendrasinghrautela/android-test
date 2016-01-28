package com.makaan.ui.property;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.pojo.HorizontalScrollItem;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.listing.CustomHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aishwarya on 25/01/16.
 */
public class ListingDataOverViewScroll extends BaseLinearLayout<ListingDetail> {

    @Bind(R.id.listing_data_overview_scroll)
    CustomHorizontalScrollView mOverViewScroll;
    List<String> mOverViewOrder;
    List<HorizontalScrollItem> mOverViewItems;
    private Context mContext;
    private ListingDetail mDetail;


/*    "status",
            "age",
            "posession",
            "bath",
            "floor",
            "balcony",
            "totalFloor",
            "category",
            "bookamt",
            "openSides",
            "entryRoadWd",
            "facing",
            "parking",
            "overlook",
            "addRoom",
            "ownerType"*/
    private enum OVERVIEW{
        STATUS("status"),AGE("age"),BATH("bath"),FLOOR("floor"),BALCONY("balcony"),
        TOTAL_FLOOR("totalFloor"),CATEGORY("category"),BOOK_AMOUNT("bookamt"),
        OPEN_SIDES("openSides"),ENTRY_ROAD_WIDTH("entryRoadWidth"),FACING("facing"),
        PARKING("parking"),OVERLOOK("overlook"),ADD_ROOM("addroom"),OWNER_TYPE("ownerType");

        private String value;
        OVERVIEW(String string) {
            this.value = string;
        }

        @Override
        public String toString() {
            return this.value;
        }
        public static OVERVIEW getEnum(String value) {
            for(OVERVIEW v : values())
                if(v.toString().equalsIgnoreCase(value)) return v;
            return null;
        }
    }

    public ListingDataOverViewScroll(Context context) {
        super(context);
        mContext = context;
    }

    public ListingDataOverViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public ListingDataOverViewScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

    @Override
    public void bindView(ListingDetail item) {
        mDetail = item;
        mOverViewOrder = MasterDataCache.getInstance().getDisplayOrder(item.listingCategory,item.property.unitType,"overview");
        createAdapterData();
        ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext,mOverViewItems);
        mOverViewScroll.setAdapter(listingOverViewAdapter);
    }

    private void createAdapterData() {
        mOverViewItems = new ArrayList<>();
        for(String string : mOverViewOrder){
            OVERVIEW overview = OVERVIEW.getEnum(string);
            if(overview!=null) {
                HorizontalScrollItem overViewItem = new HorizontalScrollItem();
                switch (overview) {
                    case STATUS:
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.possession;
                        overViewItem.value = mDetail.property.project.projectStatus;
                        break;
                    case AGE:
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.possession;
                        overViewItem.value = mDetail.possessionDate;
                        break;
                    case BATH:
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.possession;
                        overViewItem.value = String.valueOf(mDetail.property.bathrooms);
                        break;
                    case FLOOR:
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.possession;
                        overViewItem.value = String.valueOf(mDetail.floor);
                        break;
                    case FACING:
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.possession;
                        overViewItem.value = String.valueOf(mDetail.facingId);
                        break;
                    default:
                        overViewItem = null;
                }
                if (overViewItem != null) {
                    mOverViewItems.add(overViewItem);
                }
            }
        }
    }

    public class ListingOverViewAdapter extends CustomAbstractHorizontalScrollViewAdapter<HorizontalScrollItem>{

        @Bind(R.id.image)
        FadeInNetworkImageView imageView;
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
