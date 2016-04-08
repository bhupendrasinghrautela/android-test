package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.cookie.CookiePreferences;
import com.makaan.event.listing.ListingByIdsGetEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.response.wishlist.WishList;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;
import com.makaan.service.ProjectService;
import com.makaan.service.WishListService;
import com.makaan.util.ErrorUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListFavoriteFragment extends MakaanBaseFragment{

    @Bind(R.id.favorite_recycler_view)
    RecyclerView favoriteRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private ShortListCallback mCallback;
    private int mPosition;
    private ArrayList<WishList> wishListClonedData;
    private ShortListFavoriteAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_favorite;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(CookiePreferences.isUserLoggedIn(getActivity())) {
            ((WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class)).getForBuyerJourney();
            showProgress();
        } else {
            List<WishList> wishListData = MasterDataCache.getInstance().getAllWishList();
            if(wishListData != null && wishListData.size() > 0) {
                wishListClonedData = new ArrayList<>();

                if(wishListData.size() > 0) {
                    wishListClonedData.addAll(wishListData);
                }

                showProgress();
                mCallback.updateCount(mPosition, wishListData.size());
                ArrayList<String> wishListIds = new ArrayList<>();
                for(WishList wishList : wishListData) {
                    if(wishList.listingId != null && wishList.listingId > 0) {
                        wishListIds.add(String.valueOf(wishList.listingId));
                    } else if(wishList.projectId != null && wishList.projectId > 0) {
                        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectById(wishList.projectId);
                    }
                }
                if(wishListIds.size() > 0) {
                    ((ListingService) (MakaanServiceFactory.getInstance().getService(ListingService.class))).getListingDetail(wishListIds);
                }
            } else {
                showNoResults();
            }
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        favoriteRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Subscribe
    public void wishListResponse(WishListResultEvent wishListResultEvent){
        if(!isVisible()) {
            return;
        }
        if(null==wishListResultEvent ||null!=wishListResultEvent.error){
            if(wishListResultEvent != null && wishListResultEvent.error != null && wishListResultEvent.error.msg != null) {
                showNoResults(wishListResultEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }

        WishListResponse wishListResponse = wishListResultEvent.wishListResponse;
        if(wishListResponse!=null && wishListResponse.data!=null && wishListResponse.data.size()>0){
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setAdapter(new ShortListFavoriteAdapter(getActivity(), wishListResponse.data));
            mCallback.updateCount(mPosition, wishListResponse.data.size());
            showContent();
        }else{
            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, false));
        }

    }

    @Subscribe
    public void projectDataResponse(ProjectByIdEvent projectByIdEvent) {

        if(!isVisible()) {
            return;
        }

        if (null == projectByIdEvent || null != projectByIdEvent.error) {
            return;
        }

        if(wishListClonedData == null) {
            return;
        }

        if(projectByIdEvent.project != null) {
            for(WishList wishList : wishListClonedData) {
                if(wishList.projectId != null && projectByIdEvent.project.projectId != null
                        && wishList.projectId.equals(projectByIdEvent.project.projectId)) {
                    wishList.project = projectByIdEvent.project;
                    break;
                }
            }
        }

        if(adapter == null) {
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            adapter = new ShortListFavoriteAdapter(getActivity(), wishListClonedData);
            favoriteRecyclerView.setAdapter(adapter);
            showContent();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void listingDataResponse(ListingByIdsGetEvent listingByIdsGetEvent){
        if(!isVisible()) {
            return;
        }
        if (null == listingByIdsGetEvent || null != listingByIdsGetEvent.error) {
            return;
        }

        if(wishListClonedData == null) {
            return;
        }

        if (listingByIdsGetEvent.items != null && listingByIdsGetEvent.items.size() > 0) {
            for(ListingByIdsGetEvent.Listing listing : listingByIdsGetEvent.items) {
                if(listing.listing != null) {
                    for(WishList wishList : wishListClonedData) {
                        if(wishList.listingId != null && listing.listing.id != null
                                && wishList.listingId.equals(listing.listing.id)) {
                            wishList.listing = listing.listing;
                            if(wishList.listing.property != null) {
                                wishList.project = wishList.listing.property.project;
                            }
                            break;
                        }
                    }
                }
            }
            if(adapter == null) {
                favoriteRecyclerView.setVisibility(View.VISIBLE);
                adapter = new ShortListFavoriteAdapter(getActivity(), wishListClonedData);
                favoriteRecyclerView.setAdapter(adapter);
                showContent();
            } else {
                adapter.notifyDataSetChanged();
            }
        }

    }

    public void bindView(ShortListCallback shortlistPagerAdapter, int i) {
        this.mPosition = i;
        this.mCallback = shortlistPagerAdapter;
    }
}
