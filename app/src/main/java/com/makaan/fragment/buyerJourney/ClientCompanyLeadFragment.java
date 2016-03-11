package com.makaan.fragment.buyerJourney;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.event.listing.ListingByIdsGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.response.buyerjourney.Company;
import com.makaan.response.listing.detail.ListingDetail;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.ImageUtils;
import com.makaan.util.StringUtil;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientCompanyLeadFragment extends MakaanBaseFragment {
    public final static String COMPANY_ID = "company_id";
    public final static String LISTING_IDS = "listing_ids";

    @Bind(R.id.fragment_client_company_leads_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.fragment_client_company_leads_next_button)
    Button mNextButton;

    ClientCompanyLeadsAdapter mAdapter;
    private int mSelected = 0;
    private ArrayList<ListingByIdsGetEvent.Listing> mItems;
    private ClientCompanyLeadsObject mObj;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_client_company_leads;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mNextButton.setEnabled(false);
        showProgress();

        Bundle bundle = getArguments();
        if(bundle != null) {
            Long companyId = bundle.getLong(COMPANY_ID);
            ArrayList<String> listingIds = bundle.getStringArrayList(LISTING_IDS);

            if(companyId > 0) {
                ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).getListingDetailByIds(listingIds);
            }
        } else {
            // TODO
        }

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mAdapter = new ClientCompanyLeadsAdapter();

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @OnClick(R.id.fragment_client_company_leads_next_button)
    void onNextClicked(View view) {
        if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mItems != null && mSelected < mItems.size()) {
                if(mObj != null) {
                    mObj.listingDetail = mItems.get(mSelected).listing;
                    ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                            true, null, null, mObj);
                }
            }
        }
    }

    public class ClientCompanyLeadsObject {
        public ClientLeadsFragment.ClientLeadsObject clientLeadObject;
        public ListingDetail listingDetail;
    }

    @Subscribe
    public void onResults(ListingByIdsGetEvent listingByIdsGetEvent) {
        if(listingByIdsGetEvent == null || listingByIdsGetEvent.error != null) {
            mAdapter.setData(new ArrayList<ListingByIdsGetEvent.Listing>());
            showContent();
            return;
        }
        mItems = listingByIdsGetEvent.items;
        mAdapter.setData(listingByIdsGetEvent.items);
        mNextButton.setEnabled(true);
        showContent();
    }

    public void setData(Object obj) {
        if(obj instanceof ClientLeadsFragment.ClientLeadsObject) {
            mObj = new ClientCompanyLeadsObject();
            mObj.clientLeadObject = (ClientLeadsFragment.ClientLeadsObject)obj;
        }
    }

    class ClientCompanyLeadsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_LISTING = 1;
        private static final int TYPE_ADD = 2;

        ArrayList<ListingByIdsGetEvent.Listing> mItems;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_LISTING) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.client_company_leads_item_layout, parent, false);
                return new ViewHolder(view, viewType);
            } else if(viewType == TYPE_ADD) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.client_company_leads_add_item_layout, parent, false);
                return new ViewHolder(view, viewType);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position < mItems.size()) {
                ((ViewHolder) holder).setPosition(position);
                ((ViewHolder) holder).bindData(mItems.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            if(mItems == null) {
                return 0;
            }
            return mItems.size() + 1;
        }

        public void setData(ArrayList<ListingByIdsGetEvent.Listing> items) {
            if(mItems == null) {
                mItems = new ArrayList<>();
            } else {
                mItems.clear();
            }

            mItems.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(mItems != null) {
                if (position < mItems.size()) {
                    return TYPE_LISTING;
                } else if (position == mItems.size()) {
                    return TYPE_ADD;
                }
            }
            return TYPE_LISTING;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
            private final int viewType;
            FadeInNetworkImageView imageView;
            TextView addressTextView;
            TextView projectNameTextView;
            TextView priceTextView;
            RadioButton radioButton;
            private int position;

            public ViewHolder(View itemView, int viewType) {
                super(itemView);
                itemView.setOnClickListener(this);
                this.viewType = viewType;
                /*itemView.setOnTouchListener(this);*/

                if(viewType == TYPE_LISTING) {
                    imageView = (FadeInNetworkImageView) itemView.findViewById(R.id.buyer_dashboard_listing_view_image_view);
                    addressTextView = (TextView) itemView.findViewById(R.id.buyer_dashboard_listing_view_address_text_view);
                    projectNameTextView = (TextView) itemView.findViewById(R.id.buyer_dashboard_listing_view_project_text_view);
                    priceTextView = (TextView) itemView.findViewById(R.id.buyer_dashboard_listing_view_price_text_view);
                    radioButton = (RadioButton) itemView.findViewById(R.id.client_company_leads_item_layout_radio_button);
                    radioButton.setOnCheckedChangeListener(this);
                }
            }

            @Override
            public void onClick(View v) {
                if(viewType == TYPE_LISTING) {
                    mSelected = position;
                    notifyDataSetChanged();
                } else if(viewType == TYPE_ADD) {
                    if(getActivity() instanceof BuyerDashboardCallbacks) {
                        if(mObj != null) {
//                            mObj.listingDetail = mItems.get(position).listing;
                            ((BuyerDashboardCallbacks) getActivity()).loadFragment(
                                    BuyerDashboardActivity.LOAD_FRAGMENT_CLIENT_COMPANY_LEAD_ADD_PROPERTY,
                                    true, null, null, mObj);
                        }
                    }
                }
            }

            /*@Override
            public boolean onTouch(View v, MotionEvent event) {
                radioButton.dispatchTouchEvent(event);
                return true;
            }*/

            public void setPosition(int position) {
                this.position = position;
            }

            public void bindData(ListingByIdsGetEvent.Listing listingDetail, int position) {
                if(viewType != TYPE_LISTING) {
                    return;
                }
                if(listingDetail != null && listingDetail.listing != null) {
                    if(!TextUtils.isEmpty(listingDetail.listing.mainImageURL)) {
                        int width = (int) (getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
                        int height = (int)Math.ceil(getResources().getDimension(R.dimen.buyer_dashboard_listing_view_height));
                        imageView.setDefaultImageResId(R.drawable.property_placeholder);
                        imageView.setImageUrl(ImageUtils.getImageRequestUrl(listingDetail.listing.mainImageURL, width, height, false),
                                MakaanNetworkClient.getInstance().getImageLoader());
                    }

                    if(listingDetail.listing.currentListingPrice != null) {
                        priceTextView.setText(StringUtil.getDisplayPrice(listingDetail.listing.currentListingPrice.price));
                    }

                    if(listingDetail.listing.property != null && listingDetail.listing.property.project != null) {
                        if(listingDetail.listing.property.project.name != null) {
                            projectNameTextView.setText(listingDetail.listing.property.project.name.toLowerCase());
                        }

                        if(listingDetail.listing.property.project.locality != null
                                && listingDetail.listing.property.project.locality.suburb != null
                                && listingDetail.listing.property.project.locality.suburb.city != null) {
                            addressTextView.setText(String.format("%s, %s", listingDetail.listing.property.project.locality.label, listingDetail.listing.property.project.locality.suburb.city.label).toLowerCase());
                        }
                    }
                }
                radioButton.setOnCheckedChangeListener(null);
                radioButton.setChecked(position == mSelected);
                radioButton.setOnCheckedChangeListener(this);
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSelected = position;
                notifyDataSetChanged();
            }
        }
    }
}
