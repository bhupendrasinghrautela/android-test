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

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.makaan.R;
import com.makaan.activity.pyr.PyrOtpVerification;
import com.makaan.activity.pyr.SellerListingAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.network.VolleyErrorParser;
import com.makaan.request.pyr.PyrEnquiryType;
import com.makaan.request.pyr.PyrRequest;
import com.makaan.response.agents.TopAgent;
import com.makaan.response.pyr.PyrPostResponse;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.PyrService;
import com.makaan.util.AppBus;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
    private boolean mAlreadyLoaded=false;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPyrPagePresenter= PyrPagePresenter.getPyrPagePresenter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSellerRecyclerView.setLayoutManager(mLayoutManager);
        mTopAgentsDatas = mPyrPagePresenter.getmTopAgentsDatas();
        if(mTopAgentsDatas!=null && mTopAgentsDatas.size()>0){
            long agentId[]=new long[mTopAgentsDatas.size()];
            int i=0;
            for(TopAgent agent:mTopAgentsDatas) {
                if(agent.agent!=null && agent.agent.company.id!=null){
                    agentId[i]=agent.agent.company.id;
                    i++;
                }
            }
            if(!mAlreadyLoaded) {
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
                properties.put(MakaanEventPayload.LABEL, Arrays.toString(agentId));
                MakaanEventPayload.endBatch(getContext(), mPyrPagePresenter.getViewSellersAction(mPyrPagePresenter.getSourceScreenName()));

                /*--------------------track--------------code-------------*/
                Properties properties1 = MakaanEventPayload.beginBatch();
                properties1.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_PYR);
                properties1.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_PYR_TOP_SELLER);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.screenName);
                /*--------------------------------------------------------*/
                mAlreadyLoaded=true;
            }
            if(mTopAgentsDatas.size()<=DEFAULT_SELECTED_COUNT){
                changeSellerCount(mTopAgentsDatas.size());
            }

            if(mTopAgentsDatas.size()>0)
            {
                mSellerListingAdapter = new SellerListingAdapter(getActivity(),mTopAgentsDatas, this);
                mSellerRecyclerView.setAdapter(mSellerListingAdapter);
            }

        }else{
            changeSellerCount(DEFAULT_SELECTED_COUNT);
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
        if(pyrRequest.getMultipleCompanyIds().length==0){
            Toast.makeText(getActivity(),"please select at least one advisor",Toast.LENGTH_SHORT).show();
            return;
        }
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerPyr);
        properties.put(MakaanEventPayload.LABEL, Arrays.toString(pyrRequest.getMultipleCompanyIds()));
        MakaanEventPayload.endBatch(getContext(), mPyrPagePresenter.getSelectSellersAction(mPyrPagePresenter.getSourceScreenName()));

        PyrEnquiryType pyrEnquiryType = new PyrEnquiryType();
        pyrRequest.setEnquiryType(pyrEnquiryType);
        String str= new Gson().toJson(pyrRequest);
        Log.e("string==>> ", str);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        if(jsonObject!=null)
        ((PyrService) (MakaanServiceFactory.getInstance().getService(PyrService.class))).makePyrRequest(jsonObject);
    }

    @Subscribe
    public void pyrResponse(PyrPostResponse pyrPostResponse){
        if(!isVisible()) {
            return;
        }
        if(pyrPostResponse.getStatusCode()!=null && pyrPostResponse.getStatusCode().equals("2XX")) {
            Properties properties1 = MakaanEventPayload.beginBatch();
            properties1.put(MakaanEventPayload.CATEGORY, mPyrPagePresenter.getCategoryForPyrSubmit(mPyrPagePresenter.getSourceScreenName()));
            properties1.put(MakaanEventPayload.LABEL, mPyrPagePresenter.getLabelStringOnNextClick(mPyrPagePresenter.getPyrRequestObject()));
            properties1.put(MakaanEventPayload.VALUE, mPyrPagePresenter.getPyrRequestObject().getMultipleCompanyIds().length);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.leadStoredPyr);

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
        else if(pyrPostResponse.getError()!=null){
            String msg = VolleyErrorParser.getMessage(pyrPostResponse.getError());
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            Properties properties = MakaanEventPayload.beginBatch();
            properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.errorBuyer);
            properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.errorWhileSubmitting);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.errorPyr);
        }
    }

    public void changeSellerCount(int count){
        String value =count>0?String.valueOf(count):"";
        String contactValue=getResources().getString(R.string.contact_string)+" "+ value +" "+
                getResources().getString(R.string.advisors_string);
        mButtonContactAdvisors.setText(contactValue.toLowerCase());
    }

}
