package com.makaan.fragment.locality;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.pojo.TaxonomyCard;

import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalityPropertiesFragment extends Fragment {
    private View view;
    RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NearByLocalitiesAdapter mAdapter;
    private String title;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_localities_properties, null);
        initView();
        return view;
    }

    private void initView() {
        title = getArguments().getString("title");
        TextView titleTv = (TextView) view.findViewById(R.id.tv_localities_props_title);
        titleTv.setText(title);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_localities_props);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<TaxonomyCard> taxonomyCardList) {
        mAdapter = new NearByLocalitiesAdapter(taxonomyCardList);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }


    private class NearByLocalitiesAdapter extends RecyclerView.Adapter<NearByLocalitiesAdapter.ViewHolder> {
        private List<TaxonomyCard> taxonomyCardList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView descriptionTv;
            public ImageView localityIv;

            public ViewHolder(View v) {
                super(v);
                descriptionTv = (TextView) v.findViewById(R.id.tv_localities_props_label);
                localityIv = (ImageView) v.findViewById(R.id.iv_localitites_props);
            }
        }

        public NearByLocalitiesAdapter(List<TaxonomyCard> taxonomyCardList) {
            this.taxonomyCardList = taxonomyCardList;
        }

        @Override
        public NearByLocalitiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_props, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TaxonomyCard taxonomyCard = taxonomyCardList.get(position);
            holder.descriptionTv.setText(taxonomyCard.label1);
            holder.localityIv.setImageResource(R.drawable.placeholder_localities_props);
            //TODO: Picasso load imgurl
        }

        @Override
        public int getItemCount() {
            return taxonomyCardList.size();
        }

    }
}
