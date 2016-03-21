package com.makaan.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.makaan.R;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanMessageDialogFragment;
import com.makaan.network.VolleyErrorParser;
import com.makaan.response.ResponseError;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.response.wishlist.WishListResponseUICallback;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.util.AppBus;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by sunil on 15/02/16.
 */
public class WishListButton extends BaseLinearLayout<WishListButton.WishListDto> implements CompoundButton.OnCheckedChangeListener,
        WishListResponseUICallback, View.OnClickListener {

    public enum WishListType{
        listing, project
    }

    @Bind(R.id.shortlist_checkbox)
    CheckBox mShortlistCheckBox;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingProgressBar;

    private Context mContext;

    private WishListDto mWishListDto;


    public WishListButton(Context context) {
        super(context);
        mContext = context;
    }

    public WishListButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public WishListButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void bindView(WishListDto item) {
        try {
            AppBus.getInstance().register(this);
        }catch(Exception e){}
        mWishListDto = item;
        if(MasterDataCache.getInstance().getUserData()!=null) {
            boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                    mWishListDto.type == WishListType.listing ? mWishListDto.listingId : mWishListDto.projectId);

            mShortlistCheckBox.setOnCheckedChangeListener(null);
            if (isShortlisted) {
                mShortlistCheckBox.setChecked(true);
            } else {
                mShortlistCheckBox.setChecked(false);
            }
        }
        else {
            mShortlistCheckBox.setOnCheckedChangeListener(null);
            mShortlistCheckBox.setChecked(false);
        }

        mShortlistCheckBox.setOnCheckedChangeListener(this);
        this.setOnClickListener(this);
    }

    public static class WishListDto{
        Long listingId;
        Long projectId;
        Long serpItemPosition;
        WishListType type;

        public WishListDto(Long id, Long projectId, WishListType type){
            this.listingId = id;
            this.projectId = projectId;
            this.type = type;
        }

        public void setSerpItemPosition(Long position){
            serpItemPosition=position;
        }
    }

    @Override
    public void onClick(View v) {
        mShortlistCheckBox.setChecked(!mShortlistCheckBox.isChecked());
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, final boolean isChecked) {

        if(null!=MasterDataCache.getInstance().getUserData()) {
            if(mContext instanceof PropertyActivity && mWishListDto.listingId != null) {
                Properties properties = MakaanEventPayload.beginBatch();
                if(isChecked) {
                    properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_Shortlist");
                }
                else{
                    properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_UnShortlist");
                }
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
            }
            else if(mContext instanceof SerpActivity && mWishListDto.serpItemPosition != null && mWishListDto.listingId != null) {
                Properties properties = MakaanEventPayload.beginBatch();
                if (isChecked) {
                    properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_" +
                            (mWishListDto.serpItemPosition + 1)+"_Shortlist");
                } else {
                    properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_" +
                            (mWishListDto.serpItemPosition + 1)+"_UnShortlist");
                }
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
                MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickSerpPropertyShortList);
            }

            WishListService wishListService =
                    (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
            if (isChecked) {
                if(mWishListDto.type==WishListType.listing) {
                    wishListService.addListing(mWishListDto.listingId, mWishListDto.projectId, this);
                }else{
                    wishListService.addProject(mWishListDto.projectId, this);
                }
            } else {


                Long itemId = mWishListDto.type==WishListType.listing?mWishListDto.listingId:mWishListDto.projectId;
                if(null!=itemId) {
                    wishListService.delete(itemId, this);
                }
            }

            mShortlistCheckBox.setVisibility(View.GONE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);

        }else{
            setChecked(!isChecked);

            if(getContext() instanceof Activity) {
                Activity activity = (Activity) getContext();
                MakaanMessageDialogFragment.showMessage(activity.getFragmentManager(),
                        activity.getResources().getString(R.string.add_to_fav_login_prompt),
                        mContext.getResources().getString(R.string.ok),
                        mContext.getResources().getString(R.string.cancel),
                        new MakaanMessageDialogFragment.MessageDialogCallbacks() {
                            @Override
                            public void onPositiveClicked() {
                                addEntityToQueue();
                                Intent intent = new Intent(mContext, UserLoginActivity.class);
                                mContext.startActivity(intent);
                            }

                            @Override
                            public void onNegativeClicked() {

                            }
                        });
            }else {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                builder.setMessage(R.string.add_to_fav_login_prompt);
                builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        addEntityToQueue();
                        Intent intent = new Intent(mContext, UserLoginActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                builder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    private void setChecked(boolean checked){
        mShortlistCheckBox.setOnCheckedChangeListener(null);
        mShortlistCheckBox.setChecked(checked);
        mShortlistCheckBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onSuccess(WishListResponse wishListResponse) {
        mShortlistCheckBox.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(ResponseError error) {
        setChecked(!mShortlistCheckBox.isChecked());
        Toast.makeText(mContext, VolleyErrorParser.getMessage(error.error), Toast.LENGTH_SHORT).show();
        mShortlistCheckBox.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        return;
    }


    @Subscribe
    public void onResults(WishListResultEvent wishListResultEvent) {

        if(!isEntityAddedToQueue()){
            return;
        }

        //We are concerned only for get wishlist event
        if(wishListResultEvent.requestMethod!= Request.Method.GET){
            return;
        }

        if(wishListResultEvent.wishListResponse==null || wishListResultEvent.error!=null){
            Toast.makeText(mContext, VolleyErrorParser.getMessage(wishListResultEvent.error), Toast.LENGTH_SHORT).show();
        }

        boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                mWishListDto.type == WishListType.listing ? mWishListDto.listingId : mWishListDto.projectId);

        if(isShortlisted){
            setEnabled(true);
            return;
        }

        //onCheckedChanged(null, !mShortlistCheckBox.isChecked());
        mShortlistCheckBox.setChecked(true);

    }

    private void addEntityToQueue(){
        WishListService wishListService =
                (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);

        if(mWishListDto.type==WishListType.listing) {
            wishListService.setEntityIdToBeShortlisted(mWishListDto.listingId);
        }else{
            wishListService.setEntityIdToBeShortlisted(mWishListDto.projectId);
        }
    }

    private boolean isEntityAddedToQueue(){
        WishListService wishListService =
                (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);

        if(wishListService.getEntityIdToBeShortlisted()<0){
            return false;
        }

        if(mWishListDto.type==WishListType.listing) {
            return wishListService.getEntityIdToBeShortlisted() == mWishListDto.listingId;
        }else{
            return wishListService.getEntityIdToBeShortlisted() == mWishListDto.projectId;
        }
    }

}
