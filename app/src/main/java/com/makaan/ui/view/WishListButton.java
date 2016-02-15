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
import com.makaan.activity.userLogin.UserLoginActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.user.UserLoginEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.response.ResponseError;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.response.wishlist.WishListResponseUICallback;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.ui.BaseLinearLayout;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by sunil on 15/02/16.
 */
public class WishListButton extends BaseLinearLayout<WishListButton.WishListDto> implements CompoundButton.OnCheckedChangeListener,
        WishListResponseUICallback{

    public enum WishListType{
        listing, project;
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
        boolean isShortlisted = MasterDataCache.getInstance().isShortlistedProperty(
                mWishListDto.type==WishListType.listing?mWishListDto.listingId:mWishListDto.projectId);

        if(isShortlisted){
            mShortlistCheckBox.setChecked(true);
        }

        mShortlistCheckBox.setOnCheckedChangeListener(this);
    }

    public static class WishListDto{
        Long listingId;
        Long projectId;
        WishListType type;

        public WishListDto(Long id, Long projectId, WishListType type){
            this.listingId = id;
            this.projectId = projectId;
            this.type = type;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        if(CookiePreferences.isUserLoggedIn(mContext)) {

            WishListService wishListService =
                    (WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class);
            if (isChecked) {
                if(mWishListDto.type==WishListType.listing) {
                    wishListService.addListing(mWishListDto.listingId, mWishListDto.projectId, this);
                }else{
                    wishListService.addProject(mWishListDto.projectId, this);
                }
            } else {
                Long wishListId = MasterDataCache.getInstance().getWishlistId(
                        mWishListDto.type==WishListType.listing?mWishListDto.listingId:mWishListDto.projectId);

                if(null!=wishListId) {
                    wishListService.delete(wishListId, this);
                }
            }

            mShortlistCheckBox.setVisibility(View.GONE);
            mLoadingProgressBar.setVisibility(View.VISIBLE);

        }else{

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
        Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        mShortlistCheckBox.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        return;
    }

    @Subscribe
    public void loginResults(UserLoginEvent userLoginEvent){
        if(userLoginEvent.error!=null){
            Toast.makeText(mContext, R.string.generic_error, Toast.LENGTH_SHORT).show();
        } else {
            onCheckedChanged(null, !mShortlistCheckBox.isChecked());
        }
    }

}
