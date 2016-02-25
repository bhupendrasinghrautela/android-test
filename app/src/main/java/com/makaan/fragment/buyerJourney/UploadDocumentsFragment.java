package com.makaan.fragment.buyerJourney;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.makaan.R;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.request.buyerjourney.PhaseChange;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.MasterDataService;

import java.util.Date;

import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class UploadDocumentsFragment extends MakaanBaseFragment {
    ClientCompanyLeadFragment.ClientCompanyLeadsObject mObj;
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_upload_documents;
    }

    public void setData(Object obj) {
        mObj = (ClientCompanyLeadFragment.ClientCompanyLeadsObject) obj;
    }

    @OnClick(R.id.fragment_upload_documents_send_button)
    void onSendClicked(View view) {

        updatePhase();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "cashback@makaan.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "cashback request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    private void updatePhase() {
        if(mObj != null) {
            if(mObj.clientLeadObject != null && mObj.clientLeadObject.clientLead != null
                    && mObj.clientLeadObject.clientLead.id != null
                    && mObj.listingDetail.id != null) {
                PhaseChange change = new PhaseChange();
                change.agentId = mObj.clientLeadObject.clientLead.companyId;
                change.eventTypeId = LeadPhaseConstants.LEAD_EVENT_CASHBACK_REQUESTED;
                change.performTime = new Date().getTime();
                ((ClientEventsService) (MakaanServiceFactory.getInstance().getService(ClientEventsService.class))).changePhase(mObj.clientLeadObject.clientLead.id, change);
            }
        }
    }

    @OnClick(R.id.fragment_upload_documents_done_button)
    void onDoneClicked(View view) {
        updatePhase();
        getActivity().finish();
    }
}
