package com.makaan.activity.sitevisit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.activity.shortlist.ShortListCallback;
import com.makaan.activity.sitevisit.SiteVisitUpcommingAdapter.Enquiry;
import com.makaan.activity.sitevisit.SiteVisitUpcommingAdapter.EnquiryType;
import com.makaan.event.buyerjourney.ClientEventsByGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.fragment.buyerJourney.BlogContentFragment;
import com.makaan.response.buyerjourney.ClientEvent;
import com.makaan.response.buyerjourney.Company;
import com.makaan.service.ClientEventsService;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.ListingService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by aishwarya on 18/02/16.
 */
public class SiteVisitUpcommingFragment extends MakaanBaseFragment {
    @Bind(R.id.enquired_recycler_view)
    RecyclerView enquiredRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private HashMap<Long,Enquiry> mEnquiryHashMap;
    private SiteVisitUpcommingAdapter mAdapter;
    private int position;
    private ShortListCallback callback;
    private ArrayList<Long> mSellerIds;
    private BuyerDashboardCallbacks mCallback;

    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_enquired;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        enquiredRecyclerView.setLayoutManager(mLayoutManager);
        ((ClientEventsService) MakaanServiceFactory.getInstance().getService(ClientEventsService.class)).getClientEvents(0);
        showProgress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe
    public void onResults(ClientEventsByGetEvent clientEventsByGetEvent) {
        if(clientEventsByGetEvent == null || clientEventsByGetEvent.error != null) {
            if(clientEventsByGetEvent != null && clientEventsByGetEvent.error != null
                    && clientEventsByGetEvent.error.error != null && clientEventsByGetEvent.error.error.networkResponse != null
                    && clientEventsByGetEvent.error.error.networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                if(mCallback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BlogContentFragment.TYPE, BlogContentFragment.SITE_VISIT);
                    mCallback.loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT, false, bundle, null, null);
                }
            } else if(clientEventsByGetEvent != null && !TextUtils.isEmpty(clientEventsByGetEvent.error.msg)) {
                showNoResults(clientEventsByGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        if(mEnquiryHashMap == null){
            mEnquiryHashMap = new HashMap<>();
        }
        if(clientEventsByGetEvent.results!=null && clientEventsByGetEvent.results.size()>0) {
            mAdapter = new SiteVisitUpcommingAdapter(getActivity());
            mSellerIds = new ArrayList<>();
            for (ClientEvent clientEvent : clientEventsByGetEvent.results) {
                Enquiry enquiry = mAdapter.new Enquiry();
                if (clientEvent.clientEventListings != null && clientEvent.clientEventListings.size() > 0) {
                    enquiry.id = clientEvent.id;
                    enquiry.type = EnquiryType.LISTING;
                    Long listingId = clientEvent.clientEventListings.get(0).listingId;
                    enquiry.listingId = listingId;
                    ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).getListingDetailForEnquiry(listingId);
                }
                else {
                    if (clientEvent.clientEventProjects != null && clientEvent.clientEventProjects.size() > 0) {
                        enquiry.id = clientEvent.id;
                        enquiry.type = EnquiryType.PROJECT;
                        Long projectId = clientEvent.clientEventProjects.get(0).projectId;
                        enquiry.projectId = projectId;
                        ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectByIdEnquiry(projectId);
                    }
                    if (enquiry.type == null) {
                        enquiry.id = clientEvent.id;
                        enquiry.type = EnquiryType.SELLER;
                    }
                    enquiry.clientId = clientEvent.agentId;
                    mSellerIds.add(clientEvent.agentId);
                }
                if (enquiry != null) {
                        mEnquiryHashMap.put(enquiry.id, enquiry);
                }
                enquiry.time = clientEvent.performTime;
            }
            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadCompanies(mSellerIds);
            callback.updateCount(position, mEnquiryHashMap.size());
            mAdapter.setData(mEnquiryHashMap);
            enquiredRecyclerView.setVisibility(View.VISIBLE);
            enquiredRecyclerView.setAdapter(mAdapter);
            showContent();
        }else{
            if(mCallback != null) {
                Bundle bundle = new Bundle();
                bundle.putString(BlogContentFragment.TYPE, BlogContentFragment.SITE_VISIT);
                mCallback.loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_CONTENT, false, bundle, null, null);
            }
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
            for (Enquiry enquiry:mEnquiryHashMap.values()) {
                if(enquiry.listingId!=null && enquiry.listingId.equals(listingByIdGetEvent.listingDetail.id)) {
                    enquiry.listingDetail = listingByIdGetEvent.listingDetail;
                    enquiry.latitude = enquiry.listingDetail.latitude;
                    enquiry.longitude = enquiry.listingDetail.longitude;
                    mEnquiryHashMap.put(enquiry.id, enquiry);
                }
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
            for(Enquiry enquiry:mEnquiryHashMap.values()) {
                if (enquiry.projectId != null && enquiry.projectId.equals(projectByIdEvent.project.projectId)) {
                    enquiry.project = projectByIdEvent.project;
                    enquiry.longitude = enquiry.project.longitude;
                    enquiry.latitude = enquiry.project.latitude;
                    mEnquiryHashMap.put(enquiry.id, enquiry);
                }
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
            for(Company company:companies){
                for(Enquiry enquiry:mEnquiryHashMap.values()) {
                    if (enquiry.clientId != null && enquiry.clientId.equals(company.id)) {
                        enquiry.company = company;
                        mEnquiryHashMap.put(enquiry.id, enquiry);
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public void bindView(ShortListCallback shortListCallback, int i, BuyerDashboardCallbacks callbacks) {
        position = i;
        callback = shortListCallback;
        this.mCallback = callbacks;
    }
}
