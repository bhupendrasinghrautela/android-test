package com.makaan.activity.shortlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.event.wishlist.WishListResultEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.wishlist.WishListResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.WishListService;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListRecentFragment extends MakaanBaseFragment {

    @Bind(R.id.favorite_recycler_view)
    RecyclerView favoriteRecyclerView;

    @Bind(R.id.loading_progress)
    ProgressBar progressBar;

    @Bind(R.id.tv_no_shortlist)
    TextView statusMessage;

    private RecyclerView.LayoutManager mLayoutManager;
    private int mPosition;
    private ShortListCallback mCallback;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_favorite;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        RecentPropertyProjectManager manager = RecentPropertyProjectManager.getInstance(getActivity());
        ArrayList<RecentPropertyProjectManager.DataObject> entries = manager.getRecentEntries(getActivity());

        mLayoutManager = new LinearLayoutManager(getActivity());
        favoriteRecyclerView.setLayoutManager(mLayoutManager);
        //favoriteRecyclerView.setAdapter(new ShortListFavoriteAdapter(getActivity()));

        progressBar.setVisibility(View.GONE);

        if(entries == null || entries.size() == 0) {
            statusMessage.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setVisibility(View.GONE);
        } else {
            statusMessage.setVisibility(View.GONE);
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            RecentAdapter adapter = new RecentAdapter();
            adapter.setData(entries);
            favoriteRecyclerView.setAdapter(adapter);
            mCallback.updateCount(mPosition, entries.size());
        }
        return view;
    }

    public void bindView(ShortlistPagerAdapter shortListCallback, int i) {
        this.mPosition = i;
        this.mCallback = shortListCallback;
    }


    class RecentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<RecentPropertyProjectManager.DataObject> entries;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.card_shortlist_favorite, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).bindData(entries.get(position));
        }

        @Override
        public int getItemCount() {
            if(entries == null) {
                return 0;
            }
            return entries.size();
        }

        public void setData(ArrayList<RecentPropertyProjectManager.DataObject> entries) {
            if(this.entries == null) {
                this.entries = new ArrayList<>();
            } else {
                this.entries.clear();
            }

            if(entries == null) {
                return;
            }

            this.entries.addAll(entries);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View view;
            public ViewHolder(View itemView) {
                super(itemView);
                this.view = itemView;
            }

            public void bindData(final RecentPropertyProjectManager.DataObject dataObject) {
                ((FadeInNetworkImageView)view.findViewById(R.id.iv_content)).setImageUrl(dataObject.imageUrl, MakaanNetworkClient.getInstance().getImageLoader());
                ((TextView)view.findViewById(R.id.tv_price_value)).setText(StringUtil.getDisplayPrice(dataObject.price));
                ((TextView)view.findViewById(R.id.tv_area)).setText(dataObject.addressLine1);
                ((TextView)view.findViewById(R.id.tv_locality)).setText(dataObject.addressLine2);
                if(!TextUtils.isEmpty(dataObject.phoneNo)) {
                    view.findViewById(R.id.txt_get_call_back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
                            try {
                                intent.putExtra("name", dataObject.sellerName);
                                intent.putExtra("score", String.valueOf(dataObject.rating));
                                intent.putExtra("phone", "9090909090");//todo: not available in pojo
                                intent.putExtra("id", String.valueOf(dataObject.sellerId));
                                getActivity().startActivity(intent);
                            } catch (NullPointerException npe) {
                                Toast.makeText(getActivity(), "Seller data not available", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Seller data not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }
    }
}
