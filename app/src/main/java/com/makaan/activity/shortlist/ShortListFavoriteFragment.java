package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.VolleyErrorParser;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListFavoriteFragment extends MakaanBaseFragment{

    @Bind(R.id.favorite_recycler_view)
    RecyclerView favoriteRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar progressBar;

    @Bind(R.id.tv_no_shortlist)
    TextView statusMessage;

    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_favorite;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((WishListService) MakaanServiceFactory.getInstance().getService(WishListService.class)).get();
        statusMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getActivity());
        favoriteRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Subscribe
    public void wishListResponse(WishListResultEvent wishListResultEvent){
        progressBar.setVisibility(View.GONE);
        if(null==wishListResultEvent ||null!=wishListResultEvent.error){
            statusMessage.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), VolleyErrorParser.getMessage(wishListResultEvent.error.error), Toast.LENGTH_SHORT).show();
        }

        WishListResponse wishListResponse = wishListResultEvent.wishListResponse;
        if(wishListResponse!=null && wishListResponse.data!=null && wishListResponse.data.size()>0){
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setAdapter(new ShortListFavoriteAdapter(getActivity(),  wishListResponse.data));
        }else{
            statusMessage.setVisibility(View.VISIBLE);
        }

    }
}
