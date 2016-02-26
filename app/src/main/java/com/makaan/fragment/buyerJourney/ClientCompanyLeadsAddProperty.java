package com.makaan.fragment.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/17/16.
 */
public class ClientCompanyLeadsAddProperty extends MakaanBaseFragment {
    private ClientCompanyLeadFragment.ClientCompanyLeadsObject mObj;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_client_company_leads_add_property;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    public void setData(Object obj) {
        if(obj instanceof ClientCompanyLeadFragment.ClientCompanyLeadsObject) {
            mObj = (ClientCompanyLeadFragment.ClientCompanyLeadsObject)obj;
        }
    }

    @OnClick(R.id.fragment_client_leads_add_property_next_button)
    void onNextClicked(View view) {
        // TODO we are not checking any details filled by user
        if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mObj != null) {
                ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                        true, null, null, mObj);
            }
        }
    }
}
