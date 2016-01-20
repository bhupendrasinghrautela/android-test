package com.makaan.fragment.locality;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makaan.R;

import java.util.List;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalitiesApartmentsFragment extends Fragment {
    private View view;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NearByLocalitiesAdapter mAdapter;
    private String title;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_localities_apartments, null);
        initView();
        return view;
    }

    private void initView() {
        title = getArguments().getString("title");
        TextView titleTv = (TextView) view.findViewById(R.id.tv_localities_aprmnts_title);
        titleTv.setText(title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_localities_aprmnts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<Properties> nearByLocalities) {
        mAdapter = new NearByLocalitiesAdapter(nearByLocalities);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }


    private class NearByLocalitiesAdapter extends RecyclerView.Adapter<NearByLocalitiesAdapter.ViewHolder> {
        private List<Properties> propertiesList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView unitTypeTv;
            public TextView oneBhkLabel, oneBhkPriceRange, oneBhkPriceAreaRange, oneBhkAreaRange;
            public TextView twoBhkLabel, twoBhkPriceRange, twoBhkPriceAreaRange, twoBhkAreaRange;
            public TextView threeBhkLabel, threeBhkPriceRange, threeBhkPriceAreaRange, threeBhkAreaRange;

            public ViewHolder(View v) {
                super(v);
                oneBhkLabel = (TextView) v.findViewById(R.id.apartment_row_one_bhk);
                oneBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_one_price);
                oneBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_one_price_per_sqft);
                oneBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_one_area);
                unitTypeTv = (TextView) v.findViewById(R.id.apartment_unit_type);


                twoBhkLabel = (TextView) v.findViewById(R.id.apartment_row_two_bhk);
                twoBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_two_price);
                twoBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_two_price_per_sqft);
                twoBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_two_area);


                threeBhkLabel = (TextView) v.findViewById(R.id.apartment_row_three_bhk);
                threeBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_three_price);
                threeBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_three_price_per_sqft);
                threeBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_three_area);
            }
        }

        public NearByLocalitiesAdapter(List<Properties> propertiesList) {
            this.propertiesList = propertiesList;
        }

        @Override
        public NearByLocalitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.locality_apartments_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Properties property = propertiesList.get(position);
            holder.unitTypeTv.setText(property.type);

            holder.oneBhkLabel.setText(property.oneBhk);
            holder.oneBhkAreaRange.setText(property.oneBhkAreaRange);
            holder.oneBhkPriceAreaRange.setText(property.oneBhkPriceAreaRange);
            holder.oneBhkPriceRange.setText(property.oneBhkPriceRange);

            holder.twoBhkLabel.setText(property.twoBhk);
            holder.twoBhkAreaRange.setText(property.twoBhkAreaRange);
            holder.twoBhkPriceAreaRange.setText(property.twoBhkPriceAreaRange);
            holder.twoBhkPriceRange.setText(property.twoBhkPriceRange);

            holder.threeBhkLabel.setText(property.threeBhk);
            holder.threeBhkAreaRange.setText(property.threeBhkAreaRange);
            holder.threeBhkPriceAreaRange.setText(property.threeBhkPriceAreaRange);
            holder.threeBhkPriceRange.setText(property.threeBhkPriceRange);
        }

        @Override
        public int getItemCount() {
            return propertiesList.size();
        }

    }

    public static class Properties {
        String type;
        String oneBhk;
        String twoBhk;
        String threeBhk;

        String oneBhkPriceRange;
        String twoBhkPriceRange;
        String threeBhkPriceRange;

        String oneBhkAreaRange;
        String twoBhkAreaRange;
        String threeBhkAreaRange;

        String oneBhkPriceAreaRange;
        String twoBhkPriceAreaRange;
        String threeBhkPriceAreaRange;

        public Properties(String type,
                String oneBhk,
                String twoBhk,
                String threeBhk,
                String oneBhkPriceRange,
                String twoBhkPriceRange,
                String threeBhkPriceRange,
                String oneBhkAreaRange,
                String twoBhkAreaRange,
                String threeBhkAreaRange,
                String oneBhkPriceAreaRange,
                String twoBhkPriceAreaRange,
                String threeBhkPriceAreaRange) {
            this.type = type;
            this.oneBhk = oneBhk;
            this.twoBhk = twoBhk;
            this.threeBhk = threeBhk;
            this.oneBhkPriceRange = oneBhkPriceRange;
            this.twoBhkPriceRange = twoBhkPriceRange;
            this.threeBhkPriceRange = threeBhkPriceRange;
            this.oneBhkAreaRange = oneBhkAreaRange;
            this.twoBhkAreaRange = twoBhkAreaRange;
            this.threeBhkAreaRange = threeBhkAreaRange;
            this.oneBhkPriceAreaRange = oneBhkPriceAreaRange;
            this.twoBhkPriceAreaRange = twoBhkPriceAreaRange;
            this.threeBhkPriceAreaRange = threeBhkPriceAreaRange;
        }
    }
}
