package com.makaan.fragment.buyerJourney;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.segment.analytics.Properties;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/17/16.
 */
public class ClientCompanyLeadsAddProperty extends MakaanBaseFragment {
    @Bind(R.id.fragment_client_company_leads_add_property_property_name_edit_text)
    EditText mPropertyNameEditText;
    @Bind(R.id.fragment_client_company_leads_add_property_name_edit_text)
    EditText mLocalityNameEditText;
    @Bind(R.id.fragment_client_company_leads_add_property_city_name_edit_text)
    EditText mCityNameEditText;

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
        if(TextUtils.isEmpty(mPropertyNameEditText.getText().toString())) {
            mPropertyNameEditText.setError("enter some data");
        } else if(TextUtils.isEmpty(mLocalityNameEditText.getText().toString())) {
            mLocalityNameEditText.setError("enter some data");
        } else if(TextUtils.isEmpty(mCityNameEditText.getText().toString())) {
            mCityNameEditText.setError("enter some data");
        } else if(getActivity() instanceof BuyerDashboardCallbacks) {
            if(mObj != null) {
            /*----------------------- track events-------------------------*/
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, MakaanTrackerConstants.Label.addAnotherListing);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.clickCashBackListing);
            /*-----------------------------------------------------------------*/

                ((BuyerDashboardCallbacks) getActivity()).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_REVIEW_AGENT,
                        true, null, null, mObj);
            }
        }
    }
}
