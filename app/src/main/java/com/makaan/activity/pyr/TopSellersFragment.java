package com.makaan.activity.pyr;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.pyr.TopAgentsData;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;


import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 12/1/16.
 */
public class TopSellersFragment extends Fragment {

    PyrPagePresenter mPyrPagePresenter;
    TopAgentsData [] mTopAgentsDatas;
    @Bind(R.id.sellers_recycler_view)
    RecyclerView mSellerRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    SellerListingAdapter mSellerListingAdapter;
    @Bind(R.id.btn_contact)
    Button mButtonContactAdvisors;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_top_sellers, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPyrPagePresenter= PyrPagePresenter.getPyrPagePresenter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSellerRecyclerView.setLayoutManager(mLayoutManager);
        mTopAgentsDatas = mPyrPagePresenter.getmTopAgentsDatas();
        if(mTopAgentsDatas.length>0)
        {
            mSellerListingAdapter = new SellerListingAdapter(getActivity(),mTopAgentsDatas);
            mSellerRecyclerView.setAdapter(mSellerListingAdapter);
        }

    }

    @OnClick(R.id.btn_contact)
    public void ContactAdvisorsClicked() {
        PyrRequest pyrRequest = new PyrRequest();
        PyrEnquiryType pyrEnquiryType = new PyrEnquiryType();
        pyrRequest.setEnquiryType(pyrEnquiryType);
        String str= new Gson().toJson(pyrRequest);
        Log.e("string==>> ", str);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((PyrService) (MakaanServiceFactory.getInstance().getService(PyrService.class))).makePyrRequest(jsonObject);

    }
}
