package com.makaan.fragment.locality;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.agents.callback.TopAgentsCallback;
import com.makaan.event.locality.OnNearByLocalityClickEvent;
import com.makaan.event.locality.OnTopAgentClickEvent;
import com.makaan.event.locality.OnTopBuilderClickEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.locality.ListingAggregation;
import com.makaan.response.locality.Locality;
import com.makaan.response.project.Builder;
import com.makaan.service.AgentService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.DateUtil;
import com.makaan.util.ImageUtils;
import com.makaan.util.LocalityUtil;
import com.makaan.util.StringUtil;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;


/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class NearByLocalitiesFragment extends MakaanBaseFragment implements View.OnClickListener{
    private LinearLayoutManager mLayoutManager;
    private NearByLocalitiesAdapter mAdapter;
    private String title;
    private int placeholderResource;
    @Bind(R.id.ll_locality_nearby)
    public LinearLayout switchLl ;
    @Bind(R.id.switch_top_agents)
    public Switch switchPrimarySecondary;
    @Bind(R.id.rv_nearby_localities)
    public RecyclerView mRecyclerView ;
    @Bind(R.id.tv_nearby_localities_title)
    public TextView titleTv ;
    @Bind(R.id.seperator_view)
    View seperatorView;


    Long citId,localitId;
    CardType cardType;
    private List<NearByLocalities> nearByLocalities;
    private boolean isRentSelected;
    private String viewDetailText;
    private int topSellersSeen=0;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_nearby_localities;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        title = getArguments().getString("title");
        placeholderResource = getArguments().getInt("placeholder");
        viewDetailText = getArguments().getString("action");
//        switchLl.setVisibility(R.drawable.placeholder_agent == placeholderResource ? View.VISIBLE : View.GONE);
        if(title != null) {
            titleTv.setText(title.toLowerCase());
        }
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new scrollChange());
        if(this.cardType == CardType.TOPAGENTS)
            seperatorView.setVisibility(View.GONE);
        initSwitch();
    }

    private void initSwitch() {
        if(switchPrimarySecondary!=null)
        switchPrimarySecondary.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                ((AgentService) MakaanServiceFactory.getInstance().getService(AgentService.class)).getTopAgentsForLocality(citId, localitId, 10, isChecked, new TopAgentsCallback() {
                    @Override
                    public void onTopAgentsRcvd(ArrayList<TopAgent> topAgents) {
                        isRentSelected = isChecked;
                        setData(getDarForAgents(topAgents));
                    }
                });
            }
        });
    }

    public void setData(List<NearByLocalities> nearByLocalities){
        this.nearByLocalities = nearByLocalities;
        mAdapter = new NearByLocalitiesAdapter(nearByLocalities,this);
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(mAdapter);
    }

    public void setNearByLocalityData(ArrayList<Locality> nearbyLocalities) {
        setData(getDataForNearByLocalities(nearbyLocalities));
    }
    private List<NearByLocalitiesFragment.NearByLocalities> getDataForNearByLocalities(ArrayList<Locality> nearbyLocalities) {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        this.cardType = CardType.LOCALITY;
        for(Locality locality:nearbyLocalities){
            int[] counts = getCountOfNumberOfListingsBasedOnType(locality.listingAggregations);
            int[] medians = calculateMedianForListings(locality.listingAggregations);
            nearByLocalities.add(new NearByLocalitiesFragment.NearByLocalities(""+locality.localityHeroshotImageUrl,"" + counts[1],"" + counts[0],medians[0] == 0?"":"average price: "+ StringUtil.getFormattedNumber(medians[0])+"/ sq ft",locality.label, locality.localityId));
        }
        return nearByLocalities;
    }

    private int[] getCountOfNumberOfListingsBasedOnType(ArrayList<ListingAggregation> listingAggregations) {
        int[] counts = new int[2];
        int countRent = 0, countPrimary = 0;
        for(ListingAggregation listingAggregation: listingAggregations){
            if(listingAggregation.listingCategory.equalsIgnoreCase("primary") ||
                    listingAggregation.listingCategory.equalsIgnoreCase("resale")){
                countPrimary += listingAggregation.count;
            }else{
                countRent += listingAggregation.count;
            }
        }
        counts[0] = countPrimary;
        counts[1] = countRent;
        return counts;
    }

    private int[] calculateMedianForListings(ArrayList<ListingAggregation> listingAggregations) {
        int[] counts = new int[2];
            counts[0] = LocalityUtil.calculateAveragePrice(listingAggregations).intValue();
            counts[1] = LocalityUtil.calculateRentalPrice(listingAggregations).intValue();

        return counts;
    }

    public void setDataForTopAgents( Long cityId,  Long localityId,ArrayList<TopAgent> topAgents) {
        setData(getDarForAgents(topAgents));
        this.citId =cityId;
        this.localitId = localityId;

    }

    private List<NearByLocalities> getDarForAgents(ArrayList<TopAgent> topAgents) {
        List<NearByLocalities> nearByLocalities = new ArrayList<>();
        this.cardType = CardType.TOPAGENTS;
        for(TopAgent agent:topAgents){
            if(agent.agent != null && agent.agent.company != null && agent.agent.user != null) {
                if (!TextUtils.isEmpty(agent.agent.company.logo)) {
                    nearByLocalities.add(new NearByLocalities(agent.agent.company.logo, "0", "" + agent.listingCount, "" + agent.agent.company.name, "" + agent.agent.user.fullName, agent.agent.company.id));
                } else if (!TextUtils.isEmpty(agent.agent.user.profilePictureURL)) {
                    nearByLocalities.add(new NearByLocalities(agent.agent.user.profilePictureURL, "0", "" + agent.listingCount, "" + agent.agent.company.name, "" + agent.agent.user.fullName, agent.agent.company.id));
                } else {
                    nearByLocalities.add(new NearByLocalities("", "0", "" + agent.listingCount, "" + agent.agent.company.name, "" + agent.agent.user.fullName, agent.agent.company.id));
                }
            } else {
                nearByLocalities.add(new NearByLocalities("", "0", "" + agent.listingCount, "" + agent.agent.company.name, "" + agent.agent.user.fullName, agent.agent.company.id));
            }
        }
        return nearByLocalities;
    }

    public void setDataForTopBuilders(ArrayList<Builder> builders) {
        setData(getDataForTopBuilder(builders));
    }

    private List<NearByLocalities> getDataForTopBuilder(ArrayList<Builder> builders) {
        List<NearByLocalities> nearByLocalities = new ArrayList<>();
        this.cardType = CardType.TOPBUILDERS;
        String url = "";
        for(Builder builder: builders){
            if(builder.images !=null && builder.images.size() >0){
                url = builder.images.get(0).absolutePath;
            }
            if(builder.establishedDate!=null) {
                Date date = new Date(Long.parseLong(builder.establishedDate));
                int experience = DateUtil.getDiffYears(date, new Date());
                nearByLocalities.add(new NearByLocalities(url, "0", "" + builder.projectCount.intValue(), "experience : " + experience+" years", "" + builder.name, builder.id));
            }else{
                nearByLocalities.add(new NearByLocalities(url, "0", "" + builder.projectCount.intValue(), "", "" + builder.name, builder.id));
            }
            }

        return nearByLocalities;
    }

    @Override
    public void onClick(View v) {
        Integer position = (Integer) v.getTag();
        if(nearByLocalities!=null){
            switch (cardType){
                case LOCALITY:
                    Properties properties = MakaanEventPayload.beginBatch();
                    int pos=position+1;
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    properties.put(MakaanEventPayload.LABEL, nearByLocalities.get(position).id+"_"+pos);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityNearbyLocalities);

                    AppBus.getInstance().post(new OnNearByLocalityClickEvent(nearByLocalities.get(position).id));
                    break;
                case TOPAGENTS:
                    Properties property = MakaanEventPayload.beginBatch();
                    topSellersSeen=topSellersSeen+1;
                    int posn=position+1;
                    if(switchPrimarySecondary.isChecked()) {
                        property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                        property.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.rent.getValue()+"_"+nearByLocalities.get(position).id + "_" + posn);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopSellers);
                    }
                    else{
                        property.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                        property.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.buy.getValue()+"_"+nearByLocalities.get(position).id + "_" + posn);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopSellers);
                    }
                    AppBus.getInstance().post(new OnTopAgentClickEvent(nearByLocalities.get(position).id));
                    break;
                case TOPBUILDERS:
                    Properties propert = MakaanEventPayload.beginBatch();
                    int posin=position+1;
                    propert.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                    propert.put(MakaanEventPayload.LABEL, nearByLocalities.get(position).id+"_"+posin);
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopBuilders);

                    AppBus.getInstance().post(new OnTopBuilderClickEvent(nearByLocalities.get(position).id));
                    break;
            }
        }
    }

    private class NearByLocalitiesAdapter extends RecyclerView.Adapter<NearByLocalitiesAdapter.ViewHolder> {
        private final View.OnClickListener onClickListener;
        private List<NearByLocalities> nearByLocalitiesList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View view;
            public TextView nameTv;
            public TextView medianTv;
            public TextView numberOfPropsForSaleTv;
            public TextView numberOfPropsForRentTv;
            public FadeInNetworkImageView localityIv;
            public LinearLayout rentLl;
            public CardView cardView;
            public TextView primarySaleLabelTv;
            public TextView secondarySaleLabelTv;
            public TextView viewDetails;
            public ViewHolder(View v) {
                super(v);
                this.view = v;
                nameTv = (TextView) v.findViewById(R.id.tv_nearby_localities_name);
                medianTv = (TextView) v.findViewById(R.id.tv_nearby_localities_median);
                numberOfPropsForSaleTv = (TextView) v.findViewById(R.id.tv_nearby_localities_sale);
                numberOfPropsForRentTv = (TextView) v.findViewById(R.id.tv_nearby_localities_rent);
                localityIv = (FadeInNetworkImageView) v.findViewById(R.id.iv_nearby_locality);
                rentLl = (LinearLayout) v.findViewById(R.id.ll_nearby_locality_rent);
                primarySaleLabelTv = (TextView) v.findViewById(R.id.tv_nearby_localities_sale_label);
                secondarySaleLabelTv = (TextView) v.findViewById(R.id.tv_nearby_localities_secondary_sale_label);
                viewDetails = (TextView) v.findViewById(R.id.tv_nearby_localities_view_details);
                cardView = (CardView) v.findViewById(R.id.card_view_nearby_locality);
                cardView.setOnClickListener(onClickListener);
            }
        }

        public NearByLocalitiesAdapter(List<NearByLocalities> nearByLocalitiesList,View.OnClickListener onClickListener) {
            this.nearByLocalitiesList = nearByLocalitiesList;
            this.onClickListener = onClickListener;
        }

        @Override
        public NearByLocalitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_nearby_localities, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final NearByLocalities nearByLocality = nearByLocalitiesList.get(position);
            if(!TextUtils.isEmpty(nearByLocality.medianPrice)) {
                holder.medianTv.setText(nearByLocality.medianPrice.equalsIgnoreCase("null") ? "" : nearByLocality.medianPrice.toLowerCase());
                holder.medianTv.setVisibility(View.VISIBLE);
            } else {
                holder.medianTv.setVisibility(View.INVISIBLE);
            }
            if(!TextUtils.isEmpty(nearByLocality.localityName)) {
                holder.nameTv.setText(nearByLocality.localityName.toLowerCase());
                holder.nameTv.setVisibility(View.VISIBLE);
            } else {
                holder.nameTv.setVisibility(View.INVISIBLE);
            }
            if((TextUtils.isEmpty(nearByLocality.numberOfPropsForSale) || nearByLocality.numberOfPropsForSale.equalsIgnoreCase("0"))
                    && !TextUtils.isEmpty(nearByLocality.numberOfPropsForRent) && !nearByLocality.numberOfPropsForRent.equalsIgnoreCase("0")) {
                holder.numberOfPropsForSaleTv.setText(nearByLocality.numberOfPropsForRent.toLowerCase());
                holder.numberOfPropsForRentTv.setVisibility(View.INVISIBLE);
                holder.numberOfPropsForSaleTv.setVisibility(View.VISIBLE);
            } else {
                if (!TextUtils.isEmpty(nearByLocality.numberOfPropsForSale) && !nearByLocality.numberOfPropsForSale.equalsIgnoreCase("0")) {
                    holder.numberOfPropsForSaleTv.setText(nearByLocality.numberOfPropsForSale.toLowerCase());
                    holder.numberOfPropsForSaleTv.setVisibility(View.VISIBLE);
                } else {
                    holder.numberOfPropsForSaleTv.setVisibility(View.INVISIBLE);
                }
                if (!TextUtils.isEmpty(nearByLocality.numberOfPropsForRent) && !nearByLocality.numberOfPropsForRent.equalsIgnoreCase("0")) {
                    holder.numberOfPropsForRentTv.setText(nearByLocality.numberOfPropsForRent.toLowerCase());
                    holder.numberOfPropsForRentTv.setVisibility(View.VISIBLE);
                } else {
                    holder.numberOfPropsForRentTv.setVisibility(View.INVISIBLE);
                }
                if (!TextUtils.isEmpty(viewDetailText)) {
                    holder.viewDetails.setText(viewDetailText.toLowerCase());
                    holder.viewDetails.setVisibility(View.VISIBLE);
                } else {
                    holder.viewDetails.setVisibility(View.INVISIBLE);
                }
            }
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.view.getLayoutParams();
            if(params != null) {
                if (position == getItemCount() - 1) {
                    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.row_nearby_localities_right_margin);
                } else {
                    params.rightMargin = 0;
                }
            }

            holder.cardView.setTag(position);
            switch (cardType){
                case LOCALITY:
                    holder.rentLl.setVisibility(View.VISIBLE);
                    removeZeroData(holder, nearByLocality);
                    break;
                case TOPAGENTS:
                    holder.localityIv.setDefaultImageResId(R.drawable.agent_image_placeholder);
                    if(isRentSelected)
                        holder.primarySaleLabelTv.setText("properties for rent");
                    else
                        holder.primarySaleLabelTv.setText("properties for sale");
                    holder.rentLl.setVisibility(View.GONE);
                    break;
                case TOPBUILDERS:
                    holder.localityIv.setDefaultImageResId(R.drawable.builder_logo_placeholder);
                    holder.rentLl.setVisibility(View.VISIBLE);
                    holder.rentLl.setVisibility(View.GONE);
                    holder.primarySaleLabelTv.setText("total projects");
                    break;
            }

            if(nearByLocality.imgUrl!=null) {
                int width = getResources().getDimensionPixelSize(R.dimen.row_nearby_localities_width);
                int height = getResources().getDimensionPixelSize(R.dimen.row_nearby_localities_image_height);
                holder.localityIv.setImageUrl(ImageUtils.getImageRequestUrl(nearByLocality.imgUrl, width, height, true), MakaanNetworkClient.getInstance().getImageLoader());
                /*MakaanNetworkClient.getInstance().getImageLoader().get(nearByLocality.imgUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        final Bitmap image = imageContainer.getBitmap();
                        holder.localityIv.setImageBitmap(image);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });*/
            }
        }

        private void removeZeroData(ViewHolder holder, NearByLocalities nearByLocality) {
            if (!TextUtils.isEmpty(nearByLocality.numberOfPropsForSale) && !nearByLocality.numberOfPropsForSale.equalsIgnoreCase("0")) {
                holder.primarySaleLabelTv.setText("properties for sale");
                holder.primarySaleLabelTv.setVisibility(View.VISIBLE);
            } else if (!TextUtils.isEmpty(nearByLocality.numberOfPropsForRent) && !nearByLocality.numberOfPropsForRent.equalsIgnoreCase("0")) {
                holder.primarySaleLabelTv.setText("properties for rent");
                holder.primarySaleLabelTv.setVisibility(View.VISIBLE);
            } else {
                holder.primarySaleLabelTv.setText("");
                holder.numberOfPropsForSaleTv.setText("");
            }

            if (!TextUtils.isEmpty(nearByLocality.numberOfPropsForRent)
                    && !nearByLocality.numberOfPropsForRent.equalsIgnoreCase("0")
                    && !TextUtils.isEmpty(nearByLocality.numberOfPropsForSale)
                    && !nearByLocality.numberOfPropsForSale.equalsIgnoreCase("0")) {
                holder.secondarySaleLabelTv.setText("properties for rent");
                holder.secondarySaleLabelTv.setVisibility(View.VISIBLE);
            } else {
                holder.secondarySaleLabelTv.setVisibility(View.INVISIBLE);
                holder.secondarySaleLabelTv.setText("");
                holder.numberOfPropsForRentTv.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return nearByLocalitiesList.size();
        }

    }
    public static class NearByLocalities {
        String imgUrl;
        String localityName;
        String medianPrice;
        Long id;

        public NearByLocalities(String imgUrl, String numberOfPropsForRent, String numberOfPropsForSale, String medianPrice, String localityName, Long id) {
            this.imgUrl = imgUrl;
            this.numberOfPropsForRent = numberOfPropsForRent;
            this.numberOfPropsForSale = numberOfPropsForSale;
            this.medianPrice = medianPrice;
            this.localityName = localityName;
            this.id = id;
        }

        String numberOfPropsForSale;
        String numberOfPropsForRent;
    }

    public enum CardType{
        LOCALITY, TOPAGENTS, TOPBUILDERS
    }

    private class scrollChange extends RecyclerView.OnScrollListener {
        int flingCoordinate;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState==RecyclerView.SCROLL_STATE_IDLE){
                switch (cardType){
                    case LOCALITY:{
                        if(flingCoordinate>0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.right);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityNearbyLocalities);
                        }
                        else if(flingCoordinate<0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.left);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityNearbyLocalities);
                        }
                        break;
                    }
                    case TOPAGENTS:{
                        if(flingCoordinate>0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.right);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopSellers);
                        }
                        else if(flingCoordinate<0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerLocality);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.left);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopSellers);
                        }
                        break;
                    }
                    case TOPBUILDERS:{
                        if(flingCoordinate>0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.right);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopBuilders);
                        }
                        else if(flingCoordinate<0){
                            Properties properties = MakaanEventPayload.beginBatch();
                            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.left);
                            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickLocalityTopBuilders);
                        }
                        break;
                    }

                }

            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            flingCoordinate=dx;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
