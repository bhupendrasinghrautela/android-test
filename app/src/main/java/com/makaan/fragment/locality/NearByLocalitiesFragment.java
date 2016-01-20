package com.makaan.fragment.locality;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.locality.ListingAggregation;
import com.makaan.response.locality.Locality;
import com.makaan.util.Blur;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class NearByLocalitiesFragment extends Fragment {
    private View view;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NearByLocalitiesAdapter mAdapter;
    private String title;
    private int placeholderResource;
    public LinearLayout switchLl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_localities,null);
        initView();
        return view;
    }

    private void initView() {
        title = getArguments().getString("title");
        placeholderResource = getArguments().getInt("placeholder");

        switchLl = (LinearLayout) view.findViewById(R.id.ll_locality_nearby);
        switchLl.setVisibility(R.drawable.placeholder_agent == placeholderResource ? View.VISIBLE : View.GONE);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_nearby_localities_title);
        titleTv.setText(title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_nearby_localities);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<NearByLocalities> nearByLocalities){
        mAdapter = new NearByLocalitiesAdapter(nearByLocalities);
        if(mRecyclerView!=null)
            mRecyclerView.setAdapter(mAdapter);
    }

    public void setNearByLocalityData(ArrayList<Locality> nearbyLocalities) {
        setData(getData(nearbyLocalities));
    }
    private List<NearByLocalitiesFragment.NearByLocalities> getData(ArrayList<Locality> nearbyLocalities) {
        List<NearByLocalitiesFragment.NearByLocalities> nearByLocalities = new ArrayList<>();
        for(Locality locality:nearbyLocalities){
            int[] counts = getCountOfNumberOfListingsBasedOnType(locality.listingAggregations);
            int[] medians = calculateMedianForListings(locality.listingAggregations);
            nearByLocalities.add(new NearByLocalitiesFragment.NearByLocalities(""+locality.localityHeroshotImageUrl,""+counts[1]+" +",""+counts[0]+" +","median price: "+medians[0]+"/ sq ft",locality.label));
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
        for (ListingAggregation ListingAggregation:listingAggregations){
            if(ListingAggregation.listingCategory.equalsIgnoreCase("primary") ||
                    ListingAggregation.listingCategory.equalsIgnoreCase("resale")){
                saleMedian = saleMedian + ListingAggregation.avgPricePerUnitArea;
                countsSales++;
            }else{
                rentalMedian = rentalMedian + ListingAggregation.avgPricePerUnitArea;
                countsRental++;
            }
            if(countsSales!=0)
                saleMedian = saleMedian / countsSales;
            if(countsRental!=0)
                rentalMedian = rentalMedian / countsRental;

            counts[0] = (int) saleMedian;
            counts[1] = (int) rentalMedian;
        }
        return counts;
    }


    private class NearByLocalitiesAdapter extends RecyclerView.Adapter<NearByLocalitiesAdapter.ViewHolder> {
        private List<NearByLocalities> nearByLocalitiesList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView nameTv;
            public TextView medianTv;
            public TextView numberOfPropsForSaleTv;
            public TextView numberOfPropsForRentTv;
            public TextView detailsTv;
            public ImageView localityIv;
            public ViewHolder(View v) {
                super(v);
                nameTv = (TextView) v.findViewById(R.id.tv_nearby_localities_name);
                medianTv = (TextView) v.findViewById(R.id.tv_nearby_localities_median);
                numberOfPropsForSaleTv = (TextView) v.findViewById(R.id.tv_nearby_localities_sale);
                numberOfPropsForRentTv = (TextView) v.findViewById(R.id.tv_nearby_localities_rent);
                detailsTv = (TextView) v.findViewById(R.id.tv_nearby_localities_detail);
                localityIv = (ImageView) v.findViewById(R.id.iv_nearby_locality);
            }
        }

        public NearByLocalitiesAdapter(List<NearByLocalities> nearByLocalitiesList) {
            this.nearByLocalitiesList = nearByLocalitiesList;
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
            holder.medianTv.setText(nearByLocality.medianPrice);
            holder.nameTv.setText(nearByLocality.localityName);
            holder.numberOfPropsForSaleTv.setText(nearByLocality.numberOfPropsForSale);
            holder.numberOfPropsForRentTv.setText(nearByLocality.numberOfPropsForRent);
            holder.localityIv.setImageResource(placeholderResource);

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

        @Override
        public int getItemCount() {
            return nearByLocalitiesList.size();
        }

    }
    public static class NearByLocalities {
        String imgUrl;
        String localityName;
        String medianPrice;

        public NearByLocalities(String imgUrl, String numberOfPropsForRent, String numberOfPropsForSale, String medianPrice, String localityName) {
            this.imgUrl = imgUrl;
            this.numberOfPropsForRent = numberOfPropsForRent;
            this.numberOfPropsForSale = numberOfPropsForSale;
            this.medianPrice = medianPrice;
            this.localityName = localityName;
        }

        String numberOfPropsForSale;
        String numberOfPropsForRent;
    }
}
