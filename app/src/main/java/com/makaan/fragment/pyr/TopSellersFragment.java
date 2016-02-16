package com.makaan.fragment.pyr;


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
import android.widget.Toast;

import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.activity.pyr.SellerListingAdapter;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.pyr.PyrData;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by makaanuser on 12/1/16.
 */
public class TopSellersFragment extends Fragment {
    PyrPagePresenter mPyrPagePresenter;
    ArrayList<TopAgent> mTopAgentsDatas;
    @Bind(R.id.sellers_recycler_view)
    RecyclerView mSellerRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    SellerListingAdapter mSellerListingAdapter;
    @Bind(R.id.btn_contact)
    Button mButtonContactAdvisors;
    boolean mBound = false;
    private static final int DEFAULT_SELECTED_COUNT=4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_top_sellers, container, false);
        ButterKnife.bind(this, view);
        if(!mBound) {
            AppBus.getInstance().register(this);
            mBound = true;
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPyrPagePresenter= PyrPagePresenter.getPyrPagePresenter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSellerRecyclerView.setLayoutManager(mLayoutManager);
        mTopAgentsDatas = mPyrPagePresenter.getmTopAgentsDatas();
        if(mTopAgentsDatas.size()<=DEFAULT_SELECTED_COUNT){
            changeSellerCount(mTopAgentsDatas.size());
        }
        else{
            changeSellerCount(DEFAULT_SELECTED_COUNT);
        }
        if(mTopAgentsDatas.size()>0)
        {
            mSellerListingAdapter = new SellerListingAdapter(getActivity(),mTopAgentsDatas, this);
            mSellerRecyclerView.setAdapter(mSellerListingAdapter);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        AppBus.getInstance().unregister(this);
    }

    @OnClick(R.id.btn_contact)
    public void ContactAdvisorsClicked() {
        PyrRequest pyrRequest = mPyrPagePresenter.getPyrRequestObject();
        mPyrPagePresenter.setSellerIdToPyrObject(pyrRequest);
        if(pyrRequest.getMultipleSellerIds().length==0){
            Toast.makeText(getActivity(),"please select at least one advisor",Toast.LENGTH_SHORT).show();
            return;
        }
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
        if(jsonObject!=null)
        ((PyrService) (MakaanServiceFactory.getInstance().getService(PyrService.class))).makePyrRequest(jsonObject);
    }

    @Subscribe
    public void pyrResponse(PyrPostResponse pyrPostResponse){
        if(pyrPostResponse.getStatusCode().equals("2XX")) {
            if(pyrPostResponse.getData().isOtpVerified()){
                if (mPyrPagePresenter.isMakkanAssist()) {
                    PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
                    mPyrPagePresenter.showThankYouScreenFragment(true, false, false);
                }
                else {
                    PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
                    mPyrPagePresenter.showThankYouScreenFragment(false, false, false);
                }
            }
            if(!pyrPostResponse.getData().isOtpVerified()) {
                PyrPagePresenter mPyrPagePresenter = PyrPagePresenter.getPyrPagePresenter();
                PyrOtpVerification fragment = mPyrPagePresenter.showPyrOtpFragment();
                fragment.setData(pyrPostResponse.getData());
            }
        }
    }

    public void changeSellerCount(int count){
        String value =count>0?String.valueOf(count):"";
        String contactValue=getResources().getString(R.string.contact_string)+" "+ value +" "+
                getResources().getString(R.string.advisors_string);
        mButtonContactAdvisors.setText(contactValue.toLowerCase());
    }

}
