package com.makaan.fragment.locality;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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
        switchLl.setVisibility(R.drawable.placeholder_agent == placeholderResource ? View.VISIBLE : View.GONE);
        titleTv.setText(title);
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
                nearByLocalities.add(new NearByLocalitiesFragment.NearByLocalities(""+locality.localityHeroshotImageUrl,""+(counts[1] == 0?"0":counts[1]+" +"),""+(counts[0] == 0?"0":counts[0]+" +"),medians[0] == 0?"":"median price: "+medians[0]+"/ sq ft",locality.label, locality.localityId));
        }
        return nearByLocalities;
    }

    private int[] getCountOfNumberOfListingsBasedOnType(ArrayList<ListingAggregation> listingAggregations) {
        int[] counts = new int[2];
        int countRent = 0, countPrimary = 0;
        for(ListingAggregation listingAggregation: listingAggregations){
            if(listingAggregation.listingCategory.equalsIgnoreCase("primary") ||
                    listingAggregation.listingCategory.equalsIgnoreCase("resale")){
                countPrimary++;
            }else{
                countRent++;
            }
        }
        counts[0] = countPrimary;
        counts[1] = countRent;
        return counts;
    }

    private int[] calculateMedianForListings(ArrayList<ListingAggregation> listingAggregations) {
        double rentalMedian = 0, saleMedian = 0;
        int countsRental = 0, countsSales = 0;
        int[] counts = new int[2];
        for (ListingAggregation ListingAggregation:listingAggregations) {
            if (ListingAggregation.listingCategory.equalsIgnoreCase("primary") ||
                    ListingAggregation.listingCategory.equalsIgnoreCase("resale")) {
                saleMedian = saleMedian + ListingAggregation.avgPricePerUnitArea;
                countsSales++;
            } else {
                rentalMedian = rentalMedian + ListingAggregation.avgPricePerUnitArea;
                countsRental++;
            }
        }
            if(countsSales!=0)
                saleMedian = saleMedian / countsSales;
            if(countsRental!=0)
                rentalMedian = rentalMedian / countsRental;

            counts[0] = (int) saleMedian;
            counts[1] = (int) rentalMedian;

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
            nearByLocalities.add(new NearByLocalities("","0",""+agent.listingCount,""+agent.agent.company.type,""+agent.agent.user.fullName,(long) agent.agent.company.id));
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
            public TextView nameTv;
            public TextView medianTv;
            public TextView numberOfPropsForSaleTv;
            public TextView numberOfPropsForRentTv;
            public ImageView localityIv;
            public LinearLayout rentLl;
            public CardView cardView;
            public TextView primarySaleLabelTv;
            public TextView secondarySaleLabelTv;
            public ViewHolder(View v) {
                super(v);
                nameTv = (TextView) v.findViewById(R.id.tv_nearby_localities_name);
                medianTv = (TextView) v.findViewById(R.id.tv_nearby_localities_median);
                numberOfPropsForSaleTv = (TextView) v.findViewById(R.id.tv_nearby_localities_sale);
                numberOfPropsForRentTv = (TextView) v.findViewById(R.id.tv_nearby_localities_rent);
                localityIv = (ImageView) v.findViewById(R.id.iv_nearby_locality);
                rentLl = (LinearLayout) v.findViewById(R.id.ll_nearby_locality_rent);
                primarySaleLabelTv = (TextView) v.findViewById(R.id.tv_nearby_localities_sale_label);
                secondarySaleLabelTv = (TextView) v.findViewById(R.id.tv_nearby_localities_secondary_sale_label);
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
            holder.medianTv.setText(nearByLocality.medianPrice.equalsIgnoreCase("null")?"":nearByLocality.medianPrice);
            holder.nameTv.setText(nearByLocality.localityName);
            holder.numberOfPropsForSaleTv.setText(nearByLocality.numberOfPropsForSale);
            holder.numberOfPropsForRentTv.setText(nearByLocality.numberOfPropsForRent);
            holder.cardView.setTag(position);
            switch (cardType){
                case LOCALITY:
                    holder.rentLl.setVisibility(View.VISIBLE);
                    removeZeroData(holder, nearByLocality);
                    break;
                case TOPAGENTS:
                    holder.localityIv.setImageResource(R.drawable.agent_image_placeholder);
                    if(isRentSelected)
                        holder.primarySaleLabelTv.setText("properties for rent");
                    else
                        holder.primarySaleLabelTv.setText("properties for sale");
                    holder.rentLl.setVisibility(View.GONE);
                    break;
                case TOPBUILDERS:
                    holder.localityIv.setImageResource(R.drawable.builder_logo_placeholder);
                    holder.rentLl.setVisibility(View.VISIBLE);
                    holder.rentLl.setVisibility(View.GONE);
                    holder.primarySaleLabelTv.setText("total projects");
                    break;
            }

            if(nearByLocality.imgUrl!=null)
            MakaanNetworkClient.getInstance().getImageLoader().get(nearByLocality.imgUrl, new ImageLoader.ImageListener() {
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
            });
        }

        private void removeZeroData(ViewHolder holder, NearByLocalities nearByLocality) {
            if(!nearByLocality.numberOfPropsForSale.equalsIgnoreCase("0"))
                holder.primarySaleLabelTv.setText("properties for sale");
            else {
                holder.primarySaleLabelTv.setText("");
                holder.numberOfPropsForSaleTv.setText("");
                 }

            if(!nearByLocality.numberOfPropsForRent.equalsIgnoreCase("0"))
                holder.secondarySaleLabelTv.setText("properties for rent");
            else {
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

}
