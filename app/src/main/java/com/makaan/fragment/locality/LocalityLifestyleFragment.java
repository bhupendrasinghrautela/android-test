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
import com.makaan.response.city.EntityDesc;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalityLifestyleFragment extends MakaanBaseFragment{
    private LinearLayoutManager mLayoutManager;
    private LocalityLifestyleAdapter mAdapter;
    private String title;
    @Bind(R.id.rv_localities_lifestyle)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_localities_lifestyle_title)
    TextView titleTv;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_lifestyle;
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
        if(mRecyclerView.getAdapter().getItemCount() == 0)
            mRecyclerView .setVisibility(View.GONE);
        else
            mRecyclerView.setAdapter(mAdapter);
    }

    public void setData(ArrayList<EntityDesc> entityDescs) {
            mAdapter = new LocalityLifestyleAdapter(entityDescs);
    }


    private class LocalityLifestyleAdapter extends RecyclerView.Adapter<LocalityLifestyleAdapter.ViewHolder> {
        private List<EntityDesc> entityDescs;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView descriptionTv;
            public TextView descriptionFullTv;
            public ImageView localityIv;

            public ViewHolder(View v) {
                super(v);
                descriptionTv = (TextView) v.findViewById(R.id.tv_localities_props_label);
                descriptionFullTv = (TextView) v.findViewById(R.id.tv_localities_lifestyle_description);
                localityIv = (ImageView) v.findViewById(R.id.iv_localitites_props);
            }
        }

        public LocalityLifestyleAdapter(List<EntityDesc> entityDescs) {
            this.entityDescs = entityDescs;
        }

        @Override
        public LocalityLifestyleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_localities_lifestyle, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final EntityDesc nearByLocalitu = entityDescs.get(position);
//            holder.descriptionTv.setText(nearByLocalitu.description);
            holder.descriptionFullTv.setText(nearByLocalitu.description);
            holder.localityIv.setImageResource(R.drawable.placeholder_localities_props);
            //TODO: Picasso load imgurl
        }

        @Override
        public int getItemCount() {
            return entityDescs.size();
        }

    }

}
