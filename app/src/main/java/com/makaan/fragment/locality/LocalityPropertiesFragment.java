package com.makaan.fragment.locality;

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
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.pojo.TaxonomyCard;

import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalityPropertiesFragment extends MakaanBaseFragment {
    private LinearLayoutManager mLayoutManager;
    private PropertiesAdapter mAdapter;
    private String title;
    @Bind(R.id.tv_localities_props_title)
    public TextView titleTv;
    @Bind(R.id.rv_localities_props)
    public  RecyclerView mRecyclerView;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_properties;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        title = getArguments().getString("title");
        titleTv.setText(title);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(List<TaxonomyCard> taxonomyCardList) {
        mAdapter = new PropertiesAdapter(taxonomyCardList);
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(mAdapter);
    }


    private class PropertiesAdapter
            extends RecyclerView.Adapter<PropertiesAdapter.ViewHolder> {
        private List<TaxonomyCard> taxonomyCardList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView descriptionTv;
            public TextView typeTv;
            public ImageView localityIv;

            public ViewHolder(View v) {
                super(v);
                descriptionTv = (TextView) v.findViewById(R.id.tv_localities_props_label);
                typeTv = (TextView) v.findViewById(R.id.tv_localities_props_label_type);
                localityIv = (ImageView) v.findViewById(R.id.iv_localitites_props);
            }
        }

        public PropertiesAdapter(List<TaxonomyCard> taxonomyCardList) {
            this.taxonomyCardList = taxonomyCardList;
        }

        @Override
        public PropertiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_props, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final TaxonomyCard taxonomyCard = taxonomyCardList.get(position);
            holder.descriptionTv.setText(taxonomyCard.label2);
            holder.typeTv.setText(taxonomyCard.label1);
            holder.localityIv.setImageResource(getImage(position));
        }
        private int getImage(int position) {
            int id = R.drawable.placeholder_localities_props;
            switch (position) {
                case 0:
                    id = R.drawable.taxonomy_placeholder_one;
                    break;
                case 1:
                    id = R.drawable.taxonomy_placeholder_two;
                    break;
                case 2:
                    id = R.drawable.taxonomy_placeholder_three;
                    break;
                case 3:
                    id = R.drawable.taxonomy_placeholder_four;
                    break;
                case 4:
                    id = R.drawable.taxonomy_placeholder_five;
                    break;
            }
            return id;
        }

            @Override
        public int getItemCount() {
            return taxonomyCardList.size();
        }

    }
}
