package com.makaan.fragment.locality;

import android.app.Fragment;
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

import com.makaan.R;

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
        public void onBindViewHolder(ViewHolder holder, int position) {
            final NearByLocalities nearByLocalitu = nearByLocalitiesList.get(position);
            holder.medianTv.setText(nearByLocalitu.medianPrice);
            holder.nameTv.setText(nearByLocalitu.localityName);
            holder.numberOfPropsForSaleTv.setText(nearByLocalitu.numberOfPropsForSale);
            holder.numberOfPropsForRentTv.setText(nearByLocalitu.numberOfPropsForRent);
            holder.localityIv.setImageResource(placeholderResource);
            //TODO: Picasso load imgurl
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
