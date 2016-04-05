package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.Enquiry;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.EnquiryType;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.buyerjourney.SiteVisitEventGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.response.buyerjourney.ClientLead.PropertyRequirement;
import com.makaan.response.buyerjourney.Company;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.makaan.util.ErrorUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortlistEnquiredFragment extends MakaanBaseFragment implements ShortListEnquiredAdapter.OnSiteVisitRequestLitener{
    @Bind(R.id.enquired_recycler_view)
    RecyclerView enquiredRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HashMap<Long,Enquiry> mEnquiryHashMap;
    private HashMap<Long,Company> mCompanyHashMap;
    private ShortListEnquiredAdapter mAdapter;
    private int position;
    private ShortListCallback callback;
    private ArrayList<Long> mSellerIds;
    private Long enquiryId;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_enquired;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        enquiredRecyclerView.setLayoutManager(mLayoutManager);
        ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeads();
        showProgress();
       // enquiredRecyclerView.setAdapter(new ShortListEnquiredAdapter(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(clientLeadsByGetEvent == null || clientLeadsByGetEvent.error != null) {
            if(clientLeadsByGetEvent != null && clientLeadsByGetEvent.error != null && clientLeadsByGetEvent.error.msg != null) {
                showNoResults(clientLeadsByGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        if(mEnquiryHashMap == null){
            mEnquiryHashMap = new HashMap<>();
        }
        if(clientLeadsByGetEvent.results!=null && clientLeadsByGetEvent.results.size()>0) {
            mAdapter = new ShortListEnquiredAdapter(getActivity(),this);
            mSellerIds = new ArrayList<>();
            for (ClientLead clientLead : clientLeadsByGetEvent.results) {
                boolean sellerAddedOnce = false;
                Long projectId = null;
                if (clientLead.propertyRequirements != null && clientLead.propertyRequirements.size() > 0) {
                    for (PropertyRequirement propertyRequirement : clientLead.propertyRequirements) {
                        Enquiry enquiry = mAdapter.new Enquiry();
                        enquiry.leadId = propertyRequirement.leadId;
                        if (propertyRequirement.listingId != null) {
                            enquiry.id = propertyRequirement.listingId;
                            enquiry.type = EnquiryType.LISTING;
                            ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).getListingDetailForEnquiry(enquiry.id);
                        } else if (propertyRequirement.projectId != null) {
                            enquiry.id = propertyRequirement.projectId;
                            enquiry.type = EnquiryType.PROJECT;
                            projectId = propertyRequirement.projectId;
                            enquiry.clientId = clientLead.companyId;
                            mSellerIds.add(clientLead.companyId);
                            ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectByIdEnquiry(enquiry.id);
                        } else {
                            if (!sellerAddedOnce) {
                                enquiry.id = clientLead.companyId;
                                enquiry.type = EnquiryType.SELLER;
                                if(projectId!=null){
                                    enquiry.projectId = projectId;
                                }
                                mSellerIds.add(clientLead.companyId);
                                sellerAddedOnce = true;
                            } else {
                                enquiry = null;
                            }
                        }
                        if (enquiry != null) {
                            mEnquiryHashMap.put(enquiry.id,enquiry);
                        }
                    }
                }
            }
            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadCompanies(mSellerIds);
            callback.updateCount(position, mEnquiryHashMap.size());
            mAdapter.setData(mEnquiryHashMap);
            enquiredRecyclerView.setVisibility(View.VISIBLE);
            enquiredRecyclerView.setAdapter(mAdapter);
            showContent();
        } else {
            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, false));
        }


    }

    @Subscribe
    public void onResultsListing(ListingByIdGetEvent listingByIdGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(listingByIdGetEvent.listingDetail ==null || listingByIdGetEvent.error!=null){
            return;
        }
        else {
            if(mEnquiryHashMap == null){
                mEnquiryHashMap = new HashMap<>();
            }
            Enquiry enquiry = mEnquiryHashMap.get(listingByIdGetEvent.listingDetail.id);
            if(enquiry!=null) {
                enquiry.listingDetail = listingByIdGetEvent.listingDetail;
                mEnquiryHashMap.put(enquiry.id, enquiry);
            }
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onResultsProject(ProjectByIdEvent projectByIdEvent) {
        if(!isVisible()) {
            return;
        }
        if(projectByIdEvent.project ==null || projectByIdEvent.error!=null){
            return;
        }
        else {
            if(mEnquiryHashMap == null){
                mEnquiryHashMap = new HashMap<>();
            }
            Enquiry enquiry = mEnquiryHashMap.get(projectByIdEvent.project.projectId);
            if(enquiry!=null) {
                enquiry.project = projectByIdEvent.project;
                if (enquiry.clientId != null && mCompanyHashMap != null) {
                    Company company = mCompanyHashMap.get(enquiry.clientId);
                    if (company != null) {
                        enquiry.company = company;
                    }
                }
                mEnquiryHashMap.put(enquiry.id, enquiry);
            }
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onCompanyResult(ArrayList<Company> companies){
        if(!isVisible()) {
            return;
        }
        if(companies ==null || companies.size()==0){
            return;
        }
        else {
            if(mCompanyHashMap == null){
                mCompanyHashMap = new HashMap<>();
            }
            for(Company company:companies){
                mCompanyHashMap.put(company.id,company);
                Enquiry enquiry = mEnquiryHashMap.get(company.id);
                if(enquiry!=null) {
                    enquiry.company = company;
                    mEnquiryHashMap.put(enquiry.id, enquiry);
                    if(enquiry.projectId!=null){
                        Enquiry enquiryProject = mEnquiryHashMap.get(enquiry.projectId);
                        enquiryProject.company = company;
                        mEnquiryHashMap.put(enquiryProject.id,enquiryProject);
                    }
                }
            }
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void showSitevisitMessage(SiteVisitEventGetEvent siteVisitEventGetEvent){
        if(!isVisible()){
            return;
        }
        if(siteVisitEventGetEvent!=null){
            if(siteVisitEventGetEvent.error!=null){
                Toast.makeText(getActivity(),siteVisitEventGetEvent.error.msg
                        , Toast.LENGTH_SHORT).show();
            }
            else if(siteVisitEventGetEvent.isScheduled){
                /*----- track events--------*/
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", enquiryId,
                        MakaanTrackerConstants.Label.siteVisitOk));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.shortlistEnquire);
                /*-------------------------*/
                Toast.makeText(getActivity(),getString(R.string.site_visit_scheduled)
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void bindView(ShortListCallback shortListCallback, int i) {
        position = i;
        callback = shortListCallback;
    }

    @Override
    public void setEnquiryId(Long id) {
        enquiryId=id;
    }
}
