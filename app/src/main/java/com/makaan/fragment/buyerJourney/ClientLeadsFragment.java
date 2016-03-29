package com.makaan.fragment.buyerJourney;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
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

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.activity.pyr.PyrPageActivity;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.response.buyerjourney.Company;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppUtils;
import com.makaan.util.CommonUtil;
import com.makaan.util.ErrorUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientLeadsFragment extends MakaanBaseFragment {
    @Bind(R.id.fragment_client_leads_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.fragment_client_leads_next_button)
    Button mNextButton;

    @Bind(R.id.fragment_client_leads_get_best_sellers_layout)
    View mGetBestSellersLayout;
    @Bind(R.id.fragment_client_leads_with_agent_layout)
    View mWithAgentLayout;

    ArrayList<ClientLeadsObject> mClientLeadsObjects;
    private ClientLeadsAdapter mAdapter;

    int mSelected = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_client_leads;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mNextButton.setEnabled(false);

        ((ClientLeadsService)MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeads();
        showProgress();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mAdapter = new ClientLeadsAdapter();
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @OnClick(R.id.fragment_client_leads_get_best_sellers_button)
    void onBestSellerClicked(View view) {
        Intent pyrIntent = new Intent(getActivity(), PyrPageActivity.class);
        pyrIntent.putExtra(PyrPageActivity.SOURCE_SCREEN_NAME, ((BuyerDashboardActivity) getActivity()).getScreenName());
        getActivity().startActivity(pyrIntent);
    }

    @OnClick(R.id.fragment_client_leads_next_button)
    void onNextClicked(View view) {
        if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mSelected < mClientLeadsObjects.size()) {

                /*----------------------- track events-------------------------*/
                if(mClientLeadsObjects.get(mSelected).clientLead!=null &&  mClientLeadsObjects.get(mSelected).clientLead.companyId!=null) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                    properties.put(MakaanEventPayload.LABEL, mClientLeadsObjects.get(mSelected).clientLead.companyId);
                    MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickCashBackSeller);
                }
                /*-----------------------------------------------------------------*/

                Bundle bundle = new Bundle();
                bundle.putLong(ClientCompanyLeadFragment.COMPANY_ID, mClientLeadsObjects.get(mSelected).clientLead.companyId);
                ArrayList<Long> listingIds = new ArrayList<>();
                for(ClientLead.PropertyRequirement requirement : mClientLeadsObjects.get(mSelected).clientLead.propertyRequirements) {
                    listingIds.add(requirement.listingId);
                }
                Object[] array = listingIds.toArray();
                ArrayList<String> finalArray = new ArrayList<>();
                for(int i = 0; i < array.length; i++) {
                    if(array[i] != null) {
                        finalArray.add(String.valueOf(array[i]));
                    }
                }
                bundle.putStringArrayList(ClientCompanyLeadFragment.LISTING_IDS, finalArray);
                ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CLIENT_COMPANY_LEAD,
                        true, bundle, null, mClientLeadsObjects.get(mSelected));
            }
        }
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(clientLeadsByGetEvent == null || clientLeadsByGetEvent.error != null) {
            if(clientLeadsByGetEvent != null && !TextUtils.isEmpty(clientLeadsByGetEvent.error.msg)) {
                showNoResults(clientLeadsByGetEvent.error.msg);
            } else {
                showNoResults("");
            }
            return;
        }

        mClientLeadsObjects = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        if(clientLeadsByGetEvent.results != null && clientLeadsByGetEvent.results.size() > 0) {
            for (ClientLead lead : clientLeadsByGetEvent.results) {
                mClientLeadsObjects.add(new ClientLeadsObject(lead));
                ids.add(lead.companyId);
            }

            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadCompanies(ids);
        } else {
            showNoResults("");
        }
    }

    @Subscribe
    public void onResults(ArrayList<Company> companies) {
        if(!isVisible()) {
            return;
        }
        if(companies == null || companies.size() == 0) {
            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT));
            return;
        }

        for(ClientLeadsObject obj : mClientLeadsObjects) {
            obj.selectAndAddCompany(companies);
        }
        mAdapter.addData(mClientLeadsObjects);
        mNextButton.setEnabled(true);
        showContent();
    }

    public void setData(Object obj) {

    }

    public class ClientLeadsObject {
        public ClientLead clientLead;
        public Company company;

        public ClientLeadsObject(ClientLead lead) {
            this.clientLead = lead;
        }

        public void selectAndAddCompany(ArrayList<Company> companies) {
            if(companies != null) {
                for (Company company : companies) {
                    if (company.id.equals(clientLead.companyId)) {
                        this.company = company;
                        return;
                    }
                }
            }
        }
    }

    class ClientLeadsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ClientLeadsObject> clientLeadObjects;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.client_leads_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).setPosition(position);
            ((ViewHolder)holder).bindData(clientLeadObjects.get(position), position);
        }

        @Override
        public int getItemCount() {
            if(clientLeadObjects == null) {
                return 0;
            }
            return clientLeadObjects.size();
        }

        public void addData(ArrayList<ClientLeadsObject> clientLeadsObjects) {
            if(this.clientLeadObjects == null) {
                this.clientLeadObjects = new ArrayList<>();
            } else {
                this.clientLeadObjects.clear();
            }
            this.clientLeadObjects.addAll(clientLeadsObjects);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
            private final TextView logoTextView;
            ImageView imageView;
            TextView nameTextView;
            TextView companyNameTextView;
            RadioButton radioButton;
            private int position;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                /*itemView.setOnTouchListener(this);*/

                imageView = (ImageView)itemView.findViewById(R.id.client_leads_item_layout_image_view);
                logoTextView = (TextView)itemView.findViewById(R.id.client_leads_item_layout_logo_text_view);
                nameTextView = (TextView)itemView.findViewById(R.id.client_leads_item_layout_name_text_view);
                companyNameTextView = (TextView)itemView.findViewById(R.id.client_leads_item_layout_company_text_view);
                radioButton = (RadioButton)itemView.findViewById(R.id.client_leads_item_layout_radio_button);
                radioButton.setOnCheckedChangeListener(this);
            }

            @Override
            public void onClick(View v) {
                mSelected = position;
                notifyDataSetChanged();
            }

            /*@Override
            public boolean onTouch(View v, MotionEvent event) {
                radioButton.dispatchTouchEvent(event);
                return true;
            }*/

            public void setPosition(int position) {
                this.position = position;
            }

            public void bindData(ClientLeadsObject clientLeadsObject, int position) {
                imageView.setVisibility(View.GONE);
                if(clientLeadsObject.company.name != null) {
                    nameTextView.setText(clientLeadsObject.company.name.toLowerCase());
                }
                companyNameTextView.setText(AppUtils.getDDMMMYYDateStringFromEpoch(String.valueOf(clientLeadsObject.clientLead.createdAt)).toLowerCase());
                radioButton.setOnCheckedChangeListener(null);
                radioButton.setChecked(position == mSelected);
                radioButton.setOnCheckedChangeListener(this);

                if(!TextUtils.isEmpty(clientLeadsObject.company.name)) {
                    logoTextView.setText(String.valueOf(clientLeadsObject.company.name.charAt(0)));
                }
                logoTextView.setVisibility(View.VISIBLE);

//                    int[] bgColorArray = getResources().getIntArray(R.array.bg_colors);

//                    Random random = new Random();
                ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
//                    drawable.getPaint().setColor(bgColorArray[random.nextInt(bgColorArray.length)]);
                drawable.getPaint().setColor(CommonUtil.getColor(clientLeadsObject.company.name, getContext()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    logoTextView.setBackground(drawable);
                } else {
                    logoTextView.setBackgroundDrawable(drawable);
                }
            }

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSelected = position;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void showNoResults(String message) {
        super.showContent();
        mWithAgentLayout.setVisibility(View.GONE);
        mGetBestSellersLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void showContent() {
        super.showContent();
        mWithAgentLayout.setVisibility(View.VISIBLE);
        mGetBestSellersLayout.setVisibility(View.GONE);
    }

}
