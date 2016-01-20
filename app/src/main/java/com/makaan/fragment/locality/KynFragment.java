package com.makaan.fragment.locality;

import android.app.Fragment;
import android.content.Context;
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
import com.makaan.response.amenity.AmenityCluster;
import com.makaan.ui.amenity.AmenityCardView;

import java.util.List;

/**
 * Created by tusharchaudhary on 1/20/16.
 */
public class KynFragment extends Fragment {
    private View view;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NearByLocalitiesAdapter mAdapter;
    private String title;
    private Context context ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_localities_kyn, null);
        context = getActivity();
        initView();
        return view;
    }

    private void initView() {
        title = getArguments().getString("title");
        TextView titleTv = (TextView) view.findViewById(R.id.tv_localities_kyn_title);
        titleTv.setText(title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_localities_kyn);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<AmenityCluster> amenityClusters) {
        mAdapter = new NearByLocalitiesAdapter(amenityClusters);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }


    private class NearByLocalitiesAdapter extends RecyclerView.Adapter<NearByLocalitiesAdapter.ViewHolder> {
        private List<AmenityCluster> amenityClusters;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public AmenityCardView amenityCardView;

            public ViewHolder(View v) {
                super(v);
                amenityCardView = (AmenityCardView) v.findViewById(R.id.amenity_card_view);
            }
        }

        public NearByLocalitiesAdapter(List<AmenityCluster> amenityClusters) {
            this.amenityClusters = amenityClusters;
        }

        @Override
        public NearByLocalitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_kyn, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final AmenityCluster amenityCluster = amenityClusters.get(position);
            holder.amenityCardView.bindView(context,amenityCluster);
        }

        @Override
        public int getItemCount() {
            return amenityClusters.size();
        }

    }


}

