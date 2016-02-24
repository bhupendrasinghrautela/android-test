package com.makaan.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.listing.PropertyActivity;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.MakaanEvent;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.wishlist.WishListResultEvent;
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
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sunil on 15/02/16.
 */
public class WishListButton extends BaseLinearLayout<WishListButton.WishListDto> implements CompoundButton.OnCheckedChangeListener,
        WishListResponseUICallback, View.OnClickListener {

    public enum WishListType{
        listing, project;
    }

    @Bind(R.id.shortlist_checkbox)
    CheckBox mShortlistCheckBox;

    @Bind(R.id.loading_progress)
    ProgressBar mLoadingProgressBar;

    private Context mContext;

    private WishListDto mWishListDto;

    private boolean isLoginInitiatedFromWishList;

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
        isLoginInitiatedFromWishList = false;
        mWishListDto = item;
        boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                mWishListDto.type==WishListType.listing?mWishListDto.listingId:mWishListDto.projectId);

        mShortlistCheckBox.setOnCheckedChangeListener(null);
        if(isShortlisted){
            mShortlistCheckBox.setChecked(true);
        }else{
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
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(mContext instanceof PropertyActivity) {
            Properties properties = MakaanEventPayload.beginBatch();
            if(isChecked) {
                properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_Select");
            }
            else{
                properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_UnSelect");
            }
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickPropertyOverview);
        }
        else if(mContext instanceof SerpActivity){
            Properties properties = MakaanEventPayload.beginBatch();
            if(isChecked) {
                properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_"+(mWishListDto.serpItemPosition+1));
            }
            else{
                properties.put(MakaanEventPayload.LABEL, String.valueOf(mWishListDto.listingId) + "_"+(mWishListDto.serpItemPosition+1));
            }
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerSerp);
            MakaanEventPayload.endBatch(mContext, MakaanTrackerConstants.Action.clickSerpPropertyShortList);
        }
        if(null!=MasterDataCache.getInstance().getUserData()) {

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

            isLoginInitiatedFromWishList = true;
            setChecked(!isChecked);
            Intent intent = new Intent(mContext, UserLoginActivity.class);
            mContext.startActivity(intent);
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
    public void loginResults(UserLoginEvent userLoginEvent){
        if(null==userLoginEvent || null!=userLoginEvent.error){
            Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        }
        if(!isLoginInitiatedFromWishList){
            return;
        }

        isLoginInitiatedFromWishList = false;

        if(userLoginEvent.error!=null){
            Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        } else {
            onCheckedChanged(null, !mShortlistCheckBox.isChecked());
        }
    }

}
