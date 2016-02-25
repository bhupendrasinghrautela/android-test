package com.makaan.fragment.pyr;


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
import com.makaan.activity.pyr.PropertyTypeListingAdapter;
import com.makaan.cache.MasterDataCache;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.response.master.ApiIntLabel;
import com.makaan.response.serp.TermFilter;

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
    TextView mImageViewSelected;
    private RecyclerView.LayoutManager mLayoutManager;
    private PyrPagePresenter pyrPagePresenter;
    PropertyTypeListingAdapter mPropertyTypeListingAdapter;
    private ArrayList<TermFilter> mTermFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_select_property_type, container, false);
        pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mPropertyTypeRecyclerView.setLayoutManager(mLayoutManager);
        mPropertyTypeListingAdapter= new PropertyTypeListingAdapter(getActivity(),mTermFilter, this);
        mPropertyTypeRecyclerView.setAdapter(mPropertyTypeListingAdapter);

    }

    @OnClick(R.id.ll_top)
    void topLayoutClicked()
    {

    }

    @Override
    public void onPause() {
        super.onPause();
        //pyrPagePresenter.setPropertyTypeCount();
    }

    @Override
    public void onStop() {
        super.onStop();
        pyrPagePresenter.setPropertyTypeCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.ll_top)
    public void dismissPropertyTypeFragment(){
        getFragmentManager().popBackStack();
    }

    public void updateCount(int selectedCount)
    {
        if(selectedCount==0) {
            mTextViewCount.setVisibility(View.GONE);
        }
        else {
            mTextViewCount.setVisibility(View.VISIBLE);
            mTextViewCount.setText(String.valueOf(selectedCount));
        }
    }

    public void setValues(ArrayList<TermFilter> termFilter) {
        mTermFilter = termFilter;
    }
}
