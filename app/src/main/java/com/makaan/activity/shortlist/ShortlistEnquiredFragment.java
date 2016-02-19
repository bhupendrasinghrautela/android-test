package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.Enquiry;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.EnquiryType;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
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
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortlistEnquiredFragment extends MakaanBaseFragment {
    @Bind(R.id.enquired_recycler_view)
    RecyclerView enquiredRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HashMap<Long,Enquiry> mEnquiryHashMap;
    private HashMap<Long,Company> mCompanyHashMap;
    private ShortListEnquiredAdapter mAdapter;
    private int position;
    private ShortListCallback callback;
    private ArrayList<Long> mSellerIds;

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
       // enquiredRecyclerView.setAdapter(new ShortListEnquiredAdapter(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent) {
        if(clientLeadsByGetEvent == null || clientLeadsByGetEvent.error != null) {
            // TODO
            return;
        }
        if(mEnquiryHashMap == null){
            mEnquiryHashMap = new HashMap<>();
        }
        if(clientLeadsByGetEvent.results!=null && clientLeadsByGetEvent.results.size()>0) {
            mAdapter = new ShortListEnquiredAdapter(getActivity());
            mSellerIds = new ArrayList<>();
            for (ClientLead clientLead : clientLeadsByGetEvent.results) {
                boolean sellerAddedOnce = false;
                Long projectId = null;
                if (clientLead.propertyRequirements != null && clientLead.propertyRequirements.size() > 0) {
                    for (PropertyRequirement propertyRequirement : clientLead.propertyRequirements) {
                        Enquiry enquiry = mAdapter.new Enquiry();
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
        }


    }

    @Subscribe
    public void onResultsListing(ListingByIdGetEvent listingByIdGetEvent) {
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
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onResultsProject(ProjectByIdEvent projectByIdEvent) {
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
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onCompanyResult(ArrayList<Company> companies){
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
            mAdapter.notifyDataSetChanged();
        }
    }

    public void bindView(ShortListCallback shortListCallback, int i) {
        position = i;
        callback = shortListCallback;
    }
}
