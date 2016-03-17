package com.makaan.ui.property;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.adapter.listing.CustomAbstractHorizontalScrollViewAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.pojo.HorizontalScrollItem;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.project.Project;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.ui.listing.CustomHorizontalScrollView;
import com.makaan.util.AppUtils;
import com.makaan.util.ListingUtil;
import com.makaan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.makaan.util.AppUtils.getElapsedYearsFromNow;

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
    private Project mProject;


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
        STATUS("status"),POSSESSION("possession"),AGE("age"),BATH("bathrooms"),FLOOR("floor"),BALCONY("balcony"),
        TOTAL_FLOOR("totalFloor"),CATEGORY("category"),BOOK_AMOUNT("bookamt"),FURNISH_STAT("furnishStat"),
        SECURITY_DEPOSIT("securityDeposit"),AVAILABILITY("availablity"),TENANT_PREF("tenantPref"),
        OPEN_SIDES("openSides"),ENTRY_ROAD_WIDTH("entryRoadWidth"),FACING("facing"),NEGOTIABLE("priceNeg"),
        PARKING("parking"),OVERLOOK("overlook"),ADD_ROOM("addroom"),OWNER_TYPE("ownerType"),TYPE("type");

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
        createAdapterDataForListing();
        if(mOverViewItems==null || mOverViewItems.isEmpty() ){
            this.setVisibility(GONE);
            return;
        }
        else {
            this.setVisibility(VISIBLE);
            ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext, mOverViewItems);
            mOverViewScroll.setAdapter(listingOverViewAdapter);
        }
    }

    public void bindView(Project item) {
        mProject = item;
        createAdapterDataForProject();
        if(mOverViewItems==null || mOverViewItems.isEmpty() ){
            this.setVisibility(GONE);
            return;
        }
        else {
            this.setVisibility(VISIBLE);
            ListingOverViewAdapter listingOverViewAdapter = new ListingOverViewAdapter(mContext, mOverViewItems);
            mOverViewScroll.setAdapter(listingOverViewAdapter);
        }
    }

    private void createAdapterDataForProject() {
        mOverViewItems = new ArrayList<>();
        if(mProject.projectStatus!=null){
            HorizontalScrollItem overViewItem = new HorizontalScrollItem();
            overViewItem.name = OVERVIEW.STATUS.toString();
            overViewItem.resourceId = R.drawable.under_construction;
            overViewItem.value = mProject.projectStatus;
            mOverViewItems.add(overViewItem);
        }
        if(mProject.locality!=null && !ListingUtil.isReadyToMove(mProject.locality.constructionStatusId) &&mProject.possessionDate!=null){
            HorizontalScrollItem overViewItem = new HorizontalScrollItem();
            overViewItem.name = OVERVIEW.POSSESSION.toString();
            overViewItem.resourceId = R.drawable.possession;
            overViewItem.value = AppUtils.getMMMYYYYDateStringFromEpoch(mProject.possessionDate);
            mOverViewItems.add(overViewItem);
        }
        else{
            try {
                if(!TextUtils.isEmpty(mProject.possessionDate)) {
                    Long age = Long.getLong(mProject.possessionDate);
                    String ageYrs = String.valueOf((age > 0 ? getElapsedYearsFromNow(age) : 0));
                    HorizontalScrollItem overViewItem = new HorizontalScrollItem();
                    overViewItem.name = "age";
                    overViewItem.resourceId = R.drawable.age_property;
                    overViewItem.value = ageYrs.concat(" yrs");
                }
            }catch (NumberFormatException e){}
        }
        if(mProject.dominantUnitType !=null){
            HorizontalScrollItem overViewItem = new HorizontalScrollItem();
            overViewItem.name = OVERVIEW.TYPE.toString();
            overViewItem.resourceId = R.drawable.property_type;
            overViewItem.value = mProject.dominantUnitType;
            mOverViewItems.add(overViewItem);
        }
    }

    private void createAdapterDataForListing() {
        mOverViewItems = new ArrayList<>();
        for(String string : mOverViewOrder){
            OVERVIEW overview = OVERVIEW.getEnum(string);
            if(overview!=null) {
                HorizontalScrollItem overViewItem = new HorizontalScrollItem();
                switch (overview) {
                    case STATUS:
                        if(MasterDataCache.getInstance().getConstructionStatus(mDetail.constructionStatusId)!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.under_construction;
                            overViewItem.value = MasterDataCache.getInstance().getConstructionStatus(mDetail.constructionStatusId).displayName;
                        }
                        break;
                    case POSSESSION:
                        if(!ListingUtil.isReadyToMove(mDetail.constructionStatusId)&& mDetail.possessionDate!=null) {
                            overViewItem.name = "possession";
                            overViewItem.resourceId = R.drawable.possession;
                            overViewItem.value = AppUtils.getMMMYYYYDateStringFromEpoch(mDetail.possessionDate);
                        }
                        else if(mDetail.minConstructionCompletionDate!=null){
                            Long age = mDetail.minConstructionCompletionDate;
                            int ageYrs =age > 0 ? getElapsedYearsFromNow(age) : 0;
                            overViewItem.name ="age";
                            overViewItem.resourceId = R.drawable.age_property;
                            if(ageYrs <= 1) {
                                overViewItem.value =String.format("%d - %d yr",ageYrs, ageYrs + 1);
                            } else if(ageYrs <= 2) {
                                overViewItem.value = String.format("%d - %d yrs",ageYrs, ageYrs + 1);
                            } else if(ageYrs <= 5) {
                                overViewItem.value = String.format("%d - %d yrs", 2, 5);
                            } else {
                                overViewItem.value = String.format(">%d yrs", 5);
                            }
                        }
                        break;
                    case BOOK_AMOUNT:
                        if(mDetail.bookingAmount!=null){
                            overViewItem.name = "booking amount";
                            overViewItem.resourceId = R.drawable.booking_amount;
                            overViewItem.value = StringUtil.getDisplayPrice(mDetail.bookingAmount);
                        }
                        break;
                    case FURNISH_STAT:
                        if(!TextUtils.isEmpty(mDetail.furnished)) {
                            overViewItem.name = "furnished";
                            overViewItem.resourceId = R.drawable.furniture_status;
                            overViewItem.value = mDetail.furnished.toLowerCase();
                        }
                        break;
                    case SECURITY_DEPOSIT:
                        if(mDetail.securityDeposit!=0){
                            overViewItem.name= "security deposit";
                            overViewItem.resourceId = R.drawable.security_deposit;
                            overViewItem.value = String.valueOf(mDetail.securityDeposit);
                        }
                        break;
                    case BATH:
                        if(mDetail.property!=null && mDetail.property.bathrooms!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.shower;
                            overViewItem.value = String.valueOf(mDetail.property.bathrooms);
                        }
                        break;
                    case FLOOR:
                        if(mDetail.floor != null && mDetail.totalFloors != null) {
                            if(mDetail.floor == 1) {
                                overViewItem.value = String.format("%d<sup>st</sup> of %d", mDetail.floor, mDetail.totalFloors);
                            } else if(mDetail.floor == 2) {
                               overViewItem.value = String.format("%d<sup>nd</sup> of %d", mDetail.floor, mDetail.totalFloors);
                            } else if(mDetail.floor == 3) {
                                overViewItem.value = String.format("%d<sup>rd</sup> of %d", mDetail.floor, mDetail.totalFloors);
                            }else if(mDetail.floor == 0) {
                                overViewItem.value = String.format("%s of %d", "gr", mDetail.totalFloors);
                            }  else {
                                overViewItem.value = String.format("%d<sup>th</sup> of %d", mDetail.floor, mDetail.totalFloors);
                            }
                        }
                        else if(mDetail.floor !=null && mDetail.floor !=0){
                            if(mDetail.floor == 1) {
                                overViewItem.value = String.format("%d<sup>st</sup>", mDetail.floor);
                            } else if(mDetail.floor == 2) {
                                overViewItem.value = String.format("%d<sup>nd</sup>", mDetail.floor);
                            } else if(mDetail.floor == 3) {
                                overViewItem.value = String.format("%d<sup>rd</sup>", mDetail.floor);
                            }else if(mDetail.floor == 0) {
                                overViewItem.value = String.format("%s of %d", "gr", mDetail.totalFloors);
                            } else {
                                overViewItem.value = String.format("%d<sup>th</sup>", mDetail.floor);
                            }
                        }
                        overViewItem.name = overview.toString();
                        overViewItem.resourceId = R.drawable.floor;
                        break;
                    case OWNER_TYPE:
                        if(mDetail.ownershipTypeId!=null) {
                            overViewItem.name = "ownership";
                            overViewItem.resourceId = R.drawable.ownership;
                            overViewItem.value = MasterDataCache.getInstance().getOwnershipType(mDetail.ownershipTypeId);
                        }
                        break;
                    case OPEN_SIDES:
                        if(mDetail.noOfOpenSides!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.open_sides;
                            overViewItem.value = String.valueOf(mDetail.noOfOpenSides);
                        }
                        break;
                    case NEGOTIABLE:
                        if(mDetail.negotiable!=null){
                            overViewItem.name = "price negotiable";
                            overViewItem.resourceId = R.drawable.booking_amount;
                            if(mDetail.negotiable) {
                                overViewItem.value = "yes";
                            }
                            else{
                                overViewItem.value = "no";
                            }
                        }
                        break;
                    case BALCONY:
                        if(mDetail.balcony!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.balcony;
                            overViewItem.value = String.valueOf(mDetail.balcony);
                        }
                        break;
                    case CATEGORY:
                        if(mDetail.listingCategory!=null) {
                            overViewItem.name = "new/resale";
                            overViewItem.resourceId = R.drawable.new_resale;
                            if("primary".equalsIgnoreCase(mDetail.listingCategory)) {
                                overViewItem.value = "new".toLowerCase();
                            } else if("resale".equalsIgnoreCase(mDetail.listingCategory)) {
                                overViewItem.value = "resale".toLowerCase();
                            } else {
                                overViewItem.value = mDetail.listingCategory.toLowerCase();
                            }
                        }
                        break;
                    case ENTRY_ROAD_WIDTH:
                        if(mDetail.mainEntryRoadWidth!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.road_width;
                            overViewItem.value = String.valueOf(mDetail.mainEntryRoadWidth);
                        }
                        break;
                    case FACING:
                        if(mDetail.facingId!=null) {
                            overViewItem.name = overview.toString();
                            overViewItem.resourceId = R.drawable.facing;
                            overViewItem.value = MasterDataCache.getInstance().getDirection(mDetail.facingId);
                        }
                        break;
                    default:
                        overViewItem = null;
                }
                if (overViewItem != null && overViewItem.value!=null && !overViewItem.value.equals("0")) {
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
            final View view = mInflater.inflate(R.layout.horizontal_overview_scroll_item,null);
            ButterKnife.bind(this,view);
            imageView.setDefaultImageResId(dataItem.resourceId);
            nameText.setText(dataItem.name);
            if(dataItem.value!=null) {
                valueText.setText(Html.fromHtml(dataItem.value.toLowerCase()));
            }
            return view;
        }
    }
}
