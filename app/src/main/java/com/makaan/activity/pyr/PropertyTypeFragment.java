package com.makaan.activity.pyr;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.response.master.ApiIntLabel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 12/1/16.
 */
public class PropertyTypeFragment extends Fragment {

    PyrPagePresenter mPyrPagePresenter;
    @Bind(R.id.property_type_recycler_view)
    RecyclerView mPropertyTypeRecyclerView;
    @Bind(R.id.tv_selected_count)
    TextView mTextViewCount;
    @Bind(R.id.iv_select)
    ImageView mImageViewSelected;
    private RecyclerView.LayoutManager mLayoutManager;
    PropertyTypeListingAdapter mPropertyTypeListingAdapter;
    ArrayList<ApiIntLabel> listPropertyTye;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_select_property_type, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPropertyTypeRecyclerView.setLayoutManager(mLayoutManager);
        MasterDataCache masterDataCache = MasterDataCache.getInstance();
        listPropertyTye= masterDataCache.getBuyPropertyTypes();
        Toast.makeText(getActivity(), "size::" + listPropertyTye.size(), Toast.LENGTH_SHORT).show();
        mPropertyTypeListingAdapter= new PropertyTypeListingAdapter(getActivity(),listPropertyTye);
        mPropertyTypeRecyclerView.setAdapter(mPropertyTypeListingAdapter);

    }

    @OnClick(R.id.ll_top)
    void topLayoutClicked()
    {

        //finish();
    }

    public void updateCount(int selectedCount)
    {
        if(selectedCount==0) {
            mTextViewCount.setVisibility(View.GONE);
            mImageViewSelected.setVisibility(View.INVISIBLE);
        }
        else {
            mImageViewSelected.setVisibility(View.VISIBLE);
            mTextViewCount.setVisibility(View.VISIBLE);
            mTextViewCount.setText("" + selectedCount);
        }
    }
}
