package com.makaan.activity.shortlist;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.util.ErrorUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.RecentPropertyProjectManager;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListRecentFragment extends MakaanBaseFragment {

    @Bind(R.id.favorite_recycler_view)
    RecyclerView favoriteRecyclerView;

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

        if(entries == null || entries.size() == 0) {
            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, false));
            favoriteRecyclerView.setVisibility(View.GONE);
        } else {
            showContent();
            favoriteRecyclerView.setVisibility(View.VISIBLE);
            RecentAdapter adapter = new RecentAdapter();
            adapter.setData(entries);
            favoriteRecyclerView.setAdapter(adapter);
            if(mCallback != null) {
                mCallback.updateCount(mPosition, entries.size());
            }
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
                int height = (int)Math.ceil(getResources().getDimension(R.dimen.fav_card_height));
                int width = (int) (getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
                ((FadeInNetworkImageView)view.findViewById(R.id.iv_content)).setDefaultImageResId(R.drawable.property_placeholder);
                ((FadeInNetworkImageView)view.findViewById(R.id.iv_content)).setImageUrl(ImageUtils.getImageRequestUrl(dataObject.imageUrl, width, height, false),
                        MakaanNetworkClient.getInstance().getImageLoader());
                ((TextView)view.findViewById(R.id.tv_price_value)).setText(StringUtil.getDisplayPrice(dataObject.price));
                if(dataObject.addressLine1 != null) {
                    ((TextView) view.findViewById(R.id.tv_area)).setText(dataObject.addressLine1.toLowerCase());
                }
                if(dataObject.addressLine2 != null) {
                    ((TextView) view.findViewById(R.id.tv_locality)).setText(dataObject.addressLine2.toLowerCase());
                }
                // TODO check below statement
//                if(!TextUtils.isEmpty(dataObject.phoneNo)) {
                if(!TextUtils.isEmpty(dataObject.sellerName)) {
                    view.findViewById(R.id.txt_get_call_back).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*--------------------------- track events--------------------------------------*/
                            if(dataObject.id!=0) {
                                Properties properties = MakaanEventPayload.beginBatch();
                                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", dataObject.id,
                                        MakaanTrackerConstants.Label.getCallBack));
                                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickShortListRecentlyViewed);
                            }
                            /*------------------------------------------------------*/

                            Intent intent = new Intent(getActivity(), LeadFormActivity.class);
                            try {
                                intent.putExtra(KeyUtil.NAME_LEAD_FORM, dataObject.sellerName);
                                intent.putExtra(KeyUtil.SCORE_LEAD_FORM, String.valueOf(dataObject.rating));
                                intent.putExtra(KeyUtil.PHONE_LEAD_FORM, dataObject.phoneNo);//todo: not available in pojo
                                intent.putExtra(KeyUtil.SINGLE_SELLER_ID, String.valueOf(dataObject.sellerId));
                                intent.putExtra(KeyUtil.CITY_NAME_LEAD_FORM, dataObject.cityName);
                                intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, dataObject.listingCategory);
                                intent.putExtra(KeyUtil.USER_ID, dataObject.userId);

                                if(dataObject.listingCategory!=null && !TextUtils.isEmpty(dataObject.listingCategory)){
                                    if(dataObject.listingCategory.equalsIgnoreCase("primary")||dataObject.listingCategory.equalsIgnoreCase("resale")){
                                        intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "buy");
                                    }
                                    else if(dataObject.listingCategory.equalsIgnoreCase("rental")){
                                        intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, "rent");
                                    }
                                }
                                if(dataObject.cityId != 0) {
                                    intent.putExtra(KeyUtil.CITY_ID_LEAD_FORM, dataObject.cityId);
                                }
                                getActivity().startActivity(intent);
                            } catch (NullPointerException npe) {
                                Toast.makeText(getActivity(), "Seller data not available", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Seller data not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
//                }
            }
        }
    }
}
